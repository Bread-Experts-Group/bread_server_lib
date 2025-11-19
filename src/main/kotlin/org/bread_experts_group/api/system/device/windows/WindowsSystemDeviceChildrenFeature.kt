package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceChildrenFeature(pathSegment: MemorySegment) : SystemDeviceChildrenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFindFirstFileExW != null && nativePathCchRemoveBackslash != null &&
			nativePathCchAppendEx != null && nativeFindNextFileW != null

	private val localArena = Arena.ofConfined()
	private val pathSegment = localArena.allocate(pathSegment.byteSize()).copyFrom(pathSegment)

	init {
		val status = nativePathCchRemoveBackslash!!.invokeExact(
			pathSegment,
			pathSegment.byteSize() / 2
		) as Int
		if (status != 1) throwLastError()
	}

	override fun iterator(): Iterator<SystemDevice> = object : Iterator<SystemDevice> {
		private val searchHandle: MemorySegment
		private val searchData: MemorySegment
		private val searchArena = Arena.ofAuto()
		private var nextPrepared = false

		init {
			searchData = searchArena.allocate(WIN32_FIND_DATAW)
			nextPrepared = true
			Arena.ofConfined().use { tempArena ->
				val wildcard = tempArena.allocate(pathSegment.byteSize() + 4).copyFrom(pathSegment)
				val append = tempArena.allocateFrom("\\*", Charsets.UTF_16LE)
				decodeWin32Error(
					nativePathCchAppendEx!!.invokeExact(
						wildcard,
						wildcard.byteSize() / 2,
						append,
						0x00000003 // TODO PathCchAppendEx flags
					) as Int
				)
				searchHandle = nativeFindFirstFileExW!!.invokeExact(
					capturedStateSegment,
					wildcard,
					0, // TODO INFO LEVEL
					searchData,
					0, // TODO SEARCH OP
					MemorySegment.NULL,
					0 // TODO SEARCH FLAGS
				) as MemorySegment
				if (searchHandle == INVALID_HANDLE_VALUE) throwLastError()
			}
		}

		override fun hasNext(): Boolean = nextPrepared
		override fun next(): SystemDevice = Arena.ofConfined().use {
			if (!nextPrepared) throw IllegalStateException()
			val fileName = WIN32_FIND_DATAW_cFileName.invokeExact(searchData, 0L) as MemorySegment
			val fullPath = it.allocate((pathSegment.byteSize() + fileName.byteSize()) + 4)
			fullPath.copyFrom(pathSegment)
			decodeWin32Error(
				nativePathCchAppendEx!!.invokeExact(
					fullPath,
					fullPath.byteSize() / 2,
					fileName,
					0x00000003 // TODO PathCchAppendEx flags
				) as Int
			)
			val status = nativeFindNextFileW!!.invokeExact(
				capturedStateSegment,
				searchHandle, searchData
			) as Int
			if (status == 0) {
				if (win32LastError == WindowsLastError.ERROR_NO_MORE_FILES.id.toInt()) nextPrepared = false
				else throwLastError()
			}
			createPathDevice(fullPath)
		}
	}
}