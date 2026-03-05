package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeCloseHandle
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsIODevice(handle: MemorySegment) : IODevice(), WindowsHandleSupplier {
	companion object {
		val cleaner: Cleaner = Cleaner.create()

		class SegmentHolder(val handle: MemorySegment) : Runnable {
			override fun run() {
				if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0) throwLastError()
			}
		}
	}

	private val holder: SegmentHolder = SegmentHolder(handle)
	override val handle: MemorySegment
		get() = holder.handle

	init {
		this.features.add(
			IODeviceReleaseFeature(
				ImplementationSource.SYSTEM_NATIVE,
				cleaner.register(this, holder)
			)
		)
	}
}