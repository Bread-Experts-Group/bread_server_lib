package org.bread_experts_group.api.system.device.linux

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDevicePathAppendFeature

class LinuxSystemDevicePathAppendFeature(private val path: String) : SystemDevicePathAppendFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	override fun append(element: String): SystemDevice {
		if (element.isEmpty()) return linuxCreatePathDevice(path)
		val pathSegments = mutableListOf<String>()
		pathSegments.addAll(path.split('/'))
		element.split('/').forEachIndexed { i, segment ->
			when (segment) {
				"" if i == 0 -> pathSegments.clear()
				".." -> pathSegments.removeLast()
				"." -> {}
				else -> pathSegments.add(segment)
			}
		}
		return linuxCreatePathDevice(
			pathSegments.joinToString("/", if (pathSegments.firstOrNull() != "") "/" else "")
		)
	}
}