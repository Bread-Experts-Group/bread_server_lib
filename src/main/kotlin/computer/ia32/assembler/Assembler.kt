package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.command_line.stringToLong
import org.bread_experts_group.computer.ia32.IA32Processor.Companion.dummyProcessor
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.scanDelimiter
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.util.ServiceLoader
import java.util.logging.Logger
import kotlin.collections.ArrayDeque

class Assembler(private val assemblyStream: StringReader) {
	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("ia_32_assembler")

	enum class BitMode(val bits: Int) {
		BITS_8(8),
		BITS_16(16),
		BITS_32(32),
		BITS_64(64);

		companion object {
			val mapping: Map<Int, BitMode> = entries.associateBy { it.bits }
		}
	}

	private var labels: MutableMap<String, ULong> = mutableMapOf()
	var position: ULong = 0u
	var mode: BitMode = BitMode.BITS_32

	fun readLabel(label: String): ULong {
		if (!label.startsWith('@')) throw IllegalArgumentException("label does not start with @")
		val actualLabel = label.substring(1)
		val position = this.labels[actualLabel]
		if (position == null) throw IllegalArgumentException("unknown label @\"$actualLabel\"")
		return position
	}

	fun assemble(): ByteArray {
		val assembled = ByteArrayOutputStream()
		val instructions = ServiceLoader.load(AssembledInstruction::class.java).groupBy {
			(it as Instruction).mnemonic
		} + ServiceLoader.load(InstructionCluster::class.java)
			.flatMap { it.getInstructions(dummyProcessor) }.mapNotNull { it as? AssembledInstruction }
			.groupBy { (it as Instruction).mnemonic }
		val stl = stringToLong()
		var line = 1
		fun assemblerError(error: Throwable) {
			logger.severe { "error:$line: ${error.localizedMessage}" }
			throw error
		}

		fun assemblerError(error: String) = assemblerError(Error(error))

		while (true) {
			val queue = ArrayDeque<String>()
			this.assemblyStream
				.scanDelimiter("\n")
				.split(' ', '\t', '\r', ',')
				.filter { it.isNotBlank() }
				.forEach { queue.add(it) }
			if (queue.isEmpty()) break
			when (val token = queue.removeFirst()) {
				"org" -> this.position = stl(queue.removeFirst()).toULong()
				"bits" -> this.mode = BitMode.mapping.getValue(stl(queue.removeFirst()).toInt())

				else -> {
					if (instructions.containsKey(token)) {
						try {
							val sized = instructions.getValue(token).filter { it.arguments == queue.size }
							if (sized.isEmpty()) assemblerError("\"$token\": too many arguments [${queue.size}]")
							val acceptable = sized.filter { it.acceptable(this, queue) }
							if (acceptable.isEmpty()) assemblerError("\"$token\": none acceptable for arguments")
							val before = assembled.size()
							acceptable.first().produce(this, assembled, queue)
							this.position += (assembled.size() - before).toULong()
						} catch (e: Throwable) {
							assemblerError(e)
						}
					} else if (token.endsWith(':')) this.labels[token.removeSuffix(":")] = this.position
					else assemblerError("token: \"$token\"")
				}
			}
			if (queue.isNotEmpty()) assemblerError("trailing: [${queue.toList()}]")
			line++
		}
		return assembled.toByteArray()
	}
}