package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.device.io.IODevice

abstract class SystemProjectedFileHierarchyFeature :
	SystemFeatureImplementation<SystemProjectedFileHierarchyFeature>() {
	override val expresses: FeatureExpression<SystemProjectedFileHierarchyFeature> =
		SystemFeatures.PROJECTED_FILE_HIERARCHY

	abstract fun open(into: IODevice)
}