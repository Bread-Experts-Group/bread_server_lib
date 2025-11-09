package org.bread_experts_group.ffi.windows.cfgmgr

import org.bread_experts_group.Mappable

enum class WindowsCMNotifyAction(
	override val id: UInt
) : Mappable<WindowsCMNotifyAction, UInt> {
	DEVICE_INTERFACE_ARRIVAL(0u),
	DEVICE_INTERFACE_REMOVAL(1u),
	DEVICE_QUERY_REMOVE(2u),
	DEVICE_QUERY_REMOVE_FAILED(3u),
	DEVICE_REMOVE_PENDING(4u),
	DEVICE_REMOVE_COMPLETE(5u),
	DEVICE_CUSTOM_EVENT(6u),
	DEVICE_INSTANCE_ENUMERATED(7u),
	DEVICE_INSTANCE_STARTED(8u),
	DEVICE_INSTANCE_REMOVED(9u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}