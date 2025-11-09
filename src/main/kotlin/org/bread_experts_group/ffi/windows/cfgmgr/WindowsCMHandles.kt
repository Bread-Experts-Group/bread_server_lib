package org.bread_experts_group.ffi.windows.cfgmgr

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val cfgMgr32Lookup: SymbolLookup? = globalArena.getLookup("CfgMgr32.dll")

val CONFIGRET = DWORD
val nativeCM_Register_Notification: MethodHandle? = cfgMgr32Lookup.getDowncall(
	nativeLinker, "CM_Register_Notification",
	arrayOf(
		CONFIGRET,
		ValueLayout.ADDRESS /* of CM_NOTIFY_FILTER */, PVOID, ValueLayout.ADDRESS /* of CM_NOTIFY_CALLBACK */,
		ValueLayout.ADDRESS /* of HCMNOTIFICATION */
	),
	listOf()
)

val nativeCM_Unregister_Notification: MethodHandle? = cfgMgr32Lookup.getDowncall(
	nativeLinker, "CM_Unregister_Notification",
	arrayOf(
		CONFIGRET,
		ValueLayout.ADDRESS /* of HCMNOTIFICATION */
	),
	listOf()
)

val nativeCM_Get_Device_Interface_PropertyW: MethodHandle? = cfgMgr32Lookup.getDowncall(
	nativeLinker, "CM_Get_Device_Interface_PropertyW",
	arrayOf(
		CONFIGRET,
		LPCWSTR, ValueLayout.ADDRESS /* of DEVPROPKEY */, ValueLayout.ADDRESS /* of DEVPROPTYPE */,
		PBYTE, PULONG, ULONG
	),
	listOf()
)

val nativeCM_Get_Device_Interface_List_SizeW: MethodHandle? = cfgMgr32Lookup.getDowncall(
	nativeLinker, "CM_Get_Device_Interface_List_SizeW",
	arrayOf(
		CONFIGRET,
		PULONG, ValueLayout.ADDRESS /* of GUID */, ValueLayout.ADDRESS /* of DEWINSTID_W */, ULONG
	),
	listOf()
)

val nativeCM_Get_Device_Interface_ListW: MethodHandle? = cfgMgr32Lookup.getDowncall(
	nativeLinker, "CM_Get_Device_Interface_ListW",
	arrayOf(
		CONFIGRET,
		ValueLayout.ADDRESS /* of GUID */, ValueLayout.ADDRESS /* of DEVINSTID_W */,
		ValueLayout.ADDRESS /* of ZZWSTR */, ULONG, ULONG
	),
	listOf()
)