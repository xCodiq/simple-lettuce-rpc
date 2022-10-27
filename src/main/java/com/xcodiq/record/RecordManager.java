/*
 * MIT License
 *
 * Copyright (c) 2022 - Elmar (Cody) Lynn, xCodiq
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xcodiq.record;

import com.xcodiq.exception.RecordHandlerAlreadyBoundException;
import com.xcodiq.exception.RecordHandlerNotFoundException;
import com.xcodiq.lettuce.LettuceManager;
import com.xcodiq.packet.RedisPacket;
import com.xcodiq.packet.internal.IncomingPacket;
import com.xcodiq.packet.listener.PacketListener;
import com.xcodiq.packet.serialization.PacketSerializer;
import com.xcodiq.rpc.RPC;
import com.xcodiq.util.collection.ExpiringList;
import com.xcodiq.util.collection.ExpiringMap;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The record manager is responsible for handling all incoming packets and
 * sending them to the correct handler to prepare replies and send them back.
 *
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public final class RecordManager {

	private static final Logger LOG = LoggerFactory.getLogger(RecordManager.class);

	private final ExpiringList<UUID> handledPackets = new ExpiringList<>(10, TimeUnit.SECONDS);
	private final ExpiringMap<UUID, Record<?, ?>> pendingRecords = new ExpiringMap<>(
			(uuid, record) -> record.timeout(), 1, TimeUnit.SECONDS);

	private final Map<Class<? extends Record<?, ?>>, RecordHandler<?, ?>> recordHandlers = new HashMap<>();

	private final LettuceManager lettuceManager;
	private final String recordPrefix;

	/**
	 * Constructs a new record manager given a {@link RPC} instance
	 *
	 * @param rpc the {@link RPC} instance to use
	 */
	public RecordManager(@NotNull RPC<?> rpc) {
		// Get the lettuce manager
		this.lettuceManager = rpc.getLettuceManager();

		// Get the record prefix from the rpc options
		this.recordPrefix = rpc.getOptions().getRecordPrefix();
		final String replyRecordPrefix = "reply." + this.recordPrefix;

		// Create a new pub sub connection
		final StatefulRedisPubSubConnection<String, String> statefulConnection = this.lettuceManager.getRedisClient().connectPubSub();

		// Register the sent-packet listener
		statefulConnection.addListener(new PacketListener(this.recordPrefix) {
			@Override
			public void process(String channel, String serializedPacket) {
				// Read the incoming packet from the message
				final IncomingPacket incomingPacket = RPC.getGson().fromJson(serializedPacket, IncomingPacket.class);
				if (incomingPacket == null || !isRecordHandlerBound(incomingPacket.getRecordClass())) return;

				// Deserialize the packet and check if serialized correctly
				final RedisPacket redisPacket = PacketSerializer.deserialize(incomingPacket.getSerializedPacket());
				if (redisPacket == null || redisPacket.isReplyPacket()) return;

				// Check if the packet has been handled, if not add it
				if (handledPackets.contains(redisPacket.getPacketId())) return;
				else handledPackets.add(redisPacket.getPacketId());

				// Check if there is a record handler available
				final RecordHandler<?, ?> recordHandler = getRecordHandler(incomingPacket.getRecordClass());
				if (recordHandler == null) throw new RecordHandlerNotFoundException(
						"No RecordHandler has been found while preparing a reply-packet.");

				// Prepare a reply-packet using the record handler
				final RedisPacket replyPacket = recordHandler.getReplyPacket(redisPacket);
				if (replyPacket == null) return;

				// Serialize the reply-packet and publish it back to the sender
				final String serializedReplyPacket = PacketSerializer.serialize(replyPacket);
				lettuceManager.publish(replyRecordPrefix + "." + channel, serializedReplyPacket);

				// Debug log
				LOG.debug("Published reply-packet with packetId: " + replyPacket.getPacketId());
			}
		});

		// Register the reply-packet listener
		statefulConnection.addListener(new PacketListener(replyRecordPrefix) {
			@Override
			public void process(String channel, String serializedPacket) {
				// Deserialize the message to a redis-packet
				final RedisPacket replyPacket = PacketSerializer.deserialize(serializedPacket);
				if (replyPacket == null || !replyPacket.isReplyPacket()) return;

				// Remove-get the record from the pending records list
				final Record<?, ?> record = pendingRecords.remove(replyPacket.getRecordId());
				if (record == null) return;

				// Complete the record by passing the reply-packet
				record.complete(replyPacket);
			}
		});

		// Create an async connection and subscribe to the communication pattern
		final RedisPubSubAsyncCommands<String, String> pubSubAsyncCommands = statefulConnection.async();
		pubSubAsyncCommands.psubscribe(this.recordPrefix + ".*", replyRecordPrefix + ".*");

		// Debug log
		LOG.debug("Subscribed to the communication pattern: '" + this.recordPrefix + ".*' and '" + replyRecordPrefix + ".*'");
	}

	/**
	 * Bind a record class to a new record handler
	 *
	 * @param recordClass   the record class to bind
	 * @param recordHandler the record handler to bind
	 * @throws RecordHandlerAlreadyBoundException if the record class is already bound to a record handler
	 */
	public void bindRecordHandler(@NotNull Class<? extends Record<?, ?>> recordClass, @NotNull RecordHandler<?, ?> recordHandler) {
		// Check if the record handler is already bound to a record class
		if (this.recordHandlers.containsKey(recordClass)) throw new RecordHandlerAlreadyBoundException(
				"A record handler has already been bound to the record class: " + recordClass.getName());

		// If not, bind the record handler to the record class
		this.recordHandlers.put(recordClass, recordHandler);
	}

	/**
	 * Check if a record class is bound to a record handler
	 *
	 * @param recordClass the record class to check
	 * @return {@code true} if the record class is bound to a record handler, {@code false} otherwise
	 */
	public boolean isRecordHandlerBound(@NotNull Class<?> recordClass) {
		return this.recordHandlers.containsKey(recordClass);
	}

	/**
	 * Get the record handler bound to a record class
	 *
	 * @param recordClass the record class to get the record handler from
	 * @return the record handler bound to the record class
	 */
	public @Nullable RecordHandler<?, ?> getRecordHandler(Class<?> recordClass) {
		return this.recordHandlers.get(recordClass);
	}

	/**
	 * Send a new record to all listening instances
	 *
	 * @param record   the record to send
	 * @param timeout  the timeout of the record
	 * @param timeUnit the time unit of the timeout
	 * @param <P>      the generic of the redis-packet type
	 */
	public <P extends RedisPacket> void send(@NotNull Record<P, ?> record, int timeout, TimeUnit timeUnit) {
		// Make sure the sent-packet channel and record channel are the same
		record.getSentPacket().setChannel(record.getChannel());

		// Add the record to the pending records
		this.pendingRecords.put(record.getSentPacket().getRecordId(), record, timeout, timeUnit);

		// Ignore the packet from getting processed by its own sub client
		this.handledPackets.add(record.getSentPacket().getPacketId());

		// Publish the packet to the channel with the lettuce manager
		this.lettuceManager.publish(this.recordPrefix + "." + record.getRecordId(), RPC.getGson().toJson(
				IncomingPacket.of(record.getClass(), PacketSerializer.serialize(record.getSentPacket()))));

		// Finally, update the sent-at time
		record.setSentAt(Instant.now());
	}

	/**
	 * Send a new record to all listening instances
	 *
	 * @param record the record to send
	 * @param <P>    the generic of the redis-packet type
	 * @see RecordManager#send(Record, int, TimeUnit)
	 */
	public <P extends RedisPacket> void send(@NotNull Record<P, ?> record) {
		this.send(record, record.getTimeout(), record.getTimeUnit());
	}
}
