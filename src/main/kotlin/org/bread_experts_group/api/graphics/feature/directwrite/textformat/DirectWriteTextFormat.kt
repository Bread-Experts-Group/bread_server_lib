package org.bread_experts_group.api.graphics.feature.directwrite.textformat

import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

data class DirectWriteTextFormat(internal val ptr: MemorySegment) {
	private val cleaner = Cleaner.create()

	init {
		cleaner.register(this) {
			IUnknown(ptr).release()
		}
	}
}