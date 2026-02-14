package org.bread_experts_group.generic.protocol.http.h11

import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.io.reader.SequentialDataSource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Decodes an HTTP/1.1 request from the provided [source].
 * @param source The [SequentialDataSource] to read HTTP/1.1 data from.
 * @param features Additional data to influence parsing.
 * @return A list containing the supported [org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier]s,
 * successfully read [org.bread_experts_group.generic.protocol.http.h11.RequestH11Method], [org.bread_experts_group.generic.protocol.http.h11.RequestH11Target], [org.bread_experts_group.generic.protocol.http.h11.H11Fields], and
 * a variant of [org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus] on failure.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
fun h11RequestFrom(
	source: SequentialDataSource,
	vararg features: org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier
): List<org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingDataIdentifier> {
	val data = mutableListOf<org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingDataIdentifier>()
	source.timeout =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.ParsingBufferReadTimeout }
			.let {
				if (it == null) 2.toDuration(DurationUnit.SECONDS)
				else {
					data.add(it)
					it.timeout
				}
			}
	val totalReadsTimeout =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.ParsingReadsTimeout }.let {
			if (it == null) 5.toDuration(DurationUnit.SECONDS).inWholeNanoseconds
			else {
				data.add(it)
				it.timeout.inWholeNanoseconds
			}
		}
	val methodMax =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.MaxLength.Method }.let {
			if (it == null) 512
			else {
				data.add(it)
				it.length
			}
		}
	val targetMax =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.MaxLength.Target }.let {
			if (it == null) 8192
			else {
				data.add(it)
				it.length
			}
		}
	val fieldsMax =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.MaxLength.Field.Count }
			.let {
				if (it == null) 1024
				else {
					data.add(it)
					it.length
				}
			}
	val fieldKeyMax =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.MaxLength.Field.Key }.let {
			if (it == null) 1024
			else {
				data.add(it)
				it.length
			}
		}
	val fieldValueMax =
		features.firstNotNullOfOrNull { it as? org.bread_experts_group.generic.protocol.http.MaxLength.Field.Value }
			.let {
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
	while (true) {
		val next = nextCharacter()
			?: return data.also { it.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus.TimedOut) }
		if (next == "\n") {
			data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.EndedPremature.Method)
			return data
		} else {
			if (next.isBlank()) {
				if (string.isEmpty()) {
					data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.EndedPremature.Method)
					return data
				}
				break
			}
			string.append(next)
			if (string.length > methodMax) {
				data.add(
					_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.BadForm.MethodTooLarge(
						methodMax
					)
				)
				return data
			}
		}
	}
	data.add(
		_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.RequestH11Method(
			_root_ide_package_.org.bread_experts_group.generic.protocol.http.HTTPStandardMethods.entries.id(
				string.toString()
			)
		)
	)
	string.clear()
	var skippingWhiteSpace = true
	while (true) {
		val next = nextCharacter()
			?: return data.also { it.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus.TimedOut) }
		if (next == "\n") {
			data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.EndedPremature.Target)
			return data
		} else {
			if (next.isBlank()) {
				if (skippingWhiteSpace) continue
				else break
			}
			skippingWhiteSpace = false
			string.append(next)
			if (string.length > targetMax) {
				data.add(
					_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.BadForm.TargetTooLarge(
						targetMax
					)
				)
				return data
			}
		}
	}
	// TODO: Formal target parsing
	data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.RequestH11Target(string.toString()))
	string.clear()

	while (true) {
		val next = nextCharacter()
			?: return data.also { it.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus.TimedOut) }
		if (next.isBlank()) continue
		string.append(next)
		break
	}
	val versionStatus =
		_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.checkHTTPVersion(string, ::nextCharacter)
	if (versionStatus != null) {
		data.add(versionStatus)
		return data
	}
	while (true) {
		val next = nextCharacter()
			?: return data.also { it.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus.TimedOut) }
		if (next == "\n") break
		if (next.isBlank()) continue
		data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingStatus.TrashVersion)
		return data
	}

	val (headers, status) = _root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.readHeaderFields(
		string,
		fieldsMax,
		fieldKeyMax,
		fieldValueMax,
		::nextCharacter
	)
	if (headers != null) data.add(_root_ide_package_.org.bread_experts_group.generic.protocol.http.h11.H11Fields(headers))
	if (status != null) {
		data.add(status)
		return data
	}
	return data
}