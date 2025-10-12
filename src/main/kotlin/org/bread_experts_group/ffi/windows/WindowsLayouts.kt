package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import java.lang.foreign.*
import java.lang.foreign.MemoryLayout.PathElement.groupElement
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

val DCB: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("DCBlength"),
	DWORD.withName("BaudRate"),
	DWORD.withName("fBinary"),
	DWORD.withName("fParity"),
	DWORD.withName("fOutxCtsFlow"),
	DWORD.withName("fOutxDsrFlow"),
	DWORD.withName("fDtrControl"),
	DWORD.withName("fDsrSensitivity"),
	DWORD.withName("fTXContinueOnXoff"),
	DWORD.withName("fOutX"),
	DWORD.withName("fInX"),
	DWORD.withName("fErrorChar"),
	DWORD.withName("fNull"),
	DWORD.withName("fRtsControl"),
	DWORD.withName("fAbortOnError"),
	DWORD.withName("fDummy2"),
	WORD.withName("wReserved"),
	WORD.withName("XonLim"),
	WORD.withName("XoffLim"),
	BYTE.withName("ByteSize"),
	BYTE.withName("Parity"),
	BYTE.withName("StopBits"),
	char.withName("XonChar"),
	char.withName("XoffChar"),
	char.withName("ErrorChar"),
	char.withName("EofChar"),
	char.withName("EvtChar"),
	WORD.withName("wReserved1"),
)
val LPDCB: AddressLayout = AddressLayout.ADDRESS
val DCB_DCBlength: VarHandle = DCB.varHandle(groupElement("DCBlength"))
val DCB_BaudRate: VarHandle = DCB.varHandle(groupElement("BaudRate"))
val DCB_fBinary: VarHandle = DCB.varHandle(groupElement("fBinary"))
val DCB_fParity: VarHandle = DCB.varHandle(groupElement("fParity"))
val DCB_fOutxCtsFlow: VarHandle = DCB.varHandle(groupElement("fOutxCtsFlow"))
val DCB_fOutxDsrFlow: VarHandle = DCB.varHandle(groupElement("fOutxDsrFlow"))
val DCB_fDtrControl: VarHandle = DCB.varHandle(groupElement("fDtrControl"))
val DCB_fDsrSensitivity: VarHandle = DCB.varHandle(groupElement("fDsrSensitivity"))
val DCB_fTXContinueOnXoff: VarHandle = DCB.varHandle(groupElement("fTXContinueOnXoff"))
val DCB_fOutX: VarHandle = DCB.varHandle(groupElement("fOutX"))
val DCB_fInX: VarHandle = DCB.varHandle(groupElement("fInX"))
val DCB_fErrorChar: VarHandle = DCB.varHandle(groupElement("fErrorChar"))
val DCB_fNull: VarHandle = DCB.varHandle(groupElement("fNull"))
val DCB_fRtsControl: VarHandle = DCB.varHandle(groupElement("fRtsControl"))
val DCB_fAbortOnError: VarHandle = DCB.varHandle(groupElement("fAbortOnError"))
val DCB_XonLim: VarHandle = DCB.varHandle(groupElement("XonLim"))
val DCB_XoffLim: VarHandle = DCB.varHandle(groupElement("XoffLim"))
val DCB_ByteSize: VarHandle = DCB.varHandle(groupElement("ByteSize"))
val DCB_Parity: VarHandle = DCB.varHandle(groupElement("Parity"))
val DCB_StopBits: VarHandle = DCB.varHandle(groupElement("StopBits"))
val DCB_XonChar: VarHandle = DCB.varHandle(groupElement("XonChar"))
val DCB_XoffChar: VarHandle = DCB.varHandle(groupElement("XoffChar"))
val DCB_ErrorChar: VarHandle = DCB.varHandle(groupElement("ErrorChar"))
val DCB_EofChar: VarHandle = DCB.varHandle(groupElement("EofChar"))
val DCB_EvtChar: VarHandle = DCB.varHandle(groupElement("EvtChar"))

class SerialCommunicationDeviceControl(arena: Arena) {
	val ptr: MemorySegment = arena.allocate(DCB)

	init {
		DCB_DCBlength.set(ptr, 0, ptr.byteSize().toInt())
	}

	var baudRate: MappedEnumeration<UInt, WindowsBaudRate>
		get() = WindowsBaudRate.entries.id((DCB_BaudRate.get(ptr, 0) as Int).toUInt())
		set(value) {
			DCB_BaudRate.set(ptr, 0, value.raw.toInt())
		}
	var binaryMode: Boolean
		get() = DCB_fBinary.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fBinary.set(ptr, 0, if (value) 1 else 0)
		}
	var parityChecking: Boolean
		get() = DCB_fParity.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fParity.set(ptr, 0, if (value) 1 else 0)
		}
	var ctsMonitor: Boolean
		get() = DCB_fOutxCtsFlow.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fOutxCtsFlow.set(ptr, 0, if (value) 1 else 0)
		}
	var dsrMonitor: Boolean
		get() = DCB_fOutxDsrFlow.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fOutxDsrFlow.set(ptr, 0, if (value) 1 else 0)
		}
	var dtrControl: MappedEnumeration<UInt, WindowsDataTerminalReadyFlowControl>
		get() = WindowsDataTerminalReadyFlowControl.entries.id((DCB_fDtrControl.get(ptr, 0) as Int).toUInt())
		set(value) {
			DCB_fDtrControl.set(ptr, 0, value.raw.toInt())
		}
	var dsrSensitive: Boolean
		get() = DCB_fDsrSensitivity.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fDsrSensitivity.set(ptr, 0, if (value) 1 else 0)
		}
	var transmitAfterBufferFull: Boolean
		get() = DCB_fTXContinueOnXoff.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fTXContinueOnXoff.set(ptr, 0, if (value) 1 else 0)
		}
	var txXonXoffFlowControl: Boolean
		get() = DCB_fOutX.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fOutX.set(ptr, 0, if (value) 1 else 0)
		}
	var rxXonXoffFlowControl: Boolean
		get() = DCB_fInX.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fInX.set(ptr, 0, if (value) 1 else 0)
		}
	var replaceErrors: Boolean
		get() = DCB_fErrorChar.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fErrorChar.set(ptr, 0, if (value) 1 else 0)
		}
	var discardNull: Boolean
		get() = DCB_fNull.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fNull.set(ptr, 0, if (value) 1 else 0)
		}
	var rtsControl: MappedEnumeration<UInt, WindowsRequestToSendControl>
		get() = WindowsRequestToSendControl.entries.id((DCB_fRtsControl.get(ptr, 0) as Int).toUInt())
		set(value) {
			DCB_fRtsControl.set(ptr, 0, value.raw.toInt())
		}
	var abortCommunicationOnError: Boolean
		get() = DCB_fAbortOnError.get(ptr, 0) as Int != 0
		set(value) {
			DCB_fAbortOnError.set(ptr, 0, if (value) 1 else 0)
		}
	var usedBytesToXon: UShort
		get() = (DCB_XonLim.get(ptr, 0) as Short).toUShort()
		set(value) {
			DCB_XonLim.set(ptr, 0, value.toShort())
		}
	var freeBytesToXoff: UShort
		get() = (DCB_XoffLim.get(ptr, 0) as Short).toUShort()
		set(value) {
			DCB_XoffLim.set(ptr, 0, value.toShort())
		}
	var dataBits: UByte
		get() = (DCB_ByteSize.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_ByteSize.set(ptr, 0, value.toByte())
		}
	var parityScheme: MappedEnumeration<UByte, WindowsParityScheme>
		get() = WindowsParityScheme.entries.id((DCB_Parity.get(ptr, 0) as Byte).toUByte())
		set(value) {
			DCB_Parity.set(ptr, 0, value.raw.toByte())
		}
	var stopBits: MappedEnumeration<UByte, WindowsStopBits>
		get() = WindowsStopBits.entries.id((DCB_StopBits.get(ptr, 0) as Byte).toUByte())
		set(value) {
			DCB_StopBits.set(ptr, 0, value.raw.toByte())
		}
	var xONChar: UByte
		get() = (DCB_ErrorChar.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_ErrorChar.set(ptr, 0, value.toByte())
		}
	var xOFFChar: UByte
		get() = (DCB_XoffChar.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_XoffChar.set(ptr, 0, value.toByte())
		}
	var errorChar: UByte
		get() = (DCB_ErrorChar.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_ErrorChar.set(ptr, 0, value.toByte())
		}
	var eofChar: UByte
		get() = (DCB_EofChar.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_EofChar.set(ptr, 0, value.toByte())
		}
	var eventChar: UByte
		get() = (DCB_EvtChar.get(ptr, 0) as Byte).toUByte()
		set(value) {
			DCB_EvtChar.set(ptr, 0, value.toByte())
		}

	override fun toString(): String = "Device Communication Control" +
			"\n\tBaud Rate: $baudRate" +
			"\n\tBinary mode: $binaryMode" +
			"\n\tParity checking: $parityChecking" +
			"\n\tOutput flow monitors clear-to-send: $ctsMonitor" +
			"\n\tOutput flow monitors data-set-ready: $dsrMonitor" +
			"\n\tData-terminal-ready control: $dtrControl" +
			"\n\tData-set-ready sensitive: $dsrSensitive" +
			"\n\tTX after XOFF: $transmitAfterBufferFull" +
			"\n\tTX XON/XOFF flow control: $txXonXoffFlowControl" +
			"\n\tRX XON/OFF flow control: $rxXonXoffFlowControl" +
			"\n\tReplace parity errors: $replaceErrors" +
			"\n\tDiscard nulls: $discardNull" +
			"\n\tRequest-to-send control: $rtsControl" +
			"\n\tAbort communication on error: $abortCommunicationOnError" +
			"\n\tUsed bytes to XON: $usedBytesToXon" +
			"\n\tFree bytes to XOFF: $freeBytesToXoff" +
			"\n\tByte size in bits: $dataBits" +
			"\n\tParity scheme: $parityScheme" +
			"\n\tStop bits: $stopBits" +
			"\n\tXON char: 0x${xONChar.toHexString(HexFormat.UpperCase)}" +
			"\n\tXOFF char: 0x${xOFFChar.toHexString(HexFormat.UpperCase)}" +
			"\n\tError char: 0x${errorChar.toHexString(HexFormat.UpperCase)}" +
			"\n\tEOF char: 0x${eofChar.toHexString(HexFormat.UpperCase)}" +
			"\n\tEvent char: 0x${eventChar.toHexString(HexFormat.UpperCase)}"
}