package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.ref.Cleaner

class WindowsSystemDeviceChildrenFeature(private val pathSegment: MemorySegment) : SystemDeviceChildrenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFindFirstFileExWide != null && nativePathCchRemoveBackslash != null &&
			nativePathCchAppendEx != null && nativeFindNextFileWide != null && nativeFindClose != null

	private val cleaner: Cleaner = Cleaner.create()
	override fun iterator(): Iterator<SystemDevice> = object : Iterator<SystemDevice> {
		private val searchHandle: MemorySegment
		private val searchData: MemorySegment
		private val searchArena = Arena.ofConfined()
		private var nextPrepared = false

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
			var sH: MemorySegment
			var sD: MemorySegment
			try {
				sD = searchArena.allocate(WIN32_FIND_DATAW)
				Arena.ofConfined().use { tempArena ->
					val wildcard = tempArena.allocate(pathSegment.byteSize() + 4).copyFrom(pathSegment)
					val append = tempArena.allocateFrom("\\*", winCharsetWide)
					decodeWin32Error(
						nativePathCchAppendEx!!.invokeExact(
							wildcard,
							wildcard.byteSize() / 2,
							append,
							0x00000003 // TODO PathCchAppendEx flags
						) as Int
					)
					sH = nativeFindFirstFileExWide!!.invokeExact(
						capturedStateSegment,
						wildcard,
						0, // TODO INFO LEVEL
						sD,
						0, // TODO SEARCH OP
						MemorySegment.NULL,
						0 // TODO SEARCH FLAGS
					) as MemorySegment
					if (sH == INVALID_HANDLE_VALUE) throwLastError()
				}
				cleaner.register(this) { cleanup() }
				nextPrepared = true
			} catch (e: WindowsLastErrorException) {
				nextPrepared = false
				searchArena.close()
				sH = MemorySegment.NULL
				sD = MemorySegment.NULL
				if (e.error.enum != WindowsLastError.ERROR_DIRECTORY) throw e
			}
			searchHandle = sH
			searchData = sD
			if (nextPrepared) advancePastDots()
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
			val status = nativeFindNextFileWide!!.invokeExact(
				capturedStateSegment,
				searchHandle, searchData
			) as Int
			if (status == 0) {
				if (win32LastError == WindowsLastError.ERROR_NO_MORE_FILES.id.toInt()) return cleanup()
				else throwLastError()
			}
			advancePastDots()
		}

		override fun hasNext(): Boolean = nextPrepared
		override fun next(): SystemDevice {
			if (!nextPrepared) throw IllegalStateException()
			val fileName = WIN32_FIND_DATAW_cFileName.invokeExact(searchData, 0L) as MemorySegment
			val pathArena = Arena.ofShared()
			val fullPath = pathArena.allocate((pathSegment.byteSize() + fileName.byteSize()) + 4)
			fullPath.copyFrom(pathSegment)
			decodeWin32Error(
				nativePathCchAppendEx!!.invokeExact(
					fullPath,
					fullPath.byteSize() / 2,
					fileName,
					0x00000003 // TODO PathCchAppendEx flags
				) as Int
			)
			advance()
			return winCreatePathDevice(pathArena, fullPath)
		}
	}
}