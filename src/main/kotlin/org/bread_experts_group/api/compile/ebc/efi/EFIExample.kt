package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.allocateN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.naturalSize
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write16
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write32
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write64
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

	@JvmStatic
	fun CharArray?.toUTF16LE(): ByteArray {
		if (this == null) return byteArrayOf()
		val str = ByteArray((this.size * 2) + 2)
		var i = 0
		while (i < this.size) {
			val code = this[i].code
			val offset = i * 2
			str[offset] = (code and 0xFF).toByte()
			str[offset + 1] = ((code ushr 8) and 0xFF).toByte()
			i++
		}
		str[str.size - 1] = 0
		str[str.size - 2] = 0
		return str
	}

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
			error.toUTF16LE().address + 8
		)
		while (true) {
		}
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	@JvmStatic
	fun fillGUID(buffer: Address?, i: UInt, s0: UShort, s1: UShort, b: UByteArray?) {
		if (buffer == null || b == null) return
		write32(buffer, i.toInt())
		write16(buffer + 4, s0.toShort())
		write16(buffer + 6, s1.toShort())
		var i = 0
		while (i < b.size) {
			write8(buffer + 8 + i.toLong(), b[i].toByte())
			i++
		}
	}

	@JvmStatic
	@OptIn(ExperimentalUnsignedTypes::class)
	fun efiMain(imageHandle: Address?, systemTable: Address?): Long {
		if (imageHandle == null || systemTable == null) return 0x1000
		val conOut = EFISystemTable.conOut(systemTable)
		if (naturalSize() != 8L) error(conOut, 0, 0)
		EFISimpleTextOutputProtocol.outputString(
			conOut,
			charArrayOf(
				'B', 'r', 'e', 'a', 'd', ' ', 'E', 'x', 'p', 'e', 'r', 't', 's', ' ', 'G', 'r', 'o', 'u', 'p',
				'\r', '\n', 'U', 'E', 'F', 'I', ' ', 'E', 'B', 'C', ' ', 'L', 'o', 'a', 'd', 'e', 'r',
				'\r', '\n'
			).address + 8
		)
//		val novonordisk = ByteArray(16)
		// 0x5B1B31A1,0x9562,0x11d2,\
		//    {0x8E,0x3F,0x00,0xA0,0xC9,0x69,0x72,0x3B}
//		fillGUID(
//			novonordisk.address + 8,
//			0x5B1B31A1u, 0x9562u, 0x11D2u,
//			ubyteArrayOf(0x8Eu, 0x3Fu, 0x00u, 0xA0u, 0xC9u, 0x69u, 0x72u, 0x3Bu)
//		)

		val protocolNames = arrayOf(
			charArrayOf('L', 'o', 'a', 'd', 'e', 'd', 'I', 'm', 'a', 'g', 'e'),
			charArrayOf(
				'L', 'o', 'a', 'd', 'e', 'd',
				'I', 'm', 'a', 'g', 'e',
				'D', 'e', 'v', 'i', 'c', 'e',
				'P', 'a', 't', 'h'
			),
			charArrayOf('D', 'e', 'v', 'i', 'c', 'e', 'P', 'a', 't', 'h'),
			charArrayOf('D', 'e', 'v', 'i', 'c', 'e', 'P', 'a', 't', 'h', 'U', 't', 'i', 'l', 'i', 't', 'i', 'e', 's'),
			charArrayOf('D', 'r', 'i', 'v', 'e', 'r', 'B', 'i', 'n', 'd', 'i', 'n', 'g'),
			charArrayOf(
				'P', 'l', 'a', 't', 'f', 'o', 'r', 'm',
				'D', 'r', 'i', 'v', 'e', 'r',
				'O', 'v', 'e', 'r', 'r', 'i', 'd', 'e'
			),
			charArrayOf(
				'B', 'u', 's',
				'S', 'p', 'e', 'c', 'i', 'f', 'i', 'c',
				'D', 'r', 'i', 'v', 'e', 'r',
				'O', 'v', 'e', 'r', 'r', 'i', 'd', 'e'
			),
			charArrayOf('D', 'r', 'i', 'v', 'e', 'r', 'D', 'i', 'a', 'g', 'n', 'o', 's', 't', 'i', 'c', 's'),
			charArrayOf('C', 'o', 'm', 'p', 'o', 'n', 'e', 'n', 't', 'N', 'a', 'm', 'e'),
			charArrayOf(
				'P', 'l', 'a', 't', 'f', 'o', 'r', 'm',
				'T', 'o',
				'D', 'r', 'i', 'v', 'e', 'r',
				'C', 'o', 'n', 'f', 'i', 'g', 'u', 'r', 'a', 't', 'i', 'o', 'n'
			),
			charArrayOf(
				'D', 'r', 'i', 'v', 'e', 'r',
				'S', 'u', 'p', 'p', 'o', 'r', 't', 'e', 'd',
				'E', 'F', 'I',
				'V', 'e', 'r', 's', 'i', 'o', 'n'
			),
			charArrayOf(
				'D', 'r', 'i', 'v', 'e', 'r',
				'F', 'a', 'm', 'i', 'l', 'y',
				'O', 'v', 'e', 'r', 'r', 'i', 'd', 'e'
			),
//			charArrayOf('D', 'r', 'i', 'v', 'e', 'r', 'H', 'e', 'a', 'l', 't', 'h'),
//			charArrayOf('A', 'd', 'a', 'p', 't', 'e', 'r', 'I', 'n', 'f', 'o', 'r', 'm', 'a', 't', 'i', 'o', 'n'),
//			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'T', 'e', 'x', 't', 'I', 'n', 'p', 'u', 't', 'E', 'x'),
//			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'T', 'e', 'x', 't', 'I', 'n', 'p', 'u', 't'),
//			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'T', 'e', 'x', 't', 'O', 'u', 't', 'p', 'u', 't'),
//			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'P', 'o', 'i', 'n', 't', 'e', 'r'),
//			charArrayOf('A', 'b', 's', 'o', 'l', 'u', 't', 'e', 'P', 'o', 'i', 'n', 't', 'e', 'r'),
//			charArrayOf('S', 'e', 'r', 'i', 'a', 'l', 'I', '/', 'O'),
//			charArrayOf('G', 'r', 'a', 'p', 'h', 'i', 'c', 's', 'O', 'u', 't', 'p', 'u', 't'),
//			charArrayOf('L', 'o', 'a', 'd', 'F', 'i', 'l', 'e'),
//			charArrayOf('L', 'o', 'a', 'd', 'F', 'i', 'l', 'e', '2'),
//			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'F', 'i', 'l', 'e', 'S', 'y', 's', 't', 'e', 'm'),
//			charArrayOf('T', 'a', 'p', 'e', 'I', '/', 'O'),
//			charArrayOf('D', 'i', 's', 'k', 'I', '/', 'O'),
//			charArrayOf('D', 'i', 's', 'k', 'I', '/', 'O', '2'),
//			charArrayOf('B', 'l', 'o', 'c', 'k', 'I', '/', 'O'),
//			charArrayOf('B', 'l', 'o', 'c', 'k', 'I', '/', 'O', '2'),
//			charArrayOf('B', 'l', 'o', 'c', 'k', 'I', '/', 'O', 'C', 'r', 'y', 'p', 't', 'o'),
//			charArrayOf('E', 'r', 'a', 's', 'e', 'B', 'l', 'o', 'c', 'k'),
//			charArrayOf('A', 'T', 'A', 'P', 'a', 's', 's', 'T', 'h', 'r', 'u'),
//			charArrayOf(
//				'S', 't', 'o', 'r', 'a', 'g', 'e',
//				'S', 'e', 'c', 'u', 'r', 'i', 't', 'y',
//				'C', 'o', 'm', 'm', 'a', 'n', 'd'
//			),
//			charArrayOf('N', 'V', 'M', 'e', 'P', 'a', 's', 's', 'T', 'h', 'r', 'u'),
//			charArrayOf('S', 'D', 'P', 'a', 's', 's', 'T', 'h', 'r', 'u'),
//			charArrayOf('R', 'A', 'M', 'D', 'i', 's', 'k'),
//			charArrayOf(
//				'P', 'a', 'r', 't', 'i', 't', 'i', 'o', 'n',
//				'I', 'n', 'f', 'o', 'r', 'm', 'a', 't', 'i', 'o', 'n'
//			),
//			charArrayOf('N', 'V', 'D', 'I', 'M', 'M', 'L', 'a', 'b', 'e', 'l'),
//			charArrayOf('U', 'F', 'S', 'D', 'e', 'v', 'i', 'c', 'e', 'C', 'o', 'n', 'f', 'i', 'g'),
//			charArrayOf('P', 'C', 'I', 'R', 'o', 'o', 't', 'B', 'r', 'i', 'd', 'g', 'e', 'I', '/', 'O'),
//			charArrayOf('P', 'C', 'I', 'I', '/', 'O'),
//			charArrayOf('S', 'C', 'S', 'I', 'I', '/', 'O'),
//			charArrayOf(
//				'E', 'x', 't', 'e', 'n', 'd', 'e', 'd',
//				'S', 'C', 'S', 'I',
//				'P', 'a', 's', 's', 'T', 'h', 'r', 'u'
//			),
//			charArrayOf('i', 'S', 'C', 'S', 'I', 'I', 'n', 'i', 't', 'i', 'a', 't', 'o', 'r', 'N', 'a', 'm', 'e'),
//			charArrayOf('U', 'S', 'B', '2', 'H', 'o', 's', 't', 'C', 'o', 'n', 't', 'r', 'o', 'l', 'l', 'e', 'r'),
//			charArrayOf('U', 'S', 'B', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n', 'I', '/', 'O'),
//			charArrayOf('D', 'e', 'b', 'u', 'g', 'S', 'u', 'p', 'p', 'o', 'r', 't'),
//			charArrayOf('D', 'e', 'b', 'u', 'g', 'P', 'o', 'r', 't'),
//			charArrayOf('D', 'e', 'c', 'o', 'm', 'p', 'r', 'e', 's', 's'),
//			charArrayOf('A', 'C', 'P', 'I', 'T', 'a', 'b', 'l', 'e'),
//			charArrayOf('U', 'n', 'i', 'c', 'o', 'd', 'e', 'C', 'o', 'l', 'l', 'a', 't', 'i', 'o', 'n'),
//			charArrayOf('R', 'e', 'g', 'u', 'l', 'a', 'r', 'E', 'x', 'p', 'r', 'e', 's', 's', 'i', 'o', 'n'),
//			charArrayOf('F', 'i', 'r', 'm', 'w', 'a', 'r', 'e', 'M', 'a', 'n', 'a', 'g', 'e', 'm', 'e', 'n', 't')
		)
		val protocolGuids = arrayOf(
			byteArrayOf( // Loaded Image
				0xA1.toByte(), 0x31, 0x1B, 0x5B,
				0x62, 0x95.toByte(),
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x3F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			),
//			novonordisk
			byteArrayOf( // Loaded Image Device Path
				0x7E, 0x15, 0x62, 0xBC.toByte(),
				0x33, 0x3E,
				0xEC.toByte(), 0x4F,
				0x99.toByte(), 0x20, 0x2D, 0x3B, 0x36, 0xD7.toByte(), 0x50, 0xDF.toByte()
			),
			byteArrayOf( // Device Path
				0x91.toByte(), 0x6E, 0x57, 0x09,
				0x3F, 0x6D,
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			),
			byteArrayOf( // Device Path Utilities
				0x4E, 0xBE.toByte(), 0x79, 0x03,
				0x06, 0xD7.toByte(),
				0x7D, 0x43,
				0xB0.toByte(), 0x37, 0xED.toByte(), 0xB8.toByte(), 0x2F, 0xB7.toByte(), 0x72, 0xA4.toByte()
			),
			byteArrayOf( // Driver Binding
				0xAB.toByte(), 0x31, 0xA0.toByte(), 0x18,
				0x43, 0xB4.toByte(),
				0x1A, 0x4D,
				0xA5.toByte(), 0xC0.toByte(), 0x0C, 0x09, 0x26, 0x1E, 0x9F.toByte(), 0x71
			),
			byteArrayOf( // Platform Driver Override
				0x38, 0xC7.toByte(), 0x30.toByte(), 0x6B,
				0x91.toByte(), 0xA3.toByte(),
				0xD4.toByte(), 0x11,
				0x9A.toByte(), 0x3B, 0x00, 0x90.toByte(), 0x27, 0x3F, 0xC1.toByte(), 0x4D
			),
			byteArrayOf( // Bus Specific Driver Override
				0x85.toByte(), 0xB2.toByte(), 0xC1.toByte(), 0x3B,
				0x15, 0x8A.toByte(),
				0x82.toByte(), 0x4A,
				0xAA.toByte(), 0xBF.toByte(), 0x4D, 0x7D, 0x13, 0xFB.toByte(), 0x32, 0x65
			),
			byteArrayOf( // Driver Diagnostics
				0x21, 0x03, 0x33, 0x4D,
				0x5F, 0x02,
				0xAC.toByte(), 0x4A,
				0x90.toByte(), 0xD8.toByte(), 0x5E, 0xD9.toByte(), 0x00, 0x17, 0x3B, 0x63
			),
			byteArrayOf( // Component Name
				0xFF.toByte(), 0x5C, 0x7A, 0x6A,
				0xD9.toByte(), 0xE8.toByte(),
				0x70, 0x4F,
				0xBA.toByte(), 0xDA.toByte(), 0x75, 0xAB.toByte(), 0x30, 0x25, 0xCE.toByte(), 0x14
			),
			byteArrayOf( // Platform to Driver Configuration
				0x90.toByte(), 0xD5.toByte(), 0x2C, 0x64,
				0x59, 0x80.toByte(),
				0x0A, 0x4C,
				0xA9.toByte(), 0x58, 0xC5.toByte(), 0xEC.toByte(), 0x07, 0xD2.toByte(), 0x3C, 0x4B
			),
			byteArrayOf( // Driver Supported EFI Version
				0x61, 0x87.toByte(), 0x19, 0x5C.toByte(),
				0xA8.toByte(), 0x16,
				0x69, 0x4E,
				0x97.toByte(), 0x2C, 0x89.toByte(), 0xD6.toByte(), 0x79, 0x54, 0xF8.toByte(), 0x1D
			),
			byteArrayOf( // Driver Family Override
				0x9E.toByte(), 0x12, 0xEE.toByte(), 0xB1.toByte(),
				0x36, 0xDA.toByte(),
				0x81.toByte(), 0x41,
				0x91.toByte(), 0xF8.toByte(), 0x04, 0xA4.toByte(), 0x92.toByte(), 0x37, 0x66, 0xA7.toByte()
			),
//			byteArrayOf( // Driver Health
//				0x10, 0x42, 0x53, 0x2A,
//				0x80.toByte(), 0x92.toByte(),
//				0xD8.toByte(), 0x41,
//				0xAE.toByte(), 0x79.toByte(), 0xCA.toByte(), 0xDA.toByte(), 0x01, 0xA2.toByte(), 0xB1.toByte(), 0x27
//			),
//			byteArrayOf( // Adapter Information
//				0x03, 0x14, 0xDD.toByte(), 0xE5.toByte(),
//				0x22, 0xD6.toByte(),
//				0x4E, 0xC2.toByte(),
//				0x84.toByte(), 0x88.toByte(), 0xC7.toByte(), 0x1B, 0x17, 0xF5.toByte(), 0xE8.toByte(), 0x02
//			),
//			byteArrayOf( // Simple Text Input Ex
//				0x34, 0x75, 0x9E.toByte(), 0xDD.toByte(),
//				0x62, 0x77,
//				0x98.toByte(), 0x46,
//				0x8C.toByte(), 0x14, 0xF5.toByte(), 0x85.toByte(), 0x17, 0xA6.toByte(), 0x25, 0xAA.toByte()
//			),
//			byteArrayOf( // Simple Text Input
//				0xC1.toByte(), 0x77, 0x74, 0x38,
//				0xC7.toByte(), 0x69,
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Simple Text Output
//				0xC2.toByte(), 0x77, 0x74, 0x38,
//				0xC7.toByte(), 0x69,
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Simple Pointer
//				0x87.toByte(), 0x8C.toByte(), 0x87.toByte(), 0x31,
//				0x75, 0x0B,
//				0xD5.toByte(), 0x11,
//				0x9A.toByte(), 0x4F, 0x00, 0x90.toByte(), 0x27, 0x3F, 0xC1.toByte(), 0x4D
//			),
//			byteArrayOf( // Absolute Pointer
//				0x2B, 0xD3.toByte(), 0x59, 0x8D.toByte(),
//				0x55, 0xC6.toByte(),
//				0xE9.toByte(), 0x4A,
//				0x9B.toByte(), 0x15, 0xF2.toByte(), 0x59, 0x04, 0x99.toByte(), 0x2A, 0x43
//			),
//			byteArrayOf( // Serial I/O
//				0x6F, 0xCF.toByte(), 0x25, 0xBB.toByte(),
//				0xD4.toByte(), 0xF1.toByte(),
//				0xD2.toByte(), 0x11,
//				0x9A.toByte(), 0x0C, 0x00, 0x90.toByte(), 0x27, 0x3F, 0xC1.toByte(), 0xFD.toByte()
//			),
//			byteArrayOf( // Graphics Output
//				0xDE.toByte(), 0xA9.toByte(), 0x42, 0x90.toByte(),
//				0xDC.toByte(), 0x23,
//				0x38, 0x4A,
//				0x96.toByte(), 0xFB.toByte(), 0x7A, 0xDE.toByte(), 0xD0.toByte(), 0x80.toByte(), 0x51, 0x6A
//			),
//			byteArrayOf( // Load File
//				0x91.toByte(), 0x30, 0xEC.toByte(), 0x56,
//				0x4C, 0x95.toByte(),
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x3F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Load File 2
//				0xC1.toByte(), 0xC0.toByte(), 0x06, 0x40,
//				0xB3.toByte(), 0xFC.toByte(),
//				0x3E, 0x40,
//				0x99.toByte(), 0x6D, 0x4A, 0x6C, 0x87.toByte(), 0x24, 0xE0.toByte(), 0x6D
//			),
//			byteArrayOf( // Simple File System
//				0x22, 0x5B, 0x4E, 0x96.toByte(),
//				0x59, 0x64,
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Tape I/O
//				0x33, 0xE6.toByte(), 0x93.toByte(), 0x1E.toByte(),
//				0x5A, 0xD6.toByte(),
//				0x9E.toByte(), 0x45,
//				0xAB.toByte(), 0x84.toByte(), 0x93.toByte(), 0xD9.toByte(), 0xEC.toByte(), 0x26, 0x6D.toByte(), 0x18
//			),
//			byteArrayOf( // Disk I/O
//				0x71, 0x51, 0x34, 0xCE.toByte(),
//				0x0B, 0xBA.toByte(),
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x4F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Disk I/O 2
//				0xAE.toByte(), 0x8E.toByte(), 0x1C, 0x15,
//				0x2C, 0x7F,
//				0x2C, 0x47,
//				0x9E.toByte(), 0x54, 0x98.toByte(), 0x28, 0x19, 0x4F, 0x6A, 0x88.toByte()
//			),
//			byteArrayOf( // Block I/O
//				0x21, 0x5B, 0x4E, 0x96.toByte(),
//				0x59, 0x64,
//				0xD2.toByte(), 0x11,
//				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
//			),
//			byteArrayOf( // Block I/O 2
//				0x72, 0x24, 0x7B, 0xA7.toByte(),
//				0x82.toByte(), 0xE2.toByte(),
//				0x9F.toByte(), 0x4E,
//				0xA2.toByte(), 0x45, 0xC2.toByte(), 0xC0.toByte(), 0xE2.toByte(), 0x7B, 0xBC.toByte(), 0xC1.toByte()
//			),
//			byteArrayOf( // Block I/O Crypto
//				0xBA.toByte(), 0x90.toByte(), 0x04, 0xA0.toByte(),
//				0x1A, 0x3F,
//				0x4C, 0x4B,
//				0xAB.toByte(), 0x90.toByte(), 0x4F, 0xA9.toByte(), 0x97.toByte(), 0x26, 0xA1.toByte(), 0xE8.toByte()
//			),
//			byteArrayOf( // Erase Block
//				0x3E, 0xA9.toByte(), 0xA9.toByte(), 0x95.toByte(),
//				0x6E, 0xA8.toByte(),
//				0x26, 0x49,
//				0xAA.toByte(), 0xEF.toByte(), 0x99.toByte(), 0x18, 0xE7.toByte(), 0x72, 0xD9.toByte(), 0x87.toByte()
//			),
//			byteArrayOf( // ATA Pass Thru
//				0xF0.toByte(), 0xE7.toByte(), 0x3D, 0x1D,
//				0x07, 0x08,
//				0x4F, 0x42,
//				0xAA.toByte(), 0x69, 0x11, 0xA5.toByte(), 0x4E, 0x19, 0xA4.toByte(), 0x6F
//			),
//			byteArrayOf( // Storage Security Command
//				0x6D, 0x0B, 0x8B.toByte(), 0xC8.toByte(),
//				0xFC.toByte(), 0x0D,
//				0xA7.toByte(), 0x49,
//				0x9C.toByte(), 0xB4.toByte(), 0x49, 0x07, 0x4B, 0x4C, 0x3A, 0x78
//			),
//			byteArrayOf( // NVMe Pass Thru
//				0x12, 0x83.toByte(), 0xC7.toByte(), 0x52,
//				0xDC.toByte(), 0x8E.toByte(),
//				0x33, 0x42,
//				0x98.toByte(), 0xF2.toByte(), 0x1A, 0x1A, 0xA5.toByte(), 0xE3.toByte(), 0x88.toByte(), 0xA5.toByte()
//			),
//			byteArrayOf( // SD MMC Pass Thru
//				0xD9.toByte(), 0xF0.toByte(), 0x6E, 0x71,
//				0x83.toByte(), 0xFF.toByte(),
//				0x69, 0x4F,
//				0x81.toByte(), 0xE9.toByte(), 0x51, 0x8B.toByte(), 0xD3.toByte(), 0x9A.toByte(), 0x8E.toByte(), 0x70
//			),
//			byteArrayOf( // RAM Disk
//				0xDF.toByte(), 0xA0.toByte(), 0x38, 0xAB.toByte(),
//				0x73, 0x68,
//				0xA9.toByte(), 0x44,
//				0x87.toByte(), 0xE6.toByte(), 0xD4.toByte(), 0xEB.toByte(), 0x56, 0x14, 0x84.toByte(), 0x49
//			),
//			byteArrayOf( // Partition Information
//				0x2C, 0xF6.toByte(), 0xF2.toByte(), 0x8C.toByte(),
//				0x9B.toByte(), 0xBC.toByte(),
//				0x21, 0x48,
//				0x80.toByte(), 0x8D.toByte(), 0xEC.toByte(), 0x9E.toByte(), 0xC4.toByte(), 0x21, 0xA1.toByte(),
//				0xA0.toByte()
//			),
//			byteArrayOf( // NVDIMM Label
//				0x80.toByte(), 0x6B, 0x0B, 0xD4.toByte(),
//				0xD5.toByte(), 0x97.toByte(),
//				0x82.toByte(), 0x42,
//				0xBB.toByte(), 0x1D, 0x22, 0x3A, 0x16, 0x91.toByte(), 0x80.toByte(), 0x58
//			),
//			byteArrayOf( // UFS Device Config
//				0xB0.toByte(), 0xFA.toByte(), 0x1B, 0xB8.toByte(),
//				0xB3.toByte(), 0x0E,
//				0xF9.toByte(), 0x4C,
//				0x84.toByte(), 0x65, 0x7F, 0xA9.toByte(), 0x86.toByte(), 0x36, 0x16, 0x64
//			),
//			byteArrayOf( // PCI Root Bridge I/O
//				0xBB.toByte(), 0x7E, 0x70, 0x2F,
//				0x1A, 0x4A,
//				0xD4.toByte(), 0x11,
//				0x9A.toByte(), 0x38, 0x00, 0x90.toByte(), 0x27, 0x3F, 0xC1.toByte(), 0x4D
//			),
//			byteArrayOf( // PCI I/O
//				0x00, 0xB2.toByte(), 0xF5.toByte(), 0x4C,
//				0xB8.toByte(), 0x68,
//				0xA5.toByte(), 0x4C,
//				0x9E.toByte(), 0xEC.toByte(), 0xB2.toByte(), 0x3E, 0x3F, 0x50, 0x02, 0x9A.toByte()
//			),
//			byteArrayOf( // SCSI I/O
//				0xE6.toByte(), 0x47, 0x2F, 0x93.toByte(),
//				0x62, 0x23,
//				0x02, 0x40,
//				0x80.toByte(), 0x3E, 0x3C, 0xD5.toByte(), 0x4B, 0x13, 0x8F.toByte(), 0x85.toByte()
//			),
//			byteArrayOf( // Extended SCSI Pass Thru
//				0x32, 0x76, 0x3B, 0x14,
//				0x1B, 0xB8.toByte(),
//				0xB7.toByte(), 0x4C,
//				0xAB.toByte(), 0xD3.toByte(), 0xB6.toByte(), 0x25, 0xA5.toByte(), 0xB9.toByte(), 0xBF.toByte(),
//				0xFE.toByte()
//			),
//			byteArrayOf( // iSCSI Initiator Name
//				0x45, 0x49, 0x32, 0x59,
//				0x44, 0xEC.toByte(),
//				0x0D, 0x4C,
//				0xB1.toByte(), 0xCD.toByte(), 0x9D.toByte(), 0xB1.toByte(), 0x39, 0xDF.toByte(), 0x07, 0x0C
//			),
//			byteArrayOf( // USB2 Host Controller
//				0x26, 0x52, 0x74, 0x3E,
//				0x18, 0x98.toByte(),
//				0xB6.toByte(), 0x45,
//				0xA2.toByte(), 0xAC.toByte(), 0xD7.toByte(), 0xCD.toByte(), 0x0E, 0x8B.toByte(), 0xA2.toByte(),
//				0xBC.toByte()
//			),
//			byteArrayOf( // USB Function I/O
//				0x3A, 0x96.toByte(), 0xD2.toByte(), 0x32,
//				0x5D, 0xFE.toByte(),
//				0x30, 0x4F,
//				0xB6.toByte(), 0x33, 0x6E, 0x5D, 0xC5.toByte(), 0x58, 0x03, 0xCC.toByte()
//			),
//			byteArrayOf( // Debug Support
//				0x0C, 0x59, 0x55, 0x27,
//				0x3C, 0x6F,
//				0xFA.toByte(), 0x42,
//				0x9E.toByte(), 0xA4.toByte(), 0xA3.toByte(), 0xBA.toByte(), 0x54, 0x3C, 0xDA.toByte(), 0x25
//			),
//			byteArrayOf( // Debugport
//				0xD2.toByte(), 0xE8.toByte(), 0xA4.toByte(), 0xEB.toByte(),
//				0x58, 0x38,
//				0xEC.toByte(), 0x41,
//				0xA2.toByte(), 0x81.toByte(), 0x26, 0x47, 0xBA.toByte(), 0x96.toByte(), 0x60, 0xD0.toByte()
//			),
//			byteArrayOf( // Decompress
//				0xFE.toByte(), 0x7C, 0x11, 0xD8.toByte(),
//				0xA6.toByte(), 0x94.toByte(),
//				0xD4.toByte(), 0x11,
//				0x9A.toByte(), 0x3A, 0x00, 0x90.toByte(), 0x27, 0x3F, 0xC1.toByte(), 0x4D
//			),
//			byteArrayOf( // ACPI Table
//				0xDD.toByte(), 0x6B, 0xE0.toByte(), 0xFF.toByte(),
//				0x07, 0x61,
//				0xA6.toByte(), 0x46,
//				0x7B, 0xB2.toByte(), 0x5A, 0x9C.toByte(), 0x7E, 0xC5.toByte(), 0x27, 0x5C
//			),
//			byteArrayOf( // Unicode Collation
//				0xFC.toByte(), 0x51, 0xC7.toByte(), 0xA4.toByte(),
//				0xAE.toByte(), 0x23,
//				0x3E, 0x4C,
//				0x92.toByte(), 0xE9.toByte(), 0x49, 0x64, 0xCF.toByte(), 0x63, 0xF3.toByte(), 0x49
//			),
//			byteArrayOf( // Regular Expression
//				0x9A.toByte(), 0x9D.toByte(), 0xF7.toByte(), 0xB3.toByte(),
//				0x6C, 0x43,
//				0x11, 0xDC.toByte(),
//				0xB0.toByte(), 0x52, 0xCD.toByte(), 0x85.toByte(), 0xDF.toByte(), 0x52, 0x4C, 0xE6.toByte()
//			),
//			byteArrayOf( // Firmware Management
//				0x67, 0x7A, 0xC7.toByte(), 0x86.toByte(),
//				0x97.toByte(), 0x0B,
//				0x33, 0x46,
//				0xA1.toByte(), 0x87.toByte(), 0x49, 0x10, 0x4D, 0x06, 0x85.toByte(), 0xC7.toByte()
//			)
		)
		var i = 0
		var longestNameLength = 0
		while (i < protocolNames.size) {
			val size = protocolNames[i].size
			if (size > longestNameLength) longestNameLength = size
			i++
		}
		i = 0
		val okC = charArrayOf('O', 'K', '\r', '\n')
		val unsupportedC = charArrayOf('U', 'n', 's', 'u', 'p', 'p', 'o', 'r', 't', 'e', 'd', '\r', '\n')
		val divider = charArrayOf('.').toUTF16LE()
		val bootServices = EFISystemTable.bootServices(systemTable)
		val ptr = allocateN()
		write64(ptr, 0)
		while (i < protocolNames.size) {
			val name = protocolNames[i]
			EFISimpleTextOutputProtocol.outputString(conOut, name.toUTF16LE().address + 8)
			var s = 0
			while (s < (longestNameLength - name.size) + 3) {
				EFISimpleTextOutputProtocol.outputString(conOut, divider.address + 8)
				s++
			}

			val status = EFIBootServices.locateProtocol(
				bootServices,
				protocolGuids[i].address + 8, null, ptr
			)
			EFISimpleTextOutputProtocol.outputString(
				conOut,
				(status.toLong().toCharArray(10) + charArrayOf('\r', '\n')).toUTF16LE().address + 8
			)
			i++
		}
		while (true) {
		}
//		flash_EFI_SIMPLE_FILE_SYSTEM_PROTOCOL_GUID(guid)
//		val simpleFileSystemProtocol = systemTable.bootServices.locateProtocol(
//			guid, MemorySegment.NULL
//		).data as EFISimpleFileSystemProtocol
//		val fileProtocol = simpleFileSystemProtocol.openVolume().data as EFIFileProtocol
//		systemTable.conOut.outputString("Media Source\r\n")
//		readFileSystemInfo(systemTable, guid, fileProtocol, pointer)
//		val fileSystemInfo = pointer.get(ValueLayout.ADDRESS, 0) as EFIFileSystemInfo
//		systemTable.conOut.outputString(" Label: \"")
//		systemTable.conOut.outputStringAt(fileSystemInfo.volumeLabel)
//		systemTable.conOut.outputString("\" ")
//		if (fileSystemInfo.readOnly) systemTable.conOut.outputString("(read-only)\r\n")
//		else systemTable.conOut.outputString("(writable)\r\n")
//		systemTable.conOut.outputString(" Size: ")
//		printDecimal(systemTable, fileSystemInfo.volumeSize, 0)
//		systemTable.conOut.outputString(" b ")
//		systemTable.conOut.outputString("(free: ")
//		printDecimal(systemTable, fileSystemInfo.freeSpace, 0)
//		systemTable.conOut.outputString(" b)\r\n")
//		systemTable.conOut.outputString(" Block Size: ")
//		printDecimal(systemTable, fileSystemInfo.blockSize.toLong(), 0)
//		systemTable.conOut.outputString(" b\r\n")
//		val file = fileProtocol.open("\\image_bgra.bin", 1, 0).data as EFIFileProtocol
//		readFileInfo(systemTable, guid, file, pointer)
//		val fileInfo = pointer.get(ValueLayout.ADDRESS, 0) as EFIFileInfo
//		val fileData = systemTable.bootServices.allocatePool(EFIMemoryType.EfiLoaderData, fileInfo.fileSize).data
//		val fileDataSize = systemTable.bootServices.allocatePool(EFIMemoryType.EfiLoaderData, 8).data
//		fileDataSize.set(ValueLayout.JAVA_LONG, 0, fileInfo.fileSize)
//		file.read(fileDataSize, fileData)
//
//		flash_EFI_GRAPHICS_OUTPUT_PROTOCOL_GUID(guid)
//		val graphicsOutputProtocol = systemTable.bootServices.locateProtocol(
//			guid, MemorySegment.NULL
//		).data as EFIGraphicsOutputProtocol
//		graphicsOutputProtocol.blt(
//			fileData, 2,
//			0, 0,
//			0, 0,
//			608, 501, 0
//		)

		return 0
	}
}