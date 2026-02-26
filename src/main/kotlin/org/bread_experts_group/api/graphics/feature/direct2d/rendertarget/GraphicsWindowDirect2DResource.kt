package org.bread_experts_group.api.graphics.feature.direct2d.rendertarget

import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment

open class GraphicsWindowDirect2DResource(
	handle: MemorySegment
) : IUnknown(
	handle
) {
	var getFactory = {
		TODO("Nyi")
	}
}