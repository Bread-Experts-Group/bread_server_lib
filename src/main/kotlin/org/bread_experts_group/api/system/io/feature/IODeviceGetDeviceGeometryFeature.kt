package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.geometry.IODeviceGetGeometryDataIdentifier
import org.bread_experts_group.api.system.io.geometry.IODeviceGetGeometryFeatureIdentifier

abstract class IODeviceGetDeviceGeometryFeature : IODeviceFeatureImplementation<IODeviceGetDeviceGeometryFeature>() {
	override val expresses: FeatureExpression<IODeviceGetDeviceGeometryFeature> = IODeviceFeatures.GET_DEVICE_GEOMETRY

	abstract fun get(
		vararg features: IODeviceGetGeometryFeatureIdentifier
	): List<IODeviceGetGeometryDataIdentifier>
}