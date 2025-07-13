package org.bread_experts_group.coder.format.parse.id3

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.id3.frame.*
import org.bread_experts_group.stream.*
import java.io.InputStream
import java.math.BigInteger
import java.net.URI
import java.util.*

class ID3Parser(from: InputStream) : Parser<String, ID3Frame<*>, InputStream>("ID3", from) {
	private var unsupported = false
	private var version: Int = 0
	private var preFrame: ID3Header? = null
	override var fqIn: FailQuickInputStream = super.fqIn

	override fun toString(): String = super.toString() + "[${!unsupported}/v$version]"
	fun readZeroPadded() = (3 downTo 0).fold(0) { acc, i -> acc or (fqIn.read() shl (7 * i)) }

	init {
		val tag = fqIn.readString(3)
		if (tag != "ID3") throw InvalidInputException("Not an ID3 stream")
		version = fqIn.read()
		val minor = fqIn.read()
		val flags = fqIn.read()
		val size = readZeroPadded()
		if (version !in 2..4) {
			unsupported = true
			fqIn.skip(size.toLong())
		} else {
			fqIn = FailQuickInputStream(fqIn.readNBytes(size).inputStream())
			preFrame = ID3Header(version, minor, flags)
		}
	}

	override fun responsibleStream(of: ID3Frame<*>): InputStream = of.data.inputStream()
	override fun readBase(compound: CodingCompoundThrowable): ID3Frame<*>? {
		if (unsupported) throw InvalidInputException("ID3 major version is unsupported [$version]")
		preFrame.also { preFrame = null }?.let { return it }
		return when (version) {
			4 -> {
				val frameID = fqIn.readString(4)
				val size = readZeroPadded()
				val flags = fqIn.read16ui()
				if (size < 1) return null
				ID3Frame(
					frameID,
					ID3GenericFlags.entries,
					flags,
					fqIn.readNBytes(size)
				)
			}

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

			else -> throw NotImplementedError("Unsupported version [$version]")
		}
	}

	override fun refineBase(
		compound: CodingCompoundThrowable, of: ID3Frame<*>,
		vararg parameters: Any
	): Pair<ID3Frame<*>, CodingException?> {
		return super.refineBase(compound, of, version)
	}

	init {
		addPredicateParser({ it.tag[0] == 'T' }) { stream, frame, _ ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3TextFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, buildList {
					try {
						while (true) add(stream.readString(encoding.charset))
					} catch (_: FailQuickInputStream.EndOfStream) {
					}
				}.toTypedArray()
			)
		}
		addPredicateParser({ it.tag[0] == 'W' }) { stream, frame, _ ->
			ID3URLLinkFrame(
				frame.tag, frame.flags.raw().toInt(),
				URI(stream.readString(Charsets.ISO_8859_1))
			)
		}
		addParser("POPM") { stream, frame, _ ->
			ID3PopularimeterFrame(
				frame.tag, frame.flags.raw().toInt(),
				stream.readString(Charsets.ISO_8859_1),
				stream.read(),
				stream.readAllBytes().let { if (it.size == 0) BigInteger.ZERO else BigInteger(it) }
			)
		}
		addParser("COMM") { stream, frame, _ ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("USLT") { stream, frame, _ ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3CommentFrame(
				frame.tag, frame.flags.raw().toInt(),
				encoding, Locale.of(stream.readString(3)),
				stream.readString(encoding.charset),
				stream.readString(encoding.charset)
			)
		}
		addParser("APIC") { stream, frame, _ ->
			val encoding = ID3TextEncoding.entries.id(stream.read())
			ID3PictureFrame3(
				frame.tag, frame.flags.raw().toInt(),
				encoding, stream.readString(Charsets.ISO_8859_1),
				ID3PictureType.entries.id(stream.read()),
				stream.readString(encoding.charset),
				stream.readAllBytes()
			)
		}
		addParser("PIC") { stream, frame, _ ->
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
}