package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.firmware_info.IODeviceGetFirmwareInfoDataIdentifier
import org.bread_experts_group.api.system.io.firmware_info.IODeviceGetFirmwareInfoFeatureIdentifier

abstract class IODeviceGetDeviceFirmwareInfoFeature :
	IODeviceFeatureImplementation<IODeviceGetDeviceFirmwareInfoFeature>() {
	override val expresses: FeatureExpression<IODeviceGetDeviceFirmwareInfoFeature> =
		IODeviceFeatures.GET_DEVICE_FIRMWARE_INFO

	abstract fun get(
		vararg features: IODeviceGetFirmwareInfoFeatureIdentifier
	): List<IODeviceGetFirmwareInfoDataIdentifier>
}