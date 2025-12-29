package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceWriteFeature
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.iovec
import org.bread_experts_group.ffi.posix.linux.x64.iovec_iov_base
import org.bread_experts_group.ffi.posix.linux.x64.iovec_iov_len
import org.bread_experts_group.ffi.posix.linux.x64.nativeWriteV
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class LinuxX64IODeviceWriteFeature(
	private val fd: Int
) : IODeviceWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeWriteV != null

	// TODO: Asynchronous I/O
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IOSendFeatureIdentifier
	): DeferredOperation<IOSendDataIdentifier> {
		val read: Long
		Arena.ofConfined().use { tempArena ->
			val iovecs = tempArena.allocate(iovec, data.size.toLong())
			var iovecsOffset = iovecs
			data.forEach {
				iovec_iov_base.set(iovecsOffset, 0L, it)
				iovec_iov_len.set(iovecsOffset, 0L, it.byteSize())
				iovecsOffset = iovecsOffset.asSlice(iovec.byteSize())
			}
			read = nativeWriteV!!.invokeExact(
				capturedStateSegment,
				fd,
				iovecs,
				data.size
			) as Long
			if (read == -1L) throwLastErrno()
		}

		return DeferredOperation.Immediate(listOf(SendSizeData(read)))
	}
}