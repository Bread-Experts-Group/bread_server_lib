package org.bread_experts_group.coder.format.parse.flac.block

import org.bread_experts_group.formatMetric

class FLACStreamInfoMetadataBlock(
	val minimumBlockSize: Int,
	val maximumBlockSize: Int,
	val minimumFrameSize: Int,
	val maximumFrameSize: Int,
	val sampleRate: Int,
	val channels: Int,
	val bitsPerSample: Int,
	val totalSamples: Long,
	val md5: ByteArray
) : FLACMetadataBlock(FLACBlockType.STREAM_INFO, byteArrayOf()) {
	override fun toString(): String = "FLACStreamInfoMetadataBlock[block size: [$minimumBlockSize..$maximumBlockSize" +
			"], frame size: [$minimumFrameSize..$maximumFrameSize], ${sampleRate.toDouble().formatMetric()}Hz, " +
			"$channels channel(s), $bitsPerSample-bit, $totalSamples samples, ${md5.toHexString()}]"
}