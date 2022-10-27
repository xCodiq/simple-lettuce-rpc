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

package com.xcodiq.packet.serialization;

import com.xcodiq.packet.RedisPacket;
import com.xcodiq.rpc.RPC;
import org.jetbrains.annotations.Nullable;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public final class PacketSerializer {

	public static <T extends RedisPacket> String serialize(T redisPacket) {
		return RPC.getGson().toJson(redisPacket);
	}

	public static <T extends RedisPacket> @Nullable T deserialize(String serializedRedisPacket) {
		try {
			final RedisPacket redisPacket = RPC.getGson().fromJson(serializedRedisPacket, RedisPacket.class);
			Class<T> originalClassType = (Class<T>) Class.forName(redisPacket.getPacketClass());

			return RPC.getGson().fromJson(serializedRedisPacket, originalClassType);
		} catch (ClassNotFoundException ignored) {
			return null;
		}
	}
}
