package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.generic.Mappable
import java.lang.foreign.ValueLayout

enum class D3D12CommandQueuePriority(
	override val id: UInt
) : Mappable<D3D12CommandQueuePriority, UInt> {
	D3D12_COMMAND_QUEUE_PRIORITY_NORMAL(0u),
	D3D12_COMMAND_QUEUE_PRIORITY_HIGH(100u),
	D3D12_COMMAND_QUEUE_PRIORITY_GLOBAL_REALTIME(10000u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val D3D12_COMMAND_QUEUE_PRIORITY: ValueLayout.OfInt = ValueLayout.JAVA_INT