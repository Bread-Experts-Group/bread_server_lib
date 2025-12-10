package org.bread_experts_group.api.coding.png

enum class StandardPNGReadingFeatures : PNGReadingFeatureIdentifier {
	CHECK_MAGIC,
	CHUNK_GENERIC,

	// Critical
	CHUNK_HEADER,
	CHUNK_PALETTE,
	CHUNK_IMAGE_DATA,
	CHUNK_END,

	// Ancillary
	CHUNK_TRANSPARENCY,
	CHUNK_ANIMATION_CONTROL,
	CHUNK_FRAME_CONTROL,
	CHUNK_FRAME_DATA
}