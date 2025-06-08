package org.bread_experts_group.ntp

import org.bread_experts_group.stream.Writable
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal

class NTPMessage(
	val leapIndication: LeapIndication,
	val version: Int,
	val associationMode: AssociationMode,
	val stratum: Int,
	val messageInterval: Int,
	val precision: Int,
	val rootDelay: BigDecimal,
	val rootDispersion: BigDecimal,
	val referenceID: Int,
	val referenceTimestamp: BigDecimal,
	val originTimestamp: BigDecimal,
	val receiveTimestamp: BigDecimal,
	val transmitTimestamp: BigDecimal
) : Writable {
	enum class LeapIndication(val code: Int) {
		NO_WARNING(0),
		LAST_MINUTE_61_S(1),
		LAST_MINUTE_59_S(2),
		CLOCK_NOT_SYNCHRONIZED(3)
	}

	enum class AssociationMode(val code: Int) {
		RESERVED(0),
		SYMMETRIC_ACTIVE(1),
		SYMMETRIC_PASSIVE(2),
		CLIENT(3),
		SERVER(4),
		BROADCAST(5),
		CONTROL(6),
		PRIVATE(7)
	}

	override fun write(stream: OutputStream) {
		TODO("Network Time Protocol W")
	}

	companion object {
		fun read(stream: InputStream): NTPMessage {
			TODO("Network Time Protocol R")
		}
	}
}