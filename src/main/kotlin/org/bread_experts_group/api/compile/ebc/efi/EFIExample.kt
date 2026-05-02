package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.naturalSize
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write16
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write32
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write8
import org.bread_experts_group.api.compile.ebc.efi.EFIExample.plus

object EFIExample {
	@JvmStatic
	fun Long.toCharArray(radix: Int): CharArray {
		var str = charArrayOf()
		var r = this
		do {
			val newStr = CharArray(str.size + 1)
			var i = 0
			while (i < str.size) newStr[i + 1] = str[i++]
			val digit = (r % radix).toInt()
			val base = if (digit < 10) 0x30 else 0x37
			newStr[0] = (base + digit).toChar()
			str = newStr
			r /= radix
		} while (r > 0)
		return str
	}

//	@JvmStatic
//	fun CharArray?.toUTF16LE(): ByteArray {
//		if (this == null) return byteArrayOf()
//		val str = ByteArray((this.size * 2) + 2)
//		var i = 0
//		while (i < this.size) {
//			val code = this[i].code
//			val offset = i * 2
//			str[offset] = (code and 0xFF).toByte()
//			str[offset + 1] = ((code ushr 8) and 0xFF).toByte()
//			i++
//		}
//		str[str.size - 1] = 0
//		str[str.size - 2] = 0
//		return str
//	}

	@JvmStatic
	operator fun CharArray?.plus(other: CharArray?): CharArray {
		if (this == null || other == null) return charArrayOf()
		val newStr = CharArray(this.size + other.size)
		var i = 0
		var d = 0
		while (d < this.size) newStr[i++] = this[d++]
		d = 0
		while (d < other.size) newStr[i++] = other[d++]
		return newStr
	}

	@JvmStatic
	fun error(conOut: Address?, label: Long, efiStatus: Int) {
		if (conOut == null) while (true) {
		}
		var error = charArrayOf('E', 'r', 'r', 'o', 'r', '.')
		error += label.toCharArray(16) + charArrayOf('.')
		error += efiStatus.toLong().toCharArray(16)
		EFISimpleTextOutputProtocol.outputString(
			conOut,
			error.address + 8
		)
		while (true) {
		}
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	@JvmStatic
	fun fillGUID(
		buffer: Address?,
		i: UInt, s0: UShort, s1: UShort,
		b0: UByte, b1: UByte, b2: UByte, b3: UByte,
		b4: UByte, b5: UByte, b6: UByte, b7: UByte
	) {
		if (buffer == null) return
		write32(buffer, i.toInt())
		write16(buffer + 4, s0.toShort())
		write16(buffer + 6, s1.toShort())
		write8(buffer + 8, b0.toByte())
		write8(buffer + 9, b1.toByte())
		write8(buffer + 10, b2.toByte())
		write8(buffer + 11, b3.toByte())
		write8(buffer + 12, b4.toByte())
		write8(buffer + 13, b5.toByte())
		write8(buffer + 14, b6.toByte())
		write8(buffer + 15, b7.toByte())
	}

	@JvmStatic
	@OptIn(ExperimentalUnsignedTypes::class)
	fun efiMain(imageHandle: Address?, systemTable: Address?): Long {
		if (imageHandle == null || systemTable == null) return 0x1000
		val bootServices = EFISystemTable.bootServices(systemTable)
		val runtimeServices = EFISystemTable.runtimeServices(systemTable)
		val conOut = EFISystemTable.conOut(systemTable)
		val nl = charArrayOf('\r', '\n')
		val nul = charArrayOf('\u0000')
		fun println(l: Long, radix: Int = 10) {
			EFISimpleTextOutputProtocol.outputString(
				conOut,
				(l.toCharArray(radix) + nl + nul).address + 8
			)
		}

		fun println(i: Int, radix: Int = 10) = println(i.toLong(), radix)

		if (naturalSize() != 8L) error(conOut, 0, 0)
		var i = 0
		while (true) {
			EFISimpleTextOutputProtocol.setCursorPosition(conOut, 0, 0)
			println(i++)
		}
	}
}