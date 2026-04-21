package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.allocateN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.naturalSize
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.write64
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
		val protocolNames = arrayOf(
			charArrayOf('L', 'o', 'a', 'd', 'e', 'd', 'I', 'm', 'a', 'g', 'e'),
			charArrayOf('L', 'o', 'a', 'd', 'F', 'i', 'l', 'e'),
			charArrayOf('S', 'i', 'm', 'p', 'l', 'e', 'F', 'i', 'l', 'e', 'S', 'y', 's', 't', 'e', 'm'),
			charArrayOf('D', 'i', 's', 'k', 'I', '/', 'O')
		)
		val protocolGuids = arrayOf(
			byteArrayOf(
				0xA1.toByte(), 0x31, 0x1B, 0x5B,
				0x62, 0x95.toByte(),
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x3F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			),
			byteArrayOf(
				0x91.toByte(), 0x30, 0xEC.toByte(), 0x56,
				0x4C, 0x95.toByte(),
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x3F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			),
			byteArrayOf(
				0x22, 0x5B, 0x4E, 0x96.toByte(),
				0x59, 0x64,
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x39, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			),
			byteArrayOf(
				0x71, 0x51, 0x34, 0xCE.toByte(),
				0x0B, 0xBA.toByte(),
				0xD2.toByte(), 0x11,
				0x8E.toByte(), 0x4F, 0x00, 0xA0.toByte(), 0xC9.toByte(), 0x69, 0x72, 0x3B
			)
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
//			val status = EFIBootServices.locateProtocol(
//				bootServices,
//				protocolGuids[i].address + 8, null, ptr
//			)
			val status = 14
			EFISimpleTextOutputProtocol.outputString(
				conOut,
				(if (status == 14) unsupportedC else okC).toUTF16LE().address + 8
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