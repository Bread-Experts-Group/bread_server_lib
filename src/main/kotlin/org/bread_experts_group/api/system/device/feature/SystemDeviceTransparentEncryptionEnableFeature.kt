@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.transparent_encrypt.EnableTransparentEncryptionSystemDeviceFeatureIdentifier

abstract class SystemDeviceTransparentEncryptionEnableFeature :
	SystemDeviceFeatureImplementation<SystemDeviceTransparentEncryptionEnableFeature>() {
	override val expresses: FeatureExpression<SystemDeviceTransparentEncryptionEnableFeature> =
		SystemDeviceFeatures.PATH_ENABLE_TRANSPARENT_ENCRYPT

	abstract fun enable(
		vararg features: EnableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<EnableTransparentEncryptionSystemDeviceFeatureIdentifier>
}