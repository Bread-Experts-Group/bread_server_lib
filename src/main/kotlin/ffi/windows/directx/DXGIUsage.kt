package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Flaggable
import java.lang.foreign.ValueLayout

enum class DXGIUsage(override val position: Long) : Flaggable {
	DXGI_USAGE_SHADER_INPUT(1 shl (0 + 4)),
	DXGI_USAGE_RENDER_TARGET_OUTPUT(1 shl (1 + 4)),
	DXGI_USAGE_BACK_BUFFER(1 shl (2 + 4)),
	DXGI_USAGE_SHARED(1 shl (3 + 4)),
	DXGI_USAGE_READ_ONLY(1 shl (4 + 4)),
	DXGI_USAGE_DISCARD_ON_PRESENT(1 shl (5 + 4)),
	DXGI_USAGE_UNORDERED_ACCESS(1 shl (6 + 4))
}

val DXGI_USAGE: ValueLayout.OfInt = ValueLayout.JAVA_INT