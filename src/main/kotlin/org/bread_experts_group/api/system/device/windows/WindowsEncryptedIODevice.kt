package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.transparent_encrpytion.OpenTransparentEncryptionRawIODeviceDataIdentifier
import org.bread_experts_group.ffi.windows.advapi.nativeCloseEncryptedFileRaw
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsEncryptedIODevice(handle: MemorySegment) : IODevice(), WindowsHandleSupplier,
	OpenTransparentEncryptionRawIODeviceDataIdentifier {
	companion object {
		val cleaner: Cleaner = Cleaner.create()

		class SegmentHolder(val handle: MemorySegment) : Runnable {
			override fun run() {
				nativeCloseEncryptedFileRaw!!.invokeExact(handle)
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