package org.bread_experts_group.api.system.device.move

enum class WindowsMoveSystemDeviceFeatures : MoveSystemDeviceFeatureIdentifier {
	COPY_ALLOWED,
	MOVE_ON_RESTART,
	FAIL_IF_NOT_TRACKABLE,
	OVERWRITE,
	WRITE_THROUGH
}