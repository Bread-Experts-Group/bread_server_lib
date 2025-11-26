package org.bread_experts_group.api.system.socket.resolution

enum class WindowsResolutionFeatures : ResolutionFeatureIdentifier {
	PASSIVE,
	CANONICAL_NAME,
	NUMERIC_HOST,
	REQUIRE_CONFIGURED_GLOBAL_ADDRESS,
	FULLY_QUALIFIED_DOMAIN_NAME,
	HINT_FILE_SHARE_USE,
	DISABLE_IDN_ENCODING
}