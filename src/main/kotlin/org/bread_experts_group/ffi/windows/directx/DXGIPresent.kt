package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.generic.Flaggable

enum class DXGIPresent(override val position: Long) : Flaggable {
	DXGI_PRESENT_TEST(0x1),
	DXGI_PRESENT_DO_NOT_SEQUENCE(0x2),
	DXGI_PRESENT_RESTART(0x4),
	DXGI_PRESENT_DO_NOT_WAIT(0x8),
	DXGI_PRESENT_RESTRICT_TO_OUTPUT(0x10),
	DXGI_PRESENT_STEREO_PREFER_RIGHT(0x20),
	DXGI_PRESENT_STEREO_TEMPORARY_MONO(0x40),
	DXGI_PRESENT_USE_DURATION(0x100),
	DXGI_PRESENT_ALLOW_TEARING(0x200)
}