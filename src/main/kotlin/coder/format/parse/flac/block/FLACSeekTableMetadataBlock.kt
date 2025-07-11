package org.bread_experts_group.coder.format.parse.flac.block

class FLACSeekTableMetadataBlock(
	val points: List<FLACSeekPoint>
) : FLACMetadataBlock(FLACBlockType.SEEK_TABLE, byteArrayOf()) {
	override fun toString(): String = "FLACSeekTableMetadataBlock[${points.size}][$points]"
}