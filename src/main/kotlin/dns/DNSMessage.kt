package org.bread_experts_group.dns

import org.bread_experts_group.hex
import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.write16
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class DNSMessage private constructor(
	val transactionID: Int,
	val reply: Boolean,
	val opcode: DNSOpcode,
	val authoritative: Boolean,
	truncated: Boolean,
	val maxLength: Int?,
	val recursiveQuery: Boolean,
	val recursionAvailable: Boolean,
	val authenticData: Boolean,
	val checkingDisabled: Boolean,
	val responseCode: DNSResponseCode,
	val responseCodeRaw: Int,
	val questions: List<DNSQuestion>,
	val answers: List<DNSResourceRecord>,
	val authorityRecords: List<DNSResourceRecord>,
	val additionalRecords: List<DNSResourceRecord>
) : Writable {
	var truncated: Boolean = truncated
		internal set
	internal var currentSize: Int = 12

	override fun write(stream: OutputStream) {
		stream.write16(transactionID)
		var actualQuestions = 0
		var actualAnswers = 0
		var actualAA = 0
		var actualADD = 0
		val data = ByteArrayOutputStream().use { dataStream ->
			questions.forEach {
				it.write(this, dataStream)
				if (!truncated) actualQuestions++
			}
			if (truncated) return@use dataStream.toByteArray()
			answers.forEach {
				it.write(this, dataStream)
				if (!truncated) actualAnswers++
			}
			if (truncated) return@use dataStream.toByteArray()
			authorityRecords.forEach {
				it.write(this, dataStream)
				if (!truncated) actualAA++
			}
			if (truncated) return@use dataStream.toByteArray()
			additionalRecords.forEach {
				it.write(this, dataStream)
				if (!truncated) actualADD++
			}
			dataStream.toByteArray()
		}
		var thirdByte = if (recursiveQuery) 1 else 0
		if (truncated) thirdByte = thirdByte or 0b10
		if (authoritative) thirdByte = thirdByte or 0b100
		thirdByte = thirdByte or (opcode.code shl 3)
		if (reply) thirdByte = thirdByte or 0b10000000
		stream.write(thirdByte)
		var fourthByte = responseCodeRaw
		if (checkingDisabled) fourthByte = fourthByte or 0b10000
		if (authenticData) fourthByte = fourthByte or 0b100000
		if (recursionAvailable) fourthByte = fourthByte or 0b10000000
		stream.write(fourthByte)
		stream.write16(actualQuestions)
		stream.write16(actualAnswers)
		stream.write16(actualAA)
		stream.write16(actualADD)
		stream.write(data)
		currentSize = 12
		truncated = false
	}

	override fun toString(): String = "(DNS, ${if (reply) "<Res>" else "<Req>"}) ${opcode.name} " +
			"[${hex(transactionID.toShort())}] [Qst/Ans/Ath/Add:" +
			"${questions.size}/${answers.size}/${authorityRecords.size}/${additionalRecords.size}] " +
			"[${
				buildList {
					if (authoritative) add("AA")
					if (truncated) add("TC")
					if (recursiveQuery) add("RD")
					if (recursionAvailable) add("RA")
					if (authenticData) add("AD")
					if (checkingDisabled) add("CD")
				}.joinToString(" ")
			}] ${responseCode.name} [$responseCodeRaw]" +
			buildString {
				for (q in questions) append("\nQST: $q")
				for (a in answers) append("\nANS: $a")
				for (aa in authorityRecords) append("\nATH: $aa")
				for (ar in additionalRecords) append("\nADD: $ar")
			}

	companion object {
		fun query(
			transactionID: Int,
			opcode: DNSOpcode,
			recursiveQuery: Boolean,
			checkingDisabled: Boolean,
			questions: List<DNSQuestion>,
			additionalRecords: List<DNSResourceRecord> = emptyList()
		): DNSMessage = DNSMessage(
			transactionID,
			reply = false,
			opcode,
			authoritative = false,
			truncated = false,
			maxLength = null,
			recursiveQuery = recursiveQuery,
			recursionAvailable = false,
			authenticData = false,
			checkingDisabled = checkingDisabled,
			responseCode = DNSResponseCode.OK,
			responseCodeRaw = 0,
			questions = questions,
			answers = emptyList(),
			authorityRecords = emptyList(),
			additionalRecords = additionalRecords
		)

		fun reply(
			to: DNSMessage,
			maxLength: Int? = null,
			authoritative: Boolean,
			authenticData: Boolean,
			recursionAvailable: Boolean,
			responseCode: DNSResponseCode,
			answers: List<DNSResourceRecord>,
			authorityRecords: List<DNSResourceRecord> = emptyList(),
			additionalRecords: List<DNSResourceRecord> = emptyList()
		): DNSMessage = DNSMessage(
			to.transactionID,
			true,
			to.opcode,
			authoritative,
			false,
			maxLength,
			to.recursiveQuery,
			recursionAvailable,
			authenticData,
			to.checkingDisabled,
			responseCode,
			responseCode.code,
			to.questions,
			answers,
			authorityRecords,
			additionalRecords
		)

		fun read(stream: InputStream): DNSMessage {
			val lookbehind = ByteArrayOutputStream()
			val lookbehindRead = object : InputStream() {
				override fun read(): Int {
					val read = stream.read()
					lookbehind.write(read)
					return read
				}
			}
			val transactionID = lookbehindRead.read16ui()
			val flags = lookbehindRead.read16ui()
			val questions = lookbehindRead.read16ui()
			val answers = lookbehindRead.read16ui()
			val authorityRecords = lookbehindRead.read16ui()
			val additionalRecords = lookbehindRead.read16ui()
			return DNSMessage(
				transactionID,
				(flags and 0b1000000000000000) > 0,
				DNSOpcode.mapping[(flags and 0b0111100000000000) shr 11] ?: DNSOpcode.OTHER,
				(flags and 0b0000010000000000) > 0,
				(flags and 0b0000001000000000) > 0,
				null,
				(flags and 0b0000000100000000) > 0,
				(flags and 0b0000000010000000) > 0,
				(flags and 0b0000000000100000) > 0,
				(flags and 0b0000000000010000) > 0,
				DNSResponseCode.mapping[flags and 0b0000000000001111] ?: DNSResponseCode.OTHER,
				flags and 0b0000000000001111,
				List(questions) {
					DNSQuestion.read(lookbehindRead, lookbehind.toByteArray())
				},
				List(answers) {
					DNSResourceRecord.read(lookbehindRead, lookbehind.toByteArray())
				},
				List(authorityRecords) {
					DNSResourceRecord.read(lookbehindRead, lookbehind.toByteArray())
				},
				List(additionalRecords) {
					DNSResourceRecord.read(lookbehindRead, lookbehind.toByteArray())
				},
			)
		}
	}
}