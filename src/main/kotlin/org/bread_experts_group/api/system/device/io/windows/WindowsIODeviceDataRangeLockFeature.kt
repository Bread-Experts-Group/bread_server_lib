package org.bread_experts_group.api.system.device.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.feature.IODeviceDataRangeLockFeature
import org.bread_experts_group.api.system.device.io.feature.lock.IODeviceDataRangeLockHandle
import org.bread_experts_group.api.system.device.io.lock.IODeviceDataRangeLockFeatureIdentifier
import org.bread_experts_group.api.system.device.io.lock.WindowsIODeviceDataRangeLockFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.OVERLAPPED
import org.bread_experts_group.ffi.windows.nativeLockFileEx
import org.bread_experts_group.ffi.windows.nativeUnlockFileEx
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsIODeviceDataRangeLockFeature(
	private val handle: MemorySegment
) : IODeviceDataRangeLockFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeLockFileEx != null && nativeUnlockFileEx != null

	override fun acquire(
		length: Long,
		vararg features: IODeviceDataRangeLockFeatureIdentifier
	): Pair<IODeviceDataRangeLockHandle, List<IODeviceDataRangeLockFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<IODeviceDataRangeLockFeatureIdentifier>()
		var flags = 0
		if (features.contains(WindowsIODeviceDataRangeLockFeatures.EXCLUSIVE)) {
			flags = flags or 0x00000002
			supportedFeatures.add(WindowsIODeviceDataRangeLockFeatures.EXCLUSIVE)
		}
		if (!features.contains(WindowsIODeviceDataRangeLockFeatures.WAIT_UNTIL_AVAILABLE)) {
			flags = flags or 0x00000001
			supportedFeatures.add(WindowsIODeviceDataRangeLockFeatures.WAIT_UNTIL_AVAILABLE)
		}
		val arena = Arena.ofConfined()
		val overlapped = arena.allocate(OVERLAPPED)
		val status = nativeLockFileEx!!.invokeExact(
			capturedStateSegment,
			handle,
			flags,
			0,
			(length and 0xFFFFFFFF).toInt(),
			(length shr 32).toInt(),
			overlapped
		) as Int
		if (status == 0) throwLastError()
		return object : IODeviceDataRangeLockHandle() {
			override fun release() {
				val status = nativeUnlockFileEx!!.invokeExact(
					capturedStateSegment,
					handle,
					0,
					(length and 0xFFFFFFFF).toInt(),
					(length shr 32).toInt(),
					overlapped
				) as Int
				if (status == 0) throwLastError()
				arena.close()
			}
		} to supportedFeatures
	}
}