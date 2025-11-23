package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable

enum class WindowsFileInfoByHandleClasses(
	override val id: UInt
) : Mappable<WindowsFileInfoByHandleClasses, UInt> {
	FileBasicInfo(0u);//,
//	FileStandardInfo(1u),
//	FileNameInfo(2u),
//	FileRenameInfo(3u),
//	FileDispositionInfo(4u),
//	FileAllocationInfo(5u),
//	FileEndOfFileInfo(6u),
//	FileStreamInfo(7u),
//	FileCompressionInfo(8u),
//	FileAttributeTagInfo(9u),
//	FileIdBothDirectoryInfo(10u),
//	FileIdBothDirectoryRestartInfo(11u),
//	FileIoPriorityHintInfo(12u),
//	FileRemoteProtocolInfo(13u),
//	FileFullDirectoryInfo(14u),
//	FileFullDirectoryRestartInfo(15u),
//	FileStorageInfo(16u),
//	FileAlignmentInfo(17u),
//	FileIdInfo(18u),
//	FileIdExtdDirectoryInfo(19u),
//	FileIdExtdDirectoryRestartInfo(20u),
//	FileDispositionInfoEx(21u),
//	FileRenameInfoEx(22u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val FILE_INFO_BY_HANDLE_CLASS = DWORD