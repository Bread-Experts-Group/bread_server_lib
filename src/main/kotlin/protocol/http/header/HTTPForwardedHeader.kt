package org.bread_experts_group.protocol.http.header

import java.net.URI

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
class HTTPForwardedHeader {
	val forwardees = mutableListOf<HTTPForwardee>()

	override fun toString(): String = forwardees.map { forwardee ->
		val local = mutableListOf<StringBuilder>()
		if (forwardee.by != null) {
			val builder = StringBuilder()
			builder.append("by=")
			builder.append(forwardee.by)
			local.add(builder)
		}
		if (forwardee.`for` != null) {
			val builder = StringBuilder()
			builder.append("for=")
			builder.append(forwardee.`for`)
			local.add(builder)
		}
		if (forwardee.host != null) {
			val builder = StringBuilder()
			builder.append("host=")
			builder.append('"')
			builder.append(forwardee.host.toASCIIString())
			builder.append('"')
			local.add(builder)
		}
		if (forwardee.proto != null) {
			val builder = StringBuilder()
			builder.append("proto=")
			builder.append(forwardee.proto)
			local.add(builder)
		}
		local.joinToString(";") { it }
	}.joinToString(", ") { it }

	companion object {
		fun parse(value: String): HTTPForwardedHeader {
			val forwarded = HTTPForwardedHeader()
			val split = value.split(',')
			split.forEach {
				val local = it.trim().split(';').associate { pair ->
					val (key, value) = pair.split('=', limit = 2)
					key.lowercase() to value
				}
				forwarded.forwardees.add(
					HTTPForwardee(
						local["by"]?.let { by -> HTTPForwardeeIdentifier.parse(by) },
						local["for"]?.let { `for` -> HTTPForwardeeIdentifier.parse(`for`) },
						local["host"]?.let { host -> URI.create(host) },
						local["proto"]
					)
				)
			}
			return forwarded
		}
	}
}