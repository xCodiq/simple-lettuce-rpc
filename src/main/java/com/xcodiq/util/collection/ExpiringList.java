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
import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public class ExpiringList<E> extends HashSet<E> {

	private final HashMap<E, ScheduledFuture<?>> refreshFutures = new HashMap<>();
	private final HashMap<E, Instant> addedAt = new HashMap<>();
	private Function<E, Boolean> purgeFunction;

	private double delay = 5;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	public ExpiringList() {
	}

	public ExpiringList(double delay, TimeUnit timeUnit) {
		this.delay = delay;
		this.timeUnit = timeUnit;
	}

	public ExpiringList(Consumer<E> purgeFunction) {
		this.purgeFunction = (entry) -> {
			purgeFunction.accept(entry);
			return true;
		};
	}

	public ExpiringList(Function<E, Boolean> purgeFunction) {
		this.purgeFunction = purgeFunction;
	}

	public ExpiringList<E> setPurgeFunction(Function<E, Boolean> purgeFunction) {
		this.purgeFunction = purgeFunction;
		return this;
	}

	@Override
	public boolean add(E entry) {
		return add(entry, this.delay, this.timeUnit);
	}

	public boolean add(E entry, double delay, TimeUnit timeUnit) {
		this.addedAt.put(entry, Instant.now());

		this.queue(entry, delay, timeUnit);

		return super.add(entry);
	}

	public void update(E entry) {
		this.queue(entry);
	}

	public void addOrUpdate(E entry) {
		if (this.contains(entry)) this.update(entry);
		else this.add(entry);
	}

	@Override
	public boolean remove(Object object) {
		//Remove from addedAt
		this.addedAt.remove(object);

		ScheduledFuture<?> scheduledFuture = this.getScheduledFuture((E) object);
		if (scheduledFuture != null) scheduledFuture.cancel(true);
		this.refreshFutures.remove(object);

		return super.remove(object);
	}

	private void queue(E entry) {
		queue(entry, this.delay, this.timeUnit);
	}

	private void queue(E entry, double delay, TimeUnit timeUnit) {
		// Make sure to remove previous values
		if (refreshFutures.containsKey(entry)) {
			refreshFutures.get(entry).cancel(true);
			refreshFutures.remove(entry);
		}

		ScheduledFuture<?> scheduledFuture = RPC.getThreadPoolExecutor().schedule(() -> {
			this.remove(entry);
			purgeFunction.apply(entry);
		}, (long) delay, timeUnit);

		refreshFutures.put(entry, scheduledFuture);
	}

	public Instant isAddedAt(E entry) {
		return this.addedAt.computeIfAbsent(entry, e -> Instant.now());
	}

	public Instant expiresAt(E entry) {
		ScheduledFuture<?> scheduledFuture = this.getScheduledFuture(entry);
		return Instant.now().plusMillis(scheduledFuture.getDelay(TimeUnit.MILLISECONDS));
	}

	public long getTimeLeft(E key, TimeUnit timeUnit) {
		final Instant expiresAt = this.expiresAt(key);
		return timeUnit.convert(expiresAt.minusMillis(System.currentTimeMillis()).toEpochMilli(), TimeUnit.MILLISECONDS);
	}

	public double getDelay() {
		return delay;
	}

	public ExpiringList<E> setDelay(double delay) {
		this.delay = delay;
		return this;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public ExpiringList<E> setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public ScheduledFuture<?> getScheduledFuture(E entry) {
		return refreshFutures.get(entry);
	}

}
