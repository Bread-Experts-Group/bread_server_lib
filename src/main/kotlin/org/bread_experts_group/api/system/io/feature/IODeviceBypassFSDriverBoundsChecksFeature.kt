package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures

abstract class IODeviceBypassFSDriverBoundsChecksFeature :
	IODeviceFeatureImplementation<IODeviceBypassFSDriverBoundsChecksFeature>() {
	override val expresses: FeatureExpression<IODeviceBypassFSDriverBoundsChecksFeature> =
		IODeviceFeatures.BYPASS_FS_DRIVER_BOUNDS_CHECKS

	abstract fun bypass()
}