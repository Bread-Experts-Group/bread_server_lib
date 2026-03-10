package org.bread_experts_group.ffi.windows.ioctl

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class WindowsPartitionTypes(override val id: UInt) : Mappable<WindowsPartitionTypes, UInt> {
	PARTITION_STYLE_MBR(0u),
	PARTITION_STYLE_GPT(1u),
	PARTITION_STYLE_RAW(2u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val PARTITION_STYLE = DWORD