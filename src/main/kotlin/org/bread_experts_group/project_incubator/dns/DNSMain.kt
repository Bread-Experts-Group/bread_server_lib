package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.device.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.device.io.open.StandardIOOpenFeatures
import org.bread_experts_group.api.system.device.io.transparent_encrpytion.WindowsOpenTransparentEncryptionRawIODeviceFeatures

fun main() {
	val testFile = SystemProvider
		.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
		.get(SystemDeviceFeatures.APPEND).append("testFile.txt")
	testFile.get(SystemDeviceFeatures.IO_DEVICE).open(
		StandardIOOpenFeatures.CREATE,
		FileIOReOpenFeatures.WRITE
	)!!.first.also {
		it.get(IODeviceFeatures.WRITE).write("${System.currentTimeMillis()}", Charsets.UTF_8)
		it.get(IODeviceFeatures.RELEASE).close()
	}
	testFile.get(SystemDeviceFeatures.ENABLE_TRANSPARENT_ENCRYPT).enable()
	testFile.get(SystemDeviceFeatures.TRANSPARENT_ENCRYPT_RAW_IO_DEVICE).open(
		WindowsOpenTransparentEncryptionRawIODeviceFeatures.EXPORT
	)!!.first.also {
		val fileIO = SystemProvider
			.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
			.get(SystemDeviceFeatures.APPEND).append("testFile_enc.txt")
			.get(SystemDeviceFeatures.IO_DEVICE).open(
				StandardIOOpenFeatures.CREATE,
				FileIOOpenFeatures.TRUNCATE,
				FileIOReOpenFeatures.WRITE
			)!!.first
		it.get(IODeviceFeatures.READ_CALLBACK).read {
			val written = fileIO.get(IODeviceFeatures.WRITE).write(it)
			fileIO.get(IODeviceFeatures.SEEK).seek(written.toLong())
		}
		it.get(IODeviceFeatures.RELEASE).close()
	}
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}