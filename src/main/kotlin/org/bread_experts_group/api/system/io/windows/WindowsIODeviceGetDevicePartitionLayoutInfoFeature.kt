package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsIODevice
import org.bread_experts_group.api.system.io.feature.IODeviceGetDevicePartitionLayoutInfoFeature
import org.bread_experts_group.api.system.io.partition_layout_info.IODeviceGetPartitionLayoutInfoDataIdentifier
import org.bread_experts_group.api.system.io.partition_layout_info.IODeviceGetPartitionLayoutInfoFeatureIdentifier
import org.bread_experts_group.api.system.io.partition_layout_info.WindowsDriveLayout
import org.bread_experts_group.api.system.io.partition_layout_info.WindowsPartitionInformation
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.ioctl.*
import java.lang.foreign.MemorySegment

class WindowsIODeviceGetDevicePartitionLayoutInfoFeature(
	private val device: WindowsIODevice
) : IODeviceGetDevicePartitionLayoutInfoFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	fun getLayoutInfoBuffer(): MemorySegment? {
		var s = 0
		while (true) {
			val outputBuffer = autoArena.allocate(
				DRIVE_LAYOUT_INFORMATION_EX.byteSize() + (PARTITION_INFORMATION_EX.byteSize() * (++s))
			)
			val status = nativeDeviceIoControl!!.invokeExact(
				capturedStateSegment,
				device.handle,
				IOCTL_DISK_GET_DRIVE_LAYOUT_EX,
				MemorySegment.NULL, 0,
				outputBuffer, outputBuffer.byteSize().toInt(),
				threadLocalDWORD0,
				MemorySegment.NULL
			) as Int
			if (status != 0) return outputBuffer
			if (win32LastError != WindowsLastError.ERROR_INSUFFICIENT_BUFFER.id.toInt()) break
		}
		return null
	}

	override fun supported(): Boolean = nativeDeviceIoControl != null && getLayoutInfoBuffer() != null

	override fun get(
		vararg features: IODeviceGetPartitionLayoutInfoFeatureIdentifier
	): List<IODeviceGetPartitionLayoutInfoDataIdentifier> {
		val output = getLayoutInfoBuffer() ?: throwLastError()
		val data = mutableListOf<IODeviceGetPartitionLayoutInfoDataIdentifier>()
		val slice = DRIVE_LAYOUT_INFORMATION_EX_DUMMYUNIONNAME.invokeExact(output, 0L) as MemorySegment
		when (DRIVE_LAYOUT_INFORMATION_EX_PartitionStyle.get(output, 0L) as Int) {
			WindowsPartitionTypes.PARTITION_STYLE_GPT.id.toInt() -> data.add(
				WindowsDriveLayout.GPT(
					GUID(
						DRIVE_LAYOUT_INFORMATION_GPT_DiskId.invokeExact(slice, 0L) as MemorySegment
					),
					DRIVE_LAYOUT_INFORMATION_GPT_StartingUsableOffset.get(slice, 0L) as Long,
					DRIVE_LAYOUT_INFORMATION_GPT_UsableLength.get(slice, 0L) as Long,
					DRIVE_LAYOUT_INFORMATION_GPT_MaxPartitionCount.get(slice, 0L) as Int,
					Array(DRIVE_LAYOUT_INFORMATION_EX_PartitionCount.get(output, 0L) as Int) {
						val slice = output.asSlice(
							DRIVE_LAYOUT_INFORMATION_EX.byteSize() + (PARTITION_INFORMATION_EX.byteSize() * it),
							PARTITION_INFORMATION_EX.byteSize(),
							8
						)
						val data = slice.asSlice(PARTITION_INFORMATION_EX_DUMMYUNIONNAME)
						(PARTITION_INFORMATION_EX_PartitionOrdinal.get(
							slice, 0L
						) as Short).toUShort() to WindowsPartitionInformation.GPT(
							PARTITION_INFORMATION_EX_StartingOffset.get(slice, 0L) as Long,
							PARTITION_INFORMATION_EX_PartitionLength.get(slice, 0L) as Long,
							PARTITION_INFORMATION_EX_PartitionNumber.get(slice, 0L) as Int,
							PARTITION_INFORMATION_EX_RewritePartition.get(slice, 0L) as Int != 0,
							PARTITION_INFORMATION_EX_IsServicePartition.get(slice, 0L) as Int != 0,
							GUID(
								PARTITION_INFORMATION_GPT_PartitionType.invokeExact(data, 0L) as MemorySegment
							),
							GUID(
								PARTITION_INFORMATION_GPT_PartitionId.invokeExact(data, 0L) as MemorySegment
							),
							PARTITION_INFORMATION_GPT_Attributes.get(data, 0L) as Long,
							(PARTITION_INFORMATION_GPT_Name.invokeExact(data, 0L) as MemorySegment)
								.getString(0, winCharsetWide)
						)
					}.toMap()
				)
			)

			WindowsPartitionTypes.PARTITION_STYLE_MBR.id.toInt() -> data.add(
				WindowsDriveLayout.MBR(
					(DRIVE_LAYOUT_INFORMATION_MBR_PartitionType.get(slice, 0L) as Byte).toUByte(),
					DRIVE_LAYOUT_INFORMATION_MBR_BootIndicator.get(slice, 0L) as Int != 0,
					DRIVE_LAYOUT_INFORMATION_MBR_RecognizedPartition.get(slice, 0L) as Int != 0,
					DRIVE_LAYOUT_INFORMATION_MBR_HiddenSectors.get(slice, 0L) as Int,
					GUID(
						DRIVE_LAYOUT_INFORMATION_MBR_PartitionId.invokeExact(slice, 0L) as MemorySegment
					),
					Array(DRIVE_LAYOUT_INFORMATION_EX_PartitionCount.get(output, 0L) as Int) {
						val slice = output.asSlice(
							DRIVE_LAYOUT_INFORMATION_EX.byteSize() + (PARTITION_INFORMATION_EX.byteSize() * it),
							PARTITION_INFORMATION_EX.byteSize(),
							8
						)
						val data = slice.asSlice(PARTITION_INFORMATION_EX_DUMMYUNIONNAME)
						(PARTITION_INFORMATION_EX_PartitionOrdinal.get(
							slice, 0L
						) as Short).toUShort() to WindowsPartitionInformation.MBR(
							PARTITION_INFORMATION_EX_StartingOffset.get(slice, 0L) as Long,
							PARTITION_INFORMATION_EX_PartitionLength.get(slice, 0L) as Long,
							PARTITION_INFORMATION_EX_PartitionNumber.get(slice, 0L) as Int,
							PARTITION_INFORMATION_EX_RewritePartition.get(slice, 0L) as Int != 0,
							PARTITION_INFORMATION_EX_IsServicePartition.get(slice, 0L) as Int != 0,
							(PARTITION_INFORMATION_MBR_PartitionType.get(data, 0L) as Byte).toUByte(),
							(PARTITION_INFORMATION_MBR_BootIndicator.get(data, 0L) as Int) != 0,
							(PARTITION_INFORMATION_MBR_RecognizedPartition.get(data, 0L) as Int) != 0,
							PARTITION_INFORMATION_MBR_HiddenSectors.get(data, 0L) as Int,
							GUID(
								PARTITION_INFORMATION_MBR_PartitionId.invokeExact(data, 0L) as MemorySegment
							)
						)
					}.toMap()
				)
			)

			WindowsPartitionTypes.PARTITION_STYLE_RAW.id.toInt() -> data.add(
				WindowsDriveLayout.RAW
			)

			else -> {}
		}
		return data
	}
}