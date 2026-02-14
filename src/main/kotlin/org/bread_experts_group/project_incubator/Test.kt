package org.bread_experts_group.project_incubator

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures
import org.bread_experts_group.generic.io.reader.BSLWriter
import java.math.BigInteger

fun main() {
	val az = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE)
		.device
		.get(SystemDeviceFeatures.PATH_APPEND)
		.append("test.bin")
		.get(SystemDeviceFeatures.IO_DEVICE)
		.open(
			StandardIOOpenFeatures.CREATE,
			FileIOOpenFeatures.TRUNCATE,
			FileIOReOpenFeatures.WRITE,
			FileIOReOpenFeatures.READ
		)
		.firstNotNullOf { it as? IODevice }
	val wr = BSLWriter(az.get(IODeviceFeatures.WRITE), { _, _ -> })
	wr.writeExtensibleNumeric(
		BigInteger.valueOf(0xABABAB) or
				(BigInteger.valueOf(0xBCBCBC) shl (24 * 1)) or
				(BigInteger.valueOf(0xCDCDCD) shl (24 * 2)) or
				(BigInteger.valueOf(0xDEDEDE) shl (24 * 3)) or
				(BigInteger.valueOf(0xEFEFEF) shl (24 * 4)) or
				(BigInteger.valueOf(0xF1F1F1) shl (24 * 5)) or
				(BigInteger.valueOf(0x121212) shl (24 * 6)) or
				(BigInteger.valueOf(0x232323) shl (24 * 7)) or
				(BigInteger.valueOf(0x343434) shl (24 * 8)) or
				(BigInteger.valueOf(0x454545) shl (24 * 9)) or
				(BigInteger.valueOf(0x565656) shl (24 * 10))
	)
	wr.flush()
//	az.get(IODeviceFeatures.SEEK).seek(0, StandardSeekIODeviceFeatures.BEGIN)
//	val rd = BSLReader(az.get(IODeviceFeatures.READ), fileReadCheck)
//	println(rd.readExtensibleNumeric())
}