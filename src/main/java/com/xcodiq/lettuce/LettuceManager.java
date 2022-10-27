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

package com.xcodiq.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The lettuce manager is responsible for managing the redis connection
 *
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
@Getter
public final class LettuceManager {

	private static final Logger LOG = LoggerFactory.getLogger(LettuceManager.class);

	private final RedisPubSubAsyncCommands<String, String> asyncPublisher;
	private final RedisClient redisClient;

	/**
	 * Constructs a new lettuce manager given a {@link RedisClient}
	 *
	 * @param redisClient the redis client to use
	 * @apiNote Use {@link LettuceManager#LettuceManager(String)} if you want to use a new redis client as standalone
	 */
	public LettuceManager(final @NotNull RedisClient redisClient) {
		// Set the redis client connection
		this.redisClient = redisClient;

		// Create a new async pubsub connection
		final StatefulRedisPubSubConnection<String, String> connection = this.redisClient.connectPubSub();
		this.asyncPublisher = connection.async();

		LOG.debug("Successfully connected to the redis database.");
	}

	/**
	 * Constructs a new lettuce manager given a redis uri
	 *
	 * @param redisURI the redis uri to use
	 * @see LettuceManager#LettuceManager(RedisClient)
	 */
	public LettuceManager(String redisURI) {
		// Create a new redis client connection
		this(RedisClient.create(redisURI));
	}

	/**
	 * Publish a message to a specific route on the redis
	 *
	 * @param route   the route to publish to
	 * @param message the message to publish
	 */
	public void publish(String route, String message) {
		this.asyncPublisher.publish(route, message);
	}
}
