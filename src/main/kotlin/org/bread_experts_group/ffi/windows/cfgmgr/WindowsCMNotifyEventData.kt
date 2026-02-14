package org.bread_experts_group.ffi.windows.cfgmgr

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.ffi.GUID
import java.lang.foreign.MemorySegment

sealed class WindowsCMNotifyEventData(
	val filterType: MappedEnumeration<UInt, WindowsCMNotifyFilterType>
) {
	class DeviceInterface(
		val guid: GUID,
		val symbolicLink: MemorySegment
	) : WindowsCMNotifyEventData(
		MappedEnumeration(WindowsCMNotifyFilterType.DEVICE_INTERFACE)
	)

	class DeviceHandle(
		val eventGUID: GUID,
		val nameOffset: Int,
		val data: MemorySegment
	) : WindowsCMNotifyEventData(
		MappedEnumeration(WindowsCMNotifyFilterType.DEVICE_HANDLE)
	)

	class DeviceInstance(
		val instanceID: MemorySegment
	) : WindowsCMNotifyEventData(
		MappedEnumeration(WindowsCMNotifyFilterType.DEVICE_INSTANCE)
	)
}