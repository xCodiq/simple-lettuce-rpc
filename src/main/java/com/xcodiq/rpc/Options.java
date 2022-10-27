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

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
@Getter
public final class Options {

	private final String redisURI, topic, recordPrefix;

	public Options(String redisURI, String topic, String recordPrefix) {
		this.redisURI = redisURI;
		this.topic = topic;
		this.recordPrefix = recordPrefix;
	}

	@Contract("_, _, _ -> new")
	public static @NotNull Options of(String redisURI, String topic, String recordPrefix) {
		return new Options(redisURI, topic, recordPrefix);
	}
}
