package org.bread_experts_group.coder.format.id3

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.id3.frame.ID3BaseFrame
import org.bread_experts_group.coder.format.id3.frame.ID3GenericFlags
import org.bread_experts_group.coder.format.id3.frame.ID3Header
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import java.io.InputStream

class ID3Parser(from: InputStream) : Parser<String, ID3BaseFrame<*>, InputStream>("ID3", from) {
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

	override fun responsibleStream(of: ID3BaseFrame<*>): InputStream = of.data.inputStream()
	override fun readBase(): ID3BaseFrame<*>? {
		if (unsupported != -1) throw DecodingException("ID3 major version is unsupported [$unsupported]")
		preFrame.also { preFrame = null }?.let { return it }
		val frameID = fqIn.readString(4)
		val size = fqIn.read32()
		val flags = fqIn.read16ui()
		if (size < 1) return null
		return ID3BaseFrame(
			frameID,
			ID3GenericFlags.entries,
			flags,
			fqIn.readNBytes(size)
		)
	}

	init {
	}

	override var next: ID3BaseFrame<*>? = if (unsupported != -1) null else refineNext()
}