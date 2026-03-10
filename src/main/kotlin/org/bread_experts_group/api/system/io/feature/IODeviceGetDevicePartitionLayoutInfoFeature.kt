package org.bread_experts_group.api.system.io.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.partition_layout_info.IODeviceGetPartitionLayoutInfoDataIdentifier
import org.bread_experts_group.api.system.io.partition_layout_info.IODeviceGetPartitionLayoutInfoFeatureIdentifier

abstract class IODeviceGetDevicePartitionLayoutInfoFeature :
	IODeviceFeatureImplementation<IODeviceGetDevicePartitionLayoutInfoFeature>() {
	override val expresses: FeatureExpression<IODeviceGetDevicePartitionLayoutInfoFeature> =
		IODeviceFeatures.GET_DEVICE_PARTITION_LAYOUT

	abstract fun get(
		vararg features: IODeviceGetPartitionLayoutInfoFeatureIdentifier
	): List<IODeviceGetPartitionLayoutInfoDataIdentifier>
}