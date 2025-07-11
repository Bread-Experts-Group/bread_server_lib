package org.bread_experts_group.coder.format.parse.flac.block

import org.bread_experts_group.coder.Mappable

enum class FLACBlockType(
	override val id: Int,
	override val tag: String
) : Mappable<FLACBlockType, Int> {
	STREAM_INFO(0, "Stream Information"),
	PADDING(1, "Padding"),
	APPLICATION(2, "Application-Specific"),
	SEEK_TABLE(3, "Seek Table"),
	VORBIS_COMMENT(4, "Vorbis Comment"),
	CD_CUESHEET(5, "CD Cue-sheet"),
	PICTURE(6, "Picture"),
	OTHER(-1, "Other"),
	AUDIO_DATA(-2, "Audio Data");

	override fun other(): FLACBlockType? = OTHER
	override fun toString(): String = stringForm()
}