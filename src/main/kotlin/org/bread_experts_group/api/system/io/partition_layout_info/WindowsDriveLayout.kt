package org.bread_experts_group.api.system.io.partition_layout_info

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class WindowsDriveLayout : IODeviceGetPartitionLayoutInfoDataIdentifier {
	data class GPT @OptIn(ExperimentalUuidApi::class) constructor(
		val diskID: Uuid,
		val startingUsableOffset: Long,
		val usableLength: Long,
		val maxPartitionCount: Int,
		val partitions: Map<UShort, WindowsPartitionInformation.GPT>
	) : WindowsDriveLayout()

	data class MBR @OptIn(ExperimentalUuidApi::class) constructor(
		val partitionType: UByte,
		val bootable: Boolean,
		val recognized: Boolean,
		val hiddenSectors: Int,
		val partitionID: Uuid,
		val partitions: Map<UShort, WindowsPartitionInformation.MBR>
	) : WindowsDriveLayout()

	object RAW : WindowsDriveLayout()
}