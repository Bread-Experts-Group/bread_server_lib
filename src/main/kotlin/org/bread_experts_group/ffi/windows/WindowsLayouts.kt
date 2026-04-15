package org.bread_experts_group.ffi.windows

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val GUID: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(16, ValueLayout.JAVA_BYTE)
)

val WNDPROC: AddressLayout = ValueLayout.ADDRESS
val WNDCLASSEXW: StructLayout = MemoryLayout.structLayout(
	UINT.withName("cbSize"),
	UINT.withName("style"),
	WNDPROC.withName("lpfnWndProc"),
	int.withName("cbClsExtra"),
	int.withName("cbWndExtra"),
	HINSTANCE.withName("hInstance"),
	HICON.withName("hIcon"),
	HCURSOR.withName("hCursor"),
	HBRUSH.withName("hbrBackground"),
	LPCWSTR.withName("lpszMenuName"),
	LPCWSTR.withName("lpszClassName"),
	HICON.withName("hIconSm")
)
val WNDCLASSEXW_cbSize: VarHandle = WNDCLASSEXW.varHandle(groupElement("cbSize"))
val WNDCLASSEXW_style: VarHandle = WNDCLASSEXW.varHandle(groupElement("style"))
val WNDCLASSEXW_lpfnWndProc: VarHandle = WNDCLASSEXW.varHandle(groupElement("lpfnWndProc"))
val WNDCLASSEXW_hInstance: VarHandle = WNDCLASSEXW.varHandle(groupElement("hInstance"))
val WNDCLASSEXW_hIcon: VarHandle = WNDCLASSEXW.varHandle(groupElement("hIcon"))
val WNDCLASSEXW_lpszClassName: VarHandle = WNDCLASSEXW.varHandle(groupElement("lpszClassName"))

val PIXELFORMATDESCRIPTOR: StructLayout = MemoryLayout.structLayout(
	WORD.withName("nSize"),
	WORD.withName("nVersion"),
	DWORD.withName("dwFlags"),
	BYTE.withName("iPixelType"),
	BYTE.withName("cColorBits"),
	BYTE.withName("cRedBits"),
	BYTE.withName("cRedShift"),
	BYTE.withName("cGreenBits"),
	BYTE.withName("cGreenShift"),
	BYTE.withName("cBlueBits"),
	BYTE.withName("cBlueShift"),
	BYTE.withName("cAlphaBits"),
	BYTE.withName("cAlphaShift"),
	BYTE.withName("cAccumBits"),
	BYTE.withName("cAccumRedBits"),
	BYTE.withName("cAccumGreenBits"),
	BYTE.withName("cAccumBlueBits"),
	BYTE.withName("cAccumAlphaBits"),
	BYTE.withName("cDepthBits"),
	BYTE.withName("cStencilBits"),
	BYTE.withName("cAuxBuffers"),
	BYTE.withName("iLayerType"),
	BYTE.withName("bReserved"),
	DWORD.withName("dwLayerMask"),
	DWORD.withName("dwVisibleMask"),
	DWORD.withName("dwDamageMask"),
)
val PIXELFORMATDESCRIPTOR_nSize: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("nSize"))
val PIXELFORMATDESCRIPTOR_nVersion: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("nVersion"))
val PIXELFORMATDESCRIPTOR_dwFlags: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("dwFlags"))
val PIXELFORMATDESCRIPTOR_iPixelType: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("iPixelType"))
val PIXELFORMATDESCRIPTOR_cColorBits: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("cColorBits"))
val PIXELFORMATDESCRIPTOR_cDepthBits: VarHandle = PIXELFORMATDESCRIPTOR.varHandle(groupElement("cDepthBits"))

val POINT: StructLayout = MemoryLayout.structLayout(
	LONG.withName("x"),
	LONG.withName("y"),
)

val RECT: StructLayout = MemoryLayout.structLayout(
	LONG.withName("left"),
	LONG.withName("top"),
	LONG.withName("right"),
	LONG.withName("bottom")
)
val PRECT = `void*`
val LPRECT = `void*`

val RECT_left: VarHandle = RECT.varHandle(groupElement("left"))
val RECT_top: VarHandle = RECT.varHandle(groupElement("top"))
val RECT_right: VarHandle = RECT.varHandle(groupElement("right"))
val RECT_bottom: VarHandle = RECT.varHandle(groupElement("bottom"))

val MSG: StructLayout = MemoryLayout.structLayout(
	HWND.withName("hwnd"),
	UINT.withName("message"),
	MemoryLayout.paddingLayout(4),
	WPARAM.withName("wParam"),
	LPARAM.withName("lParam"),
	DWORD.withName("time"),
	POINT.withName("pt"),
	DWORD.withName("lPrivate"),
)
val LPMSG: AddressLayout = AddressLayout.ADDRESS

val KEY_EVENT_RECORD: StructLayout = MemoryLayout.structLayout(
	BOOL.withName("bKeyDown"),
	WORD.withName("wRepeatCount"),
	WORD.withName("wVirtualKeyCode"),
	WORD.withName("wVirtualScanCode"),
	MemoryLayout.unionLayout(
		WCHAR.withName("UnicodeChar"),
		CHAR.withName("AsciiChar")
	).withName("uChar"),
	DWORD.withName("dwControlKeyState"),
)
val KEY_EVENT_RECORD_bKeyDown: VarHandle = KEY_EVENT_RECORD.varHandle(groupElement("bKeyDown"))
val KEY_EVENT_RECORD_wRepeatCount: VarHandle = KEY_EVENT_RECORD.varHandle(groupElement("wRepeatCount"))
val KEY_EVENT_RECORD_wVirtualKeyCode: VarHandle = KEY_EVENT_RECORD.varHandle(groupElement("wVirtualKeyCode"))
val KEY_EVENT_RECORD_wVirtualScanCode: VarHandle = KEY_EVENT_RECORD.varHandle(groupElement("wVirtualScanCode"))
val KEY_EVENT_RECORD_uChar_UnicodeChar: VarHandle = KEY_EVENT_RECORD.varHandle(
	groupElement("uChar"),
	groupElement("UnicodeChar")
)
val KEY_EVENT_RECORD_dwControlKeyState: VarHandle = KEY_EVENT_RECORD.varHandle(groupElement("dwControlKeyState"))

val MOUSE_EVENT_RECORD: StructLayout = MemoryLayout.structLayout()

val COORD: StructLayout = MemoryLayout.structLayout(
	SHORT.withName("X"),
	SHORT.withName("Y")
)
val COORD_X: VarHandle = COORD.varHandle(groupElement("X"))
val COORD_Y: VarHandle = COORD.varHandle(groupElement("Y"))

val WINDOW_BUFFER_SIZE_RECORD: StructLayout = MemoryLayout.structLayout(
	COORD.withName("dwSize")
)
val WINDOW_BUFFER_SIZE_RECORD_COORD: MethodHandle = WINDOW_BUFFER_SIZE_RECORD.sliceHandle(
	groupElement("dwSize")
)

val MENU_EVENT_RECORD: StructLayout = MemoryLayout.structLayout()
val FOCUS_EVENT_RECORD: StructLayout = MemoryLayout.structLayout()

val INPUT_RECORD: StructLayout = MemoryLayout.structLayout(
	WORD.withName("EventType"),
	MemoryLayout.paddingLayout(2),
	MemoryLayout.unionLayout(
		KEY_EVENT_RECORD.withName("KeyEvent"),
		MOUSE_EVENT_RECORD.withName("MouseEvent"),
		WINDOW_BUFFER_SIZE_RECORD.withName("WindowBufferSizeEvent"),
		MENU_EVENT_RECORD.withName("MenuEvent"),
		FOCUS_EVENT_RECORD.withName("FocusEvent")
	).withName("Event")
)
val INPUT_RECORD_EventType: VarHandle = INPUT_RECORD.varHandle(groupElement("EventType"))
val INPUT_RECORD_KeyEvent: MethodHandle = INPUT_RECORD.sliceHandle(
	groupElement("Event"),
	groupElement("KeyEvent")
)
val INPUT_RECORD_WindowBufferSizeEvent: MethodHandle = INPUT_RECORD.sliceHandle(
	groupElement("Event"),
	groupElement("WindowBufferSizeEvent")
)

val SID_AND_ATTRIBUTES: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("Sid"),
	DWORD.withName("Attributes"),
	MemoryLayout.paddingLayout(4)
)
val SID_AND_ATTRIBUTES_Sid: VarHandle = SID_AND_ATTRIBUTES.varHandle(groupElement("Sid"))

val TOKEN_USER: StructLayout = MemoryLayout.structLayout(
	SID_AND_ATTRIBUTES.withName("User")
)
val TOKEN_USER_User: MethodHandle = TOKEN_USER.sliceHandle(groupElement("User"))

val LUID: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val TOKEN_STATISTICS: StructLayout = MemoryLayout.structLayout(
	LUID.withName("TokenId"),
	LUID.withName("AuthenticationId"),
)
val TOKEN_STATISTICS_AuthenticationId: MethodHandle = TOKEN_STATISTICS.sliceHandle(groupElement("AuthenticationId"))

val LARGE_INTEGER: ValueLayout.OfLong = ValueLayout.JAVA_LONG
val PLARGE_INTEGER: AddressLayout = ValueLayout.ADDRESS
val LSA_UNICODE_STRING: StructLayout = MemoryLayout.structLayout(
	USHORT.withName("Length"),
	USHORT.withName("MaximumLength"),
	MemoryLayout.paddingLayout(4),
	PWSTR.withName("Buffer")
)

val SECURITY_LOGON_SESSION_DATA: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("Size"),
	MemoryLayout.paddingLayout(4),
	LUID.withName("LogonId"),
	LSA_UNICODE_STRING.withName("UserName"),
	LSA_UNICODE_STRING.withName("LogonDomain"),
	LSA_UNICODE_STRING.withName("AuthenticationPackage"),
	ULONG.withName("LogonType"),
	ULONG.withName("Session"),
	ValueLayout.ADDRESS.withName("Sid"),
	LARGE_INTEGER.withName("LogonTime")
)
val SECURITY_LOGON_SESSION_DATA_LogonTime: VarHandle = SECURITY_LOGON_SESSION_DATA.varHandle(groupElement("LogonTime"))

val CM_NOTIFY_FILTER_DeviceInterface: StructLayout = MemoryLayout.structLayout(
	GUID.withName("ClassGuid")
)
val CM_NOTIFY_FILTER_DeviceHandle: StructLayout = MemoryLayout.structLayout(
	HANDLE.withName("hTarget")
)
val CM_NOTIFY_FILTER_DeviceInstance: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(200, WCHAR).withName("InstanceId")
)

val CM_NOTIFY_FILTER: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("cbSize"),
	DWORD.withName("Flags"),
	DWORD.withName("FilterType"),
	DWORD.withName("Reserved"),
	MemoryLayout.unionLayout(
		CM_NOTIFY_FILTER_DeviceInterface.withName("DeviceInterface"),
		CM_NOTIFY_FILTER_DeviceHandle.withName("DeviceHandle"),
		CM_NOTIFY_FILTER_DeviceInstance.withName("DeviceInstance")
	).withName("u")
)
val CM_NOTIFY_FILTER_cbSize: VarHandle = CM_NOTIFY_FILTER.varHandle(groupElement("cbSize"))
val CM_NOTIFY_FILTER_Flags: VarHandle = CM_NOTIFY_FILTER.varHandle(groupElement("Flags"))
val CM_NOTIFY_FILTER_FilterType: VarHandle = CM_NOTIFY_FILTER.varHandle(groupElement("FilterType"))
//val CM_NOTIFY_FILTER_u_DeviceInterface: MethodHandle = CM_NOTIFY_FILTER.sliceHandle(
//	groupElement("u"),
//	groupElement("DeviceInterface"),
//	groupElement("ClassGuid")
//)

val CM_NOTIFY_EVENT_DATA_DeviceInterface: StructLayout = MemoryLayout.structLayout(
	GUID.withName("ClassGuid"),
	MemoryLayout.sequenceLayout(0, WCHAR).withName("SymbolicLink")
)
val CM_NOTIFY_EVENT_DATA_DeviceHandle: StructLayout = MemoryLayout.structLayout(
	GUID.withName("EventGuid"),
	LONG.withName("NameOffset"),
	DWORD.withName("DataSize"),
	MemoryLayout.sequenceLayout(0, BYTE).withName("Data")
)
val CM_NOTIFY_EVENT_DATA_DeviceInstance: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(0, WCHAR).withName("InstanceId")
)
val CM_NOTIFY_EVENT_DATA: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("FilterType"),
	DWORD.withName("Reserved"),
	MemoryLayout.unionLayout(
		CM_NOTIFY_EVENT_DATA_DeviceInterface.withName("DeviceInterface"),
		CM_NOTIFY_EVENT_DATA_DeviceHandle.withName("DeviceHandle"),
		CM_NOTIFY_EVENT_DATA_DeviceInstance.withName("DeviceInstance")
	).withName("u")
)
val CM_NOTIFY_EVENT_DATA_FilterType: VarHandle = CM_NOTIFY_EVENT_DATA.varHandle(groupElement("FilterType"))
val CM_NOTIFY_EVENT_DATA_u_DeviceInterface_ClassGuid: MethodHandle = CM_NOTIFY_EVENT_DATA.sliceHandle(
	groupElement("u"),
	groupElement("DeviceInterface"),
	groupElement("ClassGuid")
)
val CM_NOTIFY_EVENT_DATA_u_DeviceInterface_SymbolicLink: MethodHandle = CM_NOTIFY_EVENT_DATA.sliceHandle(
	groupElement("u"),
	groupElement("DeviceInterface"),
	groupElement("SymbolicLink")
)

val FILETIME: ValueLayout.OfLong = ValueLayout.JAVA_LONG.withByteAlignment(4)
val WIN32_FIND_DATAW: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwFileAttributes"),
	FILETIME.withName("ftCreationTime"),
	FILETIME.withName("ftLastAccessTime"),
	FILETIME.withName("ftLastWriteTime"),
	DWORD.withName("nFileSizeHigh"),
	DWORD.withName("nFileSizeLow"),
	DWORD.withName("dwReserved0"),
	DWORD.withName("dwReserved1"),
	MemoryLayout.sequenceLayout(MAX_PATH, WCHAR).withName("cFileName"),
	MemoryLayout.sequenceLayout(14, WCHAR).withName("cAlternateFileName"),
	DWORD.withName("dwFileType"),
	DWORD.withName("dwCreatorType"),
	WORD.withName("wFinderFlags")
)
val WIN32_FIND_DATAW_cFileName: MethodHandle = WIN32_FIND_DATAW.sliceHandle(groupElement("cFileName"))

val WIN32_FIND_STREAM_DATA: StructLayout = MemoryLayout.structLayout(
	LARGE_INTEGER.withName("StreamSize"),
	MemoryLayout.sequenceLayout(MAX_PATH + 36, WCHAR).withName("cStreamName"),
)
val WIN32_FIND_STREAM_DATA_cStreamName: MethodHandle = WIN32_FIND_STREAM_DATA.sliceHandle(groupElement("cStreamName"))

val COPYFILE2_EXTENDED_PARAMETERS_V2: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwSize"),
	DWORD.withName("dwCopyFlags"),
	ValueLayout.ADDRESS.withName("pfCancel"), /* of type BOOL */
	ValueLayout.ADDRESS.withName("pProgressRoutine"), /* of type COPYFILE2_PROGRESS_ROUTINE */
	PVOID.withName("pvCallbackContext"),
	DWORD.withName("dwCopyFlagsV2"),
	ULONG.withName("ioDesiredSize"),
	ULONG.withName("ioDesiredRate"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("pProgressRoutineOld"), /* of type PROGRESS_ROUTINE */
	ValueLayout.ADDRESS.withName("SourceOplockKeys"), /* of type COPYFILE2_CREATE_OPLOCK_KEYS */
	MemoryLayout.sequenceLayout(6, PVOID), // Pertains to NTDDI_WIN10_GE
)
val COPYFILE2_EXTENDED_PARAMETERS_V2_dwSize: VarHandle = COPYFILE2_EXTENDED_PARAMETERS_V2.varHandle(
	groupElement("dwSize")
)
val COPYFILE2_EXTENDED_PARAMETERS_V2_dwCopyFlags: VarHandle = COPYFILE2_EXTENDED_PARAMETERS_V2.varHandle(
	groupElement("dwCopyFlags")
)
val COPYFILE2_EXTENDED_PARAMETERS_V2_pProgressRoutine: VarHandle = COPYFILE2_EXTENDED_PARAMETERS_V2.varHandle(
	groupElement("pProgressRoutine")
)
val COPYFILE2_EXTENDED_PARAMETERS_V2_dwCopyFlagsV2: VarHandle = COPYFILE2_EXTENDED_PARAMETERS_V2.varHandle(
	groupElement("dwCopyFlagsV2")
)

val COPYFILE2_MESSAGE_ChunkStarted: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwStreamNumber"),
	DWORD.withName("dwReserved"),
	HANDLE.withName("hSourceFile"),
	HANDLE.withName("hDestinationFile"),
	ValueLayout.JAVA_LONG.withName("uliChunkNumber"),
	ValueLayout.JAVA_LONG.withName("uliChunkSize"),
	ValueLayout.JAVA_LONG.withName("uliStreamSize"),
	ValueLayout.JAVA_LONG.withName("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_ChunkStarted_dwStreamNumber: VarHandle = COPYFILE2_MESSAGE_ChunkStarted.varHandle(
	groupElement("dwStreamNumber")
)
val COPYFILE2_MESSAGE_ChunkStarted_uliChunkNumber: VarHandle = COPYFILE2_MESSAGE_ChunkStarted.varHandle(
	groupElement("uliChunkNumber")
)
val COPYFILE2_MESSAGE_ChunkStarted_uliChunkSize: VarHandle = COPYFILE2_MESSAGE_ChunkStarted.varHandle(
	groupElement("uliChunkSize")
)
val COPYFILE2_MESSAGE_ChunkStarted_uliStreamSize: VarHandle = COPYFILE2_MESSAGE_ChunkStarted.varHandle(
	groupElement("uliStreamSize")
)
val COPYFILE2_MESSAGE_ChunkStarted_uliTotalFileSize: VarHandle = COPYFILE2_MESSAGE_ChunkStarted.varHandle(
	groupElement("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_ChunkFinished: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwStreamNumber"),
	DWORD.withName("dwFlags"),
	HANDLE.withName("hSourceFile"),
	HANDLE.withName("hDestinationFile"),
	ValueLayout.JAVA_LONG.withName("uliChunkNumber"),
	ValueLayout.JAVA_LONG.withName("uliChunkSize"),
	ValueLayout.JAVA_LONG.withName("uliStreamSize"),
	ValueLayout.JAVA_LONG.withName("uliStreamBytesTransferred"),
	ValueLayout.JAVA_LONG.withName("uliTotalFileSize"),
	ValueLayout.JAVA_LONG.withName("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE_ChunkFinished_dwStreamNumber: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("dwStreamNumber")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliChunkNumber: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliChunkNumber")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliChunkSize: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliChunkSize")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliStreamSize: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliStreamSize")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliStreamBytesTransferred: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliStreamBytesTransferred")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliTotalFileSize: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_ChunkFinished_uliTotalBytesTransferred: VarHandle = COPYFILE2_MESSAGE_ChunkFinished.varHandle(
	groupElement("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE_StreamStarted: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwStreamNumber"),
	DWORD.withName("dwReserved"),
	HANDLE.withName("hSourceFile"),
	HANDLE.withName("hDestinationFile"),
	ValueLayout.JAVA_LONG.withName("uliStreamSize"),
	ValueLayout.JAVA_LONG.withName("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_StreamStarted_dwStreamNumber: VarHandle = COPYFILE2_MESSAGE_StreamStarted.varHandle(
	groupElement("dwStreamNumber")
)
val COPYFILE2_MESSAGE_StreamStarted_uliStreamSize: VarHandle = COPYFILE2_MESSAGE_StreamStarted.varHandle(
	groupElement("uliStreamSize")
)
val COPYFILE2_MESSAGE_StreamStarted_uliTotalFileSize: VarHandle = COPYFILE2_MESSAGE_StreamStarted.varHandle(
	groupElement("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_StreamFinished: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwStreamNumber"),
	DWORD.withName("dwReserved"),
	HANDLE.withName("hSourceFile"),
	HANDLE.withName("hDestinationFile"),
	ValueLayout.JAVA_LONG.withName("uliStreamSize"),
	ValueLayout.JAVA_LONG.withName("uliStreamBytesTransferred"),
	ValueLayout.JAVA_LONG.withName("uliTotalFileSize"),
	ValueLayout.JAVA_LONG.withName("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE_StreamFinished_dwStreamNumber: VarHandle = COPYFILE2_MESSAGE_StreamFinished.varHandle(
	groupElement("dwStreamNumber")
)
val COPYFILE2_MESSAGE_StreamFinished_uliStreamSize: VarHandle = COPYFILE2_MESSAGE_StreamFinished.varHandle(
	groupElement("uliStreamSize")
)
val COPYFILE2_MESSAGE_StreamFinished_uliStreamBytesTransferred: VarHandle = COPYFILE2_MESSAGE_StreamFinished.varHandle(
	groupElement("uliStreamBytesTransferred")
)
val COPYFILE2_MESSAGE_StreamFinished_uliTotalFileSize: VarHandle = COPYFILE2_MESSAGE_StreamFinished.varHandle(
	groupElement("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_StreamFinished_uliTotalBytesTransferred: VarHandle = COPYFILE2_MESSAGE_StreamFinished.varHandle(
	groupElement("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE_PollContinue: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwReserved")
)
val COPYFILE2_MESSAGE_Error: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("CopyPhase"),
	DWORD.withName("dwStreamNumber"),
	DWORD.withName("hrFailure"),
	DWORD.withName("dwReserved"),
	ValueLayout.JAVA_LONG.withName("uliChunkNumber"),
	ValueLayout.JAVA_LONG.withName("uliStreamSize"),
	ValueLayout.JAVA_LONG.withName("uliStreamBytesTransferred"),
	ValueLayout.JAVA_LONG.withName("uliTotalFileSize"),
	ValueLayout.JAVA_LONG.withName("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE_Error_CopyPhase: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("CopyPhase")
)
val COPYFILE2_MESSAGE_Error_dwStreamNumber: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("dwStreamNumber")
)
val COPYFILE2_MESSAGE_Error_hrFailure: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("hrFailure")
)
val COPYFILE2_MESSAGE_Error_uliChunkNumber: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("uliChunkNumber")
)
val COPYFILE2_MESSAGE_Error_uliStreamSize: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("uliStreamSize")
)
val COPYFILE2_MESSAGE_Error_uliStreamBytesTransferred: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("uliStreamBytesTransferred")
)
val COPYFILE2_MESSAGE_Error_uliTotalFileSize: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("uliTotalFileSize")
)
val COPYFILE2_MESSAGE_Error_uliTotalBytesTransferred: VarHandle = COPYFILE2_MESSAGE_Error.varHandle(
	groupElement("uliTotalBytesTransferred")
)
val COPYFILE2_MESSAGE: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("Type"),
	DWORD.withName("dwPadding"),
	MemoryLayout.unionLayout(
		COPYFILE2_MESSAGE_ChunkStarted,
		COPYFILE2_MESSAGE_ChunkFinished,
		COPYFILE2_MESSAGE_StreamStarted,
		COPYFILE2_MESSAGE_StreamFinished,
		COPYFILE2_MESSAGE_PollContinue,
		COPYFILE2_MESSAGE_Error
	).withName("Info")
)
val COPYFILE2_MESSAGE_Type: VarHandle = COPYFILE2_MESSAGE.varHandle(groupElement("Type"))
val COPYFILE2_MESSAGE_Info: MethodHandle = COPYFILE2_MESSAGE.sliceHandle(groupElement("Info"))

const val INFINITE = 0xFFFFFFFF.toInt()
const val WAIT_FAILED = 0xFFFFFFFF.toInt()

const val STATUS_WAIT_0 = 0x00000000
const val WAIT_OBJECT_0 = STATUS_WAIT_0 + 0