package org.bread_experts_group.api.io.feature.serial

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.io.IOFeatureImplementation
import org.bread_experts_group.api.io.IOFeatures
import org.bread_experts_group.api.io.feature.device.IODevice

abstract class IOSerialFeatureImplementation : IOFeatureImplementation<IOSerialFeatureImplementation>() {
	override val expresses: FeatureExpression<IOSerialFeatureImplementation> = IOFeatures.SERIAL
	abstract fun enumerateDevices(): List<IOSerialDeviceEntry>
	abstract fun getDevice(device: IOSerialDeviceEntry): IODevice
}