package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.device.io.*
import org.bread_experts_group.api.system.device.io.windows.WindowsIODevice
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsSystemDeviceIODeviceFeature(
	symbolicLink: MemorySegment
) : SystemDeviceIODeviceFeature() {
	companion object {
		private val cleaner: Cleaner = Cleaner.create()
	}

	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateFile3 != null && nativeCloseHandle != null &&
			nativeCreateDirectory2W != null

	private val localArena = Arena.ofConfined()
	private val symbolicLink = localArena.allocate(symbolicLink.byteSize()).copyFrom(symbolicLink)
	override fun open(
		vararg features: OpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenIODeviceFeatureIdentifier>>? {
		val supportedFeatures = mutableListOf<OpenIODeviceFeatureIdentifier>()
		val rC = features.contains(FileIOOpenFeatures.READ)
		val wC = features.contains(FileIOOpenFeatures.WRITE)
		val eC = features.contains(FileIOOpenFeatures.EXECUTE)
		val accessRights = if (rC && wC && eC) {
			supportedFeatures.add(FileIOOpenFeatures.READ)
			supportedFeatures.add(FileIOOpenFeatures.WRITE)
			supportedFeatures.add(FileIOOpenFeatures.EXECUTE)
			WindowsGenericAccessRights.GENERIC_ALL.position.toInt()
		} else {
			var localAR = 0
			if (rC) {
				localAR = WindowsGenericAccessRights.GENERIC_READ.position.toInt()
				supportedFeatures.add(FileIOOpenFeatures.READ)
			}
			if (wC) {
				localAR = localAR or WindowsGenericAccessRights.GENERIC_WRITE.position.toInt()
				supportedFeatures.add(FileIOOpenFeatures.WRITE)
			}
			if (eC) {
				localAR = localAR or WindowsGenericAccessRights.GENERIC_EXECUTE.position.toInt()
				supportedFeatures.add(FileIOOpenFeatures.EXECUTE)
			}
			localAR
		}
		var shareMode = if (features.contains(FileIOOpenFeatures.SHARE_READ)) {
			supportedFeatures.add(FileIOOpenFeatures.SHARE_READ)
			WindowsFileSharingTypes.FILE_SHARE_READ.position.toInt()
		} else 0
		if (features.contains(FileIOOpenFeatures.SHARE_WRITE)) {
			shareMode = shareMode or WindowsFileSharingTypes.FILE_SHARE_WRITE.position.toInt()
			supportedFeatures.add(FileIOOpenFeatures.SHARE_WRITE)
		}
		if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
			val ePA = Arena.ofConfined()
			val parameters = ePA.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
			CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
			CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, 0x02000000)
			var handle = nativeCreateFile3!!.invokeExact(
				capturedStateSegment,
				symbolicLink,
				accessRights,
				shareMode,
				WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
				parameters
			) as MemorySegment
			try {
				if (handle == INVALID_HANDLE_VALUE) throwLastError()
				ePA.close()
			} catch (e: WindowsLastErrorException) {
				when (e.error.enum) {
					WindowsLastError.ERROR_FILE_NOT_FOUND -> if (features.contains(StandardIOOpenFeatures.CREATE)) {
						var directoryFlags = 0
						if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
							directoryFlags = directoryFlags or 0x00000001
							supportedFeatures.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
						}
						handle = nativeCreateDirectory2W!!.invokeExact(
							capturedStateSegment,
							symbolicLink,
							0x100021,
							shareMode,
							directoryFlags,
							MemorySegment.NULL
						) as MemorySegment
						if (handle == INVALID_HANDLE_VALUE) throwLastError()
						supportedFeatures.add(StandardIOOpenFeatures.CREATE)
					} else {
						ePA.close()
						return null
					}

					else -> if (handle == INVALID_HANDLE_VALUE) {
						ePA.close()
						throw e
					}
				}
			}
			supportedFeatures.add(StandardIOOpenFeatures.DIRECTORY)
			val newDevice = WindowsIODevice(handle)
			cleaner.register(newDevice) {
				if (nativeCloseHandle!!.invokeExact(handle) == 0) throwLastError()
			}
			return newDevice to supportedFeatures
		} else {
			val creationDisposition = if (features.contains(StandardIOOpenFeatures.CREATE)) {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.CREATE_ALWAYS
				else WindowsCreationDisposition.OPEN_ALWAYS
			} else {
				if (features.contains(FileIOOpenFeatures.TRUNCATE)) WindowsCreationDisposition.TRUNCATE_EXISTING
				else WindowsCreationDisposition.OPEN_EXISTING
			}
			val ePA = if (features.any { it is WindowsIOOpenFeatures }) Arena.ofConfined() else null
			val extendedParameters = if (ePA != null) {
				val parameters = ePA.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
				CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
				var attributes = CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.get(parameters, 0L) as Int
				if (features.contains(WindowsIOOpenFeatures.READ_ONLY)) {
					attributes = attributes or 0x1
					supportedFeatures.add(WindowsIOOpenFeatures.READ_ONLY)
				}
				if (features.contains(WindowsIOOpenFeatures.HIDDEN)) {
					attributes = attributes or 0x2
					supportedFeatures.add(WindowsIOOpenFeatures.HIDDEN)
				}
				if (features.contains(WindowsIOOpenFeatures.SYSTEM)) {
					attributes = attributes or 0x4
					supportedFeatures.add(WindowsIOOpenFeatures.SYSTEM)
				}
				if (features.contains(WindowsIOOpenFeatures.ARCHIVE)) {
					attributes = attributes or 0x20
					supportedFeatures.add(WindowsIOOpenFeatures.ARCHIVE)
				}
				if (features.contains(WindowsIOOpenFeatures.OPTIMIZE_TEMPORARY)) {
					attributes = attributes or 0x100
					supportedFeatures.add(WindowsIOOpenFeatures.OPTIMIZE_TEMPORARY)
				}
				if (features.contains(WindowsIOOpenFeatures.ENCRYPT)) {
					attributes = attributes or 0x4000
					supportedFeatures.add(WindowsIOOpenFeatures.ENCRYPT)
				}
				if (features.contains(WindowsIOOpenFeatures.REFS_INTEGRITY)) {
					attributes = attributes or 0x8000
					supportedFeatures.add(WindowsIOOpenFeatures.REFS_INTEGRITY)
				}
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.set(parameters, 0L, attributes)
				var flags = CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.get(parameters, 0L) as Int
				if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
					flags = flags or 0x00010000
					supportedFeatures.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
				}
				if (features.contains(WindowsIOOpenFeatures.DISABLE_REMOTE_RECALL)) {
					flags = flags or 0x00100000
					supportedFeatures.add(WindowsIOOpenFeatures.DISABLE_REMOTE_RECALL)
				}
				if (features.contains(WindowsIOOpenFeatures.OPEN_REPARSE_POINT)) {
					flags = flags or 0x00200000
					supportedFeatures.add(WindowsIOOpenFeatures.OPEN_REPARSE_POINT)
				}
				if (features.contains(WindowsIOOpenFeatures.DELETE_ON_CLOSE)) {
					flags = flags or 0x04000000
					supportedFeatures.add(WindowsIOOpenFeatures.DELETE_ON_CLOSE)
				}
				if (features.contains(WindowsIOOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)) {
					flags = flags or 0x08000000
					supportedFeatures.add(WindowsIOOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)
				}
				if (features.contains(WindowsIOOpenFeatures.OPTIMIZE_RANDOM_ACCESS)) {
					flags = flags or 0x10000000
					supportedFeatures.add(WindowsIOOpenFeatures.OPTIMIZE_RANDOM_ACCESS)
				}
				if (features.contains(WindowsIOOpenFeatures.DISABLE_SYSTEM_BUFFERING)) {
					flags = flags or 0x20000000
					supportedFeatures.add(WindowsIOOpenFeatures.DISABLE_SYSTEM_BUFFERING)
				}
				if (features.contains(WindowsIOOpenFeatures.WRITE_THROUGH)) {
					flags = flags or 0x80000000.toInt()
					supportedFeatures.add(WindowsIOOpenFeatures.WRITE_THROUGH)
				}
				flags = flags or 0x01000000 // FILE_FLAG_POSIX_SEMANTICS
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, flags)
				parameters
			} else MemorySegment.NULL
			val handle = nativeCreateFile3!!.invokeExact(
				capturedStateSegment,
				symbolicLink,
				accessRights,
				shareMode,
				creationDisposition.id.toInt(),
				extendedParameters
			) as MemorySegment
			ePA?.close()
			try {
				decodeWin32Error(win32LastError)
				when (creationDisposition) {
					WindowsCreationDisposition.CREATE_ALWAYS, WindowsCreationDisposition.OPEN_ALWAYS ->
						supportedFeatures.add(StandardIOOpenFeatures.CREATE)

					WindowsCreationDisposition.TRUNCATE_EXISTING -> supportedFeatures.add(FileIOOpenFeatures.TRUNCATE)
					else -> {}
				}
			} catch (e: WindowsLastErrorException) {
				when (e.error.enum) {
					WindowsLastError.ERROR_FILE_NOT_FOUND -> return null
					WindowsLastError.ERROR_ALREADY_EXISTS -> when (creationDisposition) {
						WindowsCreationDisposition.CREATE_ALWAYS -> supportedFeatures.add(FileIOOpenFeatures.TRUNCATE)
						WindowsCreationDisposition.OPEN_ALWAYS -> {}
						else -> throw e
					}

					else -> if (handle == INVALID_HANDLE_VALUE) throw e
				}
			}
			val newDevice = WindowsIODevice(handle)
			cleaner.register(newDevice) {
				if (nativeCloseHandle!!.invokeExact(handle) == 0) throwLastError()
			}
			return newDevice to supportedFeatures
		}
	}
}