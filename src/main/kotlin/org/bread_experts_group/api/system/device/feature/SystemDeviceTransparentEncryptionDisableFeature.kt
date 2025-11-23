@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.transparent_encrypt.DisableTransparentEncryptionSystemDeviceFeatureIdentifier

abstract class SystemDeviceTransparentEncryptionDisableFeature :
	SystemDeviceFeatureImplementation<SystemDeviceTransparentEncryptionDisableFeature>() {
	override val expresses: FeatureExpression<SystemDeviceTransparentEncryptionDisableFeature> =
		SystemDeviceFeatures.PATH_DISABLE_TRANSPARENT_ENCRYPT

	abstract fun disable(
		vararg features: DisableTransparentEncryptionSystemDeviceFeatureIdentifier
	): List<DisableTransparentEncryptionSystemDeviceFeatureIdentifier>
}