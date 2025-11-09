package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceEnumerationFeature
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.WCHAR
import org.bread_experts_group.ffi.windows.cfgmgr.GUID_DEVINTERFACE_COMPORT
import org.bread_experts_group.ffi.windows.cfgmgr.GUID_DEVINTERFACE_COMPORT_Segment
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Get_Device_Interface_ListW
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Get_Device_Interface_List_SizeW
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceEnumerationFeature : SystemDeviceEnumerationFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO supported
	override fun enumerate(type: SystemDeviceType?): Iterable<SystemDevice> = Arena.ofConfined().use { tempArena ->
		var status = nativeCM_Get_Device_Interface_List_SizeW!!.invokeExact(
			threadLocalDWORD0,
			GUID_DEVINTERFACE_COMPORT_Segment, // TODO: type
			MemorySegment.NULL,
			0x00000000 // TODO CM_GET_DEVICE_INTERFACE_LIST_PRESENT
		) as Int
		if (status != 0) TODO("CM Errors $status")
		val buffer = tempArena.allocate(threadLocalDWORD0.get(DWORD, 0).toLong() * WCHAR.byteSize())
		status = nativeCM_Get_Device_Interface_ListW!!.invokeExact(
			GUID_DEVINTERFACE_COMPORT_Segment, // TODO: type
			MemorySegment.NULL,
			buffer,
			buffer.byteSize().toInt(),
			0x00000000 // TODO CM_GET_DEVICE_INTERFACE_LIST_PRESENT
		) as Int
		if (status != 0) TODO("CM Errors $status")
		val list = mutableListOf<SystemDevice>() // TODO Lazy
		var offset = 0L
		while (offset < buffer.byteSize()) {
			var size = 0L
			while ((offset + size) < buffer.byteSize()) {
				val char = buffer.get(WCHAR, offset + size)
				size += 2
				if (char == 0.toShort()) break
			}
			if (size == 2L) break
			list.add(decodeDevice(GUID_DEVINTERFACE_COMPORT, buffer.asSlice(offset, size), tempArena))
			offset += size
		}
		return list
	}
}