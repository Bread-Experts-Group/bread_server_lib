package org.bread_experts_group.project_incubator

fun main() {
//	var s = ""
//	fun iterate(d: SystemDevice) {
//		d.get(SystemDeviceFeatures.PATH_CHILDREN).forEach {
//			println(s + it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity.toString())
//			val b = s
//			s += " "
//			iterate(it)
//			s = b
//		}
//	}
//
//	iterate(SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE).device)
//	val az = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE)
//		.device
//		.get(SystemDeviceFeatures.PATH_APPEND)
//		.append("test.bin")
//		.get(SystemDeviceFeatures.IO_DEVICE)
//		.open(
//			StandardIOOpenFeatures.CREATE,
//			FileIOOpenFeatures.TRUNCATE,
//			FileIOReOpenFeatures.WRITE,
//			FileIOReOpenFeatures.READ
//		)
//		.firstNotNullOf { it as? IODevice }
//	val wr = BSLWriter(az.get(IODeviceFeatures.WRITE))
//	wr.writeExtensibleNumeric(
//		BigInteger.valueOf(0xABABAB) or
//				(BigInteger.valueOf(0xBCBCBC) shl (24 * 1)) or
//				(BigInteger.valueOf(0xCDCDCD) shl (24 * 2)) or
//				(BigInteger.valueOf(0xDEDEDE) shl (24 * 3)) or
//				(BigInteger.valueOf(0xEFEFEF) shl (24 * 4)) or
//				(BigInteger.valueOf(0xF1F1F1) shl (24 * 5)) or
//				(BigInteger.valueOf(0x121212) shl (24 * 6)) or
//				(BigInteger.valueOf(0x232323) shl (24 * 7)) or
//				(BigInteger.valueOf(0x343434) shl (24 * 8)) or
//				(BigInteger.valueOf(0x454545) shl (24 * 9)) or
//				(BigInteger.valueOf(0x565656) shl (24 * 10))
//	)
//	wr.flush()
//	az.get(IODeviceFeatures.SEEK).seek(0, StandardSeekIODeviceFeatures.BEGIN)
//	val rd = BSLReader(az.get(IODeviceFeatures.READ), fileReadCheck)
//	println(rd.readExtensibleNumeric())
}