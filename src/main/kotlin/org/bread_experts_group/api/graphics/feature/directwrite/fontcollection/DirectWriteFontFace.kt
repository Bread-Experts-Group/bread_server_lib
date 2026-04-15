package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment

class DirectWriteFontFace(
	ptr: MemorySegment
) : IUnknown(
	ptr
)