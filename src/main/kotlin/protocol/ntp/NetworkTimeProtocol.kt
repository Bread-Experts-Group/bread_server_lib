package org.bread_experts_group.protocol.ntp

import org.bread_experts_group.io.BaseWritingIO
import org.bread_experts_group.io.IOEndian
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SelectiveIOLayout
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

abstract class NetworkTimeProtocol(
	val version: Int
) {
	companion object {
		@JvmStatic
		protected val shortBig = IOLayout.UNSIGNED_SHORT.order(IOEndian.BIG)

		@JvmStatic
		protected val intBig = IOLayout.UNSIGNED_INT.order(IOEndian.BIG)

		@JvmStatic
		protected val b32: BigDecimal = BigDecimal.valueOf(UInt.MAX_VALUE.toLong())

		@JvmStatic
		protected val b16: BigDecimal = BigDecimal.valueOf(UShort.MAX_VALUE.toLong())

		@JvmStatic
		protected val shortPrecision = MathContext(6, RoundingMode.HALF_EVEN)

		@JvmStatic
		val intPrecision = MathContext(9, RoundingMode.HALF_EVEN)

		fun selectV3(n: UByte) = n and 0b00111000u == 0b00011000u.toUByte()
		fun selectV4(n: UByte) = n and 0b00111000u == 0b00100000u.toUByte()
		val layout = SelectiveIOLayout<NetworkTimeProtocol>(
			mapOf(
				::selectV3 to NetworkTimeProtocolV3.layout,
				::selectV4 to NetworkTimeProtocolV4.layout
			),
			{ w, ntp -> ntp.write(w) },
			IOLayout.UNSIGNED_BYTE.passedUpwards()
		)
	}

	abstract fun write(to: BaseWritingIO)
}