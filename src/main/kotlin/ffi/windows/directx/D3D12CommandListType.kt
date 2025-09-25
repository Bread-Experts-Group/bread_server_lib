package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12CommandListType(
	override val id: UInt
) : Mappable<D3D12CommandListType, UInt> {
	D3D12_COMMAND_LIST_TYPE_DIRECT(0u),
	D3D12_COMMAND_LIST_TYPE_BUNDLE(1u),
	D3D12_COMMAND_LIST_TYPE_COMPUTE(2u),
	D3D12_COMMAND_LIST_TYPE_COPY(3u),
	D3D12_COMMAND_LIST_TYPE_VIDEO_DECODE(4u),
	D3D12_COMMAND_LIST_TYPE_VIDEO_PROCESS(5u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_COMMAND_LIST_TYPE: ValueLayout.OfInt = ValueLayout.JAVA_INT