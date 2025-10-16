package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

object EFIExample {
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
		val guid = systemTable.bootServices.allocatePool(EFIMemoryType.EfiBootServicesData, 16).data
		guid.set(ValueLayout.JAVA_INT, 0, 0x56EC3091)
		guid.set(ValueLayout.JAVA_SHORT, 4, 0x954C.toShort())
		guid.set(ValueLayout.JAVA_SHORT, 6, 0x11D2)
		guid.set(ValueLayout.JAVA_BYTE, 8, 0x8E.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 9, 0x3F)
		guid.set(ValueLayout.JAVA_BYTE, 10, 0x00)
		guid.set(ValueLayout.JAVA_BYTE, 11, 0xA0.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 12, 0xC9.toByte())
		guid.set(ValueLayout.JAVA_BYTE, 13, 0x69)
		guid.set(ValueLayout.JAVA_BYTE, 14, 0x72)
		guid.set(ValueLayout.JAVA_BYTE, 15, 0x3B)
//		systemTable.conOut.outputString("GUID @ ")
//		print(systemTable, 0xCAFEBABE)
//		systemTable.conOut.outputString("\r\n")
		return 0
//		val iface = systemTable.allocateLocalPointer()
//		systemTable.bootServices.locateProtocol(EFI_LOAD_FILE_PROTOCOL_GUID, null, iface)
//		return iface.address()
	}
}