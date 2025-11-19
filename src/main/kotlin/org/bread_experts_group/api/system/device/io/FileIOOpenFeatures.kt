package org.bread_experts_group.api.system.device.io

enum class FileIOOpenFeatures : OpenIODeviceFeatureIdentifier {
	READ,
	WRITE,
	EXECUTE,
	TRUNCATE,
	SHARE_READ,
	SHARE_WRITE
	// TODO: Figure out how to implement SHARE_DELETE w/o risking consistency
}