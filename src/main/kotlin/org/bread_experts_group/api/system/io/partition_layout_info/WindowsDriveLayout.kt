package org.bread_experts_group.api.system.io.partition_layout_info

import org.bread_experts_group.ffi.GUID

sealed class WindowsDriveLayout : IODeviceGetPartitionLayoutInfoDataIdentifier {
	data class GPT(
		val diskID: GUID,
		val startingUsableOffset: Long,
		val usableLength: Long,
		val maxPartitionCount: Int,
		val partitions: Map<UShort, WindowsPartitionInformation.GPT>
	) : WindowsDriveLayout()

	data class MBR(
		val partitionType: UByte,
		val bootable: Boolean,
		val recognized: Boolean,
		val hiddenSectors: Int,
		val partitionID: GUID,
		val partitions: Map<UShort, WindowsPartitionInformation.MBR>
	) : WindowsDriveLayout()

	object RAW : WindowsDriveLayout()
}