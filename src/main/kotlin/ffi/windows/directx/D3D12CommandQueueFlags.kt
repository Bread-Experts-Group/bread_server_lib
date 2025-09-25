package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Flaggable
import java.lang.foreign.ValueLayout

enum class D3D12CommandQueueFlags(override val position: Long) : Flaggable {
	D3D12_COMMAND_QUEUE_FLAG_DISABLE_GPU_TIMEOUT(0x1)
}

val D3D12_COMMAND_QUEUE_FLAGS: ValueLayout.OfInt = ValueLayout.JAVA_INT