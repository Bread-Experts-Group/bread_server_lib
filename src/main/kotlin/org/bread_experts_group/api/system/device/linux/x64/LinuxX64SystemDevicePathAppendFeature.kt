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

	override fun append(element: String): SystemDevice {
		val path = pathSegment.getString(0, Charsets.UTF_8).split('/').toMutableList()
		if (path.firstOrNull()?.isBlank() == true) path.removeFirst()
		element.split('/').forEachIndexed { i, pathElement ->
			val trimmedElement = pathElement.trim()
			if (trimmedElement.isNotEmpty()) {
				if (trimmedElement == "..") path.removeLast()
				else if (trimmedElement != ".") path.add(trimmedElement)
			} else if (i == 0) {
				path.clear()
			}
		}
		return linuxX64CreatePathDevice(
			autoArena.allocateFrom(
				path.joinToString("/", "/"),
				Charsets.UTF_8
			)
		)
	}
}