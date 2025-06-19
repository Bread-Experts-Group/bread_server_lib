package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.command_line.stringToLong
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.scanDelimiter
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.util.ServiceLoader
import java.util.logging.Logger
import kotlin.collections.ArrayDeque
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.associateBy
import kotlin.collections.filter
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.getValue
import kotlin.collections.groupBy
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toList

class Assembler(private val assemblyStream: StringReader) {
	private val logger: Logger = ColoredHandler.Companion.newLoggerResourced("ia_32_assembler")

	enum class BitMode(val bits: Int) {
		BITS_16(16),
		BITS_32(32),
		BITS_64(64);

		companion object {
			val mapping: Map<Int, BitMode> = entries.associateBy { it.bits }
		}
	}

	private var labels: MutableMap<String, ULong> = mutableMapOf()
	private var position: ULong = 0u
	private var mode: BitMode = BitMode.BITS_32

	fun assemble(): ByteArray {
		val assembled = ByteArrayOutputStream()
		val instructions = ServiceLoader.load(AssembledInstruction::class.java).groupBy {
			(it as Instruction).mnemonic
		}
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
							val acceptable = sized.filter { it.acceptable(logger, queue) }
							if (acceptable.isEmpty()) assemblerError("\"$token\": none acceptable for arguments")
							acceptable.first().produce(logger, mode, assembled, queue)
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