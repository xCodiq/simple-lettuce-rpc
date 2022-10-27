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

package com.xcodiq.rpc;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xcodiq.lettuce.LettuceManager;
import com.xcodiq.packet.internal.IncomingPacket;
import com.xcodiq.packet.internal.IncomingPacketAdapter;
import com.xcodiq.record.RecordManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
@Getter
@Setter
public final class RPC<T> {

	public static final Logger LOG = LoggerFactory.getLogger(RPC.class);
	private static final String THREAD_NAME = "RPC-THREAD-%d";
	private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
			.registerTypeHierarchyAdapter(IncomingPacket.class, new IncomingPacketAdapter())
			.excludeFieldsWithModifiers(128)
			.serializeNulls()
			.disableHtmlEscaping()
			.enableComplexMapKeySerialization()
			.setPrettyPrinting();
	private static RPC<?> INSTANCE;
	private static Gson GSON = Converters.registerAll(GSON_BUILDER).create();

	private final T source;
	private final Class<T> sourceClass;

	private final Options options;

	/* managers */
	private final LettuceManager lettuceManager;
	private final RecordManager recordManager;

	@Contract(pure = true)
	public RPC(@NotNull T source, @NotNull Options options) {
		INSTANCE = this;
		this.options = options;

		// Initialize the source and the source class
		this.source = source;
		this.sourceClass = (Class<T>) source.getClass();

		// Initialize a new lettuce manager for this rpc instance
		this.lettuceManager = new LettuceManager(options.getRedisURI());

		// Initialize a new record manager for this rpc instance
		this.recordManager = new RecordManager(this);

		LOG.info("Successfully created a new RPC instance for '{}'", sourceClass.getSimpleName());
	}

	public static RPC<?> getInstance() {
		if (INSTANCE == null) throw new IllegalStateException("RPC instance has not been initialized yet!");
		return INSTANCE;
	}

	@Contract(" -> new")
	public static @NotNull ScheduledExecutorService getThreadPoolExecutor() {
		return Executors.newScheduledThreadPool(0, r -> new Thread(r, String.format(THREAD_NAME, THREAD_COUNTER.getAndIncrement())));
	}

	public static Gson getGson() {
		return GSON;
	}

	public static void rebuildGson() {
		GSON = GSON_BUILDER.create();
	}

	public static void registerTypeAdapter(Type type, Object typeAdapter) {
		GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
		rebuildGson();
	}
}
