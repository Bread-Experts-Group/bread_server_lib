package org.bread_experts_group.protocol.http.h11

import org.bread_experts_group.io.reader.SequentialDataSource
import org.bread_experts_group.protocol.http.MaxLength
import org.bread_experts_group.protocol.http.ParsingBufferReadTimeout
import org.bread_experts_group.protocol.http.ParsingReadsTimeout
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Decodes an HTTP/1.1 response from the provided [source].
 * @param source The [SequentialDataSource] to read HTTP/1.1 data from.
 * @param features Additional data to influence parsing.
 * @return A list containing the supported [HTTP11ResponseParsingFeatureIdentifier]s,
 * successfully read [ResponseH11StatusCode], [ResponseH11ReasonPhrase], [H11Fields], and
 * a variant of [HTTP11ParsingStatus] on failure.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
fun h11ResponseFrom(
	source: SequentialDataSource,
	vararg features: HTTP11ResponseParsingFeatureIdentifier
): List<HTTP11ResponseParsingDataIdentifier> {
	val data = mutableListOf<HTTP11ResponseParsingDataIdentifier>()
	source.timeout = features.firstNotNullOfOrNull { it as? ParsingBufferReadTimeout }.let {
		if (it == null) 2.toDuration(DurationUnit.SECONDS)
		else {
			data.add(it)
			it.timeout
		}
	}
	val totalReadsTimeout = features.firstNotNullOfOrNull { it as? ParsingReadsTimeout }.let {
		if (it == null) 5.toDuration(DurationUnit.SECONDS).inWholeNanoseconds
		else {
			data.add(it)
			it.timeout.inWholeNanoseconds
		}
	}
	val reasonPhraseMax = features.firstNotNullOfOrNull { it as? MaxLength.ReasonPhrase }.let {
		if (it == null) 4096
		else {
			data.add(it)
			it.length
		}
	}
	val fieldsMax = features.firstNotNullOfOrNull { it as? MaxLength.Field.Count }.let {
		if (it == null) 1024
		else {
			data.add(it)
			it.length
		}
	}
	val fieldKeyMax = features.firstNotNullOfOrNull { it as? MaxLength.Field.Key }.let {
		if (it == null) 1024
		else {
			data.add(it)
			it.length
		}
	}
	val fieldValueMax = features.firstNotNullOfOrNull { it as? MaxLength.Field.Value }.let {
		if (it == null) 1024
		else {
			data.add(it)
			it.length
		}
	}

	var readWaitingNs = 0L
	fun nextCharacter(): String? {
		if (readWaitingNs > totalReadsTimeout) return null
		val pCall = System.nanoTime()
		val char = source.readUTF8()
		readWaitingNs += System.nanoTime() - pCall
		return char
	}

	val string = StringBuilder(512)
	val versionStatus = checkHTTPVersion(string, ::nextCharacter)
	if (versionStatus != null) {
		data.add(versionStatus)
		return data
	}
	while (true) {
		val next = nextCharacter() ?: return data.also { it.add(HTTP11ParsingStatus.TimedOut) }
		if (next == "\n") {
			data.add(HTTP11ResponseParsingStatus.EndedPremature.StatusLine)
			return data
		} else if (next.isBlank()) continue
		else {
			string.append(next)
			break
		}
	}
	while (string.length < 3) {
		val next = nextCharacter() ?: return data.also { it.add(HTTP11ParsingStatus.TimedOut) }
		if (next.isBlank()) {
			data.add(HTTP11ResponseParsingStatus.EndedPremature.StatusCode)
			return data
		} else string.append(next)
	}
	val statusCode = string.toString().toIntOrNull()
	if (statusCode == null) {
		data.add(HTTP11ResponseParsingStatus.BadForm.StatusCode(string))
		return data
	}
	data.add(ResponseH11StatusCode(statusCode))
	string.clear()
	var onPhrase = false
	while (true) {
		val next = nextCharacter() ?: return data.also { it.add(HTTP11ParsingStatus.TimedOut) }
		if (next == "\n") {
			if (string.last() == '\r') string.setLength(string.length - 1)
			break
		} else {
			if (!next.isBlank()) {
				if (!onPhrase) {
					data.add(HTTP11ResponseParsingStatus.BadForm.ReasonPhrase.NoSP)
					return data
				}
			} else if (!onPhrase) {
				onPhrase = true
				continue
			}
			string.append(next)
			if (string.length >= reasonPhraseMax) {
				data.add(HTTP11ResponseParsingStatus.BadForm.ReasonPhrase.TooLarge(reasonPhraseMax))
				return data
			}
		}
	}
	data.add(ResponseH11ReasonPhrase(string.toString()))
	string.clear()
	val (headers, status) = readHeaderFields(string, fieldsMax, fieldKeyMax, fieldValueMax, ::nextCharacter)
	if (headers != null) data.add(H11Fields(headers))
	if (status != null) {
		data.add(status)
		return data
	}
	return data
}