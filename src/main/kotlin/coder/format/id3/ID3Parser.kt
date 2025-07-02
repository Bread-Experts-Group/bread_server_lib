package org.bread_experts_group.coder.format.id3

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.id3.frame.*
import org.bread_experts_group.stream.*
import java.io.InputStream
import java.math.BigInteger
import java.net.URI
import java.util.*

class ID3Parser(from: InputStream) : Parser<String, ID3Frame<*>, InputStream>("ID3", from) {
	private var unsupported = false
	private var version: Int = 0
	private var preFrame: ID3Header?
	override var fqIn: FailQuickInputStream = super.fqIn

	override fun toString(): String = "ID3Parser[${!unsupported}/v$version]"

	init {
		val tag = fqIn.readString(3)
		if (tag != "ID3") throw DecodingException("Not an ID3 stream")
		version = fqIn.read()
		val minor = fqIn.read()
		val flags = fqIn.read()
		var size = 0
		for (i in 3 downTo 0) size = size or (fqIn.read() shl (7 * i))
		if (version !in 2..3) {
			preFrame = null
			unsupported = true
			fqIn.skip(size.toLong())
		} else {
			fqIn = FailQuickInputStream(fqIn.readNBytes(size).inputStream())
			preFrame = ID3Header(version, minor, flags)
		}
	}

	override fun responsibleStream(of: ID3Frame<*>): InputStream = of.data.inputStream()
	override fun readBase(): ID3Frame<*>? {
		if (unsupported) throw DecodingException("ID3 major version is unsupported [$version]")
		preFrame.also { preFrame = null }?.let { return it }
		return when (version) {
			3 -> {
				val frameID = fqIn.readString(4)
				val size = fqIn.read32()
				val flags = fqIn.read16ui()
				if (size < 1) return null
				ID3Frame(
					frameID,
					ID3GenericFlags.entries,
					flags,
					fqIn.readNBytes(size)
				)
			}

			2 -> {
				val frameID = fqIn.readString(3)
				val size = fqIn.read24()
				if (size < 1) return null
				ID3Frame(
					frameID,
					ID3GenericFlags.entries,
					0,
					fqIn.readNBytes(size)
				)
			}

			else -> throw IllegalStateException("Unsupported version [$version]")
		}
	}

	override fun refineBase(of: ID3Frame<*>, vararg parameters: Any): ID3Frame<*> {
		return super.refineBase(of, version)
	}

	override fun addParser(identifier: String, parser: (InputStream, ID3Frame<*>) -> ID3Frame<*>) =
		throw UnsupportedOperationException()

	fun addParser(identifier: String, major: Int, parser: (InputStream, ID3Frame<*>) -> ID3Frame<*>) {
		super.addParserParameterized(identifier, { stream, frame, args, params ->
			if (params[0] != args[0]) throw DecodingException("Parser encountered bad version [${params[0]}]")
			parser(stream, frame)
		}, major)
	}

	fun textFrame(id: String, major: Int) {
		addParser(id, major) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3TextFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(encoding.charset)
			)
		}
	}

	fun urlFrame3(id: String) {
		addParser(id, 3) { stream, frame ->
			ID3URLLinkFrame(
				frame.tag, frame.flags.raw().toInt(),
				URI(stream.readString(Charsets.ISO_8859_1))
			)
		}
	}

	init {
		textFrame("TIT2", 3)
		textFrame("TPE1", 3)
		textFrame("TRCK", 3)
		textFrame("TALB", 3)
		textFrame("TPOS", 3)
		textFrame("TDRC", 3)
		textFrame("TCON", 3)
		textFrame("TENC", 3)
		textFrame("TPE2", 3)
		textFrame("TCOP", 3)
		textFrame("TSSE", 3)
		textFrame("TP1", 2)
		textFrame("TP2", 2)
		textFrame("TCM", 2)
		textFrame("TAL", 2)
		textFrame("TPA", 2)
		textFrame("TYE", 2)
		textFrame("TCO", 2)
		textFrame("TRK", 2)
		textFrame("TT2", 2)
		urlFrame3("WCOM")
		urlFrame3("WCOP")
		urlFrame3("WOAF")
		urlFrame3("WOAR")
		urlFrame3("WOAS")
		urlFrame3("WORS")
		urlFrame3("WPAY")
		urlFrame3("WPUB")
		addParser("POPM", 3) { stream, frame ->
			ID3PopularimeterFrame(
				frame.tag, frame.flags.raw().toInt(),
				stream.readString(Charsets.ISO_8859_1),
				stream.read(),
				stream.readAllBytes().let { if (it.size == 0) BigInteger.ZERO else BigInteger(it) }
			)
		}
		addParser("COMM", 3) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("USLT", 3) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("APIC", 3) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3PictureFrame3(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(Charsets.ISO_8859_1),
				ID3PictureType.entries.id(stream.read()),
				stream.readString(encoding.charset),
				stream.readAllBytes()
			)
		}
		addParser("PIC", 2) { stream, frame ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3PictureFrame2(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(3),
				ID3PictureType.entries.id(stream.read()),
				stream.readString(encoding.charset),
				stream.readAllBytes()
			)
		}
	}

	override var next: ID3Frame<*>? = if (unsupported) null else refineNext()
}