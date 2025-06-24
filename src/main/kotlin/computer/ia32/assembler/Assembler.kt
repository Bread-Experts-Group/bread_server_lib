package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.command_line.stringToIntOrNull
import org.bread_experts_group.command_line.stringToLongOrNull
import org.bread_experts_group.command_line.stringToULongOrNull
import org.bread_experts_group.computer.ia32.IA32Processor.Companion.dummyProcessor
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.Reader
import java.util.ServiceLoader
import java.util.logging.Logger
import kotlin.collections.ArrayDeque

class Assembler(private val assemblyStream: Reader) {
	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("ia_32_assembler")

	private var labels: MutableMap<String, ULong> = mutableMapOf()
	var position: ULong = 0u
	var mode: BitMode = BitMode.BITS_32

	val unconsolidated = mutableListOf<ByteArrayOutputStream>()
	val labelRemap = mutableMapOf<String, Pair<Int, BitMode>>()
	fun readLabel(label: String): Pair<ULong, Boolean>? {
		if (!label.startsWith('@')) return null
		val actualLabel = label.substring(1)
		val position = this.labels[actualLabel]
		if (position == null) {
			labelRemap[label.substring(1)] = unconsolidated.lastIndex to mode
			return ULong.MAX_VALUE to false
		}
		return position to true
	}

	fun readImmediate(token: String, range: LongRange): Pair<ULong, Boolean>? {
		val label = readLabel(token)
		if (label != null) return label
		return stringToLongOrNull(range)(token)?.let { it.toULong() to true }
	}

	fun writeForMode(into: OutputStream, n: ULong, mode: BitMode = this.mode): Unit = when (mode) {
		BitMode.BITS_32 -> into.write32(n.toInt().le())
		else -> TODO(mode.name)
	}

	fun assemble(): ByteArray {
		val instructions = ServiceLoader.load(AssembledInstruction::class.java).groupBy {
			(it as Instruction).mnemonic
		} + ServiceLoader.load(InstructionCluster::class.java)
			.flatMap { it.getInstructions(dummyProcessor) }.mapNotNull { it as? AssembledInstruction }
			.groupBy { (it as Instruction).mnemonic }
		var line = 1
		fun assemblerError(error: Throwable): Nothing {
			logger.severe { "error:$line: ${error.localizedMessage}" }
			throw error
		}

		fun assemblerError(error: String): Nothing = assemblerError(Error(error))

		while (true) {
			val queue = ArrayDeque<String>()
			val buffer = StringBuilder()
			var inString = false
			while (true) {
				val code = this.assemblyStream.read()
				if (code == -1) break
				val char = Char(code)
				when {
					inString && char == '"' -> {
						queue.add(buffer.toString())
						buffer.clear()
						inString = false
					}

					!inString && (char.isWhitespace() || char == ',') -> {
						if (buffer.isNotBlank()) queue.add(buffer.toString())
						buffer.clear()
						if (char == '\n') break
					}

					!inString && char == '"' -> inString = true
					else -> buffer.append(char)
				}
			}
			if (queue.isEmpty()) break
			val toConsolidate = ByteArrayOutputStream()
			unconsolidated.add(toConsolidate)
			when (val token = queue.removeFirst()) {
				"org" -> {
					val range = when (mode) {
						BitMode.BITS_8 -> assemblerError("8-bit mode is not specifiable")
						BitMode.BITS_16 -> UShort.MIN_VALUE.toULong()..UShort.MAX_VALUE.toULong()
						BitMode.BITS_32 -> UInt.MIN_VALUE.toULong()..UInt.MAX_VALUE.toULong()
						BitMode.BITS_64 -> ULong.MIN_VALUE..ULong.MAX_VALUE
					}
					val token = queue.removeFirst()
					val org = stringToULongOrNull(range)(token)
					if (org == null) assemblerError("code position [$token] out of range [$range]")
					this.position = org
				}

				"bits" -> {
					val range = BitMode.entries.minOf { it.bits }..BitMode.entries.maxOf { it.bits }
					val token = queue.removeFirst()
					val modeBits = stringToIntOrNull(range)(token)
					if (modeBits == null) assemblerError("$token-bit out of range for supported modes [$range]")
					val mode = BitMode.mapping[modeBits]
					if (mode == null || mode == BitMode.BITS_8) assemblerError("$token-bit mode unsupported")
					this.mode = mode
				}

				"defutf" -> toConsolidate.writeString(queue.removeFirst())

				else -> {
					if (instructions.containsKey(token)) {
						try {
							val sized = instructions.getValue(token).filter { it.arguments == queue.size }
							if (sized.isEmpty()) assemblerError("\"$token\": too many arguments [${queue.size}]")
							val acceptable = sized.filter { it.acceptable(this, queue) }
							if (acceptable.isEmpty()) assemblerError("\"$token\": none acceptable for arguments")
							acceptable.first().produce(this, toConsolidate, queue)
						} catch (e: Throwable) {
							assemblerError(e)
						}
					} else if (token.endsWith(':')) {
						val label = token.removeSuffix(":")
						this.labels[label] = this.position
						labelRemap[label]?.let { (streamIndex, streamMode) ->
							this.writeForMode(unconsolidated[streamIndex], this.position + 4u, streamMode)
							labelRemap.remove(label)
						}
					} else assemblerError("token: \"$token\"")
				}
			}
			this.position += toConsolidate.size().toULong()
			if (queue.isNotEmpty()) assemblerError("trailing: [${queue.toList()}]")
			line++
		}
		if (labelRemap.isNotEmpty()) assemblerError("unknown label @\"${labelRemap.keys.first()}\"")
		return unconsolidated.fold(ByteArrayOutputStream()) { acc, array ->
			acc.write(array.toByteArray()); acc
		}.toByteArray()
	}
}