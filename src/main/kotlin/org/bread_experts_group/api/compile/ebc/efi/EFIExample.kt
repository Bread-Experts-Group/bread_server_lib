package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

object EFIExample {
	@JvmStatic
	fun printHex(systemTable: EFISystemTable, l: Long): Long {
		val string = systemTable.bootServices.allocatePool(EFIMemoryType.EfiBootServicesData, 34).data
		var hex = l
		var offset = 15
//		do {
		val nibble = (hex and 0b1111).toInt()
//		val character = ((if (nibble > 9) 0x37 else 0x30) + nibble).toByte()
//			string.set(ValueLayout.JAVA_BYTE, offset * 2L, character)
		hex = hex ushr 4
//		} while (offset-- > 0)
		systemTable.conOut.outputStringAt(string)
		return 0
	}

	@JvmStatic
	fun testGUID(
		systemTable: EFISystemTable,
		i0: UInt,
		s0: UShort,
		s1: UShort,
		b0: UByte,
		b1: UByte,
		b2: UByte,
		b3: UByte,
		b4: UByte,
		b5: UByte,
		b6: UByte,
		b7: UByte
	): Long {
		val guid = systemTable.bootServices.allocatePool(EFIMemoryType.EfiBootServicesData, 16).data
		guid.set(ValueLayout.JAVA_INT, 0, i0.toInt())
		guid.set(ValueLayout.JAVA_SHORT, 4, s0.toShort())
		guid.set(ValueLayout.JAVA_SHORT, 6, s1.toShort())
		guid.set(ValueLayout.JAVA_BYTE, 8, b0.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 9, b1.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 10, b2.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 11, b3.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 12, b4.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 13, b5.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 14, b6.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 15, b7.toByte())
		return systemTable.bootServices.locateProtocol(guid, MemorySegment.NULL).status
	}

	@JvmStatic
	@OptIn(ExperimentalUnsignedTypes::class)
	fun efiMain(imageHandle: MemorySegment, systemTable: EFISystemTable): Long {
		systemTable.conOut.reset(false)
		systemTable.conOut.outputString(
			"       #++++*       \r\n" +
					"    ==+*++==++==    \r\n" +
					" ++**+===+=====++++ \r\n" +
					" *##*#**###***#*### \r\n" +
					" *#++**+*+++####*## \r\n" +
					" +#*++++**#%*#####% \r\n" +
					" +*++++++#%##*#*#%# \r\n" +
					" ##++++*#####*##%## \r\n" +
					" **#**###*#####%### \r\n" +
					"    %#***+###%##    \r\n" +
					"       *#*##%       \r\n"
		)
		systemTable.conOut.outputString("Bread Experts Group ... UEFI Loader\r\n")
		systemTable.conOut.outputString("Firmware Vendor: ")
		systemTable.conOut.outputStringAt(systemTable.firmwareVendor)
		systemTable.conOut.outputString("\r\n")
		systemTable.conOut.outputString("EFI_LOAD_FILE_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x56EC3091u, 0x954Cu, 0x11D2u,
				0x8Eu, 0x3Fu, 0x00u, 0xA0u,
				0xC9u, 0x69u, 0x72u, 0x3Bu
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_LOAD_FILE2_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x4006C0C1u, 0xFCB3u, 0x403Eu,
				0x99u, 0x6Du, 0x4Au, 0x6Cu,
				0x87u, 0x24u, 0xE0u, 0x6Du
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_SIMPLE_FILE_SYSTEM_PROTOCOL / EFI_FILE_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x0964E5B22u, 0x6459u, 0x11D2u,
				0x8Eu, 0x39u, 0x00u, 0xA0u,
				0xC9u, 0x69u, 0x72u, 0x3Bu
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_TAPE_IO_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x1E93E633u, 0xD65Au, 0x459Eu,
				0xABu, 0x84u, 0x93u, 0xD9u,
				0xECu, 0x26u, 0x6Du, 0x18u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_DISK_IO_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xCE345171u, 0xBA0Bu, 0x11D2u,
				0x8Eu, 0x4Fu, 0x00u, 0xA0u,
				0xC9u, 0x69u, 0x72u, 0x3Bu
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_DISK_IO2_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x151C8EAEu, 0x7F2Cu, 0x472Cu,
				0x9Eu, 0x54u, 0x98u, 0x28u,
				0x19u, 0x4Fu, 0x6Au, 0x88u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_BLOCK_IO_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x964E5B21u, 0x6459u, 0x11D2u,
				0x8Eu, 0x39u, 0x00u, 0xA0u,
				0xC9u, 0x69u, 0x72u, 0x3Bu
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_BLOCK_IO2_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xA77B2472u, 0xE282u, 0x4E9Fu,
				0xA2u, 0x45u, 0xC2u, 0xC0u,
				0xE2u, 0x7Bu, 0xBCu, 0xC1u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_BLOCK_IO_CRYPTO_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xA00490BAu, 0x3F1Au, 0x4B4Cu,
				0xABu, 0x90u, 0x4Fu, 0xA9u,
				0x97u, 0x26u, 0xA1u, 0xE8u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_ERASE_BLOCK_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x95A9A93Eu, 0xA86Eu, 0x4926u,
				0xAAu, 0xEFu, 0x99u, 0x18u,
				0xE7u, 0x72u, 0xD9u, 0x87u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_ATA_PASS_THRU_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x1D3DE7F0u, 0x0807u, 0x424Fu,
				0xAAu, 0x69u, 0x11u, 0xA5u,
				0x4Eu, 0x19u, 0xA4u, 0x6Fu
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_STORAGE_SECURITY_COMMAND_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xC88B0B6Du, 0x0DFCu, 0x49A7u,
				0x9Cu, 0xB4u, 0x49u, 0x07u,
				0x4Bu, 0x4Cu, 0x3Au, 0x78u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_NVM_EXPRESS_PASS_THRU_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x52C78312u, 0x8EDCu, 0x4233u,
				0x98u, 0xF2u, 0x1Au, 0x1Au,
				0xA5u, 0xE3u, 0x88u, 0xA5u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_SD_MMC_PASS_THRU_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x716EF0D9u, 0xFF83u, 0x4F69u,
				0x81u, 0xE9u, 0x51u, 0x8Bu,
				0xD3u, 0x9Au, 0x8Eu, 0x70u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_RAM_DISK_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xAB38A0DFu, 0x6873u, 0x44A9u,
				0x87u, 0xE6u, 0xD4u, 0xEBu,
				0x56u, 0x14u, 0x84u, 0x49u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_PARTITION_INFO_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0x8CF2F62Cu, 0xBC9Bu, 0x4821u,
				0x80u, 0x8Du, 0xECu, 0x9Eu,
				0xC4u, 0x21u, 0xA1u, 0xA0u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_NVDIMM_LABEL_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xD40B6B80u, 0x97D5u, 0x4282u,
				0xBBu, 0x1Du, 0x22u, 0x3Au,
				0x16u, 0x91u, 0x80u, 0x58u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		systemTable.conOut.outputString("EFI_UFS_DEVICE_CONFIG_PROTOCOL Status: ")
		if (
			testGUID(
				systemTable,
				0xB81BFAB0u, 0x0EB3u, 0x4CF9u,
				0x84u, 0x65u, 0x7Fu, 0xA9u,
				0x86u, 0x36u, 0x16u, 0x64u
			).toInt() != 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		return 0
	}
}