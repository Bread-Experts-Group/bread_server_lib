package org.bread_experts_group.api.system.device.feature.copy.feature.routine.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeatureImplementation

data class CopyProgressRoutineNumericBytesFeature(
	override val expresses: FeatureExpression<CopyProgressRoutineNumericBytesFeature>,
	override val source: ImplementationSource,
	val bytes: Long
) : CopyProgressRoutineFeatureImplementation<CopyProgressRoutineNumericBytesFeature>()