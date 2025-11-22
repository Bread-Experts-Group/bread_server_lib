package org.bread_experts_group.api.system.device.io.open

enum class WindowsIOReOpenFeatures : ReOpenIODeviceFeatureIdentifier {
	DELETE_ON_RELEASE,
	DELETE_ON_RESTART,
	DISABLE_SYSTEM_BUFFERING,
	DISABLE_REMOTE_RECALL,
	OPEN_REPARSE_POINT,
	OPTIMIZE_RANDOM_ACCESS,
	OPTIMIZE_SEQUENTIAL_ACCESS,
	WRITE_THROUGH
}