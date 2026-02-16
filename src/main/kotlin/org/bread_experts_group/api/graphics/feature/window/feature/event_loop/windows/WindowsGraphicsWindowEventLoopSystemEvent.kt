package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.GraphicsWindowEventLoopEventParameter
import org.bread_experts_group.ffi.windows.WindowsWindowMessages
import org.bread_experts_group.generic.MappedEnumeration
import java.lang.foreign.MemorySegment

data class WindowsGraphicsWindowEventLoopSystemEvent(
	val hWnd: MemorySegment,
	val message: MappedEnumeration<Int, WindowsWindowMessages>,
	val wParam: Long,
	val lParam: Long
) : GraphicsWindowEventLoopEventParameter