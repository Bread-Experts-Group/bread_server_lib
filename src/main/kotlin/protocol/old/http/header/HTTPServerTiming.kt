package org.bread_experts_group.protocol.old.http.header

import kotlin.time.Duration

/**
 * @author Miko Elbrecht
 * @since 2.50.0
 */
data class HTTPServerTiming(
	val tag: String,
	val desc: String,
	val time: Duration
)
