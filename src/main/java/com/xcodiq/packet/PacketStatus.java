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

/**
 * @author xCodiq - Elmar (Cody) Lynn
 * @wesbite https://xcodiq.com
 * @since 1.0
 */
public enum PacketStatus {

	/**
	 * 200 OK, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>.
	 */
	OK(200, "OK"),

	/**
	 * 201 Created, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1
	 * documentation</a>.
	 */
	CREATED(201, "Created"),

	/**
	 * 202 Accepted, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1
	 * documentation</a>.
	 */
	ACCEPTED(202, "Accepted"),

	/**
	 * 204 No Content, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1
	 * documentation</a>.
	 */
	NO_CONTENT(204, "No Content"),

	/**
	 * 205 Reset Content, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.6">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	RESET_CONTENT(205, "Reset Content"),

	/**
	 * 206 Reset Content, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	PARTIAL_CONTENT(206, "Partial Content"),

	/**
	 * 301 Moved Permanently, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1
	 * documentation</a>.
	 */
	MOVED_PERMANENTLY(301, "Moved Permanently"),

	/**
	 * 302 Found, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.3">HTTP/1.1 documentation</a>.
	 *
	 * @since 2.0
	 */
	FOUND(302, "Found"),

	/**
	 * 303 See Other, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1
	 * documentation</a>.
	 */
	SEE_OTHER(303, "See Other"),

	/**
	 * 304 Not Modified, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1
	 * documentation</a>.
	 */
	NOT_MODIFIED(304, "Not Modified"),

	/**
	 * 305 Use Proxy, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.6">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	USE_PROXY(305, "Use Proxy"),

	/**
	 * 307 Temporary Redirect, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1
	 * documentation</a>.
	 */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),

	/**
	 * 400 Bad Request, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1
	 * documentation</a>.
	 */
	BAD_REQUEST(400, "Bad Request"),

	/**
	 * 401 Unauthorized, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1
	 * documentation</a>.
	 */
	UNAUTHORIZED(401, "Unauthorized"),

	/**
	 * 402 Payment Required, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.3">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	PAYMENT_REQUIRED(402, "Payment Required"),

	/**
	 * 403 Forbidden, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1
	 * documentation</a>.
	 */
	FORBIDDEN(403, "Forbidden"),

	/**
	 * 404 Not Found, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1
	 * documentation</a>.
	 */
	NOT_FOUND(404, "Not Found"),

	/**
	 * 405 Method Not Allowed, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

	/**
	 * 406 Not Acceptable, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1
	 * documentation</a>.
	 */
	NOT_ACCEPTABLE(406, "Not Acceptable"),

	/**
	 * 407 Proxy Authentication Required, see
	 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.8">HTTP/1.1 documentation</a>.
	 *
	 * @since 2.0
	 */
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),

	/**
	 * 408 Request Timeout, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.9">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	REQUEST_TIMEOUT(408, "Request Timeout"),

	/**
	 * 409 Conflict, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1
	 * documentation</a>.
	 */
	CONFLICT(409, "Conflict"),

	/**
	 * 410 Gone, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>.
	 */
	GONE(410, "Gone"),

	/**
	 * 411 Length Required, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.12">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	LENGTH_REQUIRED(411, "Length Required"),

	/**
	 * 412 Precondition Failed, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1
	 * documentation</a>.
	 */
	PRECONDITION_FAILED(412, "Precondition Failed"),

	/**
	 * 413 Request Entity Too Large, see
	 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.14">HTTP/1.1 documentation</a>.
	 *
	 * @since 2.0
	 */
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),

	/**
	 * 414 Request-URI Too Long, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.15">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),

	/**
	 * 415 Unsupported Media Type, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1
	 * documentation</a>.
	 */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

	/**
	 * 416 Requested Range Not Satisfiable, see
	 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17">HTTP/1.1 documentation</a>.
	 *
	 * @since 2.0
	 */
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),

	/**
	 * 417 Expectation Failed, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.18">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	EXPECTATION_FAILED(417, "Expectation Failed"),

	/**
	 * 428 Precondition required, see <a href="https://tools.ietf.org/html/rfc6585#section-3">RFC 6585: Additional HTTP
	 * Status Codes</a>.
	 *
	 * @since 2.1
	 */
	PRECONDITION_REQUIRED(428, "Precondition Required"),

	/**
	 * 429 Too Many Requests, see <a href="https://tools.ietf.org/html/rfc6585#section-4">RFC 6585: Additional HTTP Status
	 * Codes</a>.
	 *
	 * @since 2.1
	 */
	TOO_MANY_REQUESTS(429, "Too Many Requests"),

	/**
	 * 431 Request Header Fields Too Large, see <a href="https://tools.ietf.org/html/rfc6585#section-5">RFC 6585: Additional
	 * HTTP Status Codes</a>.
	 *
	 * @since 2.1
	 */
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),

	/**
	 * 500 Internal Server Error, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1
	 * documentation</a>.
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

	/**
	 * 501 Not Implemented, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	NOT_IMPLEMENTED(501, "Not Implemented"),

	/**
	 * 502 Bad Gateway, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	BAD_GATEWAY(502, "Bad Gateway"),

	/**
	 * 503 Service Unavailable, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1
	 * documentation</a>.
	 */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),

	/**
	 * 504 Gateway Timeout, see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1
	 * documentation</a>.
	 *
	 * @since 2.0
	 */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),

	/**
	 * 505 HTTP Version Not Supported, see
	 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.6">HTTP/1.1 documentation</a>.
	 *
	 * @since 2.0
	 */
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

	/**
	 * 511 Network Authentication Required, see <a href="https://tools.ietf.org/html/rfc6585#section-6">RFC 6585: Additional
	 * HTTP Status Codes</a>.
	 *
	 * @since 2.1
	 */
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

	/**
	 * The Status code.
	 */
	final int statusCode;

	/**
	 * The Message.
	 */
	final String message;

	PacketStatus(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	/**
	 * Gets message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}