package bread_experts_group.dns

import bread_experts_group.Writable
import bread_experts_group.hex
import bread_experts_group.read16ui
import bread_experts_group.write16
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
		questions.forEach { it.write(this, stream) }
		answers.forEach { it.write(this, stream) }
		authorityRecords.forEach { it.write(this, stream) }
		additionalRecords.forEach { it.write(this, stream) }
	}

	override fun toString() = "(DNS, ${if (reply) "<Res>" else "<Req>"}) ${opcode.name} " +
			"[${hex(transactionID.toShort())}] [Qst/Ans/Ath/Add:" +
			"${questions.size}/${answers.size}/${authorityRecords.size}/${additionalRecords.size}]" +
			"[${
				buildList {
					if (authoritative) add("AA")
					if (truncated) add("TC")
					if (recursiveQuery) add("RD")
					if (recursionAvailable) add("RA")
					if (authenticData) add("AD")
					if (checkingDisabled) add("CD")
				}.joinToString(" ")
			}] ${responseCode.name}" +
			buildString {
				for (q in questions) append("\nQST: $q")
				for (a in answers) append("\nQST: $a")
				for (aa in authorityRecords) append("\nQST: $aa")
				for (ar in additionalRecords) append("\nQST: $ar")
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
			transactionID, false, opcode, false, false, null,
			recursiveQuery, false, false, checkingDisabled, DNSResponseCode.OK,
			questions, emptyList(), emptyList(), additionalRecords
		)

		fun reply(
			transactionID: Int,
			maxLength: Int? = null,
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
			transactionID, true, opcode, authoritative, false, maxLength,
			false, recursionAvailable, authenticData, false, responseCode,
			questions, answers, authorityRecords, additionalRecords
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