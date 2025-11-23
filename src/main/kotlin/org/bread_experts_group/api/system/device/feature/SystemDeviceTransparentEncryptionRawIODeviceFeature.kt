package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.transparent_encrpytion.OpenTransparentEncryptionRawIODeviceFeatureIdentifier

abstract class SystemDeviceTransparentEncryptionRawIODeviceFeature :
	SystemDeviceFeatureImplementation<SystemDeviceTransparentEncryptionRawIODeviceFeature>() {
	override val expresses: FeatureExpression<SystemDeviceTransparentEncryptionRawIODeviceFeature> =
		SystemDeviceFeatures.TRANSPARENT_ENCRYPT_RAW_IO_DEVICE

	abstract fun open(
		vararg features: OpenTransparentEncryptionRawIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenTransparentEncryptionRawIODeviceFeatureIdentifier>>?
}