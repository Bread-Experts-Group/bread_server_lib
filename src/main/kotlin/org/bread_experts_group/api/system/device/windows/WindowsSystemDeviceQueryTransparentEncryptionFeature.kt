package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceQueryTransparentEncryptionFeature
import org.bread_experts_group.api.system.device.transparent_encrypt.TransparentEncryptionSystemDeviceStatusIdentifier
import org.bread_experts_group.api.system.device.transparent_encrypt.WindowsTransparentEncryptionStatuses
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeFileEncryptionStatusW
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceQueryTransparentEncryptionFeature(
	private val pathSegment: MemorySegment
) : SystemDeviceQueryTransparentEncryptionFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFileEncryptionStatusW != null

	override fun query(): List<TransparentEncryptionSystemDeviceStatusIdentifier> {
		val status = nativeFileEncryptionStatusW!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			threadLocalDWORD0
		) as Int
		if (status == 0) throwLastError()
		return when (threadLocalDWORD0.get(DWORD, 0)) {
			0 -> listOf(WindowsTransparentEncryptionStatuses.ENCRYPTABLE)
			1 -> listOf(WindowsTransparentEncryptionStatuses.ENCRYPTED)
			2 -> listOf(WindowsTransparentEncryptionStatuses.NOT_ENCRYPTED_SYSTEM_FILE)
			3 -> listOf(WindowsTransparentEncryptionStatuses.NOT_ENCRYPTED_ROOT_DIRECTORY)
			4 -> listOf(WindowsTransparentEncryptionStatuses.NOT_ENCRYPTED_SYSTEM_DIRECTORY)
			5 -> listOf(WindowsTransparentEncryptionStatuses.UNKNOWN)
			6 -> listOf(WindowsTransparentEncryptionStatuses.NOT_ENCRYPTED_FILE_SYSTEM_UNSUPPORTED)
			8 -> listOf(WindowsTransparentEncryptionStatuses.NOT_ENCRYPTABLE_READ_ONLY)
			else -> emptyList()
		}
	}
}