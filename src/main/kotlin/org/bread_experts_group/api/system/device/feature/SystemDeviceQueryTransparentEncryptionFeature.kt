@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.transparent_encrypt.TransparentEncryptionSystemDeviceStatusIdentifier

abstract class SystemDeviceQueryTransparentEncryptionFeature :
	SystemDeviceFeatureImplementation<SystemDeviceQueryTransparentEncryptionFeature>() {
	override val expresses: FeatureExpression<SystemDeviceQueryTransparentEncryptionFeature> =
		SystemDeviceFeatures.PATH_QUERY_TRANSPARENT_ENCRYPT

	abstract fun query(): List<TransparentEncryptionSystemDeviceStatusIdentifier>
}