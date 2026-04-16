package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.efi.ExternalCall
import java.lang.classfile.ClassFile.*
import java.lang.classfile.CodeModel
import java.lang.classfile.Label
import java.lang.classfile.Opcode
import java.lang.classfile.TypeKind
import java.lang.classfile.attribute.RuntimeInvisibleAnnotationsAttribute
import java.lang.classfile.instruction.*
import java.lang.constant.DynamicConstantDesc
import java.lang.constant.MethodTypeDesc
import java.nio.file.Path
import kotlin.math.abs
import kotlin.reflect.KClass

object EBCJVMCompilation {
	private val cf = of(
		DebugElementsOption.DROP_DEBUG,
		LineNumbersOption.DROP_LINE_NUMBERS
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
						"(L${Address::class.java.name.replace('.', '/')};" +
								"L${Address::class.java.name.replace('.', '/')};)J"
					) && it.methodName().equalsString("efiMain")
		}
		if (efiMethod == null) throw IllegalArgumentException(
			"No public static efiMain(${Address::class}, ${Address::class}): Long function " +
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

	fun EBCProcedure.popLongsV2V1R2R1() {
		POP32(EBCRegisters.R2, false, null)
		POP64(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R1, false, null)
		POP64(EBCRegisters.R1, false, null)
	}

	fun EBCProcedure.popIntsV2V1R2R1() {
		POP32(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R1, false, null)
		POP32(EBCRegisters.R1, false, null)
	}

	fun EBCProcedure.popIntLongV2V1R2R1() {
		POP32(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R1, false, null)
		POP64(EBCRegisters.R1, false, null)
	}

	fun compileMethod(
		type: MethodTypeDesc,
		code: CodeModel,
		data: EBCCompilerData,
		initialRun: Boolean
	): EBCCompilationOutput {
		val procedure = EBCProcedure()
		val parameters = type.parameterList().map { TypeKind.from(it) }
		var stackNatural = 0u
		var stackConstant = 16u
		var parameterIndex = 0
		val parameterIndices = Array(parameters.size) {
			val savedIndex = parameterIndex
			parameterIndex += when (val kind = parameters[it]) {
				TypeKind.INT, TypeKind.CHAR, TypeKind.BOOLEAN, TypeKind.REFERENCE -> 1
				TypeKind.LONG -> 2
				else -> throw NotImplementedError("Unsupported parameter kind $kind")
			}
			savedIndex
		}.let { if (initialRun) it else it.reversedArray() }
		parameterIndex = 0
		procedure.MOVIqq(
			EBCRegisters.R2, false, null,
			data.unInitBase
		)
		for (parameterKind in parameters.asReversed()) {
			if (!initialRun) stackConstant += 4u
			when (parameterKind) {
				TypeKind.REFERENCE -> {
					procedure.MOVnw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackNatural++
					val (n, c) = data.allocator.getOrAllocateNatural(parameterIndices[parameterIndex])
					procedure.MOVnw(
						EBCRegisters.R2, true, naturalIndex16(false, n, c),
						EBCRegisters.R1, false, null
					)
				}

				TypeKind.LONG -> {
					procedure.MOVqw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackConstant += 8u
					val (n, c) = data.allocator.getOrAllocate64(parameterIndices[parameterIndex])
					procedure.MOVqw(
						EBCRegisters.R2, true, naturalIndex16(false, n, c),
						EBCRegisters.R1, false, null
					)
				}

				TypeKind.INT, TypeKind.CHAR, TypeKind.BOOLEAN -> {
					procedure.MOVdw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R0, true, naturalIndex16(false, stackNatural, stackConstant)
					)
					stackConstant += 4u
					val (n, c) = data.allocator.getOrAllocate32(parameterIndices[parameterIndex])
					procedure.MOVdw(
						EBCRegisters.R2, true, naturalIndex16(false, n, c),
						EBCRegisters.R1, false, null
					)
				}

				else -> throw NotImplementedError("Unsupported parameter kind $parameterKind")
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

					is DynamicConstantDesc<*> if value.constantName() == "_" -> {
						procedure.MOVIqw(EBCRegisters.R1, false, null, 0u)
						// TODO: Unsafe (>64-bits)
						procedure.PUSHn(EBCRegisters.R1, false, null)
						procedure.MOVIdw(EBCRegisters.R1, false, null, 2u)
						procedure.PUSH32(EBCRegisters.R1, false, null)
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

					Opcode.IF_ICMPGE, Opcode.IF_ICMPLT -> {
						procedure.popIntsV2V1R2R1()
						procedure.CMP32gte(
							EBCRegisters.R1,
							EBCRegisters.R2, false, null
						)
					}

					Opcode.IF_ICMPLE, Opcode.IF_ICMPGT -> {
						procedure.popIntsV2V1R2R1()
						procedure.CMP32lte(
							EBCRegisters.R1,
							EBCRegisters.R2, false, null
						)
					}

					Opcode.IF_ICMPEQ, Opcode.IF_ICMPNE -> {
						procedure.popIntsV2V1R2R1()
						procedure.CMP32eq(
							EBCRegisters.R1,
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

				Opcode.L2I -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.PUSH32(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				else -> throw NotImplementedError("Unknown conversion opcode $opcode ($element)")
			}

			is OperatorInstruction -> when (val opcode = element.opcode()) {
				Opcode.LADD, Opcode.LSUB, Opcode.LMUL, Opcode.LDIV, Opcode.LREM,
				Opcode.LOR, Opcode.LAND, Opcode.LXOR -> {
					procedure.popLongsV2V1R2R1()
					when (opcode) {
						Opcode.LADD -> procedure::ADD64
						Opcode.LSUB -> procedure::SUB64
						Opcode.LMUL -> procedure::MUL64
						Opcode.LDIV -> procedure::DIV64
						Opcode.LREM -> procedure::MOD64
						Opcode.LOR -> procedure::OR64
						Opcode.LAND -> procedure::AND64
						Opcode.LXOR -> procedure::XOR64
					}(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				Opcode.LSHL, Opcode.LSHR, Opcode.LUSHR -> {
					procedure.popIntLongV2V1R2R1()
					when (opcode) {
						Opcode.LSHL -> procedure::SHL64
						Opcode.LSHR -> procedure::ASHR64
						Opcode.LUSHR -> procedure::SHR64
					}(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
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

				Opcode.IADD, Opcode.ISUB, Opcode.IMUL, Opcode.IDIV, Opcode.IREM,
				Opcode.ISHL, Opcode.ISHR, Opcode.IUSHR, Opcode.IOR, Opcode.IAND, Opcode.IXOR -> {
					procedure.popIntsV2V1R2R1()
					when (opcode) {
						Opcode.IADD -> procedure::ADD32
						Opcode.ISUB -> procedure::SUB32
						Opcode.IMUL -> procedure::MUL32
						Opcode.IDIV -> procedure::DIV32
						Opcode.IREM -> procedure::MOD32
						Opcode.ISHL -> procedure::SHL32
						Opcode.ISHR -> procedure::ASHR32
						Opcode.IUSHR -> procedure::SHR32
						Opcode.IOR -> procedure::OR32
						Opcode.IAND -> procedure::AND32
						Opcode.IXOR -> procedure::XOR32
					}(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				Opcode.LCMP -> {
					procedure.popLongsV2V1R2R1()
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

				else -> throw NotImplementedError("Unknown operator opcode $opcode ($element)")
			}

			is Label -> branchLocations[element] = procedure.output.size

			is ReturnInstruction -> when (val kind = element.typeKind()) {
				TypeKind.REFERENCE -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POPn(EBCRegisters.R7, true, null)
					procedure.RET()
				}

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
				"${EBCIntrinsics.internalName}.allocateN()L${Address.internalName};" -> {
					procedure.MOVIqq(EBCRegisters.R1, false, null, data.unInitBase)
					procedure.MOVInw(
						EBCRegisters.R2, false, null,
						naturalIndex16(
							false,
							data.allocator.nextFreeNatural, data.allocator.nextFreeConstant
						)
					)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 2u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					data.allocator.nextFreeNatural++
				}

				"${EBCIntrinsics.internalName}.allocate32()L${Address.internalName};" -> {
					procedure.MOVIqq(EBCRegisters.R1, false, null, data.unInitBase)
					procedure.MOVInw(
						EBCRegisters.R2, false, null,
						naturalIndex16(
							false,
							data.allocator.nextFreeNatural, data.allocator.nextFreeConstant
						)
					)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 2u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					data.allocator.nextFreeConstant += 4u
				}

				"${EBCIntrinsics.internalName}.toLong(L${Address.internalName};)J" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				"${EBCIntrinsics.internalName}.accessN(L${Address.internalName};)L${Address.internalName};" -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVnw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R1, true, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.PUSH32(EBCRegisters.R2, false, null)
				}

				"${EBCIntrinsics.internalName}.access64(L${Address.internalName};)J" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVqw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R1, true, null
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				"${EBCIntrinsics.internalName}.write64(L${Address.internalName};J)V" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVqw(
						EBCRegisters.R1, true, null,
						EBCRegisters.R2, false, null
					)
				}

				"${EBCIntrinsics.internalName}.access32(L${Address.internalName};)I" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVdw(
						EBCRegisters.R1, false, null,
						EBCRegisters.R1, true, null
					)
					procedure.PUSH32(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				"${EBCIntrinsics.internalName}.access16(L${Address.internalName};)S" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.MOVww(
						EBCRegisters.R2, false, null,
						EBCRegisters.R1, true, null
					)
					procedure.PUSH32(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				"${EBCIntrinsics.internalName}.access8(L${Address.internalName};)B" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R1, true, null
					)
					procedure.PUSH32(EBCRegisters.R2, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 0u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
				}

				"${EBCIntrinsics.internalName}.plus(L${Address.internalName};J)L${Address.internalName};" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R2, false, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.PUSH32(EBCRegisters.R3, false, null)
				}

				"${EBCIntrinsics.internalName}.nat(L${Address.internalName};J)L${Address.internalName};" -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.POP64(EBCRegisters.R2, false, null)
					procedure.POP32(EBCRegisters.R3, false, null)
					procedure.POPn(EBCRegisters.R1, false, null)
					procedure.MOVInw(
						EBCRegisters.R4, false, null,
						naturalIndex16(false, 1u, 0u)
					)
					procedure.MUL64(
						EBCRegisters.R4, false,
						EBCRegisters.R2, false, null
					)
					procedure.ADD64(
						EBCRegisters.R4, false,
						EBCRegisters.R1, false, null
					)
					procedure.PUSHn(EBCRegisters.R4, false, null)
					procedure.PUSH32(EBCRegisters.R3, false, null)
				}

				"${EBCIntrinsics.internalName}.naturalSize()J" -> {
					procedure.MOVInw(
						EBCRegisters.R1, false, null,
						naturalIndex16(false, 1u, 0u)
					)
					procedure.PUSH64(EBCRegisters.R1, false, null)
					procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
					procedure.PUSH32(EBCRegisters.R1, false, null)
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
							if (it.flags().flagsMask() and ACC_NATIVE != 0) {
								val useExternalCallForm = it.firstNotNullOfOrNull { attribute ->
									attribute as? RuntimeInvisibleAnnotationsAttribute
								}?.annotations()?.any { annotation ->
									annotation.className().equalsString(
										"L${ExternalCall::class.qualifiedName!!.replace('.', '/')};"
									)
								}
								if (useExternalCallForm == true) {
									val reversingAllocator = EBCVariableAllocator(
										data.allocator.nextFreeStringPosition,
										data.allocator.nextFreeNatural, data.allocator.nextFreeConstant
									)
									val parameters = it.methodTypeSymbol().parameterList().map { parameter ->
										TypeKind.from(parameter)
									}.asReversed()
									var index = parameters.size
									procedure.MOVIqq(EBCRegisters.R2, false, null, data.unInitBase)
									for (parameterKind in parameters) {
										procedure.POP32(EBCRegisters.R1, false, null)
										when (parameterKind) {
											TypeKind.INT, TypeKind.BOOLEAN -> {
												val (n, c) = reversingAllocator.getOrAllocate32(--index)
												procedure.POP32(
													EBCRegisters.R2,
													true, naturalIndex16(false, n, c)
												)
											}

											TypeKind.REFERENCE -> {
												val (n, c) = reversingAllocator.getOrAllocateNatural(--index)
												procedure.POPn(
													EBCRegisters.R2,
													true, naturalIndex16(false, n, c)
												)
											}

											else -> throw NotImplementedError("Unknown parameter kind $parameterKind")
										}
									}
									var dropNatural = 0u
									var dropConstant = 0u
									index = parameters.size
									for (parameterKind in parameters) {
										val (n, c) = reversingAllocator[--index]
										val nIndex = naturalIndex16(false, n, c)
										when (parameterKind) {
											TypeKind.REFERENCE if index == 0 -> procedure.MOVnw(
												EBCRegisters.R1, false, null,
												EBCRegisters.R2, true, nIndex
											)

											else if index > 0 -> when (parameterKind) {
												TypeKind.REFERENCE -> {
													dropNatural++
													procedure.PUSHn(EBCRegisters.R2, true, nIndex)
												}

												TypeKind.LONG -> {
													dropConstant += 8u
													procedure.PUSH64(EBCRegisters.R2, true, nIndex)
												}

												TypeKind.INT -> {
													dropNatural++
													procedure.EXTNDD64(
														EBCRegisters.R3, false,
														EBCRegisters.R2, true, nIndex
													)
													procedure.PUSHn(EBCRegisters.R3, false, null)
												}

												else -> throw NotImplementedError(
													"Unknown parameter kind $parameterKind"
												)
											}

											else -> throw NotImplementedError("Unknown parameter kind $parameterKind")
										}
									}
									procedure.CALL32(
										EBCRegisters.R1, false,
										relative = false, native = true, null
									)
									procedure.MOVnw(
										EBCRegisters.R0, false, null,
										EBCRegisters.R0, false, naturalIndex16(false, dropNatural, dropConstant)
									)
									when (val kind = TypeKind.from(it.methodTypeSymbol().returnType())) {
										TypeKind.LONG -> {
											procedure.PUSH64(EBCRegisters.R7, false, null)
											procedure.MOVIdw(EBCRegisters.R1, false, null, 1u)
											procedure.PUSH32(EBCRegisters.R1, false, null)
										}

										TypeKind.INT -> {
											procedure.PUSH32(EBCRegisters.R7, false, null)
											procedure.MOVIdw(EBCRegisters.R1, false, null, 0u)
											procedure.PUSH32(EBCRegisters.R1, false, null)
										}

										else -> throw NotImplementedError("Unknown return kind $kind")
									}
									continue
								} else throw NotImplementedError("Native function $descriptor")
							}
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
							TypeKind.INT, TypeKind.BOOLEAN -> dropConstant += 4u
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
						TypeKind.REFERENCE -> {
							procedure.PUSHn(EBCRegisters.R7, true, null)
							procedure.MOVIdw(EBCRegisters.R2, false, null, 2u)
							procedure.PUSH32(EBCRegisters.R2, false, null)
						}

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

				Opcode.DUP -> {
					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.CMPI32weq(
						EBCRegisters.R2, false, null,
						2u
					)
					procedure.branch({ t ->
						// Reference
						t.POPn(EBCRegisters.R1, false, null)
						t.PUSHn(EBCRegisters.R1, false, null)
						t.PUSH32(EBCRegisters.R2, false, null)
						t.PUSHn(EBCRegisters.R1, false, null)
						t.PUSH32(EBCRegisters.R2, false, null)
					}, { f ->
						// Other category 1 computational types (32-bits)
						f.POP32(EBCRegisters.R1, false, null)
						f.PUSH32(EBCRegisters.R1, false, null)
						f.PUSH32(EBCRegisters.R2, false, null)
						f.PUSH32(EBCRegisters.R1, false, null)
						f.PUSH32(EBCRegisters.R2, false, null)
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

				Opcode.IFNE, Opcode.IF_ICMPNE, Opcode.IF_ICMPLT, Opcode.IF_ICMPGT -> infill.JMP32(
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
		println(EBCDisassembly.diassemble(procedure.output))
		println()
		return EBCCompilationOutput(
			procedure.output,
			data.allocator.strings
		)
	}
}