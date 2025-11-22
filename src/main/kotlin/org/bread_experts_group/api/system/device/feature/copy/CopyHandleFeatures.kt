package org.bread_experts_group.api.system.device.feature.copy

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.copy.feature.SystemDeviceCopyProgressRoutineFeature

object CopyHandleFeatures {
	val COPY_PROGRESS_ROUTINE = object : FeatureExpression<SystemDeviceCopyProgressRoutineFeature> {
		override val name: String = "Copy Progress Routine"
	}
}