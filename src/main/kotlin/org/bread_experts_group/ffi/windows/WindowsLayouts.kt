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
val WNDCLASSEXA: StructLayout = MemoryLayout.structLayout(
	UINT.withName("cbSize"),
	UINT.withName("style"),
	WNDPROC.withName("lpfnWndProc"),
	ValueLayout.JAVA_INT.withName("cbClsExtra"),
	ValueLayout.JAVA_INT.withName("cbWndExtra"),
	HINSTANCE.withName("hInstance"),
	HICON.withName("hIcon"),
	HCURSOR.withName("hCursor"),
	HBRUSH.withName("hbrBackground"),
	LPCSTR.withName("lpszMenuName"),
	LPCSTR.withName("lpszClassName"),
	HICON.withName("hIconSm")
)
val WNDCLASSEXA_cbSize: VarHandle = WNDCLASSEXA.varHandle(groupElement("cbSize"))
val WNDCLASSEXA_lpfnWndProc: VarHandle = WNDCLASSEXA.varHandle(groupElement("lpfnWndProc"))
val WNDCLASSEXA_hInstance: VarHandle = WNDCLASSEXA.varHandle(groupElement("hInstance"))
val WNDCLASSEXA_lpszClassName: VarHandle = WNDCLASSEXA.varHandle(groupElement("lpszClassName"))

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

//val DCB: StructLayout = MemoryLayout.structLayout(
//	DWORD.withName("DCBlength"),
//	DWORD.withName("BaudRate"),
//	DWORD.withName("fBinary"),
//	DWORD.withName("fParity"),
//	DWORD.withName("fOutxCtsFlow"),
//	DWORD.withName("fOutxDsrFlow"),
//	DWORD.withName("fDtrControl"),
//	DWORD.withName("fDsrSensitivity"),
//	DWORD.withName("fTXContinueOnXoff"),
//	DWORD.withName("fOutX"),
//	DWORD.withName("fInX"),
//	DWORD.withName("fErrorChar"),
//	DWORD.withName("fNull"),
//	DWORD.withName("fRtsControl"),
//	DWORD.withName("fAbortOnError"),
//	DWORD.withName("fDummy2"),
//	WORD.withName("wReserved"),
//	WORD.withName("XonLim"),
//	WORD.withName("XoffLim"),
//	BYTE.withName("ByteSize"),
//	BYTE.withName("Parity"),
//	BYTE.withName("StopBits"),
//	char.withName("XonChar"),
//	char.withName("XoffChar"),
//	char.withName("ErrorChar"),
//	char.withName("EofChar"),
//	char.withName("EvtChar"),
//	WORD.withName("wReserved1"),
//)
//val LPDCB: AddressLayout = AddressLayout.ADDRESS
//val DCB_DCBlength: VarHandle = DCB.varHandle(groupElement("DCBlength"))
//val DCB_BaudRate: VarHandle = DCB.varHandle(groupElement("BaudRate"))
//val DCB_fBinary: VarHandle = DCB.varHandle(groupElement("fBinary"))
//val DCB_fParity: VarHandle = DCB.varHandle(groupElement("fParity"))
//val DCB_fOutxCtsFlow: VarHandle = DCB.varHandle(groupElement("fOutxCtsFlow"))
//val DCB_fOutxDsrFlow: VarHandle = DCB.varHandle(groupElement("fOutxDsrFlow"))
//val DCB_fDtrControl: VarHandle = DCB.varHandle(groupElement("fDtrControl"))
//val DCB_fDsrSensitivity: VarHandle = DCB.varHandle(groupElement("fDsrSensitivity"))
//val DCB_fTXContinueOnXoff: VarHandle = DCB.varHandle(groupElement("fTXContinueOnXoff"))
//val DCB_fOutX: VarHandle = DCB.varHandle(groupElement("fOutX"))
//val DCB_fInX: VarHandle = DCB.varHandle(groupElement("fInX"))
//val DCB_fErrorChar: VarHandle = DCB.varHandle(groupElement("fErrorChar"))
//val DCB_fNull: VarHandle = DCB.varHandle(groupElement("fNull"))
//val DCB_fRtsControl: VarHandle = DCB.varHandle(groupElement("fRtsControl"))
//val DCB_fAbortOnError: VarHandle = DCB.varHandle(groupElement("fAbortOnError"))
//val DCB_XonLim: VarHandle = DCB.varHandle(groupElement("XonLim"))
//val DCB_XoffLim: VarHandle = DCB.varHandle(groupElement("XoffLim"))
//val DCB_ByteSize: VarHandle = DCB.varHandle(groupElement("ByteSize"))
//val DCB_Parity: VarHandle = DCB.varHandle(groupElement("Parity"))
//val DCB_StopBits: VarHandle = DCB.varHandle(groupElement("StopBits"))
//val DCB_XonChar: VarHandle = DCB.varHandle(groupElement("XonChar"))
//val DCB_XoffChar: VarHandle = DCB.varHandle(groupElement("XoffChar"))
//val DCB_ErrorChar: VarHandle = DCB.varHandle(groupElement("ErrorChar"))
//val DCB_EofChar: VarHandle = DCB.varHandle(groupElement("EofChar"))
//val DCB_EvtChar: VarHandle = DCB.varHandle(groupElement("EvtChar"))

//class SerialCommunicationDeviceControl(arena: Arena) {
//	val ptr: MemorySegment = arena.allocate(DCB)
//
//	init {
//		DCB_DCBlength.set(ptr, 0, ptr.byteSize().toInt())
//	}
//
//	var baudRate: MappedEnumeration<UInt, WindowsBaudRate>
//		get() = WindowsBaudRate.entries.id((DCB_BaudRate.get(ptr, 0) as Int).toUInt())
//		set(value) {
//			DCB_BaudRate.set(ptr, 0, value.raw.toInt())
//		}
//	var binaryMode: Boolean
//		get() = DCB_fBinary.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fBinary.set(ptr, 0, if (value) 1 else 0)
//		}
//	var parityChecking: Boolean
//		get() = DCB_fParity.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fParity.set(ptr, 0, if (value) 1 else 0)
//		}
//	var ctsMonitor: Boolean
//		get() = DCB_fOutxCtsFlow.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fOutxCtsFlow.set(ptr, 0, if (value) 1 else 0)
//		}
//	var dsrMonitor: Boolean
//		get() = DCB_fOutxDsrFlow.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fOutxDsrFlow.set(ptr, 0, if (value) 1 else 0)
//		}
//	var dtrControl: MappedEnumeration<UInt, WindowsDataTerminalReadyFlowControl>
//		get() = WindowsDataTerminalReadyFlowControl.entries.id((DCB_fDtrControl.get(ptr, 0) as Int).toUInt())
//		set(value) {
//			DCB_fDtrControl.set(ptr, 0, value.raw.toInt())
//		}
//	var dsrSensitive: Boolean
//		get() = DCB_fDsrSensitivity.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fDsrSensitivity.set(ptr, 0, if (value) 1 else 0)
//		}
//	var transmitAfterBufferFull: Boolean
//		get() = DCB_fTXContinueOnXoff.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fTXContinueOnXoff.set(ptr, 0, if (value) 1 else 0)
//		}
//	var txXonXoffFlowControl: Boolean
//		get() = DCB_fOutX.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fOutX.set(ptr, 0, if (value) 1 else 0)
//		}
//	var rxXonXoffFlowControl: Boolean
//		get() = DCB_fInX.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fInX.set(ptr, 0, if (value) 1 else 0)
//		}
//	var replaceErrors: Boolean
//		get() = DCB_fErrorChar.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fErrorChar.set(ptr, 0, if (value) 1 else 0)
//		}
//	var discardNull: Boolean
//		get() = DCB_fNull.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fNull.set(ptr, 0, if (value) 1 else 0)
//		}
//	var rtsControl: MappedEnumeration<UInt, WindowsRequestToSendControl>
//		get() = WindowsRequestToSendControl.entries.id((DCB_fRtsControl.get(ptr, 0) as Int).toUInt())
//		set(value) {
//			DCB_fRtsControl.set(ptr, 0, value.raw.toInt())
//		}
//	var abortCommunicationOnError: Boolean
//		get() = DCB_fAbortOnError.get(ptr, 0) as Int != 0
//		set(value) {
//			DCB_fAbortOnError.set(ptr, 0, if (value) 1 else 0)
//		}
//	var usedBytesToXon: UShort
//		get() = (DCB_XonLim.get(ptr, 0) as Short).toUShort()
//		set(value) {
//			DCB_XonLim.set(ptr, 0, value.toShort())
//		}
//	var freeBytesToXoff: UShort
//		get() = (DCB_XoffLim.get(ptr, 0) as Short).toUShort()
//		set(value) {
//			DCB_XoffLim.set(ptr, 0, value.toShort())
//		}
//	var dataBits: UByte
//		get() = (DCB_ByteSize.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_ByteSize.set(ptr, 0, value.toByte())
//		}
//	var parityScheme: MappedEnumeration<UByte, WindowsParityScheme>
//		get() = WindowsParityScheme.entries.id((DCB_Parity.get(ptr, 0) as Byte).toUByte())
//		set(value) {
//			DCB_Parity.set(ptr, 0, value.raw.toByte())
//		}
//	var stopBits: MappedEnumeration<UByte, WindowsStopBits>
//		get() = WindowsStopBits.entries.id((DCB_StopBits.get(ptr, 0) as Byte).toUByte())
//		set(value) {
//			DCB_StopBits.set(ptr, 0, value.raw.toByte())
//		}
//	var xONChar: UByte
//		get() = (DCB_ErrorChar.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_ErrorChar.set(ptr, 0, value.toByte())
//		}
//	var xOFFChar: UByte
//		get() = (DCB_XoffChar.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_XoffChar.set(ptr, 0, value.toByte())
//		}
//	var errorChar: UByte
//		get() = (DCB_ErrorChar.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_ErrorChar.set(ptr, 0, value.toByte())
//		}
//	var eofChar: UByte
//		get() = (DCB_EofChar.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_EofChar.set(ptr, 0, value.toByte())
//		}
//	var eventChar: UByte
//		get() = (DCB_EvtChar.get(ptr, 0) as Byte).toUByte()
//		set(value) {
//			DCB_EvtChar.set(ptr, 0, value.toByte())
//		}
//
//	override fun toString(): String = "Device Communication Control" +
//			"\n\tBaud Rate: $baudRate" +
//			"\n\tBinary mode: $binaryMode" +
//			"\n\tParity checking: $parityChecking" +
//			"\n\tOutput flow monitors clear-to-send: $ctsMonitor" +
//			"\n\tOutput flow monitors data-set-ready: $dsrMonitor" +
//			"\n\tData-terminal-ready control: $dtrControl" +
//			"\n\tData-set-ready sensitive: $dsrSensitive" +
//			"\n\tTX after XOFF: $transmitAfterBufferFull" +
//			"\n\tTX XON/XOFF flow control: $txXonXoffFlowControl" +
//			"\n\tRX XON/OFF flow control: $rxXonXoffFlowControl" +
//			"\n\tReplace parity errors: $replaceErrors" +
//			"\n\tDiscard nulls: $discardNull" +
//			"\n\tRequest-to-send control: $rtsControl" +
//			"\n\tAbort communication on error: $abortCommunicationOnError" +
//			"\n\tUsed bytes to XON: $usedBytesToXon" +
//			"\n\tFree bytes to XOFF: $freeBytesToXoff" +
//			"\n\tByte size in bits: $dataBits" +
//			"\n\tParity scheme: $parityScheme" +
//			"\n\tStop bits: $stopBits" +
//			"\n\tXON char: 0x${xONChar.toHexString(HexFormat.UpperCase)}" +
//			"\n\tXOFF char: 0x${xOFFChar.toHexString(HexFormat.UpperCase)}" +
//			"\n\tError char: 0x${errorChar.toHexString(HexFormat.UpperCase)}" +
//			"\n\tEOF char: 0x${eofChar.toHexString(HexFormat.UpperCase)}" +
//			"\n\tEvent char: 0x${eventChar.toHexString(HexFormat.UpperCase)}"
//}

//val CPINFOEXW: StructLayout = MemoryLayout.structLayout(
//	UINT.withName("MaxCharSize"),
//	MemoryLayout.sequenceLayout(2, BYTE).withName("DefaultChar"),
//	MemoryLayout.sequenceLayout(12, BYTE).withName("LeadByte"),
//	WCHAR.withName("UnicodeDefaultChar"),
//	UINT.withName("CodePage"),
//	MemoryLayout.sequenceLayout(260, WCHAR).withName("CodePageName")
//)
//val CPINFOEXW_CodePageName: MethodHandle = CPINFOEXW.sliceHandle(groupElement("CodePageName"))

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

val FILETIME: ValueLayout.OfLong = ValueLayout.JAVA_LONG_UNALIGNED
val WIN32_FIND_DATAW: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwFileAttributes"),
	FILETIME.withName("ftCreationTime"),
	FILETIME.withName("ftLastAccessTime"),
	FILETIME.withName("ftLastWriteTime"),
	DWORD.withName("nFileSizeHigh"),
	DWORD.withName("nFileSizeLow"),
	DWORD.withName("dwReserved0"),
	DWORD.withName("dwReserved1"),
	MemoryLayout.sequenceLayout(260, WCHAR).withName("cFileName"),
	MemoryLayout.sequenceLayout(14, WCHAR).withName("cAlternateFileName"),
	DWORD.withName("dwFileType"),
	DWORD.withName("dwCreatorType"),
	WORD.withName("wFinderFlags")
)
val WIN32_FIND_DATAW_cFileName: MethodHandle = WIN32_FIND_DATAW.sliceHandle(groupElement("cFileName"))

val WIN32_FIND_STREAM_DATA: StructLayout = MemoryLayout.structLayout(
	LARGE_INTEGER.withName("StreamSize"),
	MemoryLayout.sequenceLayout(260 + 36, WCHAR).withName("cStreamName"),
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