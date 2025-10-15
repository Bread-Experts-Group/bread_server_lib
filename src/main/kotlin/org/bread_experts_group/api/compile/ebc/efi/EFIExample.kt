package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

object EFIExample {
	@Suppress("LocalVariableName")
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
//		return systemTable.bootServices.header.signature
		return systemTable.bootServices.allocatePool(EFIMemoryType.EfiLoaderData, 8).second.address()
//		val EFI_LOAD_FILE_PROTOCOL_GUID = systemTable.allocateGUID(
//			GUID(
//				0x56EC3091u, 0x954Cu, 0x11D2u,
//				ubyteArrayOf(0x8Eu, 0x3Fu), ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x69u, 0x72u, 0x3Bu)
//			)
//		)
//		val iface = systemTable.allocateLocalPointer()
//		systemTable.bootServices.locateProtocol(EFI_LOAD_FILE_PROTOCOL_GUID, null, iface)
//		return iface.address()
	}
}