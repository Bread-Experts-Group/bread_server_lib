package org.bread_experts_group.protocol.irc

import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.scanDelimiter
import org.bread_experts_group.stream.writeString
import java.io.InputStream
import java.io.OutputStream

class IRCMessage(
	val tags: Map<String, String>,
	val source: String,
	val command: String,
	val parameters: String
) : Writable {
	override fun toString(): String = "(IRC, $command) " +
			(if (tags.isNotEmpty()) "TAGS [${tags.size}][${
				tags.entries.joinToString(";") { "${it.key}=${it.value}" }
			}] "
			else "") +
			(if (source.isNotEmpty()) "SOURCE [$source] " else "") +
			"<$parameters>"

	override fun write(stream: OutputStream) {
		if (tags.isNotEmpty())
			stream.writeString("@${tags.entries.joinToString(";") { "${it.key}=${it.value}" }} ")
		if (source.isNotEmpty())
			stream.writeString(":$source ")
		stream.writeString("$command${if (parameters.isNotEmpty()) " $parameters" else ""}\r\n")
	}

	companion object {
		fun read(stream: InputStream): IRCMessage {
			val reader = stream.reader()
			var initialA = Char(reader.read())
			val tags = if (initialA == '@') buildMap {
				val readTags = reader.scanDelimiter(" ")
				readTags.split(';').forEach {
					val (key, value) = it.split('=')
					this[key] = value
				}
				initialA = Char(reader.read())
			} else emptyMap()
			val source = if (initialA == ':') {
				val read = reader.scanDelimiter(" ")
				initialA = Char(reader.read())
				read
			} else ""
			val (command, arguments) = reader.scanDelimiter("\r\n").split(' ', limit = 2)
			return IRCMessage(tags, source, initialA + command, arguments)
		}
	}
}