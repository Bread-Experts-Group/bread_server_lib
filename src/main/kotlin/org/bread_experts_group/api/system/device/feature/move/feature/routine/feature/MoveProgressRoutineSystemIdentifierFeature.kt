package org.bread_experts_group.api.system.device.feature.move.feature.routine.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatureImplementation

data class MoveProgressRoutineSystemIdentifierFeature(
	override val expresses: FeatureExpression<MoveProgressRoutineSystemIdentifierFeature>,
	override val source: ImplementationSource,
	val identifier: Any
) : MoveProgressRoutineFeatureImplementation<MoveProgressRoutineSystemIdentifierFeature>()