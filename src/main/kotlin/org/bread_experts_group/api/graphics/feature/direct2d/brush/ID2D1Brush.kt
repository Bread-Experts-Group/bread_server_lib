package org.bread_experts_group.api.graphics.feature.direct2d.brush

import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DResource
import java.lang.foreign.MemorySegment

open class GraphicsWindowDirect2DBrush(
	handle: MemorySegment
) : GraphicsWindowDirect2DResource(
	handle
)