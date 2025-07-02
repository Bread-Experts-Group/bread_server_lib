package org.bread_experts_group.coder.format.id3

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.id3.frame.*
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.InputStream
import java.math.BigInteger
import java.net.URI
import java.util.*

class ID3Parser(from: InputStream) : Parser<String, ID3Frame<*>, InputStream>("ID3", from) {
	private var unsupported = -1
	private var preFrame: ID3Header?
	override var fqIn: FailQuickInputStream = super.fqIn

	init {
		val tag = fqIn.readString(3)
		if (tag != "ID3") throw DecodingException("Not an ID3 stream")
		val major = fqIn.read()
		if (major != 3) {
			preFrame = null
			unsupported = major
		} else {
			val minor = fqIn.read()
			val flags = fqIn.read()
			var size = 0
			for (i in 3 downTo 0) size = size or (fqIn.read() shl (7 * i))
			fqIn = FailQuickInputStream(fqIn.readNBytes(size).inputStream())
			preFrame = ID3Header(major, minor, flags)
		}
	}

	override fun responsibleStream(of: ID3Frame<*>): InputStream = of.data.inputStream()
	override fun readBase(): ID3Frame<*>? {
		if (unsupported != -1) throw DecodingException("ID3 major version is unsupported [$unsupported]")
		preFrame.also { preFrame = null }?.let { return it }
		val frameID = fqIn.readString(4)
		val size = fqIn.read32()
		val flags = fqIn.read16ui()
		if (size < 1) return null
		return ID3Frame(
			frameID,
			ID3GenericFlags.entries,
			flags,
			fqIn.readNBytes(size)
		)
	}

	fun textFrame(id: String) {
		addParser(id) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3TextFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(encoding.charset)
			)
		}
	}

	fun urlFrame(id: String) {
		addParser(id) { stream, frame ->
			ID3URLLinkFrame(
				frame.tag, frame.flags.raw().toInt(),
				URI(stream.readString(Charsets.ISO_8859_1))
			)
		}
	}

	init {
		textFrame("TIT2")
		textFrame("TPE1")
		textFrame("TRCK")
		textFrame("TALB")
		textFrame("TPOS")
		textFrame("TDRC")
		textFrame("TCON")
		textFrame("TENC")
		textFrame("TPE2")
		textFrame("TCOP")
		textFrame("TSSE")
		urlFrame("WCOM")
		urlFrame("WCOP")
		urlFrame("WOAF")
		urlFrame("WOAR")
		urlFrame("WOAS")
		urlFrame("WORS")
		urlFrame("WPAY")
		urlFrame("WPUB")
		addParser("POPM") { stream, frame ->
			ID3PopularimeterFrame(
				frame.tag, frame.flags.raw().toInt(),
				stream.readString(Charsets.ISO_8859_1),
				stream.read(),
				stream.readAllBytes().let { if (it.size == 0) BigInteger.ZERO else BigInteger(it) }
			)
		}
		addParser("COMM") { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("USLT") { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("APIC") { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3PictureFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(Charsets.ISO_8859_1),
				ID3PictureType.entries.id(stream.read()),
				stream.readString(encoding.charset),
				stream.readAllBytes()
			)
		}
	}

	override var next: ID3Frame<*>? = if (unsupported != -1) null else refineNext()
}