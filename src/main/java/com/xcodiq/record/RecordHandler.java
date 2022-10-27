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

import com.xcodiq.packet.RedisPacket;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a handler to handle a redis packet, and create a reply packet.
 *
 * @param <P> the generic of the sent-packet type
 * @param <R> the generic of the reply-packet type
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public abstract class RecordHandler<P extends RedisPacket, R extends RedisPacket> {

	/**
	 * Handle the redis-packet
	 *
	 * @param packet the packet to handle
	 * @return a newly created reply-packet
	 */
	public abstract R handlePacket(P packet);

	/**
	 * Create the reply-packet
	 *
	 * @param redisPacket the packet to create the reply-packet from
	 * @return {@code null} if the reply-packet is not needed, otherwise the reply-packet
	 */
	public @Nullable R getReplyPacket(RedisPacket redisPacket) {
		// Use the abstract handlePacket method to get a reply-packet
		final R replyPacket = this.handlePacket((P) redisPacket);
		if (replyPacket == null) return null;

		// Adjust the recordId and return the reply-packet
		replyPacket.setRecordId(redisPacket.getRecordId());
		replyPacket.setReplyPacket(true);
		return replyPacket;
	}
}
