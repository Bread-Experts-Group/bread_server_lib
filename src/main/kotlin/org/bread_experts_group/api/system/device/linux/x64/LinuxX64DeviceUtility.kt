package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceBasicIdentifierFeature
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

fun linuxX64CreatePathDevice(
	segment: MemorySegment
): SystemDevice = SystemDevice(SystemDeviceType.FILE_SYSTEM_ENTRY).also {
	val safeSegment = segment.asReadOnly()
	it.features.add(
		SystemDeviceBasicIdentifierFeature(
			ImplementationSource.SYSTEM_NATIVE,
			SystemDeviceFeatures.SYSTEM_IDENTIFIER,
			safeSegment.getString(0, Charsets.UTF_8)
		)
	)
	it.features.add(LinuxX64SystemDevicePathAppendFeature(safeSegment))
	it.features.add(LinuxX64SystemDeviceIODeviceFeature(safeSegment))
	it.features.add(LinuxX64SystemDeviceChildrenFeature(safeSegment))
}

fun linuxX64AppendPaths(
	a: MemorySegment,
	b: MemorySegment,
	arena: Arena
): MemorySegment = arena.allocateFrom(
	linuxX64AppendPaths(
		a.getString(0, Charsets.UTF_8),
		b.getString(0, Charsets.UTF_8)
	),
	Charsets.UTF_8
)

fun linuxX64AppendPaths(
	a: String,
	b: String
): String {
	val path = a.split('/').toMutableList()
	if (path.firstOrNull()?.isBlank() == true) path.removeFirst()
	b.split('/').forEachIndexed { i, pathElement ->
		val trimmedElement = pathElement.trim()
		if (trimmedElement.isNotEmpty()) {
			if (trimmedElement == "..") path.removeLast()
			else if (trimmedElement != ".") path.add(trimmedElement)
		} else if (i == 0) {
			path.clear()
		}
	}
	return path.joinToString("/", "/")
}