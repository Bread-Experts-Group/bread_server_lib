@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.feature.move.feature.routine

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.move.feature.routine.feature.MoveProgressRoutineNumericBytesFeature
import org.bread_experts_group.api.system.device.feature.move.feature.routine.feature.MoveProgressRoutineSystemIdentifierFeature

object MoveProgressRoutineFeatures {
	val TOTAL_SIZE_BYTES = object : FeatureExpression<MoveProgressRoutineNumericBytesFeature> {
		override val name: String = "Total Size, bytes"
	}

	val TOTAL_TRANSFERRED_BYTES = object : FeatureExpression<MoveProgressRoutineNumericBytesFeature> {
		override val name: String = "Total Transferred, bytes"
	}

	val FILE_STREAM_TOTAL_SIZE_BYTES = object : FeatureExpression<MoveProgressRoutineNumericBytesFeature> {
		override val name: String = "File Stream Total Size, bytes"
	}

	val FILE_STREAM_TOTAL_TRANSFERRED_BYTES = object : FeatureExpression<MoveProgressRoutineNumericBytesFeature> {
		override val name: String = "File Stream Total Transferred, bytes"
	}

	val FILE_STREAM_SYSTEM_IDENTIFIER = object : FeatureExpression<MoveProgressRoutineSystemIdentifierFeature> {
		override val name: String = "File Stream System Identifier"
	}

	val CALL_REASON_SYSTEM_IDENTIFIER = object : FeatureExpression<MoveProgressRoutineSystemIdentifierFeature> {
		override val name: String = "Call Reason System Identifier"
	}
}