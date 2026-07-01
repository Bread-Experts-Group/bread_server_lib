package org.bread_experts_group.api.system.io.partition_layout_info

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class WindowsPartitionInformation {
	abstract val startingOffset: Long
	abstract val partitionLength: Long
	abstract val partitionNumber: Int
	abstract val rewritable: Boolean
	abstract val service: Boolean

	data class GPT @OptIn(ExperimentalUuidApi::class) constructor(
		override val startingOffset: Long,
		override val partitionLength: Long,
		override val partitionNumber: Int,
		override val rewritable: Boolean,
		override val service: Boolean,
		val partitionType: Uuid,
		val partitionID: Uuid,
		val attributes: Long,
		val name: String
	) : WindowsPartitionInformation()

	data class MBR @OptIn(ExperimentalUuidApi::class) constructor(
		override val startingOffset: Long,
		override val partitionLength: Long,
		override val partitionNumber: Int,
		override val rewritable: Boolean,
		override val service: Boolean,
		val partitionType: UByte,
		val bootable: Boolean,
		val recognized: Boolean,
		val hiddenSectors: Int,
		val partitionID: Uuid
	) : WindowsPartitionInformation()
}