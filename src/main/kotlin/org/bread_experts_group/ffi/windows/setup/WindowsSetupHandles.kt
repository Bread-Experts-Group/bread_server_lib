package org.bread_experts_group.ffi.windows.setup

import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val setupAPILookup: SymbolLookup? = globalArena.getLookup("Setupapi.dll")

val HDEVINFO = HANDLE
private val nativeSetupDiGetClassDevsWide: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiGetClassDevsW",
	arrayOf(
		HDEVINFO,
		ValueLayout.ADDRESS.withName("ClassGuid") /* of GUID */,
		LPCWSTR.withName("Enumerator"),
		HWND.withName("hwndParent"),
		DWORD.withName("Flags")
	),
	listOf(
		gleCapture
	)
)

private val nativeSetupDiGetClassDevsANSI: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiGetClassDevsA",
	arrayOf(
		HDEVINFO,
		ValueLayout.ADDRESS.withName("ClassGuid") /* of GUID */,
		LPCSTR.withName("Enumerator"),
		HWND.withName("hwndParent"),
		DWORD.withName("Flags")
	),
	listOf(
		gleCapture
	)
)

data class SetupDiGetClassDevsParameters(
	val classGuid: MemorySegment,
	val enumerator: String,
	val hwndParent: MemorySegment,
	val flags: Int
)

val nativeSetupDiGetClassDevs = codingSpecific(
	nativeSetupDiGetClassDevsANSI,
	nativeSetupDiGetClassDevsWide
) { handle, parameters: SetupDiGetClassDevsParameters ->
	Arena.ofConfined().use { tempArena ->
		val returns = handle.invokeExact(
			capturedStateSegment,
			parameters.classGuid,
			tempArena.allocateFrom(parameters.enumerator, winCharset),
			parameters.hwndParent,
			parameters.flags
		) as MemorySegment
		if (returns == INVALID_HANDLE_VALUE) throwLastError()
		returns
	}
}

private val nativeSetupDiOpenDeviceInfoWide: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiOpenDeviceInfoW",
	arrayOf(
		BOOL,
		HDEVINFO.withName("DeviceInfoSet"),
		PCWSTR.withName("DeviceInstanceId"),
		HWND.withName("hwndParent"),
		DWORD.withName("OpenFlags"),
		PSP_DEVINFO_DATA.withName("DeviceInfoData")
	),
	listOf(
		gleCapture
	)
)

private val nativeSetupDiOpenDeviceInfoANSI: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiOpenDeviceInfoA",
	arrayOf(
		BOOL,
		HDEVINFO.withName("DeviceInfoSet"),
		PCSTR.withName("DeviceInstanceId"),
		HWND.withName("hwndParent"),
		DWORD.withName("OpenFlags"),
		PSP_DEVINFO_DATA.withName("DeviceInfoData")
	),
	listOf(
		gleCapture
	)
)

data class SetupDiOpenDeviceInfoParameters(
	val deviceInfoSet: MemorySegment,
	val deviceInstanceId: String?,
	val hwndParent: MemorySegment,
	val openFlags: Int,
	val deviceInfoData: MemorySegment
)

val nativeSetupDiOpenDeviceInfo = codingSpecific(
	nativeSetupDiOpenDeviceInfoANSI,
	nativeSetupDiOpenDeviceInfoWide
) { handle, parameters: SetupDiOpenDeviceInfoParameters ->
	Arena.ofConfined().use { tempArena ->
		val status = handle.invokeExact(
			capturedStateSegment,
			parameters.deviceInfoSet,
			if (parameters.deviceInstanceId == null) MemorySegment.NULL
			else tempArena.allocateFrom(parameters.deviceInstanceId, winCharset),
			parameters.hwndParent,
			parameters.openFlags,
			parameters.deviceInfoData
		) as Int
		if (status == 0) throwLastError()
	}
}

val nativeSetupDiGetDevicePropertyWide: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiGetDevicePropertyW",
	arrayOf(
		BOOL,
		HDEVINFO, ValueLayout.ADDRESS /* of SP_DEVINFO_DATA */, ValueLayout.ADDRESS /* of DEVPROPKEY */,
		ValueLayout.ADDRESS /* of DEVPROPTYPE */, PBYTE, DWORD, PDWORD, DWORD
	),
	listOf(
		gleCapture
	)
)