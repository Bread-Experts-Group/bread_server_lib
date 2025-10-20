package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.CompilerStackException
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.efi.EFIMemoryType
import org.bread_experts_group.api.compile.ebc.efi.EFISystemTable
import org.bread_experts_group.logging.ColoredHandler
import java.lang.classfile.*
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.ClassFile.ACC_STATIC
import java.lang.classfile.constantpool.LongEntry
import java.lang.classfile.constantpool.StringEntry
import java.lang.classfile.instruction.*
import java.lang.constant.ConstantDescs
import java.lang.foreign.MemorySegment
import java.nio.file.Path
import java.util.logging.Level
import kotlin.reflect.KClass

object EBCJVMCompilation {
	private val cf = ClassFile.of()
	private val logger = ColoredHandler.newLogger("TMP logger")

	fun compileClass(
		clazz: KClass<*>, codeSource: Path,
		codeBase: ULong, initBase: ULong, unInitBase: ULong
	): EBCCompilationOutput {
		val efiClass = cf.parse(
			codeSource.resolve(clazz.java.name.replace('.', '/') + ".class")
		)
		val efiMethods = efiClass.methods()
		val efiMethod = efiMethods.firstOrNull {
			(it.flags().flagsMask() and (ACC_PUBLIC or ACC_STATIC)) == (ACC_PUBLIC or ACC_STATIC) &&
					it.methodType().equalsString(
						"(L${MemorySegment::class.java.name.replace('.', '/')};" +
								"L${EFISystemTable::class.java.name.replace('.', '/')};)J"
					) && it.methodName().equalsString("efiMain")
		}
		if (efiMethod == null) throw IllegalArgumentException(
			"No public static efiMain(${MemorySegment::class}, ${EFISystemTable::class}): Long function " +
					"was found within [$clazz]."
		)
		val code = efiMethod.code().orElseThrow { IllegalArgumentException("$efiMethod must contain code.") }
		return compileMethod(code, codeBase, initBase, unInitBase)
	}

	fun compileMethod(
		code: CodeModel,
		codeBase: ULong, initBase: ULong, unInitBase: ULong,
		stringTable: MutableMap<String, ULong> = mutableMapOf(),
		variableAllocator: EBCVariableAllocator = EBCVariableAllocator()
	): EBCCompilationOutput {
		val methodParent = code.parent().get()
		val classParent = methodParent.parent().get()
		val procedure = EBCProcedure()
		var data = byteArrayOf()
		val labelTargets = mutableMapOf<Label, ULong>()
		val branchLocations = mutableMapOf<ULong, Label>()
		val callTargets = mutableMapOf<String, MethodModel>()
		val callLocations = mutableMapOf<ULong, MethodModel>()
		procedure.MOVIqq(
			EBCRegisters.R6, false, null,
			unInitBase
		)
		var stackNatural = 0u
		var stackConstant = 16u
		methodParent.methodTypeSymbol().parameterList().forEachIndexed { i, p ->
			when (p) {
				ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
					val (natural, constant) = variableAllocator.getOrAllocate32(i)
					procedure.MOVdw(
						EBCRegisters.R6, true,
						EBCRegisters.R0, true,
						naturalIndex16(
							false,
							natural, constant
						), naturalIndex16(
							false,
							stackNatural, stackConstant
						)
					)
					stackNatural += 1u
				}

				ConstantDescs.CD_long -> {
					val (natural, constant) = variableAllocator.getOrAllocate64(i)
					procedure.MOVqw(
						EBCRegisters.R6, true,
						EBCRegisters.R0, true,
						naturalIndex16(
							false,
							natural, constant
						), naturalIndex16(
							false,
							stackNatural, stackConstant
						)
					)
					stackConstant += 8u
				}

				else -> {
					val (natural, constant) = variableAllocator.getOrAllocateNatural(i)
					procedure.MOVnw(
						EBCRegisters.R6, true,
						EBCRegisters.R0, true,
						naturalIndex16(
							false,
							natural, constant
						), naturalIndex16(
							false,
							stackNatural, stackConstant
						)
					)
					stackNatural += 1u
				}
			}
		}
		val (allocatorNatural, allocatorConstant) = variableAllocator.bumpNatural()
		val stack = ArrayDeque<EBCCompilerStackType>()
		fun expectElement(type: EBCCompilerStackType) {
			if (stack.last() != type) logger.log(
				Level.SEVERE, CompilerStackException(stack, "expected $type")
			) { "Stack consistency problem" }
			stack.removeLast()
		}
		for (element in code) when (element) {
			is LoadInstruction -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					unInitBase
				)
				val (natural, constant) = variableAllocator[element.slot()]
				when (element.typeKind()) {
					TypeKind.INT -> {
						procedure.PUSH32(
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)
						stack.addLast(EBCCompilerStackType.BIT_32)
					}

					TypeKind.LONG -> {
						procedure.PUSH64(
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)
						stack.addLast(EBCCompilerStackType.BIT_64)
					}

					TypeKind.REFERENCE -> {
						procedure.PUSHn(
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)
						stack.addLast(EBCCompilerStackType.NATURAL)
					}

					else -> throw IllegalArgumentException("Unsupported type: ${element.typeKind()}")
				}
			}

			is StoreInstruction -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					unInitBase
				)
				when (element.typeKind()) {
					TypeKind.INT -> {
						expectElement(EBCCompilerStackType.BIT_32)
						val (natural, constant) = variableAllocator.getOrAllocate32(element.slot())
						procedure.POP32(EBCRegisters.R5, false, null)
						procedure.MOVdw(
							EBCRegisters.R6, true,
							EBCRegisters.R5, false,
							naturalIndex16(false, natural, constant),
							null
						)
					}

					TypeKind.LONG -> {
						expectElement(EBCCompilerStackType.BIT_64)
						val (natural, constant) = variableAllocator.getOrAllocate64(element.slot())
						procedure.POP64(EBCRegisters.R5, false, operand1Index = null)
						procedure.MOVqw(
							EBCRegisters.R6, true,
							EBCRegisters.R5, false,
							naturalIndex16(false, natural, constant),
							null
						)
					}

					TypeKind.REFERENCE -> {
						expectElement(EBCCompilerStackType.NATURAL)
						val (natural, constant) = variableAllocator.getOrAllocateNatural(element.slot())
						procedure.POPn(EBCRegisters.R5, false, null)
						procedure.MOVnw(
							EBCRegisters.R6, true,
							EBCRegisters.R5, false,
							naturalIndex16(false, natural, constant),
							null
						)
					}

					else -> throw IllegalArgumentException("Unsupported type: ${element.typeKind()}")
				}
			}

			is IncrementInstruction -> {
				val (natural, constant) = variableAllocator[element.slot()]
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					element.constant().toUInt()
				)
				procedure.MOVIqq(
					EBCRegisters.R4, false, null,
					unInitBase
				)
				procedure.MOVdw(
					EBCRegisters.R5, false,
					EBCRegisters.R4, true,
					null, naturalIndex16(
						false,
						natural, constant
					)
				)
				procedure.ADD32(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				procedure.MOVdw(
					EBCRegisters.R4, true,
					EBCRegisters.R5, false,
					naturalIndex16(
						false,
						natural, constant
					), null
				)
			}

			is OperatorInstruction -> when (element.opcode()) {
				Opcode.IADD -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.ADD32(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				Opcode.LADD -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.ADD64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LSUB -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.SUB64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LMUL -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.MUL64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LDIV -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.DIV64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LREM -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.MOD64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LAND -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.AND64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false,
						null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LUSHR -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R5, false,
						EBCRegisters.R5, false, null
					)
					procedure.SHR64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false,
						null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.LCMP -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.CMP64gte(
						EBCRegisters.R6,
						EBCRegisters.R5, false, null
					)
					// MOVIdd = 6 bytes
					// JMP32 = 6 bytes
					// CMP64 = 2 bytes
					procedure.JMP32( // >= ?
						true,
						conditionSet = true,
						relative = true,
						EBCRegisters.R0, false,
						naturalIndex32(false, 0u, 12u) // CMP64eq
					)
					procedure.MOVIdd( // <
						EBCRegisters.R6, false, null,
						(-1).toUInt()
					)
					procedure.JMP32(
						false,
						conditionSet = false,
						relative = true,
						EBCRegisters.R0, false,
						naturalIndex32(false, 0u, 26u) // Push
					)
					procedure.CMP64eq(
						EBCRegisters.R6,
						EBCRegisters.R5, false, null
					)
					procedure.JMP32( // == ?
						true,
						conditionSet = true,
						relative = true,
						EBCRegisters.R0, false,
						naturalIndex32(false, 0u, 12u) // MOVIdd 0
					)
					procedure.MOVIdd( // >
						EBCRegisters.R6, false, null,
						1u
					)
					procedure.JMP32(
						false,
						conditionSet = false,
						relative = true,
						EBCRegisters.R0, false,
						naturalIndex32(false, 0u, 6u) // Push
					)
					procedure.MOVIdd( // ==
						EBCRegisters.R6, false, null,
						0u
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				else -> throw IllegalArgumentException("Unknown operator opcode: ${element.opcode()}")
			}

			is BranchInstruction -> when (element.opcode()) {
				Opcode.IF_ICMPGE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null)
					procedure.CMP32gte(
						EBCRegisters.R5,
						EBCRegisters.R6, false, null
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IF_ICMPLE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null)
					procedure.CMP32lte(
						EBCRegisters.R5,
						EBCRegisters.R6, false, null
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IF_ICMPNE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null)
					procedure.CMP32eq(
						EBCRegisters.R5,
						EBCRegisters.R6, false, null
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = false,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IFGT -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					// TODO : WARNING! THIS DOES X >= 0, NOT X > 0
					procedure.CMPI32wgte(
						EBCRegisters.R6, false, null,
						0u
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IFGE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.CMPI32wgte(
						EBCRegisters.R6, false, null,
						0u
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IFLE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.CMPI32wlte(
						EBCRegisters.R6, false, null,
						0u
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IFEQ -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.CMPI32weq(
						EBCRegisters.R6, false, null,
						0u
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.IFNE -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.CMPI32weq(
						EBCRegisters.R6, false, null,
						0u
					)
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = true,
						conditionSet = false,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				Opcode.GOTO -> {
					branchLocations[procedure.output.size.toULong()] = element.target()
					procedure.output += ByteArray(10)
					procedure.JMP32(
						conditional = false,
						conditionSet = false,
						relative = false,
						operand1 = EBCRegisters.R6,
						operand1Indirect = false,
						operand1Index = null
					)
				}

				else -> throw IllegalArgumentException("Unknown branch opcode: ${element.opcode()}")
			}

			is ConstantInstruction.ArgumentConstantInstruction -> {
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					element.constantValue().toInt().toUInt()
				)
				procedure.PUSH32(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.BIT_32)
			}

			is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.INT -> {
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					(element.constantValue() as Integer).toInt().toUInt()
				)
				procedure.PUSH32(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.BIT_32)
			}

			is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.INT -> {
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					(element.constantValue() as Integer).toInt().toUInt()
				)
				procedure.PUSH32(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.BIT_32)
			}

			is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.LONG -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					(element.constantValue() as java.lang.Long).toLong().toULong()
				)
				procedure.PUSH64(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.BIT_64)
			}

			is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.LONG -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					(element.constantEntry() as LongEntry).longValue().toULong()
				)
				procedure.PUSH64(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.BIT_64)
			}

			is ConstantInstruction.LoadConstantInstruction if element.constantEntry() is StringEntry -> {
				val dataPosition = stringTable.getOrPut((element.constantEntry() as StringEntry).stringValue()) {
					val saved = initBase + data.size.toULong()
					data += ((element.constantEntry() as StringEntry).stringValue() + "\u0000")
						.toByteArray(Charsets.UTF_16LE)
					saved
				}
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					dataPosition
				)
				procedure.PUSHn(EBCRegisters.R6, false, null)
				stack.addLast(EBCCompilerStackType.NATURAL)
			}

			is FieldInstruction -> when (
				val desc = element.owner().name().stringValue() + '.' + element.name()
			) {
				"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiLoaderData" -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						EFIMemoryType.EfiLoaderData.id
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiBootServicesData" -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						EFIMemoryType.EfiBootServicesData.id
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				"java/lang/foreign/ValueLayout.JAVA_LONG" -> {
				}

				"java/lang/foreign/ValueLayout.JAVA_INT" -> {
				}

				"java/lang/foreign/ValueLayout.JAVA_SHORT" -> {
				}

				"java/lang/foreign/ValueLayout.JAVA_BYTE" -> {
				}

				"java/lang/foreign/ValueLayout.ADDRESS" -> {
				}

				"java/lang/foreign/MemorySegment.NULL" -> {
					procedure.MOVIqw(EBCRegisters.R6, false, null, 0u)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIExample.INSTANCE" -> {
					procedure.PUSHn(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
					logger.severe("!!!!!! WARNING `THIS` DOES NOT EXIST AT RUNTIME !!!!!!")
				}

				else -> throw IllegalArgumentException("No translation for [$desc]")
			}

			is InvokeInstruction -> @Suppress("LongLine") when (
				val desc = element.owner().name().stringValue() + '.' + element.name() + element.type()
			) {
				"kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
				}

				"kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue(Ljava/lang/Object;Ljava/lang/String;)V" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
				}

				"kotlin/jvm/internal/Intrinsics.checkNotNull(Ljava/lang/Object;Ljava/lang/String;)V" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
				}

				"java/lang/foreign/MemorySegment.address()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.MOVIqw(EBCRegisters.R6, false, null, 0u)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/AddressLayout;J)Ljava/lang/foreign/MemorySegment;" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.ADD64(
						EBCRegisters.R5, false,
						EBCRegisters.R6, false, null
					)
					procedure.PUSHn(EBCRegisters.R5, true, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/ValueLayout$OfLong;J)J" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.ADD64(
						EBCRegisters.R5, false,
						EBCRegisters.R6, false, null
					)
					procedure.PUSH64(EBCRegisters.R5, true, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/ValueLayout$OfInt;J)I" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.ADD64(
						EBCRegisters.R5, false,
						EBCRegisters.R6, false, null
					)
					procedure.PUSH32(EBCRegisters.R5, true, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/ValueLayout$OfShort;J)S" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.ADD64(
						EBCRegisters.R5, false,
						EBCRegisters.R6, false, null
					)
					procedure.EXTNDW32(
						EBCRegisters.R5, false,
						EBCRegisters.R5, true,
						null
					)
					procedure.PUSH32(EBCRegisters.R5, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/AddressLayout;JLjava/lang/foreign/MemorySegment;)V" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R5, false, null
					)
					procedure.MOVnw(
						EBCRegisters.R4, true,
						EBCRegisters.R6, false,
						null, null
					)
				}

				$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfLong;JJ)V" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R5, false, null
					)
					procedure.MOVqw(
						EBCRegisters.R4, true,
						EBCRegisters.R6, false,
						null, null
					)
				}

				$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfInt;JI)V" -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R5, false, null
					)
					procedure.MOVdw(
						EBCRegisters.R4, true,
						EBCRegisters.R6, false,
						null, null
					)
				}

				$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfShort;JS)V" -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R5, false, null
					)
					procedure.MOVww(
						EBCRegisters.R4, true,
						EBCRegisters.R6, false,
						null, null
					)
				}

				$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfByte;JB)V" -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R5, false, null
					)
					procedure.MOVbw(
						EBCRegisters.R4, true,
						EBCRegisters.R6, false,
						null, null
					)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getFirmwareVendor()Ljava/lang/foreign/MemorySegment;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 24u
						)
					)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getFirmwareRevision()I" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							1u, 24u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getHeader()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFITableHeader.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getConOut()Lorg/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							4u, 32u
						)
					)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.reset(Z)J" -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R5, false, null)
					procedure.CALL32(
						EBCRegisters.R5,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = null
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							2u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputString(Ljava/lang/String;)J",
				"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputStringAt(Ljava/lang/foreign/MemorySegment;)J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R5, false, null)
					procedure.CALL32(
						EBCRegisters.R5,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							1u, 0u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							2u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getBootServices()Lorg/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							8u, 32u
						)
					)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.allocatePool(Lorg/bread_experts_group/api/compile/ebc/efi/EFIMemoryType;J)Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;" -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null) // Size
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R5, false, null) // PoolType
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null) // BootServices
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						unInitBase
					)
					procedure.MOVqw(
						EBCRegisters.R3, false,
						EBCRegisters.R3, false,
						null, naturalIndex16(
							false,
							allocatorNatural, allocatorConstant
						)
					)
					procedure.PUSHn(EBCRegisters.R3, false, null) // **Buffer
					procedure.PUSHn(EBCRegisters.R6, false, null) // Size
					procedure.PUSHn(EBCRegisters.R5, false, null) // PoolType
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							5u, 24u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							3u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
					procedure.PUSHn(EBCRegisters.R3, true, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.locateProtocol(Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment;)Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null) // Registration
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null) // Protocol
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null) // BootServices
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						unInitBase
					)
					procedure.MOVqw(
						EBCRegisters.R3, false,
						EBCRegisters.R3, false,
						null, naturalIndex16(
							false,
							allocatorNatural, allocatorConstant
						)
					)
					procedure.PUSHn(EBCRegisters.R3, false, null) // **Interface
					procedure.PUSHn(EBCRegisters.R5, false, null) // Registration
					procedure.PUSHn(EBCRegisters.R6, false, null) // Protocol
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							37u, 24u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							3u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
					procedure.PUSHn(EBCRegisters.R3, true, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getData()Ljava/lang/foreign/MemorySegment;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getStatus()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.getHeader()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;" -> {}
				"org/bread_experts_group/api/compile/ebc/efi/EFITableHeader.getSignature()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleFileSystemProtocol.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleFileSystemProtocol.getRevision()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleFileSystemProtocol.openVolume()Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null) // EFISimpleFileSystemProtocol
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						unInitBase
					)
					procedure.MOVqw(
						EBCRegisters.R3, false,
						EBCRegisters.R3, false,
						null, naturalIndex16(
							false,
							allocatorNatural, allocatorConstant
						)
					)
					procedure.PUSHn(EBCRegisters.R3, false, null) // **Root
					procedure.PUSHn(EBCRegisters.R4, false, null) // *This
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							0u, 8u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							2u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
					procedure.PUSHn(EBCRegisters.R3, true, null)
					stack.addLast(EBCCompilerStackType.NATURAL)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileProtocol.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileProtocol.getRevision()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileProtocol.getInfo(Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment;)J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R7, false, null) // *Buffer
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null) // *BufferSize
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R5, false, null) // *InformationType
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R4, false, null) // *This
					procedure.PUSHn(EBCRegisters.R7, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R4, false, null)
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							7u, 8u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							4u, 0u
						)
					)
					procedure.PUSH64(EBCRegisters.R7, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getStructureSize()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getReadOnly()Z" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 8u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getVolumeSize()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 16u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getFreeSpace()J" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 24u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo.getBlockSize()I" -> {
					expectElement(EBCCompilerStackType.NATURAL)
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 32u
						)
					)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				else -> {
					val localMethod = classParent.methods().firstOrNull {
						element.owner().name().contentEquals(classParent.thisClass().name()) &&
								element.name().contentEquals(it.methodName()) &&
								element.type().contentEquals(it.methodType())
					}
					if (localMethod != null) {
						callLocations[procedure.output.size.toULong()] = localMethod
						procedure.output += ByteArray(10)
						procedure.MOVqw(
							EBCRegisters.R5, false,
							EBCRegisters.R0, false,
							null, null
						)
						val parameters = localMethod.methodTypeSymbol().parameterList().asReversed()
						var offsetNatural = 0u
						var offsetConstant = 0u
						parameters.forEach { c ->
							when (c) {
								ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
									procedure.MOVIqw(EBCRegisters.R4, false, null, 0u)
									procedure.MOVdw(
										EBCRegisters.R4, false,
										EBCRegisters.R5, true,
										null, naturalIndex16(
											false,
											offsetNatural, offsetConstant
										)
									)
									offsetConstant += 4u
									procedure.PUSHn(EBCRegisters.R4, false, null)
									stack.add(EBCCompilerStackType.NATURAL)
								}

								ConstantDescs.CD_long -> {
									procedure.MOVIqw(EBCRegisters.R4, false, null, 0u)
									procedure.MOVqw(
										EBCRegisters.R4, false,
										EBCRegisters.R5, true,
										null, naturalIndex16(
											false,
											offsetNatural, offsetConstant
										)
									)
									offsetConstant += 8u
									procedure.PUSH64(EBCRegisters.R4, false, null)
									stack.add(EBCCompilerStackType.BIT_64)
								}

								else -> {
									procedure.MOVIqw(EBCRegisters.R4, false, null, 0u)
									procedure.MOVnw(
										EBCRegisters.R4, false,
										EBCRegisters.R5, true,
										null, naturalIndex16(
											false,
											offsetNatural, offsetConstant
										)
									)
									offsetNatural += 1u
									procedure.PUSHn(EBCRegisters.R4, false, null)
									stack.add(EBCCompilerStackType.NATURAL)
								}
							}
						}
						procedure.CALL32(
							EBCRegisters.R6,
							operand1Indirect = false,
							relative = false,
							native = false,
							immediate = null
						)
						val desc = localMethod.parent().get().thisClass().name().stringValue() + '.' +
								localMethod.methodName().stringValue() + localMethod.methodType().stringValue()
						if (!callTargets.contains(desc)) callTargets[desc] = localMethod
						val (naturalR, constantR) = parameters.asReversed().fold(0u to 0u) { a, c ->
							when (c) {
								ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
									expectElement(EBCCompilerStackType.NATURAL)
									(a.first + 1u) to a.second
								}

								ConstantDescs.CD_long -> {
									expectElement(EBCCompilerStackType.BIT_64)
									a.first to (a.second + 8u)
								}

								else -> {
									expectElement(EBCCompilerStackType.NATURAL)
									(a.first + 1u) to a.second
								}
							}
						}
						val (natural, constant) = parameters.fold(0u to 0u) { a, c ->
							when (c) {
								ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
									expectElement(EBCCompilerStackType.BIT_32)
									a.first to (a.second + 4u)
								}

								ConstantDescs.CD_long -> {
									expectElement(EBCCompilerStackType.BIT_64)
									a.first to (a.second + 8u)
								}

								else -> {
									expectElement(EBCCompilerStackType.NATURAL)
									(a.first + 1u) to a.second
								}
							}
						}
						procedure.MOVnw(
							EBCRegisters.R0, false,
							EBCRegisters.R0, false,
							null, naturalIndex16(
								false,
								natural + naturalR, constant + constantR
							)
						)
						procedure.PUSH64(EBCRegisters.R7, false, null)
						stack.addLast(EBCCompilerStackType.BIT_64)
						continue
					}
					throw IllegalArgumentException("No translation for [$desc]")
				}
			}

			is ConvertInstruction -> when (element.opcode()) {
				Opcode.I2L -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_64)
				}

				Opcode.L2I -> {
					expectElement(EBCCompilerStackType.BIT_64)
					procedure.POP64(EBCRegisters.R6, false, null)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}


				Opcode.I2S -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDW32(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}


				Opcode.I2B -> {
					expectElement(EBCCompilerStackType.BIT_32)
					procedure.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDB32(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
					stack.addLast(EBCCompilerStackType.BIT_32)
				}

				else -> throw IllegalArgumentException(element.opcode().toString())
			}

			is StackInstruction -> when (element.opcode()) {
				Opcode.POP -> {
					if (stack.last() == EBCCompilerStackType.BIT_32) {
						expectElement(EBCCompilerStackType.BIT_32)
						procedure.POP32(EBCRegisters.R6, false, null)
					} else {
						expectElement(EBCCompilerStackType.NATURAL)
						procedure.POPn(EBCRegisters.R6, false, null)
					}
				}

				Opcode.POP2 -> when (stack.last()) {
					EBCCompilerStackType.BIT_32 -> {
						expectElement(EBCCompilerStackType.BIT_32)
						procedure.POP32(EBCRegisters.R6, false, null)
						expectElement(EBCCompilerStackType.BIT_32)
						procedure.POP32(EBCRegisters.R6, false, null)
					}

					EBCCompilerStackType.NATURAL -> {
						expectElement(EBCCompilerStackType.NATURAL)
						procedure.POPn(EBCRegisters.R6, false, null)
						expectElement(EBCCompilerStackType.NATURAL)
						procedure.POPn(EBCRegisters.R6, false, null)
					}

					EBCCompilerStackType.BIT_64 -> {
						expectElement(EBCCompilerStackType.BIT_64)
						procedure.POP64(EBCRegisters.R6, false, null)
					}
				}

				Opcode.DUP -> {
					if (stack.last() == EBCCompilerStackType.BIT_32) {
						expectElement(EBCCompilerStackType.BIT_32)
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.PUSH32(EBCRegisters.R6, false, null)
						stack.addLast(EBCCompilerStackType.BIT_32)
						procedure.PUSH32(EBCRegisters.R6, false, null)
						stack.addLast(EBCCompilerStackType.BIT_32)
					} else {
						expectElement(EBCCompilerStackType.NATURAL)
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSHn(EBCRegisters.R6, false, null)
						stack.addLast(EBCCompilerStackType.NATURAL)
						procedure.PUSHn(EBCRegisters.R6, false, null)
						stack.addLast(EBCCompilerStackType.NATURAL)
					}
				}

				else -> throw IllegalArgumentException(element.opcode().toString())
			}

			is ReturnInstruction -> {
				expectElement(EBCCompilerStackType.BIT_64)
				procedure.POP64(EBCRegisters.R7, false, null)
				procedure.RET()
			}

			is Label -> labelTargets[element] = procedure.output.size.toULong()
			is Instruction -> logger.warning("Unknown instruction!")
			else -> logger.info("Pseudo-instruction:")
		}.also {
			logger.info { "$element $stack" }
		}
		if (stack.isNotEmpty()) logger.warning("Stack was not empty at compilation edge! $stack")
		branchLocations.forEach { (location, label) ->
			val target = labelTargets[label]!!
			val data = EBCProcedure()
				.MOVIqq(
					EBCRegisters.R6, false, null,
					target + codeBase
				)
				.output
			System.arraycopy(
				data, 0,
				procedure.output, location.toInt(),
				10
			)
		}
		val methodLocations = mutableMapOf<MethodModel, ULong>()
		callTargets.forEach { (_, model) ->
			logger.info {
				"Sub-compilation of method: ${model.methodName().stringValue()}${model.methodType().stringValue()}"
			}
			val output = compileMethod(
				model.code().get(),
				codeBase + procedure.output.size.toUInt(),
				initBase + data.size.toUInt(),
				unInitBase,
				stringTable,
				EBCVariableAllocator(
					variableAllocator.nextFreeNatural,
					variableAllocator.nextFreeConstant
				)
			)
			methodLocations[model] = procedure.output.size.toULong()
			procedure.output += output.code
			data += output.initializedData
		}
		callLocations.forEach { (location, method) ->
			val locatedAt = methodLocations[method]!!
			val data = EBCProcedure()
				.MOVIqq(
					EBCRegisters.R6, false, null,
					locatedAt + codeBase
				)
				.output
			System.arraycopy(
				data, 0,
				procedure.output, location.toInt(),
				10
			)
		}
		return EBCCompilationOutput(procedure.output, data)
	}
}