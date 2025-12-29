package org.bread_experts_group.protocol.http.h11

/**
 * Decodes header fields in an HTTP/1.1 message.
 * @param string The string builder used to read headers into.
 * @param fieldsMax The maximum header fields that can be read.
 * @param fieldKeyMax The maximum header field key length that can be read.
 * @param fieldValueMax The maximum header field value length that can be read.
 * @param nextCharacter A reference to a function which reads the next character in the stream.
 * @return Either the parsed header map, or an error.
 * @since D1F3N6P0
 * @author Miko Elbrecht
 */
internal fun readHeaderFields(
	string: StringBuilder,
	fieldsMax: Int,
	fieldKeyMax: Int,
	fieldValueMax: Int,
	nextCharacter: () -> String?
): Pair<Map<String, String>?, HTTP11ParsingStatus?> {
	val headers = mutableMapOf<String, String>()
	var key: String? = null
	while (true) {
		if (headers.size > fieldsMax) return headers to HTTP11ParsingStatus.BadForm.Fields.TooMany(
			fieldsMax
		)
		val next = nextCharacter() ?: return headers to HTTP11ParsingStatus.TimedOut
		if (key == null) {
			when (next) {
				"\n" -> {
					if (string.length > 1 || (string.length == 1 && string[0] != '\r'))
						return headers to HTTP11ParsingStatus.EndedPremature.FieldKey
					else break
				}

				":" -> {
					key = string.toString()
					string.clear()
				}

				else -> {
					string.append(next)
					if (string.length >= fieldKeyMax)
						return headers to HTTP11ParsingStatus.BadForm.Fields.KeyTooLarge(fieldKeyMax)
				}
			}
			continue
		}
		if (string.isEmpty() && next.isBlank()) continue
		if (next == "\n") {
			if (string.last() == '\r') string.setLength(string.length - 1)
			headers[key.lowercase()] = string.toString().trimEnd()
			string.clear()
			key = null
			continue
		}
		string.append(next)
		if (string.length >= fieldValueMax)
			return headers to HTTP11ParsingStatus.BadForm.Fields.ValueTooLarge(fieldValueMax)
	}
	string.clear()
	return headers to null
}

/**
 * Decodes the version in an HTTP/1.1 message.
 * @param string The string builder used to read the version into.
 * @param nextCharacter A reference to a function which reads the next character in the stream.
 * @return An error, if any.
 * @since D1F3N6P0
 * @author Miko Elbrecht
 */
internal fun checkHTTPVersion(
	string: StringBuilder,
	nextCharacter: () -> String?
): HTTP11ParsingStatus? {
	while (string.length < 4) string.append(nextCharacter() ?: return HTTP11ParsingStatus.TimedOut)
	if (!string.contentEquals("HTTP")) return HTTP11ParsingStatus.BadForm.Version.HTTP(string)
	string.clear()
	val slash = nextCharacter() ?: return HTTP11ParsingStatus.TimedOut
	if (slash != "/") return HTTP11ParsingStatus.BadForm.Version.Slash(slash)
	while (string.length < 3) string.append(nextCharacter())
	if (!string.contentEquals("1.1")) return HTTP11ParsingStatus.BadForm.Version.HTTP11(string)
	string.clear()
	return null
}