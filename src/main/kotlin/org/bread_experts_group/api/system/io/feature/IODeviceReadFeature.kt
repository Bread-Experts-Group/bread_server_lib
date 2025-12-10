package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.ReceiveFeature
import org.bread_experts_group.api.system.io.receive.IOReceiveDataIdentifier
import org.bread_experts_group.api.system.io.receive.IOReceiveFeatureIdentifier

abstract class IODeviceReadFeature : IODeviceFeatureImplementation<IODeviceReadFeature>(),
	ReceiveFeature<IOReceiveFeatureIdentifier, IOReceiveDataIdentifier> {
	override val expresses: FeatureExpression<IODeviceReadFeature> = IODeviceFeatures.READ
}