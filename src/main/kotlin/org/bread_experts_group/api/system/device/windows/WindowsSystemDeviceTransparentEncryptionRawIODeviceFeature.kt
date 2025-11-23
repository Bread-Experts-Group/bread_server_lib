package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceTransparentEncryptionRawIODeviceFeature
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.IODeviceFeatureImplementation
import org.bread_experts_group.api.system.device.io.feature.IODeviceReadCallbackFeature
import org.bread_experts_group.api.system.device.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.device.io.transparent_encrpytion.OpenTransparentEncryptionRawIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.io.transparent_encrpytion.WindowsOpenTransparentEncryptionRawIODeviceFeatures
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class WindowsSystemDeviceTransparentEncryptionRawIODeviceFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceTransparentEncryptionRawIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeOpenEncryptedFileRawW != null

	override fun open(
		vararg features: OpenTransparentEncryptionRawIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenTransparentEncryptionRawIODeviceFeatureIdentifier>>? {
		val supportedFeatures = mutableListOf<OpenTransparentEncryptionRawIODeviceFeatureIdentifier>()
		return when {
			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.EXPORT) -> {
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.EXPORT)
				decodeWin32Error(
					nativeOpenEncryptedFileRawW!!.invokeExact(
						pathSegment,
						0,
						threadLocalPTR
					) as Int
				)
				val encHandle = threadLocalPTR.get(PVOID, 0)
				object : IODevice() {
					override val features: MutableList<IODeviceFeatureImplementation<*>> = mutableListOf(
						object : IODeviceReleaseFeature() {
							override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
							override fun supported(): Boolean = nativeCloseEncryptedFileRaw != null
							override fun close() {
								nativeCloseEncryptedFileRaw!!.invokeExact(encHandle)
							}
						},
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
								decodeWin32Error(
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
				} to supportedFeatures
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_FILE) -> {
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_FILE)
				decodeWin32Error(
					nativeOpenEncryptedFileRawW!!.invokeExact(
						pathSegment,
						1,
						threadLocalPTR
					) as Int
				)
				TODO("!")
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_DIRECTORY) -> {
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.IMPORT_DIRECTORY)
				decodeWin32Error(
					nativeOpenEncryptedFileRawW!!.invokeExact(
						pathSegment,
						2,
						threadLocalPTR
					) as Int
				)
				TODO("!")
			}

			features.contains(WindowsOpenTransparentEncryptionRawIODeviceFeatures.OVERWRITE_HIDDEN) -> {
				supportedFeatures.add(WindowsOpenTransparentEncryptionRawIODeviceFeatures.OVERWRITE_HIDDEN)
				decodeWin32Error(
					nativeOpenEncryptedFileRawW!!.invokeExact(
						pathSegment,
						4,
						threadLocalPTR
					) as Int
				)
				TODO("!")
			}

			else -> object : IODevice() {
				override val features: MutableList<IODeviceFeatureImplementation<*>>
					get() = mutableListOf()
			} to emptyList()
		}
	}
}