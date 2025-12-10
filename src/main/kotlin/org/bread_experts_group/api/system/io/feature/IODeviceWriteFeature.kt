package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.SendFeature
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier

abstract class IODeviceWriteFeature : IODeviceFeatureImplementation<IODeviceWriteFeature>(),
	SendFeature<IOSendFeatureIdentifier, IOSendDataIdentifier> {
	override val expresses: FeatureExpression<IODeviceWriteFeature> = IODeviceFeatures.WRITE
}