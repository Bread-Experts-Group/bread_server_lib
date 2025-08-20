package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.command_line.stringToIntOrNull
import org.bread_experts_group.command_line.stringToLongOrNull
import org.bread_experts_group.command_line.stringToULongOrNull
import org.bread_experts_group.computer.ia32.IA32Processor.Companion.dummyProcessor
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.write16
import org.bread_experts_group.stream.write32
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.Reader
import java.nio.channels.Channels
import java.nio.charset.Charset
import java.util.ServiceLoader
import java.util.logging.Logger
import kotlin.collections.ArrayDeque

class Assembler(private val assemblyStream: Reader) {
	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("ia_32_assembler")
	var mode: BitMode = BitMode.BITS_32
	var position: ULong = 0u
	var line: Int = 1
	val labels: MutableMap<String, ULong> = mutableMapOf()

	fun assemblerError(error: Throwable): Nothing = throw error
	fun assemblerError(error: String): Nothing = assemblerError(Error(error))

	fun readImmediate(token: String, range: LongRange = this.mode.range(token)): ULong? = when {
		token.startsWith(':') -> this.labels.getValue(token.substring(1))
		token.startsWith('$') -> stringToLongOrNull(range)(token.substring(1))?.toULong()
		else -> assemblerError("unrecognized immediate token \"$token\"")
	}

	fun writeForMode(into: OutputStream, n: ULong, mode: BitMode = this.mode): Unit = when (mode) {
		BitMode.BITS_16 -> into.write16(n.toShort().le())
		BitMode.BITS_32 -> into.write32(n.toInt().le())
		else -> TODO(mode.name)
	}

	fun assemble(): ByteArray {
		val instructions = ServiceLoader.load(AssembledInstruction::class.java).groupBy {
			(it as Instruction).mnemonic
		} + ServiceLoader.load(InstructionCluster::class.java)
			.flatMap { it.getInstructions(dummyProcessor) }.mapNotNull { it as? AssembledInstruction }
			.groupBy { (it as Instruction).mnemonic }
		println(instructions)
		val main = ByteArrayOutputStream()
		var moreFile = true
		while (moreFile) {
			val queue = ArrayDeque<String>()
			val buffer = StringBuilder()
			var inString = false
			while (true) {
				val code = this.assemblyStream.read()
				if (code == -1) {
					moreFile = false
					if (buffer.isNotBlank()) {
						queue.add(buffer.toString())
						buffer.clear()
					}
					break
				}
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
			if (queue.isEmpty()) continue
			fun processGeneral(token: String) {
				when {
					token == "org" -> {
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

					token == "bits" -> {
						val range = BitMode.entries.minOf { it.id }..BitMode.entries.maxOf { it.id }
						val token = queue.removeFirst()
						val modeBits = stringToIntOrNull(range)(token)
						if (modeBits == null) assemblerError("$token-bit out of range for supported modes [$range]")
						val mode = BitMode.entries.id(modeBits).enum!!
						if (mode == BitMode.BITS_8) assemblerError("$token-bit mode unsupported")
						this.mode = mode
					}

					token == "def" -> {
						val charset = Charset.forName(queue.removeFirst())
						val encoded = charset.encode(queue.removeFirst())
						this.position += encoded.remaining().toULong()
						Channels.newInputStream(ByteBufferChannel(encoded)).transferTo(main)
					}

					token.endsWith(':') -> {
						labels[token.take(token.length - 1)] = this.position
					}

					instructions.containsKey(token) -> {
						try {
							val sized = instructions.getValue(token).filter { it.arguments == queue.size }
							if (sized.isEmpty()) assemblerError("\"$token\": too many arguments [${queue.size}]")
							val acceptable = sized.filter { it.acceptable(this, queue) }
							if (acceptable.isEmpty()) assemblerError("\"$token\": none acceptable for arguments")
							val line = ByteArrayOutputStream()
							acceptable.first().produce(this, line, queue)
							this.position += line.size().toULong()
							main.write(line.toByteArray())
						} catch (e: Throwable) {
							assemblerError(e)
						}
					}

					else -> assemblerError("token: \"$token\"")
				}
			}

			val token = queue.removeFirst()
			when {
				token == "+" -> {
					this.mode = BitMode.BITS_32
					processGeneral(queue.removeFirst())
					this.mode = BitMode.BITS_16
				}

				else -> processGeneral(token)
			}
			if (queue.isNotEmpty()) assemblerError("trailing: [${queue.toList()}]")
			line++
		}
		return main.toByteArray()
	}
}