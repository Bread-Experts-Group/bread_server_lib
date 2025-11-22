@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.feature.copy.feature.routine

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.feature.CopyProgressRoutineNumericBytesFeature
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.feature.CopyProgressRoutineSystemIdentifierFeature

object CopyProgressRoutineFeatures {
	val TOTAL_SIZE_BYTES = object : FeatureExpression<CopyProgressRoutineNumericBytesFeature> {
		override val name: String = "Total Size, bytes"
	}

	val TOTAL_TRANSFERRED_BYTES = object : FeatureExpression<CopyProgressRoutineNumericBytesFeature> {
		override val name: String = "Total Transferred, bytes"
	}

	val CHUNK_SYSTEM_IDENTIFIER = object : FeatureExpression<CopyProgressRoutineSystemIdentifierFeature> {
		override val name: String = "Chunk System Identifier"
	}

	val CHUNK_TOTAL_SIZE_BYTES = object : FeatureExpression<CopyProgressRoutineNumericBytesFeature> {
		override val name: String = "Chunk Total Size, bytes"
	}

	val FILE_STREAM_TOTAL_SIZE_BYTES = object : FeatureExpression<CopyProgressRoutineNumericBytesFeature> {
		override val name: String = "File Stream Total Size, bytes"
	}

	val FILE_STREAM_TOTAL_TRANSFERRED_BYTES = object : FeatureExpression<CopyProgressRoutineNumericBytesFeature> {
		override val name: String = "File Stream Total Transferred, bytes"
	}

	val FILE_STREAM_SYSTEM_IDENTIFIER = object : FeatureExpression<CopyProgressRoutineSystemIdentifierFeature> {
		override val name: String = "File Stream System Identifier"
	}
//
//	val ROUTINE_CALL_REASON = object : FeatureExpression<X> {
//		override val name: String = "Routine Call Reason"
//	}
}