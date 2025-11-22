package org.bread_experts_group.api.system.device.feature.move

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.move.feature.SystemDeviceMoveProgressRoutineFeature

object MoveHandleFeatures {
	val MOVE_PROGRESS_ROUTINE = object : FeatureExpression<SystemDeviceMoveProgressRoutineFeature> {
		override val name: String = "Move Progress Routine"
	}
}