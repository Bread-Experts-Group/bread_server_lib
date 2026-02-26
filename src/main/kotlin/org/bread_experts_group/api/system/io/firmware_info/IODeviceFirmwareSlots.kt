package org.bread_experts_group.api.system.io.firmware_info

data class IODeviceFirmwareSlots(
	val slots: Map<Int, List<IODeviceGetFirmwareInfoSlotDataIdentifier>>
) : IODeviceGetFirmwareInfoDataIdentifier