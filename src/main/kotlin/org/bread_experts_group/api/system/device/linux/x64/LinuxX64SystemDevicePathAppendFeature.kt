package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDevicePathAppendFeature
import org.bread_experts_group.ffi.autoArena
import java.lang.foreign.MemorySegment

class LinuxX64SystemDevicePathAppendFeature(
	private val pathSegment: MemorySegment
) : SystemDevicePathAppendFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	override fun append(element: String): SystemDevice = linuxX64CreatePathDevice(
		linuxX64AppendPaths(
			pathSegment,
			autoArena.allocateFrom(element, Charsets.UTF_8),
			autoArena
		)
	)
}