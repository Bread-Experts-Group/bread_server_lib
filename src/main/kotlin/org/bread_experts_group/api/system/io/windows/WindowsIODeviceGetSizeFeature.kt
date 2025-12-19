package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceGetSizeFeature
import org.bread_experts_group.api.system.io.size.DataSize
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeGetFileSizeEx
import org.bread_experts_group.ffi.windows.threadLocalLARGE_INTEGER0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIODeviceGetSizeFeature(private val handle: MemorySegment) : IODeviceGetSizeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetFileSizeEx != null

	override fun get(vararg features: GetSizeIODeviceFeatureIdentifier): List<GetSizeIODeviceDataIdentifier> {
		val status = nativeGetFileSizeEx!!.invokeExact(
			capturedStateSegment,
			handle,
			threadLocalLARGE_INTEGER0
		) as Int
		if (status == 0) throwLastError()
		return listOf(DataSize(threadLocalLARGE_INTEGER0.get(ValueLayout.JAVA_LONG, 0)))
	}
}