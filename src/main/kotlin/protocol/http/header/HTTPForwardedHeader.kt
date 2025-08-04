package org.bread_experts_group.protocol.http.header

import java.net.URI

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
class HTTPForwardedHeader {
	val forwardees = mutableListOf<HTTPForwardee>()

	override fun toString(): String = forwardees.joinToString(", ") { forwardee ->
		listOfNotNull(
			if (forwardee.by != null) "by=${forwardee.by}" else "",
			if (forwardee.`for` != null) "for=${forwardee.`for`}" else "",
			if (forwardee.host != null) "host=\"${forwardee.host.toASCIIString()}\"" else "",
			if (forwardee.proto != null) "proto=${forwardee.proto}" else ""
		).joinToString(";")
	}

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