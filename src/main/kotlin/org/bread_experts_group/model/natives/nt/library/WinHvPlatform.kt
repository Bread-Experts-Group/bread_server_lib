package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.c.int_t
import org.bread_experts_group.model.natives.nt.datatype.*
import java.lang.foreign.MemorySegment

@Suppress("FunctionName")
@LookupBacked("WinHvPlatform.dll")
abstract class WinHvPlatform internal constructor() : Library {
	// TODO: Windows date
	abstract fun WHvCreatePartition(Partition: Pointer<WHV_PARTITION_HANDLE>): HRESULT
	abstract fun WHvSetupPartition(Partition: WHV_PARTITION_HANDLE): HRESULT

	abstract fun WHvSetPartitionProperty(
		Partition: WHV_PARTITION_HANDLE,
		PropertyCode: WHV_PARTITION_PROPERTY_CODE,
		PropertyBuffer: MemorySegment,
		PropertyBufferSizeInBytes: UINT32
	): HRESULT

	abstract fun WHvMapGpaRange(
		Partition: WHV_PARTITION_HANDLE,
		SourceAddress: MemorySegment, GuestAddress: WHV_GUEST_PHYSICAL_ADDRESS, SizeInBytes: UINT64,
		Flags: int_t // tODO IndexedEnumSet<WHV_MAP_GPA_RANGE_FLAGS>
	): HRESULT

	abstract fun WHvUnmapGpaRange(
		Partition: WHV_PARTITION_HANDLE,
		GuestAddress: WHV_GUEST_PHYSICAL_ADDRESS, SizeInBytes: UINT64
	): HRESULT

	abstract fun WHvTranslateGva(
		Partition: WHV_PARTITION_HANDLE, VpIndex: UINT32,
		Gva: WHV_GUEST_VIRTUAL_ADDRESS, TranslateFlags: int_t /* TODO WHV_TRANSLATE_GVA_FLAGS */,
		TranslationResult: MemorySegment /* TODO: WHV_TRANSLATE_GVA_RESUTL */,
		Gpa: MemorySegment /* TODO: WHV_GUEST_PHYSICAL_ADDRESS */
	): HRESULT

	abstract fun WHvCreateVirtualProcessor(Partition: WHV_PARTITION_HANDLE, VpIndex: UINT32, Flags: UINT32): HRESULT
	abstract fun WHvRunVirtualProcessor(
		Partition: WHV_PARTITION_HANDLE, VpIndex: UINT32,
		ExitContext: MemorySegment, ExitContextSizeInBytes: UINT32
	): HRESULT

	abstract fun WHvGetVirtualProcessorRegisters(
		Partition: WHV_PARTITION_HANDLE, VpIndex: UINT32,
		RegisterNames: MemorySegment, RegisterCount: UINT32,
		RegisterValues: MemorySegment
		// TODO RegisterNames: NativeArray<WHV_REGISTER_NAME>, RegisterCount: UINT32,
		// TODO RegisterValues: NativeArray<WHV_REGISTER_VALUE>
	): HRESULT

	abstract fun WHvSetVirtualProcessorRegisters(
		Partition: WHV_PARTITION_HANDLE, VpIndex: UINT32,
		RegisterNames: MemorySegment, RegisterCount: UINT32,
		RegisterValues: MemorySegment
		// TODO RegisterNames: NativeArray<WHV_REGISTER_NAME>, RegisterCount: UINT32,
		// TODO RegisterValues: NativeArray<WHV_REGISTER_VALUE>
	): HRESULT
}