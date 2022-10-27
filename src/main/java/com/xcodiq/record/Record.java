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

import com.xcodiq.exception.RecordManagerNotFoundException;
import com.xcodiq.packet.PacketStatus;
import com.xcodiq.packet.RedisPacket;
import com.xcodiq.rpc.RPC;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
@Getter
public class Record<P extends RedisPacket, R extends RedisPacket> {

	private final UUID recordId; // record unique id

	private final P sentPacket;
	private final String channel;

	private Consumer<P> timeoutConsumer; // called when the record times out
	private Consumer<R> replyConsumer; // called when the record receives a reply

	private int timeout = 5; // the maximum time to wait
	private TimeUnit timeUnit = TimeUnit.SECONDS; // the time unit of the timeout argument

	@Setter
	private Instant sentAt;

	/**
	 * Constructs a new {@link Record} given an instance of the required sent-packet
	 *
	 * @param sentPacket the sent-packet instance to publish
	 */
	public Record(@NotNull P sentPacket) {
		// Generate a new random identifier for the record
		this.recordId = UUID.randomUUID();

		// Link the record id to the sent-packet
		this.sentPacket = sentPacket;
		this.sentPacket.setRecordId(this.recordId);

		// Set the channel to the sent-packet channel
		this.channel = sentPacket.getChannel();
	}

	/**
	 * Complete the {@link Record} by accepting the {@link Record#onReply(Consumer)} consumer
	 *
	 * @param replyPacket the reply packet to use as acceptance
	 */
	public void complete(RedisPacket replyPacket) {
		// Check if the reply consumer is null
		if (this.replyConsumer != null) {
			// If not, call the reply consumer
			this.replyConsumer.accept((R) replyPacket);
		} else {
			// If it is, set the packet status to NO_CONTENT and call the timeout consumer
			this.sentPacket.setPacketStatus(PacketStatus.NO_CONTENT);
			this.timeout();
		}
	}

	/**
	 * Timeout the {@link Record} by accepting the {@link Record#onTimeout(Consumer)} consumer
	 *
	 * @apiNote This method is called automatically when the record times out,
	 * which can be configured using {@link Record#setTimeout(int, TimeUnit)}
	 */
	public void timeout() {
		// Check if the timeout consumer has been configured
		if (this.timeoutConsumer == null) return;

		// If it has, call the timeout consumer with the sent-packet
		this.timeoutConsumer.accept(this.sentPacket);
	}

	/**
	 * Set the timeout of the {@link Record}
	 *
	 * @param timeout  the timeout to set
	 * @param timeUnit the time unit of the timeout
	 * @return the record instance, for chaining
	 */
	public Record<P, R> setTimeout(int timeout, TimeUnit timeUnit) {
		// Update the timeout and timeUnit fields
		this.timeout = timeout;
		this.timeUnit = timeUnit;

		// Return itself (builder)
		return this;
	}

	/**
	 * Send the {@link Record} using a specific {@link RecordManager}
	 *
	 * @param recordManager the record manager to use
	 * @return the record instance, for chaining
	 *
	 * @see RecordManager#send(Record)
	 */
	public Record<P, R> send(@NotNull RecordManager recordManager) {
		// Send the record using the given record manager
		recordManager.send(this);

		// Return itself (builder)
		return this;
	}

	/**
	 * Send the {@link Record} using the default {@link RecordManager}
	 *
	 * @return the record instance, for chaining
	 *
	 * @throws RecordManagerNotFoundException if the default record manager is not found
	 * @see RecordManager#send(Record)
	 */
	public Record<P, R> send() {
		// Get the record manager from the RPC
		final RecordManager recordManager = RPC.getInstance().getRecordManager();
		if (recordManager == null) throw new RecordManagerNotFoundException(
				"Unable to send Record; RecordManager is null, meaning the RPC has not been initialized yet...");

		// Send the record using this record manager
		return this.send(recordManager);
	}

	/**
	 * Set the current {@link Record#timeoutConsumer} to a new consumer
	 *
	 * @param timeoutConsumer the new timeout consumer
	 * @return the record instance, for chaining
	 */
	public Record<P, R> onTimeout(Consumer<P> timeoutConsumer) {
		// Set the timeout consumer
		this.timeoutConsumer = timeoutConsumer;

		// Return itself (builder)
		return this;
	}

	/**
	 * Set the current {@link Record#replyConsumer} to a new consumer
	 *
	 * @param replyConsumer the new reply consumer
	 * @return the record instance, for chaining
	 */
	public Record<P, R> onReply(Consumer<R> replyConsumer) {
		// Set the reply consumer
		this.replyConsumer = replyConsumer;

		// Return itself (builder)
		return this;
	}
}
