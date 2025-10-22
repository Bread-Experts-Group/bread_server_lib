package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.efi.EFIMemoryType
import org.bread_experts_group.api.compile.ebc.efi.EFISystemTable
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import org.bread_experts_group.logging.ColoredHandler
import java.lang.classfile.*
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.ClassFile.ACC_STATIC
import java.lang.classfile.constantpool.LongEntry
import java.lang.classfile.constantpool.StringEntry
import java.lang.classfile.instruction.*
import java.lang.constant.ConstantDescs
import java.lang.constant.DirectMethodHandleDesc
import java.lang.foreign.MemorySegment
import java.nio.file.Path
import java.util.*
import kotlin.reflect.KClass

object EBCJVMCompilation {
	private val cf = ClassFile.of()
	private val logger = ColoredHandler.newLogger("TMP logger")
	private val intrinsics = ServiceLoader.load(KotlinEBCIntrinsicProvider::class.java).fold(
		mutableMapOf<String, MutableMap<String,
				MutableMap<String, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit>>>()
	) { a, m ->
		m.intrinsics().forEach {
			val desc = (it.key as DirectMethodHandleDesc)
			val className = desc.owner().descriptorString().let { s -> s.substring(1, s.length - 1) }
			val methods = a.getOrPut(className) { mutableMapOf() }
			val descriptors = methods.getOrPut(desc.methodName()) { mutableMapOf() }
			descriptors[desc.lookupDescriptor()] = it.value
		}
		a
	}

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
								"L${EFISystemTable::class.java.name.replace('.', '/')};)J"
					) && it.methodName().equalsString("efiMain")
		}
		if (efiMethod == null) throw IllegalArgumentException(
			"No public static efiMain(${MemorySegment::class}, ${EFISystemTable::class}): Long function " +
					"was found within [$clazz]."
		)
		val code = efiMethod.code().orElseThrow { IllegalArgumentException("$efiMethod must contain code.") }
		return compileMethod(code, codeBase, initBase, unInitBase, instructionSpaceBase)
	}

	fun compileMethod(
		code: CodeModel,
		codeBase: ULong, initBase: ULong, unInitBase: ULong, instructionSpaceBase: ULong,
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
		for (localVariable in code.mapNotNull { it as? LocalVariable }) when (localVariable.typeSymbol()) {
			ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> variableAllocator.getOrAllocate32(
				localVariable.slot()
			)

			ConstantDescs.CD_long -> variableAllocator.getOrAllocate64((localVariable.slot()))
			else -> variableAllocator.getOrAllocateNatural(localVariable.slot())
		}
		var variableOffset = 0
		var stackNatural = 0u
		var stackConstant = 16u
		methodParent.methodTypeSymbol().parameterList().forEachIndexed { i, p ->
			// TODO: Instead of using try-catch, in the future, detect encodability
			val (natural, constant) = variableAllocator[i + variableOffset]
			when (p) {
				ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
					try {
						procedure.MOVdw(
							EBCRegisters.R6, true, naturalIndex16(
								false,
								natural, constant
							),
							EBCRegisters.R0, true, naturalIndex16(
								false,
								stackNatural, stackConstant
							)
						)
					} catch (_: IllegalArgumentException) {
						procedure.MOVdd(
							EBCRegisters.R6, true, naturalIndex32(
								false,
								natural, constant
							),
							EBCRegisters.R0, true, naturalIndex32(
								false,
								stackNatural, stackConstant
							)
						)
					}
					stackNatural += 1u
				}

				ConstantDescs.CD_long -> {
					variableOffset += 1
					try {
						procedure.MOVqw(
							EBCRegisters.R6, true, naturalIndex16(
								false,
								natural, constant
							),
							EBCRegisters.R0, true, naturalIndex16(
								false,
								stackNatural, stackConstant
							)
						)
					} catch (_: IllegalArgumentException) {
						TODO("ALPHA.")
					}
					stackConstant += 8u
				}

				else -> {
					try {
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
					} catch (_: IllegalArgumentException) {
						TODO("ALPHA.")
					}
					stackNatural += 1u
				}
			}
		}
		val (allocatorNatural, allocatorConstant) = variableAllocator.bumpNatural()
		val stack = EBCStackTracker(procedure)
		for (element in code) when (element) {
			is LoadInstruction -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					unInitBase
				)
				when (element.typeKind()) {
					// TODO: Instead of using try-catch, in the future, detect encodability
					TypeKind.INT -> {
						val (natural, constant) = variableAllocator.getOrAllocate32(element.slot())
						try {
							stack.PUSH32(
								EBCRegisters.R6, true,
								naturalIndex16(false, natural, constant)
							)
						} catch (_: IllegalArgumentException) {
							procedure.MOVqd(
								EBCRegisters.R6, false, null,
								EBCRegisters.R6, false, naturalIndex32(
									false,
									natural, constant
								)
							)
							stack.PUSH32(EBCRegisters.R6, true, null)
						}
					}

					TypeKind.LONG -> {
						val (natural, constant) = variableAllocator.getOrAllocate64(element.slot())
						try {
							stack.PUSH64(
								EBCRegisters.R6, true,
								naturalIndex16(false, natural, constant)
							)
						} catch (_: IllegalArgumentException) {
							TODO("ALPHA.")
						}
					}

					TypeKind.REFERENCE -> {
						val (natural, constant) = variableAllocator.getOrAllocateNatural(element.slot())
						try {
							stack.PUSHn(
								EBCRegisters.R6, true,
								naturalIndex16(false, natural, constant)
							)
						} catch (_: IllegalArgumentException) {
							TODO("ALPHA.")
						}
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
						val (natural, constant) = variableAllocator.getOrAllocate32(element.slot())
						stack.POP32(EBCRegisters.R5, false, null)
						procedure.MOVdw(
							EBCRegisters.R6, true, naturalIndex16(false, natural, constant),
							EBCRegisters.R5, false, null
						)
					}

					TypeKind.LONG -> {
						val (natural, constant) = variableAllocator.getOrAllocate64(element.slot())
						stack.POP64(EBCRegisters.R5, false, operand1Index = null)
						procedure.MOVqw(
							EBCRegisters.R6, true, naturalIndex16(false, natural, constant),
							EBCRegisters.R5, false, null
						)
					}

					TypeKind.REFERENCE -> {
						val (natural, constant) = variableAllocator.getOrAllocateNatural(element.slot())
						stack.POPn(EBCRegisters.R5, false, null)
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
					EBCRegisters.R5, false, null,
					EBCRegisters.R4, true, naturalIndex16(
						false,
						natural, constant
					)
				)
				procedure.ADD32(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				procedure.MOVdw(
					EBCRegisters.R4, true, naturalIndex16(
						false,
						natural, constant
					),
					EBCRegisters.R5, false, null
				)
			}

			is OperatorInstruction -> when (element.opcode()) {
				Opcode.IADD -> {
					stack.POP32(EBCRegisters.R5, false, null)
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.ADD32(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}

				Opcode.IAND -> {
					stack.POP32(EBCRegisters.R5, false, null)
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.AND32(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}

				Opcode.LADD -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.ADD64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LSUB -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.SUB64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LMUL -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.MUL64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LDIV -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.DIV64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LREM -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.MOD64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false, null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LAND -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.AND64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false,
						null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LUSHR -> {
					stack.POP32(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R5, false,
						EBCRegisters.R5, false, null
					)
					procedure.SHR64(
						EBCRegisters.R6, false,
						EBCRegisters.R5, false,
						null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.LCMP -> {
					stack.POP64(EBCRegisters.R5, false, null)
					stack.POP64(EBCRegisters.R6, false, null)
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
					stack.PUSH32(EBCRegisters.R6, false, null)
				}

				else -> throw IllegalArgumentException("Unknown operator opcode: ${element.opcode()}")
			}

			is BranchInstruction -> when (element.opcode()) {
				Opcode.IF_ICMPGE -> {
					stack.POP32(EBCRegisters.R6, false, null)
					stack.POP32(EBCRegisters.R5, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
					stack.POP32(EBCRegisters.R5, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
					stack.POP32(EBCRegisters.R5, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.CMPI32wgte(
						EBCRegisters.R6, false, null,
						0u
					)
					procedure.JMP32(
						conditional = true,
						conditionSet = false,
						relative = true,
						operand1 = EBCRegisters.R0,
						operand1Indirect = false,
						operand1Index = naturalIndex32(false, 0u, 22u)
					)
					procedure.CMPI32weq(
						EBCRegisters.R6, false, null,
						0u
					)
					procedure.JMP32(
						conditional = true,
						conditionSet = true,
						relative = true,
						operand1 = EBCRegisters.R0,
						operand1Indirect = false,
						operand1Index = naturalIndex32(false, 0u, 12u)
					)
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

				Opcode.IFGE -> {
					stack.POP32(EBCRegisters.R6, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
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
					stack.POP32(EBCRegisters.R6, false, null)
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
				stack.PUSH32(EBCRegisters.R6, false, null)
			}

			is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.INT -> {
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					(element.constantValue() as Integer).toInt().toUInt()
				)
				stack.PUSH32(EBCRegisters.R6, false, null)
			}

			is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.INT -> {
				procedure.MOVIdd(
					EBCRegisters.R6, false, null,
					(element.constantValue() as Integer).toInt().toUInt()
				)
				stack.PUSH32(EBCRegisters.R6, false, null)
			}

			is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.LONG -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					(element.constantValue() as java.lang.Long).toLong().toULong()
				)
				stack.PUSH64(EBCRegisters.R6, false, null)
			}

			is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.LONG -> {
				procedure.MOVIqq(
					EBCRegisters.R6, false, null,
					(element.constantEntry() as LongEntry).longValue().toULong()
				)
				stack.PUSH64(EBCRegisters.R6, false, null)
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
				stack.PUSHn(EBCRegisters.R6, false, null)
			}

			is FieldInstruction -> when (
				val desc = element.owner().name().stringValue() + '.' + element.name()
			) {
				"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiLoaderData" -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						EFIMemoryType.EfiLoaderData.id
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiBootServicesData" -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						EFIMemoryType.EfiBootServicesData.id
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
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
					stack.PUSHn(EBCRegisters.R6, false, null)
				}

				"org/bread_experts_group/api/compile/ebc/efi/EFIExample.INSTANCE" -> {
					stack.PUSHn(EBCRegisters.R6, false, null)
					logger.severe("!!!!!! WARNING `THIS` DOES NOT EXIST AT RUNTIME !!!!!!")
				}

				else -> throw IllegalArgumentException("No translation for [$desc]")
			}

			is InvokeInstruction -> {
				val methods = intrinsics[element.owner().name().stringValue()]
				val method = if (methods != null) {
					val descriptors = methods[element.name().stringValue()]
					if (descriptors != null) descriptors[element.type().stringValue()] else null
				} else null
				if (method != null) method(
					procedure, stack,
					EBCCompilerData(
						codeBase, unInitBase, initBase, instructionSpaceBase,
						allocatorNatural, allocatorConstant
					)
				) else {
					val localMethod = classParent.methods().firstOrNull {
						element.owner().name().contentEquals(classParent.thisClass().name()) &&
								element.name().contentEquals(it.methodName()) &&
								element.type().contentEquals(it.methodType())
					}
					if (localMethod != null) {
						callLocations[procedure.output.size.toULong()] = localMethod
						procedure.output += ByteArray(10)
						procedure.MOVqw(
							EBCRegisters.R5, false, null,
							EBCRegisters.R0, false, null
						)
						val parameters = localMethod.methodTypeSymbol().parameterList().asReversed()
						var offsetNatural = 0u
						var offsetConstant = 0u
						parameters.forEach { c ->
							when (c) {
								ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
									procedure.MOVIqw(EBCRegisters.R4, false, null, 0u)
									procedure.MOVdw(
										EBCRegisters.R4, false, null,
										EBCRegisters.R5, true, naturalIndex16(
											false,
											offsetNatural, offsetConstant
										)
									)
									offsetConstant += 4u
									stack.PUSHn(EBCRegisters.R4, false, null)
								}

								ConstantDescs.CD_long -> {
									procedure.MOVIqw(EBCRegisters.R4, false, null, 0u)
									procedure.MOVqw(
										EBCRegisters.R4, false, null,
										EBCRegisters.R5, true, naturalIndex16(
											false,
											offsetNatural, offsetConstant
										)
									)
									offsetConstant += 8u
									stack.PUSH64(EBCRegisters.R4, false, null)
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
									stack.PUSHn(EBCRegisters.R4, false, null)
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
									stack.expectNatural()
									(a.first + 1u) to a.second
								}

								ConstantDescs.CD_long -> {
									stack.expect64()
									a.first to (a.second + 8u)
								}

								else -> {
									stack.expectNatural()
									(a.first + 1u) to a.second
								}
							}
						}
						val (natural, constant) = parameters.fold(0u to 0u) { a, c ->
							when (c) {
								ConstantDescs.CD_byte, ConstantDescs.CD_short, ConstantDescs.CD_int -> {
									stack.expect32()
									a.first to (a.second + 4u)
								}

								ConstantDescs.CD_long -> {
									stack.expect64()
									a.first to (a.second + 8u)
								}

								else -> {
									stack.expectNatural()
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
						stack.PUSH64(EBCRegisters.R7, false, null)
					} else throw IllegalArgumentException(
						"No translation for [" +
								element.owner().name().stringValue() + '.' + element.name() + element.type() + "]"
					)
				}
			}

			is ConvertInstruction -> when (element.opcode()) {
				Opcode.I2L -> {
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDD64(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					stack.PUSH64(EBCRegisters.R6, false, null)
				}

				Opcode.L2I -> {
					stack.POP64(EBCRegisters.R6, false, null)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}


				Opcode.I2S -> {
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDW32(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}


				Opcode.I2B -> {
					stack.POP32(EBCRegisters.R6, false, null)
					procedure.EXTNDB32(
						EBCRegisters.R6, false,
						EBCRegisters.R6, false,
						null
					)
					stack.PUSH32(EBCRegisters.R6, false, null)
				}

				else -> throw IllegalArgumentException(element.opcode().toString())
			}

			is StackInstruction -> when (element.opcode()) {
				Opcode.POP -> {
					if (stack.last == EBCCompilerStackType.BIT_32) {
						stack.POP32(EBCRegisters.R6, false, null)
					} else {
						stack.POPn(EBCRegisters.R6, false, null)
					}
				}

				Opcode.POP2 -> when (stack.last) {
					EBCCompilerStackType.BIT_32 -> {
						stack.POP32(EBCRegisters.R6, false, null)
						stack.POP32(EBCRegisters.R6, false, null)
					}

					EBCCompilerStackType.NATURAL -> {
						stack.POPn(EBCRegisters.R6, false, null)
						stack.POPn(EBCRegisters.R6, false, null)
					}

					EBCCompilerStackType.BIT_64 -> {
						stack.POP64(EBCRegisters.R6, false, null)
					}
				}

				Opcode.DUP -> {
					if (stack.last == EBCCompilerStackType.BIT_32) {
						stack.POP32(EBCRegisters.R6, false, null)
						stack.PUSH32(EBCRegisters.R6, false, null)
						stack.PUSH32(EBCRegisters.R6, false, null)
					} else {
						stack.POPn(EBCRegisters.R6, false, null)
						stack.PUSHn(EBCRegisters.R6, false, null)
						stack.PUSHn(EBCRegisters.R6, false, null)
					}
				}

				else -> throw IllegalArgumentException(element.opcode().toString())
			}

			is ReturnInstruction -> {
				stack.POP64(EBCRegisters.R7, false, null)
				procedure.RET()
			}

			is Label -> labelTargets[element] = procedure.output.size.toULong()
			is Instruction -> logger.warning("Unknown instruction!")
			else -> logger.info("Pseudo-instruction:")
		}.also {
			logger.info { "$element" }
		}
//		if (stack.isNotEmpty()) logger.warning("Stack was not empty at compilation edge! $stack")
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
				instructionSpaceBase,
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