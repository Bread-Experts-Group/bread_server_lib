package org.bread_experts_group.coder.fixed.json

import org.bread_experts_group.coder.DecodingException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal

sealed class JSONElement {
	class TrackingBufferedReader(reader: InputStreamReader) : BufferedReader(reader) {
		private var lastRead: Char? = null
		fun readWithCached(): Char =
			if (lastRead == null) readCharNoCache()
			else lastRead!!.also { lastRead = null }

		fun readCharNoCache(): Char = Char(super.read())
		fun readCharAndCache(): Char {
			val next = Char(super.read())
			lastRead = next
			return next
		}

		@Deprecated("Use TrackingBufferedReader.readChar instead", ReplaceWith("readChar()"))
		override fun read(): Int = throw DecodingException("Not designated for normal use")
	}

	fun <T> asObject(init: JSONObject.() -> T): T = (this as JSONObject).init()
	fun <T> asBoolean(init: JSONBoolean.() -> T): T = (this as JSONBoolean).init()
	fun <T> asNumber(init: JSONNumber.() -> T): T = (this as JSONNumber).init()
	fun <T> asString(init: JSONString.() -> T): T = (this as JSONString).init()
	fun <T> asArray(init: JSONArray.() -> T): T = (this as JSONArray).init()

	companion object {
		abstract class BoundsExit(char: Char) : DecodingException("JSON element entry '$char'")
		class ObjectExit : BoundsExit('}')
		class ArrayExit : BoundsExit(']')

		fun read(stream: TrackingBufferedReader): JSONElement {
			while (true) {
				return when (val entry = stream.readWithCached()) {
					'{' -> JSONObject.localRead(stream)
					'[' -> JSONArray.localRead(stream)
					'"' -> {
						var concatenated = ""
						while (true) {
							val next = stream.readCharNoCache()
							if (next == '"') break
							concatenated += next
						}
						JSONString(concatenated)
					}

					't' -> JSONBoolean(stream.skip(3).let { true })
					'f' -> JSONBoolean(stream.skip(4).let { false })
					'}' -> throw ObjectExit()
					']' -> throw ArrayExit()
					else -> if (entry.isDigit()) {
						var concatenated = entry.toString()
						while (true) {
							val next = stream.readCharAndCache()
							if (!(next.isDigit() || next == '.')) break
							concatenated += next
						}
						JSONNumber(BigDecimal(concatenated))
					} else if (entry.isWhitespace() || entry == ',') continue
					else throw DecodingException("JSON element entry '$entry'")
				}
			}
		}

		fun json(stream: InputStream): JSONElement = read(
			TrackingBufferedReader(InputStreamReader(stream, Charsets.UTF_8))
		)
	}
}