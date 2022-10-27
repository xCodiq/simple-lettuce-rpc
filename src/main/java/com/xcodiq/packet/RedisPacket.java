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

package com.xcodiq.packet;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
@Getter
@Setter
public class RedisPacket implements Packet {

	private final String packetClass;
	private final UUID packetId;

	private String channel;
	private UUID recordId;
	private Instant createdAt;

	private PacketStatus packetStatus;
	private boolean isReplyPacket = false;

	public RedisPacket(final PacketStatus packetStatus) {
		this.packetClass = this.getClass().getName();
		this.packetId = UUID.randomUUID();
		this.createdAt = Instant.now();
		this.packetStatus = packetStatus;
	}

	public RedisPacket() {
		this(PacketStatus.OK);
	}

	@Override
	public String getPacketClass() {
		return this.packetClass;
	}

	@Override
	public PacketStatus getPacketStatus() {
		return this.packetStatus;
	}
}