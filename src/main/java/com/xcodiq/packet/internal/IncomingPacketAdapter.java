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

package com.xcodiq.packet.internal;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public final class IncomingPacketAdapter implements JsonSerializer<IncomingPacket>, JsonDeserializer<IncomingPacket> {

	@Override
	public IncomingPacket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject jsonObject = json.getAsJsonObject();
		try {
			return IncomingPacket.of(Class.forName(jsonObject.get("recordClass").getAsString()),
					jsonObject.get("serializedPacket").getAsString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public JsonElement serialize(IncomingPacket incomingPacket, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("recordClass", incomingPacket.getRecordClass().getName());
		jsonObject.addProperty("serializedPacket", incomingPacket.getSerializedPacket());

		return jsonObject;
	}
}
