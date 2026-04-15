package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getDesiredAccessO
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getFlags
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature3.Companion.getShareModeO
import org.bread_experts_group.api.system.io.open.*
import org.bread_experts_group.api.system.io.status.StandardIOStatus
import org.bread_experts_group.ffi.autoArena
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

		val expectDirectory = features.contains(StandardIOOpenFeatures.DIRECTORY)
		val creationDisposition = if (expectDirectory) WindowsCreationDisposition.OPEN_EXISTING
		else {
			if (features.contains(StandardIOOpenFeatures.CREATE)) {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.CREATE_ALWAYS
				else WindowsCreationDisposition.OPEN_ALWAYS
			} else {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.TRUNCATE_EXISTING
				else WindowsCreationDisposition.OPEN_EXISTING
			}
		}
		val handle = Arena.ofConfined().use { ePA ->
			val extendedParameters = ePA.allocate(CREATEFILE2_EXTENDED_PARAMETERS)
			CREATEFILE2_EXTENDED_PARAMETERS_dwSize.set(extendedParameters, 0L, extendedParameters.byteSize().toInt())
			var attributes = CREATEFILE2_EXTENDED_PARAMETERS_dwFileAttributes.get(extendedParameters, 0L) as Int
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
			CREATEFILE2_EXTENDED_PARAMETERS_dwFileAttributes.set(extendedParameters, 0L, attributes)
			CREATEFILE2_EXTENDED_PARAMETERS_dwFileFlags.set(extendedParameters, 0L, getFlags(features, data))
			var handle = nativeCreateFile2!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				desiredAccess,
				shareMode,
				creationDisposition.id.toInt(),
				extendedParameters
			) as MemorySegment
			when (win32LastError) {
				WindowsLastError.ERROR_SUCCESS.id.toInt() if expectDirectory -> {
					val info = autoArena.allocate(FILE_STANDARD_INFO)
					val status = nativeGetFileInformationByHandleEx!!.invokeExact(
						capturedStateSegment,
						handle,
						WindowsFileInfoByHandleClasses.FileStandardInfo.id.toInt(),
						info,
						info.byteSize().toInt()
					) as Int
					if (status == 0) throwLastError()
					if ((FILE_STANDARD_INFO_Directory.get(info, 0L) as Byte).toInt() == 0) {
						data.add(StandardIOStatus.NOT_DIRECTORY)
						return data
					}
				}

				WindowsLastError.ERROR_SUCCESS.id.toInt() -> {}

				WindowsLastError.ERROR_ALREADY_EXISTS.id.toInt() -> when (creationDisposition) {
					WindowsCreationDisposition.CREATE_ALWAYS -> data.add(FileIOOpenFeatures.TRUNCATE)
					WindowsCreationDisposition.OPEN_ALWAYS -> {}
					else -> throwLastError()
				}

				WindowsLastError.ERROR_FILE_NOT_FOUND.id.toInt() if expectDirectory -> {
					var directoryFlags = 0
					if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
						directoryFlags = directoryFlags or 0x00000001
						data.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
					}
					handle = nativeCreateDirectory2Wide!!.invokeExact(
						capturedStateSegment,
						pathSegment,
						0x001F0000,
						0x00000007,
						directoryFlags,
						MemorySegment.NULL
					) as MemorySegment
					if (handle == INVALID_HANDLE_VALUE) throwLastError()
					val status = nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int
					if (status == 0) throwLastError()
					handle = nativeCreateFile2.invokeExact(
						capturedStateSegment,
						pathSegment,
						desiredAccess,
						shareMode,
						creationDisposition.id.toInt(),
						extendedParameters
					) as MemorySegment
					if (handle == INVALID_HANDLE_VALUE) throwLastError()
				}

				else -> {
					data.add(getIOStatusForError())
					return data
				}
			}
			handle
		}
		when (creationDisposition) {
			WindowsCreationDisposition.CREATE_ALWAYS, WindowsCreationDisposition.OPEN_ALWAYS ->
				data.add(StandardIOOpenFeatures.CREATE)

			WindowsCreationDisposition.TRUNCATE_EXISTING -> data.add(FileIOOpenFeatures.TRUNCATE)
			else -> {}
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