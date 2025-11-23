package org.bread_experts_group.api.system.device.io.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.feature.IODeviceSeekFeature
import org.bread_experts_group.api.system.device.io.seek.SeekIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.io.seek.StandardSeekIODeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.LARGE_INTEGER
import org.bread_experts_group.ffi.windows.nativeSetFilePointerEx
import org.bread_experts_group.ffi.windows.threadLocalLARGE_INTEGER0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceSeekFeature(private val handle: MemorySegment) : IODeviceSeekFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSetFilePointerEx != null

	override fun seek(
		n: Long,
		vararg features: SeekIODeviceFeatureIdentifier
	): Pair<Long, List<SeekIODeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<SeekIODeviceFeatureIdentifier>()
		val status = nativeSetFilePointerEx!!.invokeExact(
			capturedStateSegment,
			handle,
			n,
			threadLocalLARGE_INTEGER0,
			when {
				features.contains(StandardSeekIODeviceFeatures.BEGIN) -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.BEGIN)
					0
				}

				features.contains(StandardSeekIODeviceFeatures.END) -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.END)
					2
				}

				else -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.CURRENT)
					1
				}
			}
		) as Int
		if (status == 0) throwLastError()
		return threadLocalLARGE_INTEGER0.get(LARGE_INTEGER, 0) to emptyList()
	}
}