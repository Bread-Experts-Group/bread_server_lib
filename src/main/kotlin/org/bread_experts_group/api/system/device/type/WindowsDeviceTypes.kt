package org.bread_experts_group.api.system.device.type

enum class WindowsDeviceTypes : SystemDeviceTypeIdentifier {
	DISK,
	CDROM,
	PARTITION,
	TAPE,
	WRITE_ONCE_DISK,
	VOLUME,
	MEDIUM_CHANGER,
	FLOPPY,
	CD_CHANGER,
	STORAGE_PORT,
	VM_LUN,
	SCSI_ENCLOSURE_SERVICES,
	NVME_ZONED_NAMESPACE_DISK
}