package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceWriteFeature
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeWriteFile
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.util.concurrent.TimeUnit

class WindowsIODeviceWriteFeature(
	private val handle: MemorySegment
) : IODeviceWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		threadLocalDWORD0.set(DWORD, 0, 0)
		val status = (nativeWriteFile ?: return false).invokeExact(
			capturedStateSegment,
			handle,
			threadLocalDWORD0,
			0,
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		return status != 0
	}

	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IOSendFeatureIdentifier
	): DeferredOperation<IOSendDataIdentifier> = object : DeferredOperation<IOSendDataIdentifier> {
		fun write(): List<IOSendDataIdentifier> {
			var size = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeWriteFile!!.invokeExact(
					capturedStateSegment,
					handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					MemorySegment.NULL
				) as Int
				if (status == 0) throwLastError()
				size += threadLocalDWORD0.get(DWORD, 0)
			}
			return listOf(SendSizeData(size))
		}

		override fun block(): List<IOSendDataIdentifier> = write()
		override fun block(time: Long, unit: TimeUnit): List<IOSendDataIdentifier> = write()
	}

}