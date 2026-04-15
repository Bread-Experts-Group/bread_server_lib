package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenStreamsFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.getFirstNull2Offset
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.ref.Cleaner

class WindowsSystemDeviceChildrenStreamsFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceChildrenStreamsFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFindFirstStreamWide != null && nativeFindClose != null &&
			nativeFindNextStreamWide != null

	private companion object {
		val cleaner: Cleaner = Cleaner.create()

		class SegmentHolder(val segment: MemorySegment) : Runnable {
			var nextPrepared = false
			override fun run() {
				val status = nativeFindClose!!.invokeExact(
					capturedStateSegment,
					segment
				) as Int
				if (status == 0) throwLastError()
				nextPrepared = false
			}
		}
	}

	override fun iterator(): Iterator<SystemDevice> = object : Iterator<SystemDevice> {
		private var searchHolder: SegmentHolder? = null
		private lateinit var cleanable: Cleaner.Cleanable
		private val searchHandle: MemorySegment
			get() = searchHolder!!.segment
		private val nextPrepared: Boolean
			get() = searchHolder!!.nextPrepared

		private val searchData: MemorySegment

		init {
			searchData = autoArena.allocate(WIN32_FIND_DATAW)
			var newSearchHandle = nativeFindFirstStreamWide!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				0, // TODO INFO LEVEL
				searchData,
				0
			) as MemorySegment

			fun processStartError() = when (win32LastError) {
				WindowsLastError.ERROR_HANDLE_EOF.id.toInt() -> {}
				else -> throwLastError()
			}

			fun setupHandle() {
				val holder = SegmentHolder(newSearchHandle)
				holder.nextPrepared = true
				searchHolder = holder
				cleanable = cleaner.register(this, holder)
			}

			if (newSearchHandle == INVALID_HANDLE_VALUE) when (win32LastError) {
				WindowsLastError.ERROR_PATH_NOT_FOUND.id.toInt() -> {
					val unc = autoArena.allocate(WCHAR.byteSize() * 4 + pathSegment.byteSize())
					unc[ValueLayout.JAVA_CHAR, 0] = '\\'
					unc[ValueLayout.JAVA_CHAR, 2] = '\\'
					unc[ValueLayout.JAVA_CHAR, 4] = '?'
					unc[ValueLayout.JAVA_CHAR, 6] = '\\'
					unc.asSlice(8).copyFrom(pathSegment)
					newSearchHandle = nativeFindFirstStreamWide.invokeExact(
						capturedStateSegment,
						unc,
						0, // TODO INFO LEVEL
						searchData,
						0
					) as MemorySegment
					if (newSearchHandle == INVALID_HANDLE_VALUE) processStartError()
					else setupHandle()
				}

				else -> processStartError()
			} else setupHandle()
		}

		private fun advance() {
			val status = nativeFindNextStreamWide!!.invokeExact(
				capturedStateSegment,
				searchHandle, searchData
			) as Int
			if (status == 0) {
				if (win32LastError == WindowsLastError.ERROR_HANDLE_EOF.id.toInt()) cleanable.clean()
				else throwLastError()
			}
		}

		override fun hasNext(): Boolean = searchHolder != null && nextPrepared
		override fun next(): SystemDevice {
			if (!nextPrepared) throw IllegalStateException()
			val streamName = WIN32_FIND_STREAM_DATA_cStreamName.invokeExact(searchData, 0L) as MemorySegment
			val fullPath = autoArena.allocate((pathSegment.byteSize() + streamName.byteSize())).copyFrom(pathSegment)
			fullPath.asSlice(fullPath.getFirstNull2Offset()).copyFrom(streamName)
			advance()
			return winCreatePathDevice(fullPath)
		}
	}
}