package org.bread_experts_group.coder.format.mp3

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.mp3.header.*
import org.bread_experts_group.hex
import org.bread_experts_group.stream.ConsolidatedInputStream
import java.io.InputStream

class MP3Parser(from: InputStream) : Parser<Nothing?, MP3Frame, InputStream>("MPEG 3", from) {
	private val consolidatoryStream = ConsolidatedInputStream()
	override fun readBase(): MP3Frame {
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
		val channelMode = ChannelMode.get((header4 and 0b11000000) shr 6)
		val emphasis = MP3Emphasis.get(header4 and 0b00000001)

		val header = MP3Header(
			versionId,
			layerDescription,
			(header2 and 0b00000001) == 0,
			bitrate,
			sampleRate,
			((header3 and 0b00000010) shr 1) == 1,
			(header3 and 0b00000001) == 1,
			channelMode,
			"${(header4 and 0b00110000) shr 4} (todo)",
			((header4 and 0b00000100) shr 2) == 1,
			((header4 and 0b00000010) shr 1) == 1,
			emphasis
		)

		return MP3Frame(header, ByteArray(0))
	}

	val id3Header: ID3Header?

	init {
		consolidatoryStream.streams.addFirst(fqIn)
		val id3Scan = fqIn.readNBytes(3)
		if (id3Scan.decodeToString() == "ID3") {
			val version = "${consolidatoryStream.read()}.${consolidatoryStream.read()}"
			val flags = consolidatoryStream.read()
			val unSynchronisation = (flags shr 7) == 1
			val extended = (flags shr 6) == 1
			val experimental = (flags shr 5) == 1
			var size = 0

			for (i in 3 downTo 0) {
				val read = consolidatoryStream.read()
				size = size or ((read and 0b01111111) shl (i * 7))
			}

			val offsetInt = size + 10
			val hexOffset = hex(offsetInt)

			id3Header = ID3Header(
				version, unSynchronisation, extended, experimental,
				size, hexOffset, offsetInt
			)

			consolidatoryStream.readNBytes(offsetInt - 10)
			logger.info(id3Header.toString())
		} else {
			consolidatoryStream.streams.addFirst(id3Scan.inputStream())
			id3Header = null
		}
	}

	override fun responsibleStream(of: MP3Frame): InputStream = fqIn
	override var next: MP3Frame? = readBase()
}