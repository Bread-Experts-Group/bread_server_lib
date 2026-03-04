package org.bread_experts_group.api.system.device.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.feature.SystemDeviceChildrenFeature
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.*
import org.bread_experts_group.ffi.posix.x64.errno
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.ref.Cleaner

class LinuxX64SystemDeviceChildrenFeature(private val pathSegment: MemorySegment) : SystemDeviceChildrenFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeOpenDir != null && nativeReadDir != null && nativeCloseDir != null

	private val cleaner: Cleaner = Cleaner.create()
	override fun iterator(): Iterator<SystemDevice> {
		val dirHandle = nativeOpenDir!!.invokeExact(capturedStateSegment, pathSegment) as MemorySegment
		if (dirHandle == MemorySegment.NULL) throwLastErrno()
		val cleanup = {
			val status = nativeCloseDir!!.invokeExact(capturedStateSegment, dirHandle) as Int
			if (status == -1) throwLastErrno()
		}

		val iterator = object : Iterator<SystemDevice> {
			var nextEntry: MemorySegment = readNext()

			fun readNext(): MemorySegment {
				errno = 0
				while (true) {
					val next = nativeReadDir!!.invokeExact(capturedStateSegment, dirHandle) as MemorySegment
					if (next == MemorySegment.NULL) when (errno) {
						0 -> {}
						11 -> return MemorySegment.NULL
						else -> throwLastErrno()
					}
					val ent = next.reinterpret(dirent.byteSize())
					val entName = dirent_d_name.invokeExact(ent, 0L) as MemorySegment
					if (entName.get(ValueLayout.JAVA_BYTE, 0) == '.'.code.toByte()) {
						val second = entName.get(ValueLayout.JAVA_BYTE, 1)
						when (second) {
							'.'.code.toByte() -> {
								if (entName.get(ValueLayout.JAVA_BYTE, 2) == 0.toByte())
									return readNext()
							}

							0.toByte() -> return readNext()
							else -> {}
						}
					}
					return ent
				}
			}

			override fun hasNext(): Boolean = nextEntry != MemorySegment.NULL
			override fun next(): SystemDevice {
				val device = linuxX64CreatePathDevice(
					linuxX64AppendPaths(
						pathSegment,
						dirent_d_name.invokeExact(nextEntry, 0L) as MemorySegment,
						autoArena
					)
				)
				nextEntry = readNext()
				if (!hasNext()) cleanup()
				return device
			}
		}
		cleaner.register(iterator, cleanup)
		return iterator
	}
}