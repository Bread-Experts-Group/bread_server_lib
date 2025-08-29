package org.bread_experts_group.vfs.windows

import org.bread_experts_group.ffi.win32GUID
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val prjCallbacks: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("StartDirectoryEnumerationCallback"),
	ValueLayout.ADDRESS.withName("EndDirectoryEnumerationCallback"),
	ValueLayout.ADDRESS.withName("GetDirectoryEnumerationCallback"),
	ValueLayout.ADDRESS.withName("GetPlaceholderInfoCallback"),
	ValueLayout.ADDRESS.withName("GetFileDataCallback"),
	ValueLayout.ADDRESS.withName("QueryFileNameCallback"),
	ValueLayout.ADDRESS.withName("NotificationCallback"),
	ValueLayout.ADDRESS.withName("CancelCommandCallback")
)
val prjCallbacksSDEC: VarHandle = prjCallbacks.varHandle(groupElement("StartDirectoryEnumerationCallback"))
val prjCallbacksEDEC: VarHandle = prjCallbacks.varHandle(groupElement("EndDirectoryEnumerationCallback"))
val prjCallbacksGDEC: VarHandle = prjCallbacks.varHandle(groupElement("GetDirectoryEnumerationCallback"))
val prjCallbacksGPIC: VarHandle = prjCallbacks.varHandle(groupElement("GetPlaceholderInfoCallback"))
val prjCallbacksGFDC: VarHandle = prjCallbacks.varHandle(groupElement("GetFileDataCallback"))
val prjCallbacksQFNC: VarHandle = prjCallbacks.varHandle(groupElement("QueryFileNameCallback"))
val prjCallbacksNC: VarHandle = prjCallbacks.varHandle(groupElement("NotificationCallback"))
val prjCallbacksCCC: VarHandle = prjCallbacks.varHandle(groupElement("CancelCommandCallback"))

val prjCallbackData: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("Size"),
	ValueLayout.JAVA_INT.withName("Flags"),
	ValueLayout.ADDRESS.withName("NamespaceVirtualizationContext"),
	ValueLayout.JAVA_INT.withName("CommandId"),
	win32GUID.withName("FileId"),
	win32GUID.withName("DataStreamId"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("FilePathName"),
	ValueLayout.ADDRESS.withName("VersionInfo"),
	ValueLayout.JAVA_INT.withName("TriggeringProcessId"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("TriggeringProcessImageFileName"),
	ValueLayout.ADDRESS.withName("InstanceContext"),
)
val prjCallbackDataFlags: VarHandle = prjCallbackData.varHandle(groupElement("Flags"))
val prjCallbackDataNamespaceVirtualizationContext: VarHandle = prjCallbackData.varHandle(
	groupElement("NamespaceVirtualizationContext")
)
val prjCallbackDataDataStreamID: MethodHandle = prjCallbackData.sliceHandle(groupElement("DataStreamId"))
val prjCallbackDataFilePathName: VarHandle = prjCallbackData.varHandle(groupElement("FilePathName"))
val prjCallbackDataTriggeringProcessImageFileName: VarHandle = prjCallbackData.varHandle(
	groupElement("TriggeringProcessImageFileName")
)

val prjFileBasicInfo: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_BOOLEAN.withName("IsDirectory"),
	MemoryLayout.paddingLayout(7),
	ValueLayout.JAVA_LONG.withName("FileSize"),
	ValueLayout.JAVA_LONG.withName("CreationTime"),
	ValueLayout.JAVA_LONG.withName("LastAccessTime"),
	ValueLayout.JAVA_LONG.withName("LastWriteTime"),
	ValueLayout.JAVA_LONG.withName("ChangeTime"),
	ValueLayout.JAVA_INT.withName("FileAttributes")
)
val prjFileBasicInfoIsDirectory: VarHandle = prjFileBasicInfo.varHandle(groupElement("IsDirectory"))
val prjFileBasicInfoFileSize: VarHandle = prjFileBasicInfo.varHandle(groupElement("FileSize"))
val prjFileBasicInfoCreationTime: VarHandle = prjFileBasicInfo.varHandle(groupElement("CreationTime"))
val prjFileBasicInfoAccessTime: VarHandle = prjFileBasicInfo.varHandle(groupElement("LastAccessTime"))
val prjFileBasicInfoWriteTime: VarHandle = prjFileBasicInfo.varHandle(groupElement("LastWriteTime"))
val prjFileBasicInfoChangeTime: VarHandle = prjFileBasicInfo.varHandle(groupElement("ChangeTime"))

val prjPlaceholderInfoEaInformation: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("EaBufferSize"),
	ValueLayout.JAVA_INT.withName("OffsetToFirstEa")
)
val prjPlaceholderInfoSecurityInformation: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("SecurityBufferSize"),
	ValueLayout.JAVA_INT.withName("OffsetToSecurityDescriptor")
)
val prjPlaceholderInfoStreamsInformation: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("StreamsInfoBufferSize"),
	ValueLayout.JAVA_INT.withName("OffsetToFirstStreamInfo")
)
val prjPlaceholderVersionInfo: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(128, ValueLayout.JAVA_BYTE).withName("ProviderID"),
	MemoryLayout.sequenceLayout(128, ValueLayout.JAVA_BYTE).withName("ContentID")
)

val prjPlaceholderInfo: StructLayout = MemoryLayout.structLayout(
	prjFileBasicInfo.withName("FileBasicInfo"),
	prjPlaceholderInfoEaInformation.withName("EaInformation"),
	prjPlaceholderInfoSecurityInformation.withName("SecurityInformation"),
	prjPlaceholderInfoStreamsInformation.withName("StreamsInformation"),
	prjPlaceholderVersionInfo.withName("VersionInfo"),
	MemoryLayout.sequenceLayout(12, ValueLayout.JAVA_BYTE).withName("VariableData"),
)
val prjPlaceholderInfoFileBasicInfo: MethodHandle = prjPlaceholderInfo.sliceHandle(groupElement("FileBasicInfo"))

val prjNotificationMapping: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("NotificationBitMask"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("NotificationRoot")
)
val prjNotificationMappingNotificationBitMask: VarHandle = prjNotificationMapping.varHandle(
	groupElement("NotificationBitMask")
)
val prjNotificationMappingNotificationRoot: VarHandle = prjNotificationMapping.varHandle(
	groupElement("NotificationRoot")
)

val prjStartVirtualizingOptions: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("Flags"),
	ValueLayout.JAVA_INT.withName("PoolThreadCount"),
	ValueLayout.JAVA_INT.withName("ConcurrentThreadCount"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("NotificationMappings"),
	ValueLayout.JAVA_INT.withName("NotificationMappingsCount")
)
val prjStartVirtualizingOptionsNotificationMappings: VarHandle = prjStartVirtualizingOptions.varHandle(
	groupElement("NotificationMappings")
)
val prjStartVirtualizingOptionsNotificationMappingsCount: VarHandle = prjStartVirtualizingOptions.varHandle(
	groupElement("NotificationMappingsCount")
)