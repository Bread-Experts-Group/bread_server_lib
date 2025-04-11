package bread_experts_group.dns

import bread_experts_group.SmartToString
import bread_experts_group.hex
import bread_experts_group.read16
import bread_experts_group.write16
import java.io.InputStream
import java.io.OutputStream

class DNSMessage private constructor(
	val transactionID: Int,
	val reply: Boolean,
	val opcode: DNSOpcode,
	val authoritative: Boolean,
	val truncated: Boolean,
	val recursiveQuery: Boolean,
	val recursionAvailable: Boolean,
	val authenticData: Boolean,
	val checkingDisabled: Boolean,
	val responseCode: DNSResponseCode,
	val questions: List<DNSQuestion>,
	val answers: List<DNSResourceRecord>,
	val authorityRecords: List<DNSResourceRecord>,
	val additionalRecords: List<DNSResourceRecord>
) : SmartToString() {
	fun write(stream: OutputStream) {
		stream.write16(transactionID)
		var thirdByte = if (recursiveQuery) 1 else 0
		if (truncated) thirdByte = thirdByte or 0b10
		if (authoritative) thirdByte = thirdByte or 0b100
		thirdByte = thirdByte or (opcode.code shl 3)
		if (reply) thirdByte = thirdByte or 0b10000000
		stream.write(thirdByte)
		var fourthByte = responseCode.code
		if (checkingDisabled) fourthByte = fourthByte or 0b10000
		if (authenticData) fourthByte = fourthByte or 0b100000
		if (recursionAvailable) fourthByte = fourthByte or 0b10000000
		stream.write(fourthByte)
		stream.write16(questions.size)
		stream.write16(answers.size)
		stream.write16(authorityRecords.size)
		stream.write16(additionalRecords.size)
		questions.forEach { it.write(stream) }
		answers.forEach { it.write(stream) }
		authorityRecords.forEach { it.write(stream) }
		additionalRecords.forEach { it.write(stream) }
	}

	override fun gist(): String = "(DNS, ${if (reply) "<Res>" else "<Req>"}, ${opcode.name}) " + buildString {
		append("ID#${hex(transactionID)}; ")
		append("Qst# ${questions.size}, ")
		append("Ans# ${answers.size}, ")
		append("Ath# ${authorityRecords.size}, ")
		append("Add# ${additionalRecords.size} ")
		append('[')
		if (authoritative) append("AA ")
		if (truncated) append("TC ")
		if (recursiveQuery) append("RD ")
		if (recursionAvailable) append("RA ")
		if (authenticData) append("AD ")
		if (checkingDisabled) append("CD ")
		append(']')
		append(" ${responseCode.name}")
		questions.forEach { append("\nQST: $it") }
		answers.forEach { append("\nANS: $it") }
		authorityRecords.forEach { append("\nATH: $it") }
		additionalRecords.forEach { append("\nADD: $it") }
	}

	companion object {
		fun query(
			transactionID: Int,
			opcode: DNSOpcode,
			recursiveQuery: Boolean,
			checkingDisabled: Boolean,
			questions: List<DNSQuestion>,
			additionalRecords: List<DNSResourceRecord> = emptyList()
		) = DNSMessage(
			transactionID, true, opcode, false, false,
			recursiveQuery, false, false, checkingDisabled, DNSResponseCode.OK,
			questions, emptyList(), emptyList(), additionalRecords
		)

		fun reply(
			transactionID: Int,
			opcode: DNSOpcode,
			authoritative: Boolean,
			authenticData: Boolean,
			recursionAvailable: Boolean,
			responseCode: DNSResponseCode,
			questions: List<DNSQuestion>,
			answers: List<DNSResourceRecord>,
			authorityRecords: List<DNSResourceRecord> = emptyList(),
			additionalRecords: List<DNSResourceRecord> = emptyList()
		) = DNSMessage(
			transactionID, false, opcode, authoritative, false,
			false, recursionAvailable, authenticData, false, responseCode,
			questions, answers, authorityRecords, additionalRecords
		)

		fun read(stream: InputStream): DNSMessage {
			val transactionID = stream.read16()
			val flags = stream.read16()
			val questions = stream.read16()
			val answers = stream.read16()
			val authorityRecords = stream.read16()
			val additionalRecords = stream.read16()
			return DNSMessage(
				transactionID,
				(flags and 0b1000000000000000) > 0,
				DNSOpcode.mapping.getValue((flags and 0b0111100000000000) shr 11),
				(flags and 0b0000010000000000) > 0,
				(flags and 0b0000001000000000) > 0,
				(flags and 0b0000000100000000) > 0,
				(flags and 0b0000000010000000) > 0,
				(flags and 0b0000000000100000) > 0,
				(flags and 0b0000000000010000) > 0,
				DNSResponseCode.mapping.getValue(flags and 0b0000000000001111),
				List(questions) { DNSQuestion.read(stream) },
				List(answers) { DNSResourceRecord.read(stream) },
				List(authorityRecords) { DNSResourceRecord.read(stream) },
				List(additionalRecords) { DNSResourceRecord.read(stream) },
			)
		}
	}
}