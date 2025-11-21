package org.bread_experts_group.api.system.device.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import java.lang.AutoCloseable

abstract class IODeviceReleaseFeature : IODeviceFeatureImplementation<IODeviceReleaseFeature>(), AutoCloseable {
	override val expresses: FeatureExpression<IODeviceReleaseFeature> = IODeviceFeatures.RELEASE
}