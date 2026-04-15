package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.ref.Cleaner

class WindowsSystemDeviceChildrenFeature(private val pathSegment: MemorySegment) : SystemDeviceChildrenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFindFirstFileExWide != null && nativePathCchRemoveBackslash != null &&
			nativePathCchAppendEx != null && nativeFindNextFileWide != null && nativeFindClose != null

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

		private fun advancePastDots() {
			val fileName = WIN32_FIND_DATAW_cFileName.invokeExact(searchData, 0L) as MemorySegment
			if (fileName.getAtIndex(ValueLayout.JAVA_SHORT, 0) == '.'.code.toShort()) {
				val s = fileName.getAtIndex(ValueLayout.JAVA_SHORT, 1)
				if (s == '.'.code.toShort()) {
					if (fileName.getAtIndex(ValueLayout.JAVA_SHORT, 2) == 0.toShort()) advance()
				} else if (s == 0.toShort()) advance()
			}
		}

		init {
			searchData = autoArena.allocate(WIN32_FIND_DATAW)
			val wildcard = autoArena.allocate(pathSegment.byteSize() + 4).copyFrom(pathSegment)
			val append = autoArena.allocateFrom("\\*", winCharsetWide)
			tryThrowWin32Error(
				nativePathCchAppendEx!!.invokeExact(
					wildcard,
					wildcard.byteSize() / 2,
					append,
					0x00000003 // TODO PathCchAppendEx flags
				) as Int
			)
			var newSearchHandle = nativeFindFirstFileExWide!!.invokeExact(
				capturedStateSegment,
				wildcard,
				0, // TODO INFO LEVEL
				searchData,
				0, // TODO SEARCH OP
				MemorySegment.NULL,
				0 // TODO SEARCH FLAGS
			) as MemorySegment

			fun processStartError() = when (win32LastError) {
				WindowsLastError.ERROR_DIRECTORY.id.toInt() -> {}
				else -> throwLastError()
			}

			fun setupHandle() {
				val holder = SegmentHolder(newSearchHandle)
				holder.nextPrepared = true
				searchHolder = holder
				cleanable = cleaner.register(this, holder)
				if (nextPrepared) advancePastDots()
			}

			if (newSearchHandle == INVALID_HANDLE_VALUE) when (win32LastError) {
				WindowsLastError.ERROR_PATH_NOT_FOUND.id.toInt() -> {
					val unc = autoArena.allocate(WCHAR.byteSize() * 4 + wildcard.byteSize())
					unc[ValueLayout.JAVA_CHAR, 0] = '\\'
					unc[ValueLayout.JAVA_CHAR, 2] = '\\'
					unc[ValueLayout.JAVA_CHAR, 4] = '?'
					unc[ValueLayout.JAVA_CHAR, 6] = '\\'
					unc.asSlice(8).copyFrom(wildcard)
					newSearchHandle = nativeFindFirstFileExWide.invokeExact(
						capturedStateSegment,
						unc,
						0, // TODO INFO LEVEL
						searchData,
						0, // TODO SEARCH OP
						MemorySegment.NULL,
						0 // TODO SEARCH FLAGS
					) as MemorySegment
					if (newSearchHandle == INVALID_HANDLE_VALUE) processStartError()
					else setupHandle()
				}

				else -> processStartError()
			} else setupHandle()
		}

		private fun advance() {
			val status = nativeFindNextFileWide!!.invokeExact(
				capturedStateSegment,
				searchHandle, searchData
			) as Int
			if (status == 0) {
				if (win32LastError == WindowsLastError.ERROR_NO_MORE_FILES.id.toInt()) return cleanable.clean()
				else throwLastError()
			}
			advancePastDots()
		}

		override fun hasNext(): Boolean = searchHolder != null && nextPrepared
		override fun next(): SystemDevice {
			if (!nextPrepared) throw IllegalStateException()
			val fileName = WIN32_FIND_DATAW_cFileName.invokeExact(searchData, 0L) as MemorySegment
			val fullPath = autoArena.allocate(
				(pathSegment.byteSize() + fileName.byteSize()) + (WCHAR.byteSize() * 2)
			)
			fullPath.copyFrom(pathSegment)
			tryThrowWin32Error(
				nativePathCchAppendEx!!.invokeExact(
					fullPath,
					fullPath.byteSize() / WCHAR.byteSize(),
					fileName,
					0x00000003 // TODO PathCchAppendEx flags
				) as Int
			)
			advance()
			return winCreatePathDevice(fullPath)
		}
	}
}