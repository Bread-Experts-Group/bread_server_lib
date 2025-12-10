package org.bread_experts_group.api.system.device.linux

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.linux.LinuxIODevice
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.nativeClose
import org.bread_experts_group.ffi.posix.nativeOpen
import org.bread_experts_group.ffi.posix.throwLastErrno
import java.lang.foreign.Arena

class LinuxSystemDeviceIODeviceFeature(
	private val path: String
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeOpen != null

	override fun open(vararg features: OpenIODeviceFeatureIdentifier): List<OpenIODeviceDataIdentifier> {
		val supportedFeatures = mutableListOf<OpenIODeviceDataIdentifier>()
		val fd = Arena.ofConfined().use { tempArena ->
			val pathSeg = tempArena.allocateFrom(path, Charsets.UTF_8)
			var flags = 0
			if (features.contains(FileIOReOpenFeatures.WRITE)) {
				if (features.contains(FileIOReOpenFeatures.READ)) {
					flags = 0x2
					supportedFeatures.add(FileIOReOpenFeatures.READ)
					supportedFeatures.add(FileIOReOpenFeatures.WRITE)
				} else {
					flags = 0x1
					supportedFeatures.add(FileIOReOpenFeatures.WRITE)
				}
			}
			if (features.contains(StandardIOOpenFeatures.CREATE)) {
				flags = flags or 0x40
				supportedFeatures.add(StandardIOOpenFeatures.CREATE)
			}
			if (features.contains(StandardIOOpenFeatures.DIRECTORY)) TODO("Need directory support, linux")
			val fd = nativeOpen!!.invokeExact(
				capturedStateSegment,
				pathSeg,
				flags,
				0x180 // TODO ... Permissions
			) as Int
			if (fd == -1) throwLastErrno()
			fd
		}
		val device = LinuxIODevice(fd)
		device.features.add(
			IODeviceReleaseFeature(
				ImplementationSource.SYSTEM_NATIVE,
				{
					val status = nativeClose!!.invokeExact(capturedStateSegment, fd) as Int
					if (status == -1) throwLastErrno()
				}
			)
		)
		supportedFeatures.add(device)
		return supportedFeatures
	}
}