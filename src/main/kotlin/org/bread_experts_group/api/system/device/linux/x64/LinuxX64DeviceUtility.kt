package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceBasicIdentifierFeature
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
}