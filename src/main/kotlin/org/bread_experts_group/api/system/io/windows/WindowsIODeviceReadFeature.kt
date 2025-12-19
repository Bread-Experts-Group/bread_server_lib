package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceReadFeature
import org.bread_experts_group.api.system.io.receive.IOReceiveDataIdentifier
import org.bread_experts_group.api.system.io.receive.IOReceiveFeatureIdentifier
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeReadFile
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.util.concurrent.TimeUnit

class WindowsIODeviceReadFeature(private val handle: MemorySegment) : IODeviceReadFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReadFile != null

	// TODO: Asynchronous I/O
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IOReceiveFeatureIdentifier
	): DeferredOperation<IOReceiveDataIdentifier> = object : DeferredOperation<IOReceiveDataIdentifier> {
		private fun read(): List<IOReceiveDataIdentifier> {
			var read = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeReadFile!!.invokeExact(
					capturedStateSegment,
					handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					MemorySegment.NULL
				) as Int
				if (status == 0) throwLastError()
				read += threadLocalDWORD0.get(DWORD, 0)
			}
			return listOf(ReceiveSizeData(read))
		}

		override fun block(): List<IOReceiveDataIdentifier> = read()
		override fun block(time: Long, unit: TimeUnit): List<IOReceiveDataIdentifier> = read()
	}
}