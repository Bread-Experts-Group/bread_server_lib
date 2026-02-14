package org.bread_experts_group.project_incubator.sim2

sealed interface SegmentInformation {
	val base: UInt
	val limit: UInt
	val privilegeLevel: UByte
	val present: Boolean
	val systemAvailable: Boolean
	val operationSize32: Boolean
	val granularity: Boolean

	data class Code(
		override val base: UInt,
		override val limit: UInt,
		override val privilegeLevel: UByte,
		override val present: Boolean,
		override val systemAvailable: Boolean,
		override val operationSize32: Boolean,
		override val granularity: Boolean,
		val conforming: Boolean,
		val read: Boolean,
		val accessed: Boolean
	) : SegmentInformation

	data class Data(
		override val base: UInt,
		override val limit: UInt,
		override val privilegeLevel: UByte,
		override val present: Boolean,
		override val systemAvailable: Boolean,
		override val operationSize32: Boolean,
		override val granularity: Boolean,
		val expandDown: Boolean,
		val write: Boolean,
		val accessed: Boolean
	) : SegmentInformation
}