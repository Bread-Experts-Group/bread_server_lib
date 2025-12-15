package org.bread_experts_group.ffi.windows.cfgmgr

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.windows.GUID
import org.bread_experts_group.ffi.windows.ULONG
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val DEVPROPGUID = GUID
val DEVPROPID = ULONG
val DEVPROPKEY: StructLayout = MemoryLayout.structLayout(
	DEVPROPGUID.withName("fmtid"),
	DEVPROPID.withName("pid"),
)
val DEVPROPKEY_fmtid: MethodHandle = DEVPROPKEY.sliceHandle(groupElement("fmtid"))
val DEVPROPKEY_pid: VarHandle = DEVPROPKEY.varHandle(groupElement("pid"))

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_COMPORT = GUID(
	0x86E0D1E0u,
	0x8089u,
	0x11D0u,
	ubyteArrayOf(0x9Cu, 0xE4u),
	ubyteArrayOf(0x08u, 0x00u, 0x3Eu, 0x30u, 0x1Fu, 0x73u)
)

val GUID_DEVINTERFACE_COMPORT_Segment = GUID_DEVINTERFACE_COMPORT.allocate(globalArena)

@OptIn(ExperimentalUnsignedTypes::class)
val DEVPKEY_Device_FriendlyName: MemorySegment = globalArena.allocate(DEVPROPKEY).also {
	GUID(
		0xA45C254Eu,
		0xDF1Cu,
		0x4EFDu,
		ubyteArrayOf(0x80u, 0x20u),
		ubyteArrayOf(0x67u, 0xD1u, 0x46u, 0xA8u, 0x50u, 0xE0u)
	).allocate(DEVPROPKEY_fmtid.invokeExact(it, 0L) as MemorySegment)
	DEVPROPKEY_pid.set(it, 0, 14)
}

@OptIn(ExperimentalUnsignedTypes::class)
val DEVPKEY_Device_InstanceId: MemorySegment = globalArena.allocate(DEVPROPKEY).also {
	GUID(
		0x78C34FC8u,
		0x104Au,
		0x4ACAu,
		ubyteArrayOf(0x9Eu, 0xA4u),
		ubyteArrayOf(0x52u, 0x4Du, 0x52u, 0x99u, 0x6Eu, 0x57u)
	).allocate(DEVPROPKEY_fmtid.invokeExact(it, 0L) as MemorySegment)
	DEVPROPKEY_pid.set(it, 0, 256)
}

@OptIn(ExperimentalUnsignedTypes::class)
val DEVPKEY_DeviceInterface_Serial_PortName: MemorySegment = globalArena.allocate(DEVPROPKEY).also {
	GUID(
		0x4C6BF15Cu,
		0x4C03u,
		0x4AACu,
		ubyteArrayOf(0x91u, 0xF5u),
		ubyteArrayOf(0x64u, 0xC0u, 0xF8u, 0x52u, 0xBCu, 0xF4u)
	).allocate(DEVPROPKEY_fmtid.invokeExact(it, 0L) as MemorySegment)
	DEVPROPKEY_pid.set(it, 0, 4)
}