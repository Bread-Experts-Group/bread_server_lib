package org.bread_experts_group.irc

import org.bread_experts_group.CharacterWritable
import org.bread_experts_group.socket.scanDelimiter
import java.io.InputStreamReader
import java.io.Writer

class IRCMessage(
	val tags: Map<String, String>,
	val source: String,
	val command: String,
	val parameters: String
) : CharacterWritable {
	override fun toString(): String = "(IRC, $command) " +
			(if (tags.isNotEmpty()) "TAGS [${tags.size}][${tags.entries.joinToString(";") { "${it.key}=${it.value}" }}] "
			else "") +
			(if (source.isNotEmpty()) "SOURCE [$source] " else "") +
			"<$parameters>"

	override fun write(writer: Writer) {
		if (tags.isNotEmpty())
			writer.write("@${tags.entries.joinToString(";") { "${it.key}=${it.value}" }} ")
		if (source.isNotEmpty())
			writer.write(":$source ")
		writer.write("$command${if (parameters.isNotEmpty()) " $parameters" else ""}\r\n")
	}

	companion object {
		fun read(stream: InputStreamReader): IRCMessage {
			var initialA = Char(stream.read())
			val tags = if (initialA == '@') buildMap {
				val readTags = stream.scanDelimiter(" ")
				readTags.split(';').forEach {
					val (key, value) = it.split('=')
					this[key] = value
				}
				initialA = Char(stream.read())
			} else emptyMap()
			val source = if (initialA == ':') {
				val read = stream.scanDelimiter(" ")
				initialA = Char(stream.read())
				read
			} else ""
			val (command, arguments) = stream.scanDelimiter("\r\n").split(' ', limit = 2)
			return IRCMessage(tags, source, initialA + command, arguments)
		}
	}
}