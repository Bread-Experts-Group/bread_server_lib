package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.feature.IODeviceReleaseFeature
import org.bread_experts_group.api.system.io.open.*
import org.bread_experts_group.api.system.io.windows.*
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsSystemDeviceIODeviceFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateFile3 != null && nativeCloseHandle != null &&
			nativeCreateDirectory2Wide != null

	companion object {
		private val cleaner = Cleaner.create()
		fun winCleanFileHandle(handle: MemorySegment): Cleaner.Cleanable = cleaner.register(handle) {
			if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0)
				throwLastError()
		}

		@Suppress("UNCHECKED_CAST")
		// internally consistent
		internal fun getDesiredAccessO(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			data: MutableList<OpenIODeviceDataIdentifier>
		): Int = getDesiredAccess(features, data)

		internal fun getDesiredAccess(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			data: MutableList<OpenIODeviceDataIdentifier>
		): Int {
			val rC = features.contains(FileIOReOpenFeatures.READ)
			val wC = features.contains(FileIOReOpenFeatures.WRITE)
			val eC = features.contains(FileIOReOpenFeatures.EXECUTE)
			return if (rC && wC && eC) {
				data.add(FileIOReOpenFeatures.READ)
				data.add(FileIOReOpenFeatures.WRITE)
				data.add(FileIOReOpenFeatures.EXECUTE)
				WindowsGenericAccessRights.GENERIC_ALL.position.toInt()
			} else {
				var localAR = 0
				if (rC) {
					localAR = WindowsGenericAccessRights.GENERIC_READ.position.toInt()
					data.add(FileIOReOpenFeatures.READ)
				}
				if (wC) {
					localAR = localAR or WindowsGenericAccessRights.GENERIC_WRITE.position.toInt()
					data.add(FileIOReOpenFeatures.WRITE)
				}
				if (eC) {
					localAR = localAR or WindowsGenericAccessRights.GENERIC_EXECUTE.position.toInt()
					data.add(FileIOReOpenFeatures.EXECUTE)
				}
				localAR
			}
		}

		// internally consistent
		@Suppress("UNCHECKED_CAST")
		internal fun getShareModeO(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			data: MutableList<OpenIODeviceDataIdentifier>
		): Int = getShareMode(features, data)

		internal fun getShareMode(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			data: MutableList<OpenIODeviceDataIdentifier>
		): Int {
			var shareMode = if (features.contains(FileIOReOpenFeatures.SHARE_READ)) {
				data.add(FileIOReOpenFeatures.SHARE_READ)
				WindowsFileSharingTypes.FILE_SHARE_READ.position.toInt()
			} else 0
			if (features.contains(FileIOReOpenFeatures.SHARE_WRITE)) {
				shareMode = shareMode or WindowsFileSharingTypes.FILE_SHARE_WRITE.position.toInt()
				data.add(FileIOReOpenFeatures.SHARE_WRITE)
			}
			return shareMode
		}

		internal fun getFlags(
			features: Array<out OpenIODeviceFeatureIdentifier>,
			data: MutableList<OpenIODeviceDataIdentifier>
		): Int {
			var flags = 0
			if (features.contains(WindowsIOReOpenFeatures.DISABLE_REMOTE_RECALL)) {
				flags = 0x00100000
				data.add(WindowsIOReOpenFeatures.DISABLE_REMOTE_RECALL)
			}
			if (features.contains(WindowsIOReOpenFeatures.OPEN_REPARSE_POINT)) {
				flags = flags or 0x00200000
				data.add(WindowsIOReOpenFeatures.OPEN_REPARSE_POINT)
			}
			if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
				flags = flags or 0x00010000
				data.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
			}
			if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
				flags = 0x02000000
				data.add(StandardIOOpenFeatures.DIRECTORY)
			} else {
				if (features.contains(WindowsIOReOpenFeatures.DELETE_ON_RELEASE)) {
					flags = flags or 0x04000000
					data.add(WindowsIOReOpenFeatures.DELETE_ON_RELEASE)
				}
				if (features.contains(WindowsIOReOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)) {
					flags = flags or 0x08000000
					data.add(WindowsIOReOpenFeatures.OPTIMIZE_SEQUENTIAL_ACCESS)
				}
				if (features.contains(WindowsIOReOpenFeatures.OPTIMIZE_RANDOM_ACCESS)) {
					flags = flags or 0x10000000
					data.add(WindowsIOReOpenFeatures.OPTIMIZE_RANDOM_ACCESS)
				}
				if (features.contains(WindowsIOReOpenFeatures.DISABLE_SYSTEM_BUFFERING)) {
					flags = flags or 0x20000000
					data.add(WindowsIOReOpenFeatures.DISABLE_SYSTEM_BUFFERING)
				}
				if (features.contains(WindowsIOReOpenFeatures.WRITE_THROUGH)) {
					flags = flags or 0x80000000.toInt()
					data.add(WindowsIOReOpenFeatures.WRITE_THROUGH)
				}
			}
			return flags or 0x01000000 // FILE_FLAG_POSIX_SEMANTICS
		}
	}

	override fun open(
		vararg features: OpenIODeviceFeatureIdentifier
	): List<OpenIODeviceDataIdentifier> = Arena.ofConfined().use { tempArena ->
		val data = mutableListOf<OpenIODeviceDataIdentifier>()

		@Suppress("UNCHECKED_CAST")
		val desiredAccess = getDesiredAccessO(features, data)
		val shareMode = getShareModeO(features, data)

		if (features.contains(StandardIOOpenFeatures.DIRECTORY)) {
			val parameters = tempArena.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
			CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
			CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, getFlags(features, data))
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
			} catch (e: WindowsLastErrorException) {
				when (e.error.enum) {
					WindowsLastError.ERROR_FILE_NOT_FOUND -> if (features.contains(StandardIOOpenFeatures.CREATE)) {
						var directoryFlags = 0
						if (features.contains(WindowsIOOpenFeatures.DISABLE_REDIRECTION)) {
							directoryFlags = directoryFlags or 0x00000001
							data.add(WindowsIOOpenFeatures.DISABLE_REDIRECTION)
						}
						handle = nativeCreateDirectory2Wide!!.invokeExact(
							capturedStateSegment,
							pathSegment,
							0x100021,
							shareMode,
							directoryFlags,
							MemorySegment.NULL
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
			val newDevice = IODevice()
			newDevice.features.add(
				IODeviceReleaseFeature(
					ImplementationSource.SYSTEM_NATIVE,
					winCleanFileHandle(handle).let { { it.clean() } }
				)
			)
			data.add(newDevice)
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
				val parameters = ePA.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
				CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(parameters, 0L, parameters.byteSize().toInt())
				var attributes = CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.get(parameters, 0L) as Int
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
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes.set(parameters, 0L, attributes)
				CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(parameters, 0L, getFlags(features, data))
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
			val newDevice = IODevice()
			newDevice.features.add(
				IODeviceReleaseFeature(
					ImplementationSource.SYSTEM_NATIVE,
					winCleanFileHandle(handle).let { { it.clean() } }
				)
			)
			val oR = data.contains(FileIOReOpenFeatures.READ)
			val oW = data.contains(FileIOReOpenFeatures.WRITE)
			if (oR || oW) newDevice.features.add(WindowsIODeviceSeekFeature(handle))
			if (oR) newDevice.features.add(WindowsIODeviceReadFeature(handle))
			if (oW) {
				newDevice.features.add(WindowsIODeviceWriteFeature(handle))
				newDevice.features.add(WindowsIODeviceFlushFeature(handle))
				newDevice.features.add(WindowsIODeviceSetSizeFeature(handle))
			}
			newDevice.features.add(WindowsIOGetDeviceGeometryFeature(handle))
			newDevice.features.add(WindowsIODeviceBypassFSDriverBoundsChecksFeature(handle))
			newDevice.features.add(WindowsIODeviceGetDeviceFirmwareInfoFeature(handle))
			newDevice.features.add(WindowsIODeviceReopenFeature(handle))
			newDevice.features.add(WindowsIODeviceGetSizeFeature(handle))
			newDevice.features.add(WindowsIODeviceDataRangeLockFeature(handle))
			data.add(newDevice)
			return data
		}
	}
}