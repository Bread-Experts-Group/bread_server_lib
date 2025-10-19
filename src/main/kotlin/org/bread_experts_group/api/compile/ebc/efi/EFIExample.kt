package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

object EFIExample {
	@JvmStatic
	fun print(systemTable: EFISystemTable, l: Long) {
		val string = systemTable.bootServices.allocatePool(EFIMemoryType.EfiBootServicesData, 34).data
		var hex = l
		var offset = 15
		do {
			val nibble = (hex and 0b1111).toInt()
			val character = ((if (nibble > 9) 0x37 else 0x30) + nibble).toByte()
			string.set(ValueLayout.JAVA_BYTE, offset * 2L, character)
			hex = hex ushr 4
		} while (offset-- > 0)
		systemTable.conOut.outputStringAt(string)
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
		if (systemTable.bootServices.locateProtocol(guid, MemorySegment.NULL).status.toInt() != 0) return 0
		return 1
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
			).toInt() == 0
		) systemTable.conOut.outputString("Un")
		systemTable.conOut.outputString("Supported\r\n")
		return 0
	}
}