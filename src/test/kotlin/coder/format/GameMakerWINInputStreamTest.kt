package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.gamemaker_win.GameMakerWINParser
import org.bread_experts_group.coder.format.gamemaker_win.bytecode.*
import org.bread_experts_group.coder.format.gamemaker_win.chunk.*
import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINBytecode
import org.bread_experts_group.formatTime
import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.*
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GameMakerWINInputStreamTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/gamemaker/data.win"
	)
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.gamemaker")

	@Test
	fun readParsed(): Unit = assertDoesNotThrow {
		val tempFile = Files.createTempFile("test", ".win")
		tempFile.writeBytes(testFile!!.readAllBytes())
		val fileStream = FileInputStream(tempFile.toFile())
		val testStream = GameMakerWINParser(fileStream)
		val dialA = System.currentTimeMillis()
		val read = testStream.readAllParsed()
		logger.info((System.currentTimeMillis() - dialA).toDuration(DurationUnit.MILLISECONDS).formatTime())
		logger.info(read.toString())
		logger.info("Attempting disassembly")
		val chunks = (read.first() as GameMakerWINContainerChunk).chunks
		val disassembly = mutableMapOf<Long, MutableList<GameMakerWINBytecode>>()
		chunks.firstNotNullOf { it as? GameMakerWINBytecodeChunk }.bytecode.forEach {
			val bytecodes = disassembly.getOrPut(it.address) { mutableListOf() }
			bytecodes.add(it)
		}
		val disassemblyOutput = PrintStream(FileOutputStream("./disa.txt"))
		val functions = chunks.firstNotNullOf { it as? GameMakerWINFunctionsChunk }.functions
		disassemblyOutput.println("FUNCTIONS (${functions.size}):")
		functions.forEach {
			disassemblyOutput.print(" - ${it.name} @ ${hex(it.offset.toUInt())} [# ${it.runCount}]")
			disassemblyOutput.println(", first at ${hex(it.firstOccurrence.toUInt())}")
		}
		disassemblyOutput.println()
		val variableNames = mutableSetOf<String>()
		val variables = chunks.firstNotNullOf { it as? GameMakerWINVariablesChunk }.variables
		disassemblyOutput.println("VARIABLES (${variables.size}):")
		variables.forEach {
			disassemblyOutput.print(" - ${it.name} @ ${hex(it.offset.toUInt())} [# ${it.runCount}]")
			disassemblyOutput.println(", first at ${it.firstOccurrence?.let { f -> hex(f.toUInt()) } ?: "nowhere"}")
			variableNames.add(it.name)
		}
		disassemblyOutput.println()
		val strings = chunks.firstNotNullOf { it as? GameMakerWINStringsChunk }.strings
		disassembly.forEach { (address, disassembly) ->
			disassemblyOutput.println("ADDRESS : ${hex(address.toUInt())}")
			disassemblyOutput.println("APPLICABLE FUNCTIONS (${disassembly.size})")
			var length = 0L
			disassembly.forEach {
				disassemblyOutput.print(" - ${it.name}")
				disassemblyOutput.println(" (${it.locals} local(s), ${it.arguments} argument(s), # ${it.length})")
				if (length == 0L) length = it.length
				else if (it.length != length) throw UnsupportedOperationException("mismatch detected; $it")
			}
			val variableMap =
				mutableMapOf<GameMakerWINEnvironment, MutableMap<GameMakerWINVariableType, MutableMap<Short, String>>>()
			var refCount = 0
			fun resolveReference(b2: Int, b3: Int) {
				val environment = GameMakerWINEnvironment.mapping.getValue(((b2 shl 8) or b3))
				val types = variableMap.getOrPut(environment) { mutableMapOf() }
				val identifier = fileStream.read16().le()
				val type = GameMakerWINVariableType.mapping.getValue(fileStream.read16ui())
				val variables = types.getOrPut(type) { mutableMapOf() }
				disassemblyOutput.print("$environment.$type:${hex(identifier.toUShort())}: ")
				disassemblyOutput.println(variables.getOrPut(identifier) {
					val foundName = strings.getOrNull(identifier.toInt())
					if (foundName != null && variableNames.contains(foundName)) foundName
					else "<anonymous ${refCount++}>"
				})
			}
			disassemblyOutput.println("-- DISASSEMBLY --")
			fileStream.channel.position(address)
			val edge = fileStream.channel.position() + length
			while (fileStream.channel.position() < edge) {
				disassemblyOutput.print("${hex(fileStream.channel.position().toUInt())} | ")
				val word = fileStream.read32()
				val b3 = word ushr 24
				val b2 = (word ushr 16) and 0xFF
				val b1 = (word ushr 8) and 0xFF
				val opcode = GameMakerWINOpcode.mapping[word and 0xFF]
				val padLength = GameMakerWINOpcode.entries.maxOf { it.name.length }
				disassemblyOutput.print("${hex(word.toUInt())} | ${(opcode?.name ?: "???").padEnd(padLength)} | ")
				when (opcode?.variant) {
					GameMakerWINOpcodeVariant.SINGLE_TYPE, GameMakerWINOpcodeVariant.DOUBLE_TYPE,
					GameMakerWINOpcodeVariant.COMPARE -> {
						val typeA = GameMakerWINDatatype.mapping[b1 and 0xF]
						val typeB = GameMakerWINDatatype.mapping[b1 shr 4]
						disassemblyOutput.println("$typeA, $typeB")
					}

					GameMakerWINOpcodeVariant.GOTO -> {
						var to = (((b2 shl 8) or (b3)).toShort()) * 4
						to -= 4
						disassemblyOutput.println("$to [${hex((fileStream.channel.position() + to).toUInt())}]")
					}

					GameMakerWINOpcodeVariant.POP -> {
						val typeA = GameMakerWINDatatype.mapping[b1 and 0xF]
						disassemblyOutput.print((typeA?.name ?: hex((b2 and 0xF).toUByte())) + ' ')
						resolveReference(b2, b3)
					}

					GameMakerWINOpcodeVariant.PUSH -> when (opcode) {
						GameMakerWINOpcode.PUSH_SHORT -> {
							disassemblyOutput.println((b2 shl 8) or b3)
						}

						GameMakerWINOpcode.PUSH, GameMakerWINOpcode.PUSH_LOCAL -> {
							val typeARaw = b1 and 0xF
							val typeA = GameMakerWINDatatype.mapping[typeARaw]
							disassemblyOutput.print((typeA?.name ?: hex(typeARaw.toUByte())) + ' ')
							when (typeA) {
								GameMakerWINDatatype.VARIABLE -> resolveReference(b2, b3)

								GameMakerWINDatatype.DOUBLE ->
									disassemblyOutput.println(Double.fromBits(fileStream.read64().le()))

								GameMakerWINDatatype.STRING ->
									disassemblyOutput.println("\"${strings[fileStream.read32().le()]}\"")

								GameMakerWINDatatype.INTEGER ->
									disassemblyOutput.println(hex(fileStream.read32().le().toUInt()))

								GameMakerWINDatatype.SHORT ->
									disassemblyOutput.println((b2 shl 8) or b3)

								else -> disassemblyOutput.println("??!")
							}
						}

						else -> disassemblyOutput.println("??")
					}

					GameMakerWINOpcodeVariant.CALL -> when (opcode) {
						GameMakerWINOpcode.CALL -> disassemblyOutput.println(strings[fileStream.read32().le()])
						GameMakerWINOpcode.CALL_CLOSURE -> disassemblyOutput.println("?")
						else -> disassemblyOutput.println("??")
					}

					GameMakerWINOpcodeVariant.BREAK -> {
						disassemblyOutput.println("todo break")
					}

					else -> disassemblyOutput.println()
				}
			}
			disassemblyOutput.println("--COMPUTED VARS--")
			variableMap.forEach { (environment, types) ->
				disassemblyOutput.println("$environment (${hex(environment.code.toUShort())}) (${types.size}):")
				types.forEach { (type, variables) ->
					disassemblyOutput.println(" - $type (${hex(type.code.toUShort())}) (${variables.size}):")
					variables.forEach { (code, name) ->
						disassemblyOutput.println(" - - ${hex(code.toUShort())} | $name")
					}
				}
			}
			disassemblyOutput.println("-- -- -- -- -- --")
		}
		disassemblyOutput.close()
	}
}