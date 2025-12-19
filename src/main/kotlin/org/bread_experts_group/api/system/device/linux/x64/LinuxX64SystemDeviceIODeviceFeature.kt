package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.linux.x64.*
import org.bread_experts_group.api.system.io.open.*
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.*
import org.bread_experts_group.ffi.posix.x64.S_IRWXU
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.MemorySegment

class LinuxX64SystemDeviceIODeviceFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeOpen_vInt != null

	override fun open(vararg features: OpenIODeviceFeatureIdentifier): List<OpenIODeviceDataIdentifier> {
		if (features.contains(StandardIOOpenFeatures.DIRECTORY)) return emptyList() // TODO: Directories
		val data = mutableListOf<OpenIODeviceDataIdentifier>()
		var flags = 0
		val rOK = features.contains(FileIOReOpenFeatures.READ)
		val wOK = features.contains(FileIOReOpenFeatures.WRITE)
		if (rOK && wOK) {
			flags = O_RDWR
			data.add(FileIOReOpenFeatures.READ)
			data.add(FileIOReOpenFeatures.WRITE)
		} else {
			if (rOK) {
				flags = O_RDONLY
				data.add(FileIOReOpenFeatures.READ)
			}
			if (wOK) {
				flags = O_WRONLY
				data.add(FileIOReOpenFeatures.WRITE)
			}
		}
		if (features.contains(FileIOOpenFeatures.TRUNCATE)) {
			flags = flags or O_TRUNC
			data.add(FileIOOpenFeatures.TRUNCATE)
		}
		val fd = if (features.contains(StandardIOOpenFeatures.CREATE)) {
			flags = flags or O_CREAT
			data.add(StandardIOOpenFeatures.CREATE)
			nativeOpen_vInt!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				flags,
				S_IRWXU
			) as Int
		} else {
			nativeOpen_vInt!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				flags,
				0
			) as Int
		}
		if (fd == -1) throwLastErrno()
		val ioDevice = IODevice()
		val oR = data.contains(FileIOReOpenFeatures.READ)
		val oW = data.contains(FileIOReOpenFeatures.WRITE)
		val seek = LinuxIODeviceSeekFeature(fd)
		if (oR || oW) ioDevice.features.add(seek)
		if (oR) ioDevice.features.add(LinuxX64IODeviceReadFeature(fd))
		if (oW) {
			ioDevice.features.add(LinuxX64IODeviceWriteFeature(fd))
			ioDevice.features.add(LinuxX64IODeviceSetSizeFeature(fd, seek))
		}
		ioDevice.features.add(LinuxX64IODeviceGetSizeFeature(fd))
		data.add(ioDevice)
		return data
	}
}