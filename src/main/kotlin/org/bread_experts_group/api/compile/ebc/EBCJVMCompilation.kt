package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import java.lang.classfile.*
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.ClassFile.ACC_STATIC
import java.lang.classfile.instruction.*
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import kotlin.math.abs
import kotlin.reflect.KClass

object EBCJVMCompilation {
	private val cf = ClassFile.of(
		ClassFile.DebugElementsOption.DROP_DEBUG,
		ClassFile.LineNumbersOption.DROP_LINE_NUMBERS
	)

	fun compileClass(
		clazz: KClass<*>, codeSource: Path,
		codeBase: ULong, initBase: ULong, unInitBase: ULong, instructionSpaceBase: ULong
	): EBCCompilationOutput {
		val efiClass = cf.parse(
			codeSource.resolve(clazz.java.name.replace('.', '/') + ".class")
		)
		val efiMethods = efiClass.methods()
		val efiMethod = efiMethods.firstOrNull {
			(it.flags().flagsMask() and (ACC_PUBLIC or ACC_STATIC)) == (ACC_PUBLIC or ACC_STATIC) &&
					it.methodType().equalsString(
						"(L${MemorySegment::class.java.name.replace('.', '/')};" +
								"L${MemorySegment::class.java.name.replace('.', '/')};)J"
					) && it.methodName().equalsString("efiMain")
		}
		if (efiMethod == null) throw IllegalArgumentException(
			"No public static efiMain(${MemorySegment::class}, ${MemorySegment::class}): Long function " +
					"was found within [$clazz]."
		)
		val code = efiMethod.code().orElseThrow { IllegalArgumentException("$efiMethod must contain code.") }
		return compileMethod(
			efiMethod.methodTypeSymbol(),
			code,
			EBCCompilerData(
				codeBase, unInitBase, initBase, instructionSpaceBase,
				allocator = EBCVariableAllocator(initBase)
			),
			true
		)
	}

	fun compileMethod(
		type: MethodTypeDesc,
		code: CodeModel,
		data: EBCCompilerData,
		initialRun: Boolean
	): EBCCompilationOutput {
		val procedure = EBCProcedure()
		val parameters = type.parameterList()
		var stackNatural = 0u
		var stackConstant = 16u
		var parameterIndex = 0
		val parameterIndices = Array(parameters.size) {
			val savedIndex = parameterIndex
			parameterIndex += when (val kind = TypeKind.from(parameters[it])) {
				TypeKind.INT, TypeKind.REFERENCE -> 1
				TypeKind.LONG -> 2
				else -> throw NotImplementedError("Unsupported parameter kind $kind")
			}
			savedIndex
		}.let { if (initialRun) it else it.reversedArray() }
		parameterIndex = 0
		for (parameter in parameters.asReversed()) {
			if (!initialRun) stackConstant += 4u
			when (val kind = TypeKind.from(parameter)) {
				TypeKind.REFERENCE -> {
					procedure.MOVnw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackNatural++
					val (n, c) = data.allocator.getOrAllocateNatural(parameterIndices[parameterIndex])
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						data.unInitBase
					)
					procedure.MOVnw(
						EBCRegisters.R3, true, naturalIndex16(false, n, c),
						EBCRegisters.R2, false, null
					)
				}

				TypeKind.LONG -> {
					procedure.MOVqw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackConstant += 8u
					val (n, c) = data.allocator.getOrAllocate64(parameterIndices[parameterIndex])
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						data.unInitBase
					)
					procedure.MOVqw(
						EBCRegisters.R3, true, naturalIndex16(false, n, c),
						EBCRegisters.R2, false, null
					)
				}

				TypeKind.INT -> {
					procedure.MOVdw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackConstant += 4u
					val (n, c) = data.allocator.getOrAllocate32(parameterIndices[parameterIndex])
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						data.unInitBase
					)
					procedure.MOVdw(
						EBCRegisters.R3, true, naturalIndex16(false, n, c),
						EBCRegisters.R2, false, null
					)
				}

				else -> throw NotImplementedError("Unsupported parameter kind $kind")
			}
			parameterIndex++
		}

		// Types:
		// 2u - Reference
		// 1u - Long
		// 0u - Int
		val branchingLocations = mutableMapOf<Int, BranchInstruction>()
		val branchLocations = mutableMapOf<Label, Int>()
		val callingLocations = mutableMapOf<Int, String>()
		val functions = mutableMapOf<String, ByteArray>()
		for (element in code) when (element) {
			is StoreInstruction -> when (val kind = element.typeKind()) {
				TypeKind.REFERENCE -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.MOVIqq(
						EBCRegisters.R2, false, null,
						data.unInitBase
					)
					val (n, c) = data.allocator.getOrAllocateNatural(element.slot())
					procedure.POPn(EBCRegisters.R2, true, naturalIndex16(false, n, c))
				}

				TypeKind.LONG -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.MOVIqq(
						EBCRegisters.R2, false, null,
						data.unInitBase
					)
					val (n, c) = data.allocator.getOrAllocate64(element.slot())
					procedure.POP64(EBCRegisters.R2, true, naturalIndex16(false, n, c))
				}

				TypeKind.INT -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.MOVIqq(
						EBCRegisters.R2, false, null,
						data.unInitBase
					)
					val (n, c) = data.allocator.getOrAllocate32(element.slot())
					procedure.POP32(EBCRegisters.R2, true, naturalIndex16(false, n, c))
				}

				else -> throw NotImplementedError("Unknown type kind $kind ($element)")
			}

			is LoadInstruction -> {
				val (n, c) = data.allocator[element.slot()]
				when (val kind = element.typeKind()) {
					TypeKind.REFERENCE -> {
						procedure.MOVIqq(
							EBCRegisters.R2, false, null,
							data.unInitBase
						)
						procedure.PUSHn(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						procedure.MOVIdw(EBCRegisters.R2, false, null, 2u)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					TypeKind.LONG -> {
						procedure.MOVIqq(
							EBCRegisters.R2, false, null,
							data.unInitBase
						)
						procedure.PUSH64(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					TypeKind.INT -> {
						procedure.MOVIqq(
							EBCRegisters.R2, false, null,
							data.unInitBase
						)
						procedure.PUSH32(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					else -> throw NotImplementedError("Unknown type kind $kind ($element)")
				}
			}

			is ConstantInstruction -> when (val kind = element.typeKind()) {
				TypeKind.REFERENCE -> @Suppress(
					"IMPOSSIBLE_IS_CHECK_WARNING", "KotlinConstantConditions"
				) when (val value = element.constantValue()) {
					is String -> {
						val addr = data.allocator.getOrAllocateString(value)
						procedure.MOVIqq(EBCRegisters.R2, false, null, addr)
						procedure.PUSHn(EBCRegisters.R2, false, null)
						procedure.MOVIdw(EBCRegisters.R2, false, null, 2u)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					else -> throw NotImplementedError("Unknown reference type $value ($element)")
				}

				TypeKind.LONG -> {
					@Suppress("CAST_NEVER_SUCCEEDS")
					procedure.MOVIqq(EBCRegisters.R2, false, null, (element.constantValue() as Long).toULong())
					procedure.PUSH64(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				TypeKind.INT -> {
					@Suppress("CAST_NEVER_SUCCEEDS")
					procedure.MOVIqd(EBCRegisters.R2, false, null, (element.constantValue() as Int).toUInt())
					procedure.PUSH32(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				else -> throw NotImplementedError("Unknown type kind $kind ($element)")
			}

			is BranchInstruction -> {
				when (element.opcode()) {
					Opcode.IFNONNULL, Opcode.IFNULL -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R2, false, null)
						procedure.CMPI64weq( // TODO: USE BETTER CHECK, WILL NOT WORK FOR >64 BITS
							EBCRegisters.R2, false, null,
							0u
						)
					}

					Opcode.IF_ICMPGE -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null) // 2
						procedure.POP32(EBCRegisters.R3, false, null)
						procedure.POP32(EBCRegisters.R3, false, null) // 1
						procedure.CMP32gte( // o1 >= o2, 1 >= 2
							EBCRegisters.R3,
							EBCRegisters.R2, false, null
						)
					}

					Opcode.IF_ICMPLE -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R3, false, null)
						procedure.POP32(EBCRegisters.R3, false, null)
						procedure.CMP32lte(
							EBCRegisters.R3,
							EBCRegisters.R2, false, null
						)
					}

					Opcode.IF_ICMPEQ, Opcode.IF_ICMPNE -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R3, false, null)
						procedure.POP32(EBCRegisters.R3, false, null)
						procedure.CMP32eq(
							EBCRegisters.R3,
							EBCRegisters.R2, false, null
						)
					}

					Opcode.IFGE -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.CMPI32wgte(
							EBCRegisters.R2, false, null,
							0u
						)
					}

					Opcode.IFLE -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.CMPI32wlte(
							EBCRegisters.R2, false, null,
							0u
						)
					}

					Opcode.IFNE, Opcode.IFEQ -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.CMPI32weq(
							EBCRegisters.R2, false, null,
							0u
						)
					}

					Opcode.GOTO -> {}
					else -> throw NotImplementedError("Unknown branch element $element")
				}

				branchingLocations[procedure.output.size] = element
				procedure.output += ByteArray(8) // TODO: Use a JMP8 optimization where possible
			}

			is IncrementInstruction -> {
				val (n, c) = data.allocator[element.slot()]
				procedure.MOVIqq(
					EBCRegisters.R3, false, null,
					data.unInitBase
				)
				procedure.MOVdw(
					EBCRegisters.R2, false, null,
					EBCRegisters.R3, true, naturalIndex16(false, n, c)
				)
				val a = element.constant()
				procedure.MOVdw(
					EBCRegisters.R2, false, null,
					EBCRegisters.R2, false,
					naturalIndex16(a < 0, 0u, abs(a).toUInt())
				)
				procedure.MOVdw(
					EBCRegisters.R3, true, naturalIndex16(false, n, c),
					EBCRegisters.R2, false, null
				)
			}

			is ConvertInstruction -> when (val opcode = element.opcode()) {
				Opcode.I2L -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R2, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				else -> throw NotImplementedError("Unknown conversion opcode $opcode ($element)")
			}

			is OperatorInstruction -> when (val opcode = element.opcode()) {
				Opcode.LADD -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.IADD -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.ADD32(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LSUB -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.SUB64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.LMUL -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.MUL64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.IMUL -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.MUL32(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LDIV -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.DIV64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.IDIV -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.DIV32(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.IREM -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.MOD32(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LCMP -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.CMP64eq(
						EBCRegisters.R1,
						EBCRegisters.R2, false, null
					)
					procedure.branch({ eq ->
						eq.MOVIdw(EBCRegisters.R1, false, null, 0u)
					}, { ne ->
						ne.CMP64lte(
							EBCRegisters.R1,
							EBCRegisters.R2, false, null
						)
						ne.branch({ lt ->
							lt.MOVIdd(EBCRegisters.R1, false, null, (-1).toUInt())
						}, { gt ->
							gt.MOVIdw(EBCRegisters.R1, false, null, 1u)
						})
					})
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.LNEG -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R1, false, null)
					procedure.NEG64(
						EBCRegisters.R1, false,
						EBCRegisters.R1, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.IUSHR -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.SHR32(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LUSHR -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP64(EBCRegisters.R3, false, null)
					procedure.SHR64(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LSHL -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP64(EBCRegisters.R3, false, null)
					procedure.SHL64(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.ISHL -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.SHL32(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LOR -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP64(EBCRegisters.R3, false, null)
					procedure.OR64(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.IOR -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.OR32(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R3, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				else -> throw NotImplementedError("Unknown operator opcode $opcode ($element)")
			}

			is Label -> branchLocations[element] = procedure.output.size

			is ReturnInstruction -> when (val kind = element.typeKind()) {
				TypeKind.LONG -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R7, false, null)
					procedure.RET()
				}

				TypeKind.INT -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R7, false, null)
					procedure.RET()
				}

				TypeKind.VOID -> procedure.RET()

				else -> throw NotImplementedError("Unknown return kind $kind ($element)")
			}

			is InvokeInstruction -> when (
				val descriptor = "${element.owner().name().stringValue()}." +
						"${element.name().stringValue()}${element.type().stringValue()}"
			) {
				"java/lang/foreign/MemorySegment.address()J" -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POPn(EBCRegisters.R2, false, null)
					procedure.PUSH64(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				else -> {
					if (!functions.contains(descriptor)) {
						val classFile = element.owner().asInternalName().replace('/', '.')
						val output = cf.parse(
							ClassLoader.getSystemClassLoader()
								.loadClass(classFile)
								.getResource(classFile.substringAfterLast('.') + ".class")
							!!.readBytes()
						).methods().first {
							it.methodName() == element.name() && it.methodType() == element.type()
						}.let {
							compileMethod(
								it.methodTypeSymbol(),
								it.code().get(),
								EBCCompilerData(
									data.codeBase, data.unInitBase, data.initBase, data.instructionSpaceBase,
									EBCVariableAllocator(
										data.allocator.nextFreeStringPosition,
										data.allocator.nextFreeNatural,
										data.allocator.nextFreeConstant
									)
								),
								false
							)
						}
						if (output.initializedData.isNotEmpty()) TODO("INIT EXTRA")
						functions[descriptor] = output.code
					}
					callingLocations[procedure.output.size] = descriptor
					procedure.output += ByteArray(8)
					var dropNatural = 0u
					var dropConstant = 0u
					for (parameter in element.typeSymbol().parameterList()) {
						when (val kind = TypeKind.from(parameter)) {
							TypeKind.REFERENCE -> dropNatural++
							TypeKind.INT -> dropConstant += 4u
							TypeKind.LONG -> dropConstant += 8u
							else -> throw NotImplementedError("Unknown parameter kind $kind")
						}
						dropConstant += 4u
					}
					procedure.MOVnw(
						EBCRegisters.R0, false, null,
						EBCRegisters.R0, false, naturalIndex16(false, dropNatural, dropConstant)
					)
					when (val kind = TypeKind.from(element.typeSymbol().returnType())) {
						TypeKind.LONG -> {
							procedure.PUSH64(EBCRegisters.R7, false, null)
							procedure.MOVIdw(EBCRegisters.R2, false, null, 1u)
							procedure.PUSH32(EBCRegisters.R2, false, null)
						}

						TypeKind.INT -> {
							procedure.PUSH32(EBCRegisters.R7, false, null)
							procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
							procedure.PUSH32(EBCRegisters.R2, false, null)
						}

						else -> throw NotImplementedError("Unknown return type $kind ($element)")
					}
				}
			}

			/* TODO: It may be possible to read the stack map tables to determine the type
			    of the object being popped */
			is StackInstruction -> when (element.opcode()) {
				Opcode.POP -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.CMPI32weq(
						EBCRegisters.R2, false, null,
						2u
					)
					procedure.branch({ t ->
						// Reference
						t.POPn(EBCRegisters.R2, false, null)
					}, { f ->
						// Other category 1 computational types (32-bits)
						f.POP32(EBCRegisters.R2, false, null)
					})
				}

				else -> throw NotImplementedError("Unknown stack element $element")
			}

			else -> throw NotImplementedError("Unknown compile element $element")
		}
		for ((location, instruction) in branchingLocations) {
			val jumpLocation = branchLocations[instruction.target()]!!
			val infill = EBCProcedure()
			infill.MOVIqd(
				EBCRegisters.R2, false, null,
				(jumpLocation - (location + 8)).toUInt()
			)
			when (val opcode = instruction.opcode()) {
				Opcode.IFNONNULL, Opcode.IFNULL -> infill.JMP32(
					conditional = true, conditionSet = opcode == Opcode.IFNULL, relative = true,
					EBCRegisters.R2, false, null
				)

				Opcode.IF_ICMPGE, Opcode.IF_ICMPLE, Opcode.IF_ICMPEQ,
				Opcode.IFGE, Opcode.IFLE, Opcode.IFEQ -> infill.JMP32(
					conditional = true, conditionSet = true, relative = true,
					EBCRegisters.R2, false, null
				)

				Opcode.IFNE, Opcode.IF_ICMPNE -> infill.JMP32(
					conditional = true, conditionSet = false, relative = true,
					EBCRegisters.R2, false, null
				)

				Opcode.GOTO -> infill.JMP32(
					conditional = false, conditionSet = false, relative = true,
					EBCRegisters.R2, false, null
				)

				else -> throw NotImplementedError("Unknown branch opcode $opcode")
			}
			var i = 0
			for (byte in infill.output) procedure.output[location + i++] = byte
		}
		val functionLocations = mutableMapOf<String, Int>()
		for ((function, code) in functions) {
			functionLocations[function] = procedure.output.size
			procedure.output += code
		}
		for ((location, function) in callingLocations) {
			val functionLocation = functionLocations[function]!!
			val infill = EBCProcedure()
			infill.MOVIqd(
				EBCRegisters.R2, false, null,
				(functionLocation - (location + 8)).toUInt()
			)
			infill.CALL32(
				EBCRegisters.R2,
				operand1Indirect = false, relative = true, native = false,
				null
			)
			var i = 0
			for (byte in infill.output) procedure.output[location + i++] = byte
		}
		// decompilation
		code.forEach { println(it) }
		fun decodeIndex(n: Short): String {
			val n = n.toInt()
			var string = if ((n ushr 15) != 0) "-(" else "("
			val naturalBitCount = ((n ushr 12) and 0b111) * 2
			string += (n and ((1 shl naturalBitCount) - 1)).toString() + ", "
			string += ((n and ((1 shl 12) - 1)) ushr naturalBitCount).toString() + ")"
			return string
		}

		fun decodeIndex(n: Int): String {
			val n = n
			var string = if ((n ushr 31) != 0) "-(" else "("
			val naturalBitCount = ((n ushr 28) and 0b111) * 4
			string += (n and ((1 shl naturalBitCount) - 1)).toString() + ", "
			string += ((n and ((1 shl 28) - 1)) ushr naturalBitCount).toString() + ")"
			return string
		}

		fun decodeIndex(n: Long): String {
			val n = n
			var string = if ((n ushr 63) != 0L) "-(" else "("
			val naturalBitCount = (((n ushr 60) and 0b111) * 8).toInt()
			string += (n and ((1L shl naturalBitCount) - 1)).toString() + ", "
			string += ((n and ((1L shl 60) - 1)) ushr naturalBitCount).toString() + ")"
			return string
		}

		val buffer = ByteBuffer.wrap(procedure.output).order(ByteOrder.LITTLE_ENDIAN)
		while (buffer.hasRemaining()) {
			val byte1 = buffer.get().toInt() and 0xFF
			val byte2 = buffer.get().toInt() and 0xFF
			var description = ""
			when (val instruction = byte1 and 0b111111) {
				0x01 -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "JMP${if (b64) "64" else "32"}${if (byte2 and 0b10000 != 0) "" else "a"}" +
							"${if (byte2 and 0b10000000 != 0) (if (byte2 and 0b1000000 != 0) "cs" else "cc") else ""} "
					if (b64) description += "x${buffer.getLong().toHexString()}"
					else {
						description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
						if (byte1 and 0b10000000 != 0) description += ' ' + (if (byte2 and 0b1000 != 0) decodeIndex(
							buffer.getInt()
						) else "x${buffer.getInt().toHexString()}")
					}
				}

				0x02 -> {
					description += "JMP8${
						if (byte1 and 0b10000000 != 0) (if (byte1 and 0b1000000 != 0) "cs" else "cc")
						else ""
					} ${byte2.toByte()}"
				}

				0x03 -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "CALL${if (b64) "64" else "32"}${if (byte2 and 0b100000 != 0) "EX" else ""}" +
							"${if (byte2 and 0b10000 != 0) "" else "a"} "
					if (b64) description += "x${buffer.getLong().toHexString()}"
					else {
						description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
						if (byte1 and 0b10000000 != 0) description += ' ' + (if (byte2 and 0b1000 != 0) decodeIndex(
							buffer.getInt()
						) else "x${buffer.getInt().toHexString()}")
					}
				}

				0x04 -> description += "RET"

				0x05, 0x06, 0x07, 0x08, 0x09 -> {
					val b64 = byte1 and 0b1000000 != 0
					val kind = when (instruction) {
						0x05 -> "eq"
						0x06 -> "lte"
						0x07 -> "gte"
						0x08 -> "ulte"
						0x09 -> "ugte"
						else -> throw InternalError()
					}
					description += "CMP${if (b64) 64 else 32}$kind "
					description += EBCRegisters.entries[byte2 and 0b111]
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
				}

				0x2D, 0x2E, 0x2F, 0x30, 0x31 -> {
					val immediate32 = byte1 and 0b10000000 != 0
					val b64 = byte1 and 0b1000000 != 0
					val kind = when (instruction) {
						0x2D -> "eq"
						0x2E -> "lte"
						0x2F -> "gte"
						0x30 -> "ulte"
						0x31 -> "ugte"
						else -> throw InternalError()
					}
					description += "CMPI${if (b64) 64 else 32}$kind "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte2 and 0b10000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", x" + if (immediate32) buffer.getInt().toHexString()
					else buffer.getShort().toHexString()
				}

				0x0C -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "ADD${if (b64) 64 else 32} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23, 0x24, 0x28 -> {
					val size: EBCMoveTypes
					val indexSize: EBCMoveTypes
					if (instruction == 0x28) {
						size = EBCMoveTypes.BITS_64_QUADWORD
						indexSize = EBCMoveTypes.BITS_64_QUADWORD
					} else if (instruction >= 0x21) {
						size = when (instruction - 0x21) {
							0 -> EBCMoveTypes.BITS_8_BYTE
							1 -> EBCMoveTypes.BITS_16_WORD
							2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
							3 -> EBCMoveTypes.BITS_64_QUADWORD
							else -> throw InternalError()
						}
						indexSize = EBCMoveTypes.BITS_32_DOUBLEWORD
					} else {
						size = when (instruction - 0x1D) {
							0 -> EBCMoveTypes.BITS_8_BYTE
							1 -> EBCMoveTypes.BITS_16_WORD
							2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
							3 -> EBCMoveTypes.BITS_64_QUADWORD
							else -> throw InternalError()
						}
						indexSize = EBCMoveTypes.BITS_16_WORD
					}
					description += "MOV${size.letter}${indexSize.letter} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + when (indexSize) {
						EBCMoveTypes.BITS_64_QUADWORD -> decodeIndex(buffer.getLong())
						EBCMoveTypes.BITS_32_DOUBLEWORD -> decodeIndex(buffer.getInt())
						EBCMoveTypes.BITS_16_WORD -> decodeIndex(buffer.getShort())
						else -> throw InternalError()
					}
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b1000000 != 0) {
						description += ' ' + when (indexSize) {
							EBCMoveTypes.BITS_64_QUADWORD -> decodeIndex(buffer.getLong())
							EBCMoveTypes.BITS_32_DOUBLEWORD -> decodeIndex(buffer.getInt())
							EBCMoveTypes.BITS_16_WORD -> decodeIndex(buffer.getShort())
							else -> throw InternalError()
						}
					}
				}

				0x2B, 0x2C -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "${if (instruction == 0x2B) "PUSH" else "POP"}${if (b64) 64 else 32} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x32, 0x33 -> {
					description += "MOVn${if (instruction == 0x32) 'w' else 'd'} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b1000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x35, 0x36 -> {
					description += "${if (instruction == 0x35) "PUSH" else "POP"}n "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x37 -> {
					val move: EBCMoveTypes = when ((byte2 ushr 4) and 0b11) {
						0 -> EBCMoveTypes.BITS_8_BYTE
						1 -> EBCMoveTypes.BITS_16_WORD
						2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
						3 -> EBCMoveTypes.BITS_64_QUADWORD
						else -> throw InternalError()
					}
					val immediateSize: EBCMoveTypes = when (byte1 ushr 6) {
						1 -> EBCMoveTypes.BITS_16_WORD
						2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
						3 -> EBCMoveTypes.BITS_64_QUADWORD
						else -> throw InternalError()
					}
					description += "MOVI${move.letter}${immediateSize.letter} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte2 and 0b1000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", x" + when (immediateSize) {
						EBCMoveTypes.BITS_16_WORD -> buffer.getShort().toHexString()
						EBCMoveTypes.BITS_32_DOUBLEWORD -> buffer.getInt().toHexString()
						EBCMoveTypes.BITS_64_QUADWORD -> buffer.getLong().toHexString()
						else -> throw InternalError()
					}
				}

//				else -> throw NotImplementedError(
//					"Unknown instruction for decompilation 0x${instruction.toString(16)}"
//				)
			}
			println(description)
		}
		println()
		return EBCCompilationOutput(
			procedure.output,
			data.allocator.strings
		)
	}
}