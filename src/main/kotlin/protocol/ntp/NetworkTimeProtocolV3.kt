package org.bread_experts_group.protocol.ntp

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.BaseWritingIO
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.isoLatin1
import org.bread_experts_group.io.SequentialIOLayout
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log2

data class NetworkTimeProtocolV3(
	val leapStatus: MappedEnumeration<Int, NTPLeapIndicator>,
	val associationMode: MappedEnumeration<Int, NTPAssociationMode>,
	val stratum: Int,
	val poll: Double,
	val precision: Double,
	val rootDelay: BigDecimal,
	val rootDispersion: BigDecimal,
	val referenceID: String,
	val referenceTimestamp: BigDecimal,
	val originTimestamp: BigDecimal,
	val receiveTimestamp: BigDecimal,
	val transmitTimestamp: BigDecimal,
) : NetworkTimeProtocol(3) {
	companion object {
		fun read(
			flags: UByte,
			stratum: UByte,
			poll: UByte,
			precision: UByte,
			rootDelaySeconds: UShort,
			rootDelayFraction: UShort,
			rootDispersionSeconds: UShort,
			rootDispersionFraction: UShort,
			referenceID: String,
			referenceTimestampSeconds: UInt,
			referenceTimestampFraction: UInt,
			originTimestampSeconds: UInt,
			originTimestampFraction: UInt,
			receiveTimestampSeconds: UInt,
			receiveTimestampFraction: UInt,
			transmitTimestampSeconds: UInt,
			transmitTimestampFraction: UInt
		): NetworkTimeProtocolV3 {
			return NetworkTimeProtocolV3(
				NTPLeapIndicator.entries.id(flags.toInt() shr 6),
				NTPAssociationMode.entries.id(flags.toInt() and 0b111),
				stratum.toInt(),
				log2(poll.toDouble()),
				log2(precision.toDouble()),
				BigDecimal.valueOf(rootDelaySeconds.toLong()).add(
					BigDecimal(rootDelayFraction.toInt()).divide(b16, shortPrecision)
				),
				BigDecimal.valueOf(rootDispersionSeconds.toLong()).add(
					BigDecimal(rootDispersionFraction.toInt()).divide(b16, shortPrecision)
				),
				referenceID,
				BigDecimal.valueOf(referenceTimestampSeconds.toLong()).add(
					BigDecimal(referenceTimestampFraction.toLong()).divide(b32, intPrecision)
				),
				BigDecimal.valueOf(originTimestampSeconds.toLong()).add(
					BigDecimal(originTimestampFraction.toLong()).divide(b32, intPrecision)
				),
				BigDecimal.valueOf(receiveTimestampSeconds.toLong()).add(
					BigDecimal(receiveTimestampFraction.toLong()).divide(b32, intPrecision)
				),
				BigDecimal.valueOf(transmitTimestampSeconds.toLong()).add(
					BigDecimal(transmitTimestampFraction.toLong()).divide(b32, intPrecision)
				)
			)
		}

		val layout = SequentialIOLayout(
			::read,
			IOLayout.UNSIGNED_BYTE.passedUpwards(), // flags
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE,
			shortBig,
			shortBig,
			shortBig,
			shortBig,
			IOLayout.CHAR.sequence(4).isoLatin1(),
			intBig,
			intBig,
			intBig,
			intBig,
			intBig,
			intBig,
			intBig,
			intBig
		)
	}

	override fun write(to: BaseWritingIO) {
		var flags = 0b00011000u
		flags = flags or (leapStatus.raw.toUInt() shl 6)
		flags = flags or associationMode.raw.toUInt()
		IOLayout.UNSIGNED_BYTE.write(to, flags.toUByte())
		IOLayout.UNSIGNED_BYTE.write(to, stratum.toUByte())
		IOLayout.UNSIGNED_BYTE.write(to, 0u) // POLL
		IOLayout.UNSIGNED_BYTE.write(to, 0u) // PRECISION
		shortBig.write(to, rootDelay.setScale(0, RoundingMode.DOWN).toShort().toUShort())
		shortBig.write(to, rootDelay.remainder(BigDecimal.ONE).multiply(b16).toShort().toUShort())
		shortBig.write(to, rootDispersion.setScale(0, RoundingMode.DOWN).toShort().toUShort())
		shortBig.write(to, rootDispersion.remainder(BigDecimal.ONE).multiply(b16).toShort().toUShort())
		IOLayout.CHAR.sequence(4).isoLatin1().write(to, referenceID)
		intBig.write(to, referenceTimestamp.setScale(0, RoundingMode.DOWN).toInt().toUInt())
		intBig.write(to, referenceTimestamp.remainder(BigDecimal.ONE).multiply(b32).toInt().toUInt())
		intBig.write(to, originTimestamp.setScale(0, RoundingMode.DOWN).toInt().toUInt())
		intBig.write(to, originTimestamp.remainder(BigDecimal.ONE).multiply(b32).toInt().toUInt())
		intBig.write(to, receiveTimestamp.setScale(0, RoundingMode.DOWN).toInt().toUInt())
		intBig.write(to, receiveTimestamp.remainder(BigDecimal.ONE).multiply(b32).toInt().toUInt())
		intBig.write(to, transmitTimestamp.setScale(0, RoundingMode.DOWN).toInt().toUInt())
		intBig.write(to, transmitTimestamp.remainder(BigDecimal.ONE).multiply(b32).toInt().toUInt())
		to.flush()
	}

	override fun toString(): String = "Network Time Protocol version 3" +
			"\n\tLeap Indicator: $leapStatus" +
			"\n\tAssociation Mode: $associationMode" +
			"\n\tStratum: $stratum" +
			"\n\tPoll: $poll s" +
			"\n\tPrecision: $precision s" +
			"\n\tRoot Delay: $rootDelay s" +
			"\n\tRoot Dispersion: $rootDispersion s" +
			"\n\tReference: \"$referenceID\"" +
			"\n\tReference / Origin: $referenceTimestamp / $originTimestamp s" +
			"\n\tRx / Tx: $receiveTimestamp / $transmitTimestamp s"
}