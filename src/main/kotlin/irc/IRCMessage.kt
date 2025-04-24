package org.bread_experts_group.irc

import org.bread_experts_group.socket.scanDelimiter
import java.io.InputStream

class IRCMessage(
	val tags: Map<String, String>,
	val source: String,
	val command: String,
	val parameters: String
) {
	companion object {
		fun read(stream: InputStream): IRCMessage {
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