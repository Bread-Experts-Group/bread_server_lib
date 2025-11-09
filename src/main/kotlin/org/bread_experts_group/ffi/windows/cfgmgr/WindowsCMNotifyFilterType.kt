package org.bread_experts_group.ffi.windows.cfgmgr

import org.bread_experts_group.Mappable

enum class WindowsCMNotifyFilterType(
	override val id: UInt
) : Mappable<WindowsCMNotifyFilterType, UInt> {
	DEVICE_INTERFACE(0u),
	DEVICE_HANDLE(1u),
	DEVICE_INSTANCE(2u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}