package org.bread_experts_group.api.system.device.transparent_encrypt

enum class WindowsTransparentEncryptionStatuses : TransparentEncryptionSystemDeviceStatusIdentifier {
	ENCRYPTABLE,
	ENCRYPTED,
	NOT_ENCRYPTABLE_READ_ONLY,
	NOT_ENCRYPTED_ROOT_DIRECTORY,
	NOT_ENCRYPTED_SYSTEM_FILE,
	NOT_ENCRYPTED_SYSTEM_DIRECTORY,
	NOT_ENCRYPTED_FILE_SYSTEM_UNSUPPORTED,
	UNKNOWN
}