package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.feature.windows.WindowsIODeviceReleaseFeature
import org.bread_experts_group.api.system.device.io.open.*
import org.bread_experts_group.api.system.device.io.windows.WindowsIODevice
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceIODeviceFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateFile3 != null && nativeCloseHandle != null &&
			nativeCreateDirectory2W != null

	companion object {
		internal fun getDesiredAccess(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			supportedFeatures: MutableList<ReOpenIODeviceFeatureIdentifier>
		): Int {
			val rC = features.contains(FileIOReOpenFeatures.READ)
			val wC = features.contains(FileIOReOpenFeatures.WRITE)
			val eC = features.contains(FileIOReOpenFeatures.EXECUTE)
			return if (rC && wC && eC) {
				supportedFeatures.add(FileIOReOpenFeatures.READ)
				supportedFeatures.add(FileIOReOpenFeatures.WRITE)
				supportedFeatures.add(FileIOReOpenFeatures.EXECUTE)
				WindowsGenericAccessRights.GENERIC_ALL.position.toInt()
			} else {
				var localAR = 0
				if (rC) {
					localAR = WindowsGenericAccessRights.GENERIC_READ.position.toInt()
					supportedFeatures.add(FileIOReOpenFeatures.READ)
				}
				if (wC) {
					localAR = localAR or WindowsGenericAccessRights.GENERIC_WRITE.position.toInt()
					supportedFeatures.add(FileIOReOpenFeatures.WRITE)
				}
				if (eC) {
					localAR = localAR or WindowsGenericAccessRights.GENERIC_EXECUTE.position.toInt()
					supportedFeatures.add(FileIOReOpenFeatures.EXECUTE)
				}
				localAR
			}
		}

		internal fun getShareMode(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			supportedFeatures: MutableList<ReOpenIODeviceFeatureIdentifier>
		): Int {
			var shareMode = if (features.contains(FileIOReOpenFeatures.SHARE_READ)) {
				supportedFeatures.add(FileIOReOpenFeatures.SHARE_READ)
				WindowsFileSharingTypes.FILE_SHARE_READ.position.toInt()
			} else 0
			if (features.contains(FileIOReOpenFeatures.SHARE_WRITE)) {
				shareMode = shareMode or WindowsFileSharingTypes.FILE_SHARE_WRITE.position.toInt()
				supportedFeatures.add(FileIOReOpenFeatures.SHARE_WRITE)
			}
			return shareMode
		}

		internal fun getFlags(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			supportedFeatures: MutableList<ReOpenIODeviceFeatureIdentifier>
		): Int {
			var flags = 0
			if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
				flags = 0x02000000
			} else {
				if (features.contains(WindowsIOReOpenFeatures.DISABLE_REMOTE_RECALL)) {
					flags = 0x00100000
					supportedFeatures.add(WindowsIOReOpenFeatures.DISABLE_REMOTE_RECALL)
				}
				if (features.contains(WindowsIOReOpenFeatures.OPEN_REPARSE_POINT)) {
					flags = flags or 0x00200000
					supportedFeatures.add(WindowsIOReOpenFeatures.OPEN_REPARSE_POINT)
				}
				if (features.contains(WindowsIOReOpenFeatures.DELETE_ON_RELEASE)) {
					flags = flags or 0x04000000
					supportedFeatures.add(WindowsIOReOpenFeatures.DELETE_ON_RELEASE)
				}
				if (features.contains(WindowsIOReOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)) {
					flags = flags or 0x08000000
					supportedFeatures.add(WindowsIOReOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)
				}
				if (features.contains(WindowsIOReOpenFeatures.OPTIMIZE_RANDOM_ACCESS)) {
					flags = flags or 0x10000000
					supportedFeatures.add(WindowsIOReOpenFeatures.OPTIMIZE_RANDOM_ACCESS)
				}
				if (features.contains(WindowsIOReOpenFeatures.DISABLE_SYSTEM_BUFFERING)) {
					flags = flags or 0x20000000
					supportedFeatures.add(WindowsIOReOpenFeatures.DISABLE_SYSTEM_BUFFERING)
				}
				if (features.contains(WindowsIOReOpenFeatures.WRITE_THROUGH)) {
					flags = flags or 0x80000000.toInt()
					supportedFeatures.add(WindowsIOReOpenFeatures.WRITE_THROUGH)
				}
			}
			return flags or 0x01000000 // FILE_FLAG_POSIX_SEMANTICS
		}
	}

	override fun open(
		vararg features: OpenIODeviceFeatureIdentifier
	): Pair<IODevice, List<OpenIODeviceFeatureIdentifier>>? {
		val supportedFeatures = mutableListOf<OpenIODeviceFeatureIdentifier>()

		@Suppress("UNCHECKED_CAST")
		val desiredAccess = getDesiredAccess(
			features,
			supportedFeatures as MutableList<ReOpenIODeviceFeatureIdentifier>
		)
		val shareMode = getShareMode(features, supportedFeatures)

		if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
			val ePA = Arena.ofConfined()
			val parameters = ePA.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
			CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
			CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, 0x02000000)
			var handle = nativeCreateFile3!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				desiredAccess,
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
							pathSegment,
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
			newDevice.features.add(
				WindowsIODeviceReleaseFeature(newDevice.registerCleaningAction {
					if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0)
						throwLastError()
				})
			)
			return newDevice to supportedFeatures
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
				val parameters = ePA.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
				CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
				var attributes = CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.get(parameters, 0L) as Int
				if (features.contains(WindowsIOOpenAttributeFeatures.READ_ONLY)) {
					attributes = attributes or 0x1
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.READ_ONLY)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.HIDDEN)) {
					attributes = attributes or 0x2
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.HIDDEN)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.SYSTEM)) {
					attributes = attributes or 0x4
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.SYSTEM)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.ARCHIVE)) {
					attributes = attributes or 0x20
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.ARCHIVE)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.TEMPORARY)) {
					attributes = attributes or 0x100
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.TEMPORARY)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.ENCRYPT)) {
					attributes = attributes or 0x4000
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.ENCRYPT)
				}
				if (features.contains(WindowsIOOpenAttributeFeatures.REFS_INTEGRITY)) {
					attributes = attributes or 0x8000
					supportedFeatures.add(WindowsIOOpenAttributeFeatures.REFS_INTEGRITY)
				}
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.set(parameters, 0L, attributes)
				var flags = getFlags(features, supportedFeatures)
				if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
					flags = flags or 0x00010000
					supportedFeatures.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
				}
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, flags)
				parameters
			} else MemorySegment.NULL
			val handle = nativeCreateFile3!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				desiredAccess,
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
			if (features.contains(WindowsIOReOpenFeatures.DELETE_ON_RESTART) && nativeMoveFileWithProgressW != null) {
				val status = nativeMoveFileWithProgressW.invokeExact(
					capturedStateSegment,
					pathSegment,
					MemorySegment.NULL,
					MemorySegment.NULL,
					MemorySegment.NULL,
					0x4
				) as Int
				if (status == 0) throwLastError()
				supportedFeatures.add(WindowsIOReOpenFeatures.DELETE_ON_RESTART)
			}
			val newDevice = WindowsIODevice(handle)
			newDevice.features.add(
				WindowsIODeviceReleaseFeature(newDevice.registerCleaningAction {
					if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0)
						throwLastError()
				})
			)
			return newDevice to supportedFeatures
		}
	}
}