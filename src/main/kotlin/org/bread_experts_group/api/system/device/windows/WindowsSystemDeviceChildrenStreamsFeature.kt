package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenStreamsFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.getFirstNull2Offset
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsSystemDeviceChildrenStreamsFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceChildrenStreamsFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFindFirstStreamW != null && nativeFindClose != null &&
			nativeFindNextStreamW != null

	private val cleaner: Cleaner = Cleaner.create()
	override fun iterator(): Iterator<SystemDevice> = object : Iterator<SystemDevice> {
		private val searchHandle: MemorySegment
		private val searchData: MemorySegment
		private val searchArena = Arena.ofConfined()
		private var nextPrepared = false

		init {
			var sH: MemorySegment
			var sD: MemorySegment
			try {
				sD = searchArena.allocate(WIN32_FIND_STREAM_DATA)
				sH = nativeFindFirstStreamW!!.invokeExact(
					capturedStateSegment,
					pathSegment,
					0, // TODO INFO LEVEL
					sD,
					0
				) as MemorySegment
				if (sH == INVALID_HANDLE_VALUE) throwLastError()
				cleaner.register(this) { cleanup() }
				nextPrepared = true
			} catch (e: WindowsLastErrorException) {
				nextPrepared = false
				searchArena.close()
				sH = MemorySegment.NULL
				sD = MemorySegment.NULL
				if (e.error.enum != WindowsLastError.ERROR_HANDLE_EOF) throw e
			}
			searchHandle = sH
			searchData = sD
		}

		private fun cleanup() {
			nextPrepared = false
			searchArena.close()
			val status = nativeFindClose!!.invokeExact(
				capturedStateSegment,
				searchHandle
			) as Int
			if (status == 0) throwLastError()
		}

		private fun advance() {
			val status = nativeFindNextStreamW!!.invokeExact(
				capturedStateSegment,
				searchHandle, searchData
			) as Int
			if (status == 0) {
				if (win32LastError == WindowsLastError.ERROR_HANDLE_EOF.id.toInt()) cleanup()
				else throwLastError()
			}
		}

		override fun hasNext(): Boolean = nextPrepared
		override fun next(): SystemDevice {
			if (!nextPrepared) throw IllegalStateException()
			val streamName = WIN32_FIND_STREAM_DATA_cStreamName.invokeExact(searchData, 0L) as MemorySegment
			val pathArena = Arena.ofShared()
			val fullPath = pathArena.allocate((pathSegment.byteSize() + streamName.byteSize())).copyFrom(pathSegment)
			fullPath.asSlice(fullPath.getFirstNull2Offset()).copyFrom(streamName)
			advance()
			return winCreatePathDevice(pathArena, fullPath)
		}
	}
}