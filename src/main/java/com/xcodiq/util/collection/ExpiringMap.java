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

package com.xcodiq.util.collection;

import com.xcodiq.rpc.RPC;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public class ExpiringMap<K, V> extends HashMap<K, V> {

	private final HashMap<K, ScheduledFuture<?>> refreshFutures = new HashMap<>();
	private final HashMap<K, Instant> addedAt = new HashMap<>();

	private BiFunction<K, V, Boolean> purgeFunction;

	private double delay = 5;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	public ExpiringMap() {
	}

	public ExpiringMap(double delay, TimeUnit timeUnit) {
		this.delay = delay;
		this.timeUnit = timeUnit;
	}

	public ExpiringMap(BiConsumer<K, V> purgeFunction) {
		this.purgeFunction = (k, v) -> {
			purgeFunction.accept(k, v);
			return true;
		};
	}

	public ExpiringMap(BiConsumer<K, V> purgeFunction, double delay, TimeUnit timeUnit) {
		this.purgeFunction = (k, v) -> {
			purgeFunction.accept(k, v);
			return true;
		};

		this.delay = delay;
		this.timeUnit = timeUnit;
	}

	public ExpiringMap(BiFunction<K, V, Boolean> purgeFunction) {
		this.purgeFunction = purgeFunction;
	}

	public ExpiringMap<K, V> setPurgeFunction(BiFunction<K, V, Boolean> purgeFunction) {
		this.purgeFunction = purgeFunction;
		return this;
	}

	@Override
	public V put(K key, V value) {
		this.queue(key, value, this.delay, this.timeUnit);
		return super.put(key, value);
	}

	public V put(K key, V value, int delay, TimeUnit timeUnit) {
		this.queue(key, value, delay, timeUnit);
		return super.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V previousKey = super.putIfAbsent(key, value);
		if (previousKey == null) this.queue(key, value, this.delay, this.timeUnit);
		return previousKey;
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		V computedValue = super.compute(key, remappingFunction);
		if (!this.isQueued(key)) this.queue(key, computedValue, delay, timeUnit);
		return computedValue;
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		V computedValue = super.computeIfAbsent(key, mappingFunction);
		if (!this.isQueued(key)) this.queue(key, computedValue, delay, timeUnit);
		return computedValue;
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		V computedValue = super.computeIfPresent(key, remappingFunction);
		if (!this.isQueued(key)) this.queue(key, computedValue, delay, timeUnit);
		return computedValue;
	}

	@Override
	public boolean remove(Object key, Object value) {
		//Remove from addedAt
		this.addedAt.remove(key);

		ScheduledFuture<?> scheduledFuture = refreshFutures.get(key);
		if (scheduledFuture != null) scheduledFuture.cancel(true);
		this.refreshFutures.remove(key);

		return super.remove(key, value);
	}

	@Override
	public V remove(Object key) {
		// Remove from addedAt
		this.addedAt.remove(key);

		ScheduledFuture<?> scheduledFuture = this.getScheduledFuture((K) key);
		if (scheduledFuture != null) scheduledFuture.cancel(true);
		this.refreshFutures.remove(key);

		return super.remove(key);
	}

	private void queue(K key, V value, double delay, TimeUnit timeUnit) {
		this.addedAt.put(key, Instant.now());

		// Make sure to remove previous values
		if (this.refreshFutures.containsKey(key)) {
			this.refreshFutures.get(key).cancel(true);
			this.refreshFutures.remove(key);
		}

		ScheduledFuture<?> scheduledFuture = RPC.getThreadPoolExecutor().schedule(() -> {
			this.remove(key);
			this.purgeFunction.apply(key, value);
		}, (long) delay, timeUnit);

		this.refreshFutures.put(key, scheduledFuture);
	}

	private boolean isQueued(K key) {
		return this.addedAt.get(key) != null;
	}

	public Instant isAddedAt(K key) {
		return this.addedAt.computeIfAbsent(key, k -> Instant.now());
	}

	public Instant expiresAt(K key) {
		ScheduledFuture<?> scheduledFuture = this.getScheduledFuture(key);
		return Instant.now().plusMillis(scheduledFuture.getDelay(TimeUnit.MILLISECONDS));
	}

	public long getTimeLeft(K key, TimeUnit timeUnit) {
		final Instant expiresAt = this.expiresAt(key);
		return timeUnit.convert(expiresAt.minusMillis(System.currentTimeMillis()).toEpochMilli(), TimeUnit.MILLISECONDS);
	}

	public double getDelay() {
		return delay;
	}

	public ExpiringMap<K, V> setDelay(int delay) {
		this.delay = delay;
		return this;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public ExpiringMap<K, V> setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public ScheduledFuture<?> getScheduledFuture(K key) {
		return refreshFutures.get(key);
	}
}
