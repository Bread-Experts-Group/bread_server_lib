package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.GUID
import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.PCWSTR
import org.bread_experts_group.model.natives.nt.datatype.projectedfslib.PRJ_CALLBACKS
import org.bread_experts_group.model.natives.nt.datatype.projectedfslib.PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT
import org.bread_experts_group.model.natives.nt.datatype.projectedfslib.PRJ_PLACEHOLDER_VERSION_INFO
import org.bread_experts_group.model.natives.nt.datatype.projectedfslib.PRJ_STARTVIRTUALIZING_OPTIONS
import java.lang.foreign.MemorySegment

@Suppress("FunctionName")
@LookupBacked("ProjectedFSLib.dll")
abstract class ProjectedFSLib internal constructor() : Library {
	//	abstract fun PrjAllocateAlignedBuffer(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		size: size_t
//	): MemorySegment
//
//	abstract fun PrjClearNegativePathCache(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		totalEntryNumber: SegmentReference<UINT32>
//	): HRESULT
//
//	abstract fun PrjCompleteCommand(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		commandId: INT32,
//		completionResult: HRESULT,
//		extendedParameters: PRJ_COMPLETE_COMMAND_EXTENDED_PARAMETERS?
//	): HRESULT
//
//	abstract fun PrjDeleteFile(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		destinationFileName: PCWSTR,
//		updateFlags: int_t,
//		failureReason: SegmentReference<PRJ_UPDATE_FAILURE_CAUSES>?
//	): HRESULT
//
//	abstract fun PrjDoesNameContainWildCards(
//		fileName: LPCWSTR
//	): BOOLEAN
//
//	abstract fun PrjFileNameCompare(
//		fileName1: PCWSTR,
//		fileName2: PCWSTR
//	): int_t
//
//	abstract fun PrjFileNameMatch(
//		fileNameToCheck: PCWSTR,
//		pattern: PCWSTR
//	): BOOLEAN
//
//	abstract fun PrjFillDirEntryBuffer(
//		fileName: PCWSTR,
//		fileBasicInfo: PRJ_FILE_BASIC_INFO?,
//		dirEntryBufferHandle: PRJ_DIR_ENTRY_BUFFER_HANDLE
//	): HRESULT
//
//	abstract fun PrjFillDirEntryBuffer2(
//		dirEntryBufferHandle: PRJ_DIR_ENTRY_BUFFER_HANDLE,
//		fileName: PCWSTR,
//		fileBasicInfo: PRJ_FILE_BASIC_INFO?,
//		extendedInfo: PRJ_EXTENDED_INFO?
//	): HRESULT
//
//	abstract fun PrjFreeAlignedBuffer(
//		buffer: MemorySegment
//	)
//
//	abstract fun PrjGetOnDiskFileState(
//		destinationFileName: PCWSTR,
//		fileState: SegmentReference<PRJ_FILE_STATE>
//	): HRESULT
//
//	abstract fun PrjGetVirtualizationInstanceInfo(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		virtualizationInstanceInfo: SegmentReference<PRJ_VIRTUALIZATION_INSTANCE_INFO>
//	): HRESULT
//
	abstract fun PrjMarkDirectoryAsPlaceholder(
		rootPathName: PCWSTR,
		targetPathName: PCWSTR?,
		versionInfo: PRJ_PLACEHOLDER_VERSION_INFO?,
		virtualizationInstanceID: GUID
	): HRESULT

	//
	abstract fun PrjStartVirtualizing(
		virtualizationRootPath: PCWSTR,
		callbacks: PRJ_CALLBACKS,
		instanceContext: MemorySegment?,
		options: PRJ_STARTVIRTUALIZING_OPTIONS?,
		namespaceVirtualizationContext: Pointer<PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT>
	): HRESULT
//
//	abstract fun PrjStopVirtualizing(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT
//	)
//
//	abstract fun PrjUpdateFileIfNeeded(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		destinationFileName: PCWSTR,
//		placeholderInfo: PRJ_PLACEHOLDER_INFO,
//		placeholderInfoSize: UINT32,
//		updateFlags: PRJ_UPDATE_TYPES,
//		failureReason: SegmentReference<PRJ_UPDATE_FAILURE_CAUSES>
//	): HRESULT
//
//	abstract fun PrjWriteFileData(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		dataStreamId: GUID,
//		buffer: MemorySegment,
//		byteOffset: UINT64,
//		length: UINT32
//	): HRESULT
//
//	abstract fun PrjWritePlaceholderInfo(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		destinationFileName: PCWSTR,
//		placeholderInfo: PRJ_PLACEHOLDER_INFO,
//		placeholderInfoSize: UINT32
//	): HRESULT
//
//	abstract fun PrjWritePlaceholderInfo2(
//		namespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT,
//		destinationFileName: PCWSTR,
//		placeholderInfo: PRJ_PLACEHOLDER_INFO,
//		placeholderInfoSize: UINT32,
//		ExtendedInfo: PRJ_EXTENDED_INFO
//	): HRESULT
}