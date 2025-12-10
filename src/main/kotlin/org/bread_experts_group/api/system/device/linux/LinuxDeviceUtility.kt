package org.bread_experts_group.api.system.device.linux

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceBasicIdentifierFeature

fun linuxCreatePathDevice(
	path: String
): SystemDevice = SystemDevice(SystemDeviceType.FILE_SYSTEM_ENTRY).also {
	it.features.add(
		SystemDeviceBasicIdentifierFeature(
			ImplementationSource.SYSTEM_NATIVE,
			SystemDeviceFeatures.SYSTEM_IDENTIFIER,
			path
		)
	)
	it.features.add(LinuxSystemDeviceIODeviceFeature(path))
	it.features.add(LinuxSystemDevicePathAppendFeature(path))
}