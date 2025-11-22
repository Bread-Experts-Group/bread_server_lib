package org.bread_experts_group.api.system.device.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatureImplementation
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

abstract class SystemDeviceChildrenStreamsFeature :
	SystemDeviceFeatureImplementation<SystemDeviceChildrenStreamsFeature>(), Iterable<SystemDevice> {
	override val expresses: FeatureExpression<SystemDeviceChildrenStreamsFeature> =
		SystemDeviceFeatures.CHILDREN_STREAMS
}