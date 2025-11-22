package org.bread_experts_group.api.system.device.feature.move.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.feature.move.MoveHandleFeatures
import org.bread_experts_group.api.system.device.feature.move.SystemDeviceMoveFeatureImplementation
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeature
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatureImplementation

abstract class SystemDeviceMoveProgressRoutineFeature :
	SystemDeviceMoveFeatureImplementation<SystemDeviceMoveProgressRoutineFeature>() {
	override val expresses: FeatureExpression<SystemDeviceMoveProgressRoutineFeature> =
		MoveHandleFeatures.MOVE_PROGRESS_ROUTINE

	var routine: ((FeatureProvider<MoveProgressRoutineFeatureImplementation<*>>) -> List<MoveProgressRoutineFeature>)? =
		null
}