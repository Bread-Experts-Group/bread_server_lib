package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceSetSizeFeature
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.size.StandardSetSizeFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeSetEndOfFile
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceSetSizeFeature(private val handle: MemorySegment) : IODeviceSetSizeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeSetEndOfFile != null

	override fun set(vararg features: SetSizeIODeviceFeatureIdentifier): List<SetSizeIODeviceDataIdentifier> {
		if (features.contains(StandardSetSizeFeatures.CURRENT_POSITION)) {
			val status = nativeSetEndOfFile!!.invokeExact(
				capturedStateSegment,
				handle
			) as Int
			if (status == 0) throwLastError()
			return listOf(StandardSetSizeFeatures.CURRENT_POSITION)
		}
		return emptyList()
	}
}