package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.MappedEnumeration

sealed class DNSMessage(
	val identifier: Short,
	val opcode: MappedEnumeration<UInt, DNSOpcode>,
	answer: Boolean,
	val authoritative: Boolean,
	val truncated: Boolean,
	val recursionDesired: Boolean,
	val recursionAvailable: Boolean,
	val responseCode: MappedEnumeration<UInt, DNSResponseCode>,
	val questions: MutableList<DNSQuestion> = mutableListOf(),
	val answerRecords: MutableList<DNSResourceRecord> = mutableListOf(),
	val authorityRecords: MutableList<DNSResourceRecord> = mutableListOf(),
	val additionalRecords: MutableList<DNSResourceRecord> = mutableListOf()
) {
	val flagBits: Short

	init {
		var flags = 0b0_0000_0_0_0_0_000_0000 or responseCode.raw.toInt() or (opcode.raw.toInt() shl 11)
		if (truncated) flags = flags or (1 shl 9)
		if (recursionAvailable) flags = flags or (1 shl 7)
		if (recursionDesired) flags = flags or (1 shl 8)
		if (authoritative) flags = flags or (1 shl 10)
		if (answer) flags = flags or (1 shl 15)
		flagBits = flags.toShort()
	}

	override fun toString(): String = "DNS ${if (this is Request) "Request" else "Response"} " +
			"[0x${identifier.toHexString()}]" +
			"\n$responseCode [$opcode]" +
			"\n[${
				run {
					val flags = mutableListOf<String>()
					if (authoritative) flags.add("AUTHORITATIVE")
					if (truncated) flags.add("TRUNCATED")
					if (recursionDesired) flags.add("RECURSION DESIRED")
					if (recursionAvailable) flags.add("RECURSION AVAILABLE")
					flags.joinToString(", ")
				}
			}]" +
			"\nQuestions [${questions.size}]: $questions" +
			"\nAnswers [${answerRecords.size}]: $answerRecords" +
			"\nAuthorities [${this.authorityRecords.size}]: ${this.authorityRecords}" +
			"\nAdditional [${this.additionalRecords.size}]: ${this.additionalRecords}"

	class Request(
		identifier: Short,
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		truncated: Boolean,
		recursionDesired: Boolean,
		questions: MutableList<DNSQuestion> = mutableListOf(),
		answerRecords: MutableList<DNSResourceRecord> = mutableListOf(),
		authorityRecords: MutableList<DNSResourceRecord> = mutableListOf(),
		additionalRecords: MutableList<DNSResourceRecord> = mutableListOf()
	) : DNSMessage(
		identifier,
		opcode,
		false,
		false,
		truncated,
		recursionDesired,
		false,
		MappedEnumeration(DNSResponseCode.NoError),
		questions,
		answerRecords,
		authorityRecords,
		additionalRecords
	)

	class Response(
		identifier: Short,
		opcode: MappedEnumeration<UInt, DNSOpcode>,
		authoritative: Boolean,
		truncated: Boolean,
		recursionDesired: Boolean,
		recursionAvailable: Boolean,
		responseCode: MappedEnumeration<UInt, DNSResponseCode>,
		questions: MutableList<DNSQuestion> = mutableListOf(),
		answerRecords: MutableList<DNSResourceRecord> = mutableListOf(),
		authorityRecords: MutableList<DNSResourceRecord> = mutableListOf(),
		additionalRecords: MutableList<DNSResourceRecord> = mutableListOf()
	) : DNSMessage(
		identifier,
		opcode,
		true,
		authoritative,
		truncated,
		recursionDesired,
		recursionAvailable,
		responseCode,
		questions, answerRecords, authorityRecords, additionalRecords
	) {
		constructor(
			toRequest: Request,
			authoritative: Boolean,
			truncated: Boolean,
			recursionAvailable: Boolean,
			responseCode: MappedEnumeration<UInt, DNSResponseCode>,
			answerRecords: MutableList<DNSResourceRecord> = mutableListOf(),
			authorityRecords: MutableList<DNSResourceRecord> = mutableListOf(),
			additionalRecords: MutableList<DNSResourceRecord> = mutableListOf()
		) : this(
			toRequest.identifier,
			toRequest.opcode,
			authoritative,
			truncated,
			toRequest.recursionDesired,
			recursionAvailable,
			responseCode,
			toRequest.questions,
			answerRecords,
			authorityRecords,
			additionalRecords,
		)
	}
}