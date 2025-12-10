package org.bread_experts_group.api.system.io.linux

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceWriteFeature
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.nativeWrite
import org.bread_experts_group.ffi.posix.throwLastErrno
import java.lang.foreign.MemorySegment
import java.util.concurrent.TimeUnit

class LinuxIODeviceWriteFeature(
	private val handle: Int
) : IODeviceWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	// TODO ascertain write
	override fun supported(): Boolean = true

	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IOSendFeatureIdentifier
	): DeferredOperation<IOSendDataIdentifier> = object : DeferredOperation<IOSendDataIdentifier> {
		fun write(): List<IOSendDataIdentifier> {
			var size = 0L
			data.forEach { segment ->
				val writtenSize = nativeWrite!!.invokeExact(
					capturedStateSegment,
					handle,
					segment,
					segment.byteSize()
				) as Long
				if (writtenSize == -1L) throwLastErrno()
				size += writtenSize
			}
			return listOf(SendSizeData(size))
		}

		override fun block(): List<IOSendDataIdentifier> = write()
		override fun block(time: Long, unit: TimeUnit): List<IOSendDataIdentifier> = write()
	}

}