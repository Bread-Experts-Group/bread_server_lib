package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceFlushFeature
import org.bread_experts_group.api.system.io.flush.FlushIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeFlushFileBuffers
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIODeviceFlushFeature(
	private val handle: MemorySegment
) : IODeviceFlushFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFlushFileBuffers != null

	override fun flush(
		vararg features: FlushIODeviceFeatureIdentifier
	): List<FlushIODeviceFeatureIdentifier> {
		val status = nativeFlushFileBuffers!!.invokeExact(capturedStateSegment, handle) as Int
		if (status == 0) throwLastError()
		return emptyList()
	}
}