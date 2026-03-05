package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.nativeClose
import java.lang.ref.Cleaner

class LinuxX64IODevice(fd: Int) : IODevice() {
	companion object {
		val cleaner: Cleaner = Cleaner.create()

		class FDHolder(val fd: Int) : Runnable {
			override fun run() {
				nativeClose!!.invokeExact(capturedStateSegment, fd) as Int
			}
		}
	}

	private val holder: FDHolder = FDHolder(fd)
	internal val fd: Int
		get() = holder.fd

	init {
		this.features.add(
			IODeviceReleaseFeature(
				ImplementationSource.SYSTEM_NATIVE,
				cleaner.register(this, holder)
			)
		)
	}
}