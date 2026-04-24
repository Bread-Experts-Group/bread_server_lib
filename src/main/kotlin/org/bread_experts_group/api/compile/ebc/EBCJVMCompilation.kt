package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.efi.EFITableHeader
import org.bread_experts_group.api.compile.ebc.efi.ExternalCall
import java.lang.classfile.Attributes
import java.lang.classfile.ClassFile.*
import java.lang.classfile.Opcode
import java.lang.classfile.TypeKind
import java.lang.classfile.attribute.CodeAttribute
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
		codeBase: ULong, initBase: ULong, unInitBase: ULong
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
		val code = efiMethod.findAttribute(Attributes.code()).orElseThrow {
			IllegalArgumentException("$efiMethod must contain code.")
		}
		return compileMethod(
			efiMethod.methodTypeSymbol(),
			code,
			EBCCompilerData(
				codeBase, unInitBase, initBase,
				allocator = EBCVariableAllocator(initBase)
			),
			true
		)
	}

	fun EBCProcedure.popLongsV2V1R2R1() {
		POP64(EBCRegisters.R2, false, null)
		POP64(EBCRegisters.R1, false, null)
	}

	fun EBCProcedure.popIntsV2V1R2R1() {
		POP32(EBCRegisters.R2, false, null)
		POP32(EBCRegisters.R1, false, null)
	}

	fun EBCProcedure.popIntLongV2V1R2R1() {
		POP32(EBCRegisters.R2, false, null)
		POP64(EBCRegisters.R1, false, null)
	}

	// REFERENCE STRUCTURE (STACK)
	// UINT32 - REFERENCE KIND
	// NAT    - Kind 0 (null)
	//            Reserved
	//          Kind 1 (array)
	//            Array Address
	//              UINT32 - Reference PTR Count
	//              UINT32 - Element Count
	//              ...... - Elements
	//          Kind 2 (object)
	//            Object Address
	//              Unknown
	//          Kind 3 (address)
	//            Address

	fun compileMethod(
		type: MethodTypeDesc,
		code: CodeAttribute,
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
				TypeKind.INT, TypeKind.CHAR, TypeKind.SHORT, TypeKind.BYTE, TypeKind.BOOLEAN, TypeKind.REFERENCE -> 1
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

				TypeKind.INT, TypeKind.CHAR, TypeKind.SHORT, TypeKind.BYTE, TypeKind.BOOLEAN -> {
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
		if (initialRun) {
			procedure.MOVIqq(
				EBCRegisters.R3, false, null,
				data.unInitBase
			)
			val (n, c) = data.allocator[1]
			procedure.MOVnw(
				EBCRegisters.R1, false, null,
				EBCRegisters.R3, true, naturalIndex16(false, n, c)
			)
			procedure.MOVInw(
				EBCRegisters.R2, false, null,
				naturalIndex16(false, 9u, EFITableHeader.OFFSET.toUInt())
			)
			procedure.ADD64(
				EBCRegisters.R1, false,
				EBCRegisters.R2, false, null
			)
			procedure.MOVnw(
				EBCRegisters.R1, false, null,
				EBCRegisters.R1, true, null
			)
			procedure.MOVInw(
				EBCRegisters.R2, false, null,
				naturalIndex16(false, 5u, EFITableHeader.OFFSET.toUInt())
			)
			procedure.ADD64(
				EBCRegisters.R1, false,
				EBCRegisters.R2, false, null
			)
			val aP = EBCCompilerData.AllocationEntry(
				data.allocator.nextFreeNatural,
				data.allocator.nextFreeConstant
			)
			data.bootSvcAllocatePool = aP
			procedure.MOVnw(
				EBCRegisters.R3, true, naturalIndex16(false, aP.natural, aP.constant),
				EBCRegisters.R1, true, null
			)
			data.allocator.nextFreeNatural += 2u
		}
		println(EBCDisassembly.diassemble(procedure.output))
		val branchingLocations = mutableMapOf<Int, BranchInstruction>()
		val bciToCode = mutableMapOf<Int, Int>()
		val callingLocations = mutableMapOf<Int, String>()
		val functions = mutableMapOf<String, ByteArray>()
		val codeStream = analyze(type.parameterList(), code)
		var bci = 0
		for ((instruction, frames) in codeStream) {
			val frame = frames.first() // TODO: Merge in analysis
			// TODO: Use frame to automatically allocate variables
			val beforeGen = procedure.output.size
			bciToCode[bci] = beforeGen
			bci += instruction.sizeInBytes()
			when (instruction) {
				is StoreInstruction -> {
					procedure.MOVIqq(
						EBCRegisters.R1, false, null,
						data.unInitBase
					)
					when (val kind = instruction.typeKind()) {
						TypeKind.REFERENCE -> {
							val (n, c) = data.allocator.getOrAllocateNatural(instruction.slot())
							procedure.POPn(EBCRegisters.R1, true, naturalIndex16(false, n, c))
						}

						TypeKind.LONG -> {
							val (n, c) = data.allocator.getOrAllocate64(instruction.slot())
							procedure.POP64(EBCRegisters.R1, true, naturalIndex16(false, n, c))
						}

						TypeKind.INT -> {
							val (n, c) = data.allocator.getOrAllocate32(instruction.slot())
							procedure.POP32(EBCRegisters.R1, true, naturalIndex16(false, n, c))
						}

						TypeKind.FLOAT, TypeKind.DOUBLE -> TODO("Floating point")
						else -> throw InternalError("Illegal kind for store ($kind), ${instruction.opcode()}")
					}
				}

				is LoadInstruction -> {
					val (n, c) = data.allocator[instruction.slot()]
					when (val kind = instruction.typeKind()) {
						TypeKind.REFERENCE -> {
							procedure.MOVIqq(
								EBCRegisters.R2, false, null,
								data.unInitBase
							)
							procedure.PUSHn(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						}

						TypeKind.LONG -> {
							procedure.MOVIqq(
								EBCRegisters.R2, false, null,
								data.unInitBase
							)
							procedure.PUSH64(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						}

						TypeKind.INT -> {
							procedure.MOVIqq(
								EBCRegisters.R2, false, null,
								data.unInitBase
							)
							procedure.PUSH32(EBCRegisters.R2, true, naturalIndex16(false, n, c))
						}

						else -> throw NotImplementedError("Unknown type kind $kind ($instruction)")
					}
				}

				is ConstantInstruction -> when (val kind = instruction.typeKind()) {
					TypeKind.REFERENCE -> @Suppress(
						"IMPOSSIBLE_IS_CHECK_WARNING"
					) when (val value = instruction.constantValue()) {
						is String -> {
							val addr = data.allocator.getOrAllocateString(value)
							procedure.MOVIqq(EBCRegisters.R2, false, null, addr)
							procedure.PUSHn(EBCRegisters.R2, false, null)
						}

						is DynamicConstantDesc<*> if value.constantName() == "_" -> {
							procedure.MOVIqw(EBCRegisters.R1, false, null, 0u)
							// TODO: Unsafe (>64-bits)
							procedure.PUSHn(EBCRegisters.R1, false, null)
						}

						else -> throw NotImplementedError("Unknown reference type $value ($instruction)")
					}

					TypeKind.LONG -> {
						@Suppress("CAST_NEVER_SUCCEEDS")
						procedure.MOVIqq(EBCRegisters.R2, false, null, (instruction.constantValue() as Long).toULong())
						procedure.PUSH64(EBCRegisters.R2, false, null)
					}

					TypeKind.INT -> {
						@Suppress("CAST_NEVER_SUCCEEDS")
						procedure.MOVIqd(EBCRegisters.R2, false, null, (instruction.constantValue() as Int).toUInt())
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					else -> throw NotImplementedError("Unknown type kind $kind ($instruction)")
				}

				is BranchInstruction -> {
					when (instruction.opcode()) {
						Opcode.IFNONNULL, Opcode.IFNULL -> {
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

						Opcode.IFGE, Opcode.IFLT -> {
							procedure.POP32(EBCRegisters.R2, false, null)
							procedure.CMPI32wgte(
								EBCRegisters.R2, false, null,
								0u
							)
						}

						Opcode.IFLE, Opcode.IFGT -> {
							procedure.POP32(EBCRegisters.R2, false, null)
							procedure.CMPI32wlte(
								EBCRegisters.R2, false, null,
								0u
							)
						}

						Opcode.IFNE, Opcode.IFEQ -> {
							procedure.POP32(EBCRegisters.R2, false, null)
							procedure.CMPI32weq(
								EBCRegisters.R2, false, null,
								0u
							)
						}

						Opcode.GOTO -> {}
						else -> throw NotImplementedError("Unknown branch element $instruction")
					}

					branchingLocations[procedure.output.size] = instruction
					procedure.output += ByteArray(8) // TODO: Use a JMP8 optimization where possible
				}

				is IncrementInstruction -> {
					val (n, c) = data.allocator[instruction.slot()]
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						data.unInitBase
					)
					procedure.MOVdw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R3, true, naturalIndex16(false, n, c)
					)
					val a = instruction.constant()
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

				is ConvertInstruction -> when (val opcode = instruction.opcode()) {
					Opcode.I2B -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.EXTNDB32(
							EBCRegisters.R2, false,
							EBCRegisters.R2, false, null
						)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					Opcode.I2C -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.MOVIdw(EBCRegisters.R3, false, null, 0u)
						procedure.MOVww(EBCRegisters.R3, false, null, EBCRegisters.R2, false, null)
						procedure.PUSH32(EBCRegisters.R3, false, null)
					}

					Opcode.I2L -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.EXTNDD64(
							EBCRegisters.R2, false,
							EBCRegisters.R2, false, null
						)
						procedure.PUSH64(EBCRegisters.R2, false, null)
					}

					Opcode.L2I -> {
						procedure.POP64(EBCRegisters.R2, false, null)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					else -> throw NotImplementedError("Unknown conversion opcode $opcode ($instruction)")
				}

				is OperatorInstruction -> when (val opcode = instruction.opcode()) {
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
					}

					Opcode.LNEG -> {
						procedure.POP64(EBCRegisters.R1, false, null)
						procedure.NEG64(
							EBCRegisters.R1, false,
							EBCRegisters.R1, false, null
						)
						procedure.PUSH64(EBCRegisters.R1, false, null)
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
					}

					Opcode.ARRAYLENGTH -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.PUSH32(EBCRegisters.R1, true, naturalIndex16(false, 0u, 4u))
					}

					else -> throw NotImplementedError("Unknown operator opcode $opcode ($instruction)")
				}

				is ReturnInstruction -> when (val kind = instruction.typeKind()) {
					TypeKind.REFERENCE -> {
						procedure.POPn(EBCRegisters.R7, false, null)
						procedure.RET()
					}

					TypeKind.LONG -> {
						procedure.POP64(EBCRegisters.R7, false, null)
						procedure.RET()
					}

					TypeKind.INT -> {
						procedure.POP32(EBCRegisters.R7, false, null)
						procedure.RET()
					}

					TypeKind.VOID -> procedure.RET()

					else -> throw NotImplementedError("Unknown return kind $kind ($instruction)")
				}

				is InvokeInstruction -> when (
					val descriptor = "${instruction.owner().name().stringValue()}." +
							"${instruction.name().stringValue()}${instruction.type().stringValue()}"
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
						data.allocator.nextFreeConstant += 4u
					}

					"${EBCIntrinsics.internalName}.toLong(L${Address.internalName};)J" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.PUSH64(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.accessN(L${Address.internalName};)L${Address.internalName};" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVnw(
							EBCRegisters.R1, false, null,
							EBCRegisters.R1, true, null
						)
						procedure.PUSHn(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.access64(L${Address.internalName};)J" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVqw(
							EBCRegisters.R1, false, null,
							EBCRegisters.R1, true, null
						)
						procedure.PUSH64(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.writeN(L${Address.internalName};L${Address.internalName};)V" -> {
						procedure.POPn(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVnw(
							EBCRegisters.R1, true, null,
							EBCRegisters.R2, false, null
						)
					}

					"${EBCIntrinsics.internalName}.write64(L${Address.internalName};J)V" -> {
						procedure.POP64(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVqw(
							EBCRegisters.R1, true, null,
							EBCRegisters.R2, false, null
						)
					}

					"${EBCIntrinsics.internalName}.write32(L${Address.internalName};I)V" -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVdw(
							EBCRegisters.R1, true, null,
							EBCRegisters.R2, false, null
						)
					}

					"${EBCIntrinsics.internalName}.write16(L${Address.internalName};S)V" -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVww(
							EBCRegisters.R1, true, null,
							EBCRegisters.R2, false, null
						)
					}

					"${EBCIntrinsics.internalName}.write8(L${Address.internalName};B)V" -> {
						procedure.POP32(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVbw(
							EBCRegisters.R1, true, null,
							EBCRegisters.R2, false, null
						)
					}

					"${EBCIntrinsics.internalName}.access32(L${Address.internalName};)I" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVdw(
							EBCRegisters.R1, false, null,
							EBCRegisters.R1, true, null
						)
						procedure.PUSH32(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.access16(L${Address.internalName};)S" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
						procedure.MOVww(
							EBCRegisters.R2, false, null,
							EBCRegisters.R1, true, null
						)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					"${EBCIntrinsics.internalName}.access8(L${Address.internalName};)B" -> {
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.MOVIdw(EBCRegisters.R2, false, null, 0u)
						procedure.MOVbw(
							EBCRegisters.R2, false, null,
							EBCRegisters.R1, true, null
						)
						procedure.PUSH32(EBCRegisters.R2, false, null)
					}

					"${EBCIntrinsics.internalName}.plus(L${Address.internalName};J)L${Address.internalName};" -> {
						procedure.POP64(EBCRegisters.R2, false, null)
						procedure.POPn(EBCRegisters.R1, false, null)
						procedure.ADD64(
							EBCRegisters.R1, false,
							EBCRegisters.R2, false, null
						)
						procedure.PUSHn(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.nat(L${Address.internalName};J)L${Address.internalName};" -> {
						procedure.POP64(EBCRegisters.R2, false, null)
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
					}

					"${EBCIntrinsics.internalName}.naturalSize()J" -> {
						procedure.MOVInw(
							EBCRegisters.R1, false, null,
							naturalIndex16(false, 1u, 0u)
						)
						procedure.PUSH64(EBCRegisters.R1, false, null)
					}

					"${EBCIntrinsics.internalName}.getAddress(Ljava/lang/Object;)L${Address.internalName};" -> {
						procedure.POP32(EBCRegisters.R1, false, null)
						procedure.POPn(EBCRegisters.R2, false, null)
						procedure.PUSHn(EBCRegisters.R2, false, null)
						procedure.PUSH32(EBCRegisters.R1, false, null)
					}

					else -> {
						// TODO: BETTER LINKING
						if (true || !functions.contains(descriptor)) {
							val classFile = instruction.owner().asInternalName().replace('/', '.')
							val output = cf.parse(
								ClassLoader.getSystemClassLoader()
									.loadClass(classFile)
									.getResource(classFile.substringAfterLast('.') + ".class")
								!!.readBytes()
							).methods().first {
								it.methodName() == instruction.name() && it.methodType() == instruction.type()
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
										for (parameterKind in parameters) when (parameterKind) {
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
//										procedure.MOVnw(
//											EBCRegisters.R0, false, null,
//											EBCRegisters.R0, false,
//											naturalIndex16(true, 0u, 32u)
//										)
										var dropNatural = 0u
										var dropConstant = 0u //32u
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
											EBCRegisters.R0, false,
											naturalIndex16(false, dropNatural, dropConstant)
										)
										when (val kind = TypeKind.from(it.methodTypeSymbol().returnType())) {
											TypeKind.LONG -> {
												procedure.PUSH64(EBCRegisters.R7, false, null)
											}

											TypeKind.INT -> {
												procedure.PUSH32(EBCRegisters.R7, false, null)
											}

											TypeKind.VOID -> {}
											else -> throw NotImplementedError("Unknown return kind $kind")
										}
										println(
											EBCDisassembly.diassemble(
												procedure.output.sliceArray(beforeGen..<procedure.output.size)
											)
										)
										continue
									} else throw NotImplementedError("Native function $descriptor")
								}
								println("---")
								compileMethod(
									it.methodTypeSymbol(),
									it.findAttribute(Attributes.code()).get(),
									EBCCompilerData(
										data.codeBase, data.unInitBase, data.initBase,
										EBCVariableAllocator(
											data.allocator.nextFreeStringPosition,
											data.allocator.nextFreeNatural,
											data.allocator.nextFreeConstant
										),
										data.bootSvcAllocatePool
									),
									false
								).also { println("---") }
							}
							if (output.initializedData.isNotEmpty()) TODO("INIT EXTRA")
							functions[descriptor] = output.code
						}
						callingLocations[procedure.output.size] = descriptor
						procedure.output += ByteArray(8)
						var dropNatural = 0u
						var dropConstant = 0u
						for (parameter in instruction.typeSymbol().parameterList()) {
							when (val kind = TypeKind.from(parameter)) {
								TypeKind.REFERENCE -> dropNatural++
								TypeKind.INT, TypeKind.SHORT, TypeKind.BYTE, TypeKind.BOOLEAN -> dropConstant += 4u
								TypeKind.LONG -> dropConstant += 8u
								else -> throw NotImplementedError("Unknown parameter kind $kind")
							}
						}
						procedure.MOVnw(
							EBCRegisters.R0, false, null,
							EBCRegisters.R0, false, naturalIndex16(false, dropNatural, dropConstant)
						)
						when (val kind = TypeKind.from(instruction.typeSymbol().returnType())) {
							TypeKind.REFERENCE -> {
								procedure.PUSHn(EBCRegisters.R7, false, null)
							}

							TypeKind.LONG -> {
								procedure.PUSH64(EBCRegisters.R7, false, null)
							}

							TypeKind.INT, TypeKind.BYTE -> {
								procedure.PUSH32(EBCRegisters.R7, false, null)
							}

							TypeKind.VOID -> {}
							else -> throw NotImplementedError("Unknown return type $kind ($instruction)")
						}
					}
				}

				is StackInstruction -> when (instruction.opcode()) {
					Opcode.POP -> {
						when (frame.stack.last()) {
							StackElement.Primitive.INT, StackElement.Primitive.FLOAT -> procedure.POP32(
								EBCRegisters.R1, false, null
							)

							StackElement.Primitive.REFERENCE -> procedure.POPn(
								EBCRegisters.R1, false, null
							)

							else -> throw InternalError()
						}
					}

//					Opcode.DUP -> {
//						procedure.POP32(EBCRegisters.R2, false, null)
//						procedure.CMPI32weq(
//							EBCRegisters.R2, false, null,
//							REFERENCE
//						)
//						procedure.branch({ t ->
//							t.POPn(EBCRegisters.R1, false, null)
//							t.PUSHn(EBCRegisters.R1, false, null)
//							t.PUSH32(EBCRegisters.R2, false, null)
//							t.PUSHn(EBCRegisters.R1, false, null)
//							t.PUSH32(EBCRegisters.R2, false, null)
//						}, { f ->
//							f.POP32(EBCRegisters.R1, false, null)
//							f.PUSH32(EBCRegisters.R1, false, null)
//							f.PUSH32(EBCRegisters.R2, false, null)
//							f.PUSH32(EBCRegisters.R1, false, null)
//							f.PUSH32(EBCRegisters.R2, false, null)
//						})
//					}

					else -> throw NotImplementedError("Unknown stack element $instruction")
				}

				is NewReferenceArrayInstruction -> {
					val (apN, apC) = data.bootSvcAllocatePool ?: throw InternalError(
						"Boot services allocate pool unset"
					)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R1, false,
						EBCRegisters.R1, false, null
					)

					procedure.MOVIqq(
						EBCRegisters.R2, false, null,
						data.unInitBase
					)
					procedure.MOVnw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R2, false, naturalIndex16(false, apN + 1u, apC)
					)
					procedure.PUSHn(EBCRegisters.R2, false, null)
					procedure.MOVInw(
						EBCRegisters.R3, false, null,
						naturalIndex16(false, 1u, 0u)
					)
					procedure.MOVqw(
						EBCRegisters.R4, false, null,
						EBCRegisters.R1, false, null
					)
					procedure.MUL64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.MOVIqw(EBCRegisters.R3, false, null, 8u)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.MOVIqw(EBCRegisters.R1, false, null, 2u)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.CALL32(
						EBCRegisters.R2, true,
						relative = false, native = true,
						naturalIndex32(true, 1u, 0u)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false, null,
						EBCRegisters.R0, false, naturalIndex16(false, 3u, 0u)
					)
					procedure.MOVnw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R2, true, null
					)
					procedure.MOVdw(
						EBCRegisters.R2, true, naturalIndex16(false, 0u, 4u),
						EBCRegisters.R4, false, null
					)
					procedure.PUSHn(EBCRegisters.R2, false, null)
					// TODO: Error checking
				}

				is NewPrimitiveArrayInstruction -> {
					val (apN, apC) = data.bootSvcAllocatePool ?: throw InternalError(
						"Boot services allocate pool unset"
					)
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R1, false,
						EBCRegisters.R1, false, null
					)

					procedure.MOVIqq(
						EBCRegisters.R2, false, null,
						data.unInitBase
					)
					procedure.MOVnw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R2, false, naturalIndex16(false, apN + 1u, apC)
					)
					procedure.PUSHn(EBCRegisters.R2, false, null)
					procedure.MOVIqw(
						EBCRegisters.R3, false, null,
						when (val kind = instruction.typeKind()) {
							TypeKind.LONG -> 8u
							TypeKind.SHORT, TypeKind.CHAR -> 2u
							TypeKind.BYTE -> 1u
							else -> throw NotImplementedError("Unknown array type kind $kind")
						}
					)
					procedure.MOVqw(
						EBCRegisters.R4, false, null,
						EBCRegisters.R1, false, null
					)
					procedure.MUL64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.MOVIqw(EBCRegisters.R3, false, null, 8u)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.MOVIqw(EBCRegisters.R1, false, null, 2u)
					procedure.PUSHn(EBCRegisters.R1, false, null)
					procedure.CALL32(
						EBCRegisters.R2, true,
						relative = false, native = true,
						naturalIndex32(true, 1u, 0u)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false, null,
						EBCRegisters.R0, false, naturalIndex16(false, 3u, 0u)
					)
					procedure.MOVnw(
						EBCRegisters.R2, false, null,
						EBCRegisters.R2, true, null
					)
					procedure.MOVdw(
						EBCRegisters.R2, true, naturalIndex16(false, 0u, 4u),
						EBCRegisters.R4, false, null
					)
					procedure.PUSHn(EBCRegisters.R2, false, null)
					// TODO: Error checking
				}

				// Value, Index, Array
				is ArrayStoreInstruction -> {
					when (val kind = instruction.typeKind()) {
						TypeKind.LONG -> {
							procedure.POP64(EBCRegisters.R1, false, null)
						}

						TypeKind.SHORT, TypeKind.CHAR, TypeKind.BYTE -> {
							procedure.POP32(EBCRegisters.R1, false, null)
						}

						TypeKind.REFERENCE -> {
							procedure.POPn(EBCRegisters.R1, false, null)
						}

						else -> throw NotImplementedError("Unknown array type kind $kind")
					}

					procedure.POP32(EBCRegisters.R2, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R2, false,
						EBCRegisters.R2, false, null
					)
					procedure.POPn(EBCRegisters.R3, false, null)

					if (instruction.typeKind() == TypeKind.REFERENCE) procedure.MOVInw(
						EBCRegisters.R4, false, null,
						naturalIndex16(false, 1u, 0u)
					) else procedure.MOVIqw(
						EBCRegisters.R4, false, null,
						when (val kind = instruction.typeKind()) {
							TypeKind.LONG -> 8u
							TypeKind.SHORT, TypeKind.CHAR -> 2u
							TypeKind.BYTE -> 1u
							else -> throw NotImplementedError("Unknown array type kind $kind")
						}
					)
					procedure.MUL64(
						EBCRegisters.R2, false,
						EBCRegisters.R4, false, null
					)
					procedure.MOVIqw(EBCRegisters.R4, false, null, 8u)
					procedure.ADD64(
						EBCRegisters.R2, false,
						EBCRegisters.R4, false, null
					)
					procedure.ADD64(
						EBCRegisters.R3, false,
						EBCRegisters.R2, false, null
					)
					when (val kind = instruction.typeKind()) {
						TypeKind.LONG -> procedure::MOVqw
						TypeKind.SHORT, TypeKind.CHAR -> procedure::MOVww
						TypeKind.BYTE -> procedure::MOVbw
						TypeKind.REFERENCE -> procedure::MOVnw
						else -> throw NotImplementedError("Unknown array type kind $kind")
					}(
						EBCRegisters.R3, true, null,
						EBCRegisters.R1, false, null
					)
					// TODO: Error checking
				}

				// Index, Array -> Value
				is ArrayLoadInstruction -> {
					procedure.POP32(EBCRegisters.R1, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R1, false,
						EBCRegisters.R1, false, null
					)
					procedure.POPn(EBCRegisters.R2, false, null)
					if (instruction.typeKind() == TypeKind.REFERENCE) procedure.MOVInw(
						EBCRegisters.R3, false, null,
						naturalIndex16(false, 1u, 0u)
					) else procedure.MOVIqw(
						EBCRegisters.R3, false, null,
						when (val kind = instruction.typeKind()) {
							TypeKind.LONG -> 8u
							TypeKind.SHORT, TypeKind.CHAR -> 2u
							TypeKind.BYTE -> 1u
							else -> throw NotImplementedError("Unknown array type kind $kind")
						}
					)
					procedure.MUL64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.MOVIqw(EBCRegisters.R3, false, null, 8u)
					procedure.ADD64(
						EBCRegisters.R1, false,
						EBCRegisters.R3, false, null
					)
					procedure.ADD64(
						EBCRegisters.R2, false,
						EBCRegisters.R1, false, null
					)
					when (val kind = instruction.typeKind()) {
						TypeKind.LONG -> {
							procedure.PUSH64(EBCRegisters.R2, true, null)
						}

						TypeKind.SHORT, TypeKind.CHAR, TypeKind.BYTE -> {
							when (kind) {
								TypeKind.SHORT, TypeKind.CHAR -> procedure::EXTNDW32
								TypeKind.BYTE -> procedure::EXTNDB32
							}(
								EBCRegisters.R2, false,
								EBCRegisters.R2, true, null
							)
							procedure.PUSH32(EBCRegisters.R2, false, null)
						}

						TypeKind.REFERENCE -> {
							procedure.PUSHn(EBCRegisters.R2, true, null)
						}

						else -> throw NotImplementedError("Unknown array type kind $kind")
					}
					// TODO: Error checking
				}

				is TypeCheckInstruction -> println("*** TODO: TYPECHECKING")
				else -> throw NotImplementedError("Unknown compile element $instruction")
			}
			println(EBCDisassembly.diassemble(procedure.output.sliceArray(beforeGen..<procedure.output.size)))
		}
		for ((location, instruction) in branchingLocations) {
			val jumpLocation = bciToCode[code.labelToBci(instruction.target())]!!
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

				Opcode.IFNE, Opcode.IF_ICMPNE, Opcode.IF_ICMPLT, Opcode.IF_ICMPGT, Opcode.IFGT -> infill.JMP32(
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
		var localProcedureEdge = procedure.output.size
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
		return EBCCompilationOutput(
			procedure.output,
			data.allocator.strings
		)
	}
}