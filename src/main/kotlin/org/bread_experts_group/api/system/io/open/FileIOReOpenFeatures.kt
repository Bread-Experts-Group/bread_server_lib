package org.bread_experts_group.api.system.io.open

enum class FileIOReOpenFeatures : ReOpenIODeviceFeatureIdentifier {
	READ,
	WRITE,
	EXECUTE,
	SHARE_READ,
	SHARE_WRITE
	// TODO: Figure out how to implement SHARE_DELETE w/o risking consistency
}