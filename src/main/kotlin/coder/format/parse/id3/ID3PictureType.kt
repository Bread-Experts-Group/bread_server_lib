package org.bread_experts_group.coder.format.parse.id3

import org.bread_experts_group.coder.Mappable

enum class ID3PictureType(
	override val id: Int, override val tag: String
) : Mappable<ID3PictureType, Int> {
	OTHER(0x00, "Generic"),
	FILE_ICON_32_32(0x01, "32x32 File Icon"),
	OTHER_FILE_ICON(0x02, "Generic File Icon"),
	COVER_FRONT(0x03, "Front Cover"),
	COVER_BACK(0x04, "Back Cover"),
	LEAFLET(0x05, "Leaflet Page"),
	MEDIA(0x06, "Media"),
	LEAD_ARTIST(0x07, "Lead Artist"),
	ARTIST(0x08, "Artist"),
	CONDUCTOR(0x09, "Conductor"),
	BAND(0x0A, "Band"),
	COMPOSER(0x0B, "Composer"),
	LYRICIST(0x0C, "Lyricist"),
	RECORDING_LOCATION(0x0D, "Recording Location"),
	DURING_RECORDING(0x0E, "During Recording"),
	DURING_PERFORMANCE(0x0F, "During Performance"),
	SCREEN_CAPTURE(0x10, "Screen Capture"),
	BRIGHT_COLORED_FISH(0x11, "Bright Colored Fish (What?)"),
	ILLUSTRATION(0x12, "Illustration"),
	BAND_LOGOTYPE(0x13, "Band Logotype"),
	PUBLISHER_LOGOTYPE(0x14, "Publisher Logotype");

	override fun toString(): String = stringForm()
}