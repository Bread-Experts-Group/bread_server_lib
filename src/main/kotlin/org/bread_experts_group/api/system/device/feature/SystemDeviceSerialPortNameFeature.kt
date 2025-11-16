package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

class SystemDeviceSerialPortNameFeature(
	val name: String,
	override val source: ImplementationSource
) : SystemDeviceFeatureImplementation<SystemDeviceSerialPortNameFeature>() {
	override val expresses: FeatureExpression<SystemDeviceSerialPortNameFeature> =
		SystemDeviceFeatures.SERIAL_PORT_NAME

	override fun supported(): Boolean = true
}