package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionRawIODeviceFeature
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReadCallbackFeature
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.transparent_encrpytion.OpenTransparentEncryptionRawIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.transparent_encrpytion.WindowsOpenTransparentEncryptionRawIODeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.advapi.nativeCloseEncryptedFileRaw
import org.bread_experts_group.ffi.windows.advapi.nativeOpenEncryptedFileRawWide
import org.bread_experts_group.ffi.windows.advapi.nativeReadEncryptedFileRaw
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class WindowsSystemDeviceTransparentEncryptionRawIODeviceFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceTransparentEncryptionRawIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeOpenEncryptedFileRawWide != null

	override fun open(
		vararg features: OpenTransparentEncryptionRawIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenTransparentEncryptionRawIODeviceFeatureIdentifier>>? {
		val supportedFeatures = mutableListOf<OpenTransparentEncryptionRawIODeviceFeatureIdentifier>()
		return when {
			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.EXPORT) -> {
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.EXPORT)
				tryThrowWin32Error(
					nativeOpenEncryptedFileRawWide!!.invokeExact(
						capturedStateSegment,
						pathSegment,
						0,
						threadLocalPTR
					) as Int
				)
				val encHandle = threadLocalPTR.get(PVOID, 0)
				val ioDevice = IODevice()
				ioDevice.features.add(
					object : IODeviceReleaseFeature(
						ImplementationSource.SYSTEM_NATIVE,
						{ nativeCloseEncryptedFileRaw!!.invokeExact(encHandle) }
					) {
						override fun supported(): Boolean = true
					}
				)
				ioDevice.features.add(
					object : IODeviceReadCallbackFeature() {
						override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
						override fun supported(): Boolean = nativeReadEncryptedFileRaw != null
						private var internal: (MemorySegment) -> Unit = {}

						@Suppress("unused")
						fun decode(
							pbData: MemorySegment,
							pvCallbackContext: MemorySegment,
							ulLength: Int
						): Int {
							internal(pbData.reinterpret(ulLength.toLong()))
							return 0
						}

						override fun read(into: (MemorySegment) -> Unit) {
							internal = into
							val arena = Arena.ofConfined()
							tryThrowWin32Error(
								nativeReadEncryptedFileRaw!!.invokeExact(
									nativeLinker.upcallStub(
										MethodHandles.lookup().findSpecial(
											this::class.java, "decode",
											MethodType.methodType(
												Int::class.java,
												MemorySegment::class.java,
												MemorySegment::class.java,
												Int::class.java
											), this::class.java
										).bindTo(this),
										FunctionDescriptor.of(
											DWORD,
											PBYTE, PVOID, ULONG
										),
										arena
									),
									MemorySegment.NULL,
									encHandle
								) as Int
							)
							arena.close()
						}
					}
				)
				ioDevice to supportedFeatures
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_FILE) -> {
				tryThrowWin32Error(
					nativeOpenEncryptedFileRawWide!!.invokeExact(
						capturedStateSegment,
						pathSegment,
						1,
						threadLocalPTR
					) as Int
				)
				val encHandle = threadLocalPTR.get(PVOID, 0)
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_FILE)
				TODO("!")
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_DIRECTORY) -> {
				tryThrowWin32Error(
					nativeOpenEncryptedFileRawWide!!.invokeExact(
						capturedStateSegment,
						pathSegment,
						2,
						threadLocalPTR
					) as Int
				)
				val encHandle = threadLocalPTR.get(PVOID, 0)
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_DIRECTORY)
				TODO("!")
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.OVERWRITE_HIDDEN) -> {
				tryThrowWin32Error(
					nativeOpenEncryptedFileRawWide!!.invokeExact(
						capturedStateSegment,
						pathSegment,
						4,
						threadLocalPTR
					) as Int
				)
				val encHandle = threadLocalPTR.get(PVOID, 0)
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.OVERWRITE_HIDDEN)
				TODO("!")
			}

			else -> IODevice() to emptyList()
		}
	}
}