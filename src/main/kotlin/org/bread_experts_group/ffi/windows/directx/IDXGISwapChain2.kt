package org.bread_experts_group.ffi.windows.directx

import java.lang.foreign.MemorySegment

open class IDXGISwapChain2(
	ptr: MemorySegment
) : IDXGISwapChain1(
	ptr
) {
	var setSourceSize = {
		TODO("Not yet implemented")
	}

	var getSourceSize = {
		TODO("Not yet implemented")
	}

	var setMaximumFrameLatency = {
		TODO("Not yet implemented")
	}

	var getMaximumFrameLatency = {
		TODO("Not yet implemented")
	}

	var getFrameLatencyWaitableObject = {
		TODO("Not yet implemented")
	}

	var setMatrixTransform = {
		TODO("Not yet implemented")
	}

	var getMatrixTransform = {
		TODO("Not yet implemented")
	}

}