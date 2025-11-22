package org.bread_experts_group.api.system.device.feature.move.feature.routine.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatureImplementation

data class MoveProgressRoutineNumericBytesFeature(
	override val expresses: FeatureExpression<MoveProgressRoutineNumericBytesFeature>,
	override val source: ImplementationSource,
	val bytes: Long
) : MoveProgressRoutineFeatureImplementation<MoveProgressRoutineNumericBytesFeature>()