package org.bread_experts_group.api.system.device.enumerate

import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.type.SystemDeviceTypeIdentifier

abstract class SystemDeviceIterator(
	val type: SystemDeviceTypeIdentifier
) : SystemDeviceEnumerationDataIdentifier, Iterator<SystemDevice>