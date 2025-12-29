package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceReadFeature
import org.bread_experts_group.api.system.io.receive.IOReceiveDataIdentifier
import org.bread_experts_group.api.system.io.receive.IOReceiveFeatureIdentifier
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.iovec
import org.bread_experts_group.ffi.posix.linux.x64.iovec_iov_base
import org.bread_experts_group.ffi.posix.linux.x64.iovec_iov_len
import org.bread_experts_group.ffi.posix.linux.x64.nativeReadV
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class LinuxX64IODeviceReadFeature(private val fd: Int) : IODeviceReadFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeReadV != null

	// TODO: Asynchronous I/O
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IOReceiveFeatureIdentifier
	): DeferredOperation<IOReceiveDataIdentifier> {
		val read: Long
		Arena.ofConfined().use { tempArena ->
			val iovecs = tempArena.allocate(iovec, data.size.toLong())
			var iovecsOffset = iovecs
			data.forEach {
				iovec_iov_base.set(iovecsOffset, 0L, it)
				iovec_iov_len.set(iovecsOffset, 0L, it.byteSize())
				iovecsOffset = iovecsOffset.asSlice(iovec.byteSize())
			}
			read = nativeReadV!!.invokeExact(
				capturedStateSegment,
				fd,
				iovecs,
				data.size
			) as Long
			if (read == -1L) throwLastErrno()
		}

		return DeferredOperation.Immediate(listOf(ReceiveSizeData(read)))
	}
}