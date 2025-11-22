package org.bread_experts_group.api.system.device.feature.copy.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.feature.copy.CopyHandleFeatures
import org.bread_experts_group.api.system.device.feature.copy.SystemDeviceCopyFeatureImplementation
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeature
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeatureImplementation

abstract class SystemDeviceCopyProgressRoutineFeature :
	SystemDeviceCopyFeatureImplementation<SystemDeviceCopyProgressRoutineFeature>() {
	override val expresses: FeatureExpression<SystemDeviceCopyProgressRoutineFeature> =
		CopyHandleFeatures.COPY_PROGRESS_ROUTINE

	var routine: ((FeatureProvider<CopyProgressRoutineFeatureImplementation<*>>) -> List<CopyProgressRoutineFeature>)? =
		null
}