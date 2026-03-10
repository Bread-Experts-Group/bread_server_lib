package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getDesiredAccessO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getShareModeO
import org.bread_experts_group.api.system.io.open.*
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceIODeviceFeature2(
	private val pathSegment: MemorySegment
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateFile2 != null && nativeCloseHandle != null &&
			nativeCreateDirectoryWide != null

	override fun open(
		vararg features: OpenIODeviceFeatureIdentifier
	): List<OpenIODeviceDataIdentifier> = Arena.ofConfined().use { tempArena ->
		val data = mutableListOf<OpenIODeviceDataIdentifier>()

		@Suppress("UNCHECKED_CAST")
		val desiredAccess = getDesiredAccessO(features, data)
		val shareMode = getShareModeO(features, data)

		if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
			val parameters = tempArena.allocate(CREATEFILE2_EXTENDED_PARAMETERS)
			CREATEFILE2_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
			CREATEFILE2_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, getFlags(features, data))
			var handle = nativeCreateFile2!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				desiredAccess,
				shareMode,
				WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
				parameters
			) as MemorySegment
			try {
				if (handle == INVALID_HANDLE_VALUE) throwLastError()
			} catch (e: WindowsLastErrorException) {
				when (e.error.enum) {
					WindowsLastError.ERROR_FILE_NOT_FOUND -> if (features.contains(StandardIOOpenFeatures.CREATE)) {
						val status = nativeCreateDirectoryWide!!.invokeExact(
							capturedStateSegment,
							pathSegment,
							MemorySegment.NULL
						) as Int
						if (status == 0) throwLastError()
						handle = nativeCreateFile2.invokeExact(
							capturedStateSegment,
							pathSegment,
							desiredAccess,
							shareMode,
							WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
							parameters
						) as MemorySegment
						if (handle == INVALID_HANDLE_VALUE) throwLastError()
						data.add(StandardIOOpenFeatures.CREATE)
					} else {
						data.add(StandardIOOpenStatus.DEVICE_NOT_FOUND)
						return data
					}

					else -> if (handle == INVALID_HANDLE_VALUE) throw e
				}
			}
			data.add(StandardIOOpenFeatures.DIRECTORY)
			data.add(WindowsIODevice(handle))
			return data
		} else {
			val creationDisposition = if (features.contains(StandardIOOpenFeatures.CREATE)) {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.CREATE_ALWAYS
				else WindowsCreationDisposition.OPEN_ALWAYS
			} else {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.TRUNCATE_EXISTING
				else WindowsCreationDisposition.OPEN_EXISTING
			}
			val ePA = if (features.any { it is WindowsIOReOpenFeatures }) Arena.ofConfined() else null
			val extendedParameters = if (ePA != null) {
				val parameters = ePA.allocate(CREATEFILE2_EXTENDED_PARAMETERS)
				CREATEFILE2_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
				var attributes = CREATEFILE2_EXTENDED_PARAMETERS_dwFileAttributes.get(parameters, 0L) as Int
				if (features.contains(WindowsIOOpenAttributeFeatures.READ_ONLY)) {
					attributes = attributes or 0x1
					data.add(WindowsIOOpenAttributeFeatures.READ_ONLY)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.HIDDEN)) {
					attributes = attributes or 0x2
					data.add(WindowsIOOpenAttributeFeatures.HIDDEN)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.SYSTEM)) {
					attributes = attributes or 0x4
					data.add(WindowsIOOpenAttributeFeatures.SYSTEM)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.ARCHIVE)) {
					attributes = attributes or 0x20
					data.add(WindowsIOOpenAttributeFeatures.ARCHIVE)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.TEMPORARY)) {
					attributes = attributes or 0x100
					data.add(WindowsIOOpenAttributeFeatures.TEMPORARY)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.ENCRYPT)) {
					attributes = attributes or 0x4000
					data.add(WindowsIOOpenAttributeFeatures.ENCRYPT)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.REFS_INTEGRITY)) {
					attributes = attributes or 0x8000
					data.add(WindowsIOOpenAttributeFeatures.REFS_INTEGRITY)
				}
				CREATEFILE2_EXTENDED_PARAMETERS_dwFileAttributes.set(parameters, 0L, attributes)
				CREATEFILE2_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, getFlags(features, data))
				parameters
			} else MemorySegment.NULL
			val handle = nativeCreateFile2!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				desiredAccess,
				shareMode,
				creationDisposition.id.toInt(),
				extendedParameters
			) as MemorySegment
			ePA?.close()
			try {
				tryThrowWin32Error(win32LastError)
				when (creationDisposition) {
					WindowsCreationDisposition.CREATE_ALWAYS, WindowsCreationDisposition.OPEN_ALWAYS ->
						data.add(StandardIOOpenFeatures.CREATE)

					WindowsCreationDisposition.TRUNCATE_EXISTING -> data.add(FileIOOpenFeatures.TRUNCATE)
					else -> {}
				}
			} catch (e: WindowsLastErrorException) {
				when (e.error.enum) {
					WindowsLastError.ERROR_FILE_NOT_FOUND -> {
						data.add(StandardIOOpenStatus.DEVICE_NOT_FOUND)
						return data
					}

					WindowsLastError.ERROR_ALREADY_EXISTS -> when (creationDisposition) {
						WindowsCreationDisposition.CREATE_ALWAYS -> data.add(FileIOOpenFeatures.TRUNCATE)
						WindowsCreationDisposition.OPEN_ALWAYS -> {}
						else -> throw e
					}

					else -> if (handle == INVALID_HANDLE_VALUE) throw e
				}
			}
			if (
				features.contains(WindowsIOReOpenFeatures.DELETE_ON_RESTART) &&
				nativeMoveFileWithProgressWide != null
			) {
				val status = nativeMoveFileWithProgressWide.invokeExact(
					capturedStateSegment,
					pathSegment,
					MemorySegment.NULL,
					MemorySegment.NULL,
					MemorySegment.NULL,
					0x4
				) as Int
				if (status == 0) throwLastError()
				data.add(WindowsIOReOpenFeatures.DELETE_ON_RESTART)
			}
			val newDevice = WindowsIODevice(handle)
			val oR = data.contains(FileIOReOpenFeatures.READ)
			val oW = data.contains(FileIOReOpenFeatures.WRITE)
			addFileFeatures(newDevice, oR, oW)
			data.add(newDevice)
			return data
		}
	}
}