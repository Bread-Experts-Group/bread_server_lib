package org.bread_experts_group.api.system.io.status

import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier

enum class StandardIOStatus : OpenIODeviceDataIdentifier, IOSendDataIdentifier {
	DEVICE_NOT_FOUND,
	DEVICE_IN_USE,
	ACCESS_DENIED,
	NOT_DIRECTORY
}