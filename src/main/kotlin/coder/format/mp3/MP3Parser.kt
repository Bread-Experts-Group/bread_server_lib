package org.bread_experts_group.coder.format.mp3

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.id3.ID3Parser
import org.bread_experts_group.coder.format.mp3.frame.MP3BaseFrame
import org.bread_experts_group.coder.format.mp3.frame.MP3Frame
import org.bread_experts_group.coder.format.mp3.frame.MP3ID3Frame
import org.bread_experts_group.coder.format.mp3.frame.header.*
import org.bread_experts_group.stream.ConsolidatedInputStream
import java.io.InputStream

class MP3Parser(from: InputStream) : Parser<Nothing?, MP3BaseFrame, InputStream>("MPEG 3", from) {
	private val consolidatoryStream = ConsolidatedInputStream()
	private var preFrame: MP3ID3Frame? = null
	override fun readBase(): MP3BaseFrame {
		preFrame.also { preFrame = null }?.let { return it }
		val sync = consolidatoryStream.read()
		val header2 = consolidatoryStream.read()
		if ((((header2 and 0b11100000) shl 3) or sync) != 0b11111111111)
			throw IllegalStateException("MP3 header misalignment [$sync, $header2]")
		val header3 = consolidatoryStream.read()
		val header4 = consolidatoryStream.read()

		val versionId = MPEGAudioVersionID.get((header2 and 0b00011000) shr 3)
		val layerDescription = LayerDescription.get((header2 and 0b00000110) shr 1)
		val bitrate = MP3Bitrate((header3 and 0b11110000) shr 4, versionId, layerDescription)
		val sampleRate = MP3SampleRate((header3 and 0b00001100) shr 2, versionId)
		val padding = ((header3 and 0b00000010) shr 1) == 1
		val channelMode = ChannelMode.get((header4 and 0b11000000) shr 6)
		val emphasis = MP3Emphasis.get(header4 and 0b00000001)

		val frameSize = if (layerDescription == LayerDescription.LAYER_1)
			((12 * bitrate.raw / (sampleRate.raw / 1000.0) + if (padding) 1 else 0) * 4).toInt() else
			(144 * bitrate.raw / (sampleRate.raw / 1000.0) + if (padding) 1 else 0).toInt()

		val header = MP3Header(
			versionId,
			layerDescription,
			(header2 and 0b00000001) == 0,
			bitrate,
			sampleRate,
			padding,
			(header3 and 0b00000001) == 1,
			channelMode,
			"${(header4 and 0b00110000) shr 4} (todo)",
			((header4 and 0b00000100) shr 2) == 1,
			((header4 and 0b00000010) shr 1) == 1,
			emphasis
		)

		return MP3Frame(header, fqIn.readNBytes(frameSize - 4))
	}

	init {
		consolidatoryStream.streams.addFirst(fqIn)
		val id3Scan = fqIn.readNBytes(3)
		consolidatoryStream.streams.addFirst(id3Scan.inputStream())
		if (id3Scan.decodeToString() == "ID3") preFrame = MP3ID3Frame(ID3Parser(consolidatoryStream))
	}

	override fun responsibleStream(of: MP3BaseFrame): InputStream = fqIn
	override var next: MP3BaseFrame? = readBase()
}