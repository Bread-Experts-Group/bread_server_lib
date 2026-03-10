package org.bread_experts_group.api.system.io.partition_layout_info

import org.bread_experts_group.ffi.GUID

sealed class WindowsPartitionInformation {
	abstract val startingOffset: Long
	abstract val partitionLength: Long
	abstract val partitionNumber: Int
	abstract val rewritable: Boolean
	abstract val service: Boolean

	data class GPT(
		override val startingOffset: Long,
		override val partitionLength: Long,
		override val partitionNumber: Int,
		override val rewritable: Boolean,
		override val service: Boolean,
		val partitionType: GUID,
		val partitionID: GUID,
		val attributes: Long,
		val name: String
	) : WindowsPartitionInformation()

	data class MBR(
		override val startingOffset: Long,
		override val partitionLength: Long,
		override val partitionNumber: Int,
		override val rewritable: Boolean,
		override val service: Boolean,
		val partitionType: UByte,
		val bootable: Boolean,
		val recognized: Boolean,
		val hiddenSectors: Int,
		val partitionID: GUID
	) : WindowsPartitionInformation()
}