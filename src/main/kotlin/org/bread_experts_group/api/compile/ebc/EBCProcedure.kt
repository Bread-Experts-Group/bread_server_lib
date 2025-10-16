package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.api.compile.ebc.efi.EFIMemoryType
import org.bread_experts_group.api.compile.ebc.efi.EFISystemTable
import org.bread_experts_group.normalize
import java.lang.classfile.*
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.constantpool.LongEntry
import java.lang.classfile.constantpool.StringEntry
import java.lang.classfile.instruction.*
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import kotlin.reflect.KClass

// RETURN VALUES: R7 (8Bytes or less)
// PARAMETERS: STACK
// R0 STACK POINTER
// R1 / R2 / R3 CALL PRESERVED
// R4 / R5 / R6 / R7 NOT PRESERVED
@Suppress("FunctionName")
class EBCProcedure {
	var output = byteArrayOf()

	companion object {
		fun naturalIndex16(
			negative: Boolean,
			naturalUnits: UInt,
			constantUnits: UInt
		): UShort {
			val encoded = if (negative) 1u shl 15 else 0u
			var naturalRemainder = naturalUnits
			var naturalBits = 0u
			while (naturalRemainder > 0u) {
				naturalRemainder = naturalRemainder shr 1
				naturalBits++
			}
			naturalBits = normalize(naturalBits, 2u)
			var constantRemainder = constantUnits
			var constantBits = 0
			while (constantRemainder > 0u) {
				constantRemainder = constantRemainder shr 1
				constantBits++
			}
			if (constantBits.toUInt() + naturalBits > 12u)
				throw IllegalArgumentException("Natural / constant indices not encodable")
			return (encoded or ((naturalBits / 2u) shl 12) or
					(constantUnits shl naturalBits.toInt()) or naturalUnits).toUShort()
		}

		fun naturalIndex32(
			negative: Boolean,
			naturalUnits: UInt,
			constantUnits: UInt
		): UInt {
			val encoded = if (negative) 1u shl 31 else 0u
			var naturalRemainder = naturalUnits
			var naturalBits = 0u
			while (naturalRemainder > 0u) {
				naturalRemainder = naturalRemainder shr 1
				naturalBits++
			}
			naturalBits = normalize(naturalBits, 4u)
			var constantRemainder = constantUnits
			var constantBits = 0
			while (constantRemainder > 0u) {
				constantRemainder = constantRemainder shr 1
				constantBits++
			}
			if (constantBits.toUInt() + naturalBits > 28u)
				throw IllegalArgumentException("Natural / constant indices not encodable")
			return (encoded or ((naturalBits / 4u) shl 28) or
					(constantUnits shl naturalBits.toInt()) or naturalUnits)
		}

		private val cf = ClassFile.of()
		fun compile(
			clazz: KClass<*>, codeSource: Path,
			codeBase: ULong, initBase: ULong, unInitBase: ULong
		): Array<ByteArray> {
			if (clazz.objectInstance == null) throw IllegalArgumentException("$clazz must be an object.")
			val efiMethod = cf.parse(
				codeSource.resolve(clazz.java.name.replace('.', '/') + ".class")
			).methods().firstOrNull {
				(it.flags().flagsMask() and ACC_PUBLIC) != 0 && it.methodType().equalsString(
					"(L${MemorySegment::class.java.name.replace('.', '/')};" +
							"L${EFISystemTable::class.java.name.replace('.', '/')};)J"
				) && it.methodName().equalsString("efiMain")
			}
			if (efiMethod == null) throw IllegalArgumentException(
				"No public efiMain(${MemorySegment::class}, ${EFISystemTable::class}): Long function " +
						"was found within [$clazz]."
			)
			val code = efiMethod.code().orElseThrow { IllegalArgumentException("$efiMethod must contain code.") }
			val procedure = EBCProcedure()
			var data = byteArrayOf()
			val strings = mutableMapOf<String, ULong>()
			val variables = mutableMapOf<Int, Pair<UInt, UInt>>()
			val labelTargets = mutableMapOf<Label, ULong>()
			val branchLocations = mutableMapOf<ULong, Label>()
			procedure.MOVIxq(
				EBCRegisters.R6, false, null,
				EBCMoveTypes.QUADWORD_64, unInitBase
			)
			procedure.MOVnw(
				EBCRegisters.R6, true,
				EBCRegisters.R0, true,
				null, naturalIndex16(
					false,
					0u, 16u
				)
			)
			variables[1] = 0u to 0u
			procedure.MOVnw(
				EBCRegisters.R6, true,
				EBCRegisters.R0, true,
				naturalIndex16(
					false,
					1u, 0u
				), naturalIndex16(
					false,
					1u, 16u
				)
			)
			variables[2] = 1u to 0u
			var variablesFreeConstant = 0u
			var variablesFreeNatural = 3u
			// VARIABLE MAP
			// [0] = Image Handle
			// [1] = System Table
			// [2] = Allocator Storage Address
			for (element in code) when (element) {
				is LoadInstruction -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64, unInitBase
					)
					val (natural, constant) = variables.getValue(element.slot())
					when (element.typeKind()) {
						TypeKind.INT -> procedure.PUSH(
							false,
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)

						TypeKind.LONG -> procedure.PUSH(
							true,
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)

						TypeKind.REFERENCE -> procedure.PUSHn(
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)

						else -> throw IllegalArgumentException("Unsupported type: ${element.typeKind()}")
					}
				}

				is StoreInstruction -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64, unInitBase
					)
					when (element.typeKind()) {
						TypeKind.INT -> {
							val (natural, constant) = variables.getOrPut(element.slot()) {
								val free = variablesFreeNatural to variablesFreeConstant
								variablesFreeConstant += 4u
								free
							}
							procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null)
							procedure.MOVdw(
								EBCRegisters.R6, true,
								EBCRegisters.R5, false,
								naturalIndex16(false, natural, constant),
								null
							)
						}

						TypeKind.LONG -> {
							val (natural, constant) = variables.getOrPut(element.slot()) {
								val free = variablesFreeNatural to variablesFreeConstant
								variablesFreeConstant += 8u
								free
							}
							procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
							procedure.MOVqw(
								EBCRegisters.R6, true,
								EBCRegisters.R5, false,
								naturalIndex16(false, natural, constant),
								null
							)
						}

						TypeKind.REFERENCE -> {
							val (natural, constant) = variables.getOrPut(element.slot()) {
								val free = variablesFreeNatural to variablesFreeConstant
								variablesFreeNatural += 1u
								free
							}
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
					val (natural, constant) = variables.getValue(element.slot())
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.DOUBLEWORD_32, element.constant().toLong().toULong()
					)
					procedure.MOVIxq(
						EBCRegisters.R4, false, null,
						EBCMoveTypes.QUADWORD_64, unInitBase
					)
					procedure.MOVdw(
						EBCRegisters.R5, false,
						EBCRegisters.R4, true,
						null, naturalIndex16(
							false,
							natural, constant
						)
					)
					procedure.ADD(
						false,
						EBCRegisters.R5, false,
						EBCRegisters.R6, false,
						null
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
						procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.ADD(
							false,
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH(false, EBCRegisters.R6, false, null)
					}

					Opcode.LADD -> {
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.ADD(
							true,
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH(true, EBCRegisters.R6, false, null)
					}

					Opcode.LMUL -> {
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.MUL(
							true,
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH(true, EBCRegisters.R6, false, null)
					}

					Opcode.LAND -> {
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.AND(
							true,
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH(true, EBCRegisters.R6, false, null)
					}

					Opcode.LUSHR -> {
						procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.EXTNDD(
							true,
							EBCRegisters.R5, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.SHR(
							true,
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH(true, EBCRegisters.R6, false, null)
					}

					else -> throw IllegalArgumentException("Unknown operator opcode: ${element.opcode()}")
				}

				is BranchInstruction -> when (element.opcode()) {
					Opcode.IF_ICMPGE -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null)
						procedure.CMPgte(
							false,
							EBCRegisters.R5,
							EBCRegisters.R6, false,
							null
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
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null)
						procedure.CMPlte(
							false,
							EBCRegisters.R5,
							EBCRegisters.R6, false,
							null
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

					Opcode.IFGT -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						// TODO : WARNING! THIS DOES X >= 0, NOT X > 0
						procedure.CMPIgte(
							false,
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
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.CMPIlte(
							false,
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
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.DOUBLEWORD_32,
						element.constantValue().toInt().toULong()
					)
					procedure.PUSH(false, EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.INT -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.DOUBLEWORD_32,
						@Suppress("KotlinConstantConditions")
						(element.constantValue() as Integer).toInt().toULong()
					)
					procedure.PUSH(false, EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.INT -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.DOUBLEWORD_32,
						@Suppress("KotlinConstantConditions")
						(element.constantValue() as Integer).toInt().toULong()
					)
					procedure.PUSH(false, EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.LONG -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64,
						@Suppress("KotlinConstantConditions")
						(element.constantValue() as java.lang.Long).toLong().toULong()
					)
					procedure.PUSH(true, EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.LONG -> {
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64,
						(element.constantEntry() as LongEntry).longValue().toULong()
					)
					procedure.PUSH(true, EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.LoadConstantInstruction if element.constantEntry() is StringEntry -> {
					val dataPosition = strings.getOrPut((element.constantEntry() as StringEntry).stringValue()) {
						val saved = initBase + data.size.toULong()
						data += ((element.constantEntry() as StringEntry).stringValue() + "\u0000")
							.toByteArray(Charsets.UTF_16LE)
						saved
					}
					procedure.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64,
						dataPosition
					)
					procedure.PUSH(true, EBCRegisters.R6, false, null)
				}

				is NewObjectInstruction -> when (val desc = element.className().asInternalName()) {
					else -> throw IllegalArgumentException("No translation for [$desc]")
				}

				is FieldInstruction -> when (
					val desc = element.owner().name().stringValue() + '.' + element.name()
				) {
					"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiLoaderData" -> {
						procedure.MOVIxq(
							EBCRegisters.R6, false, null,
							EBCMoveTypes.DOUBLEWORD_32, EFIMemoryType.EfiLoaderData.id.toULong()
						)
						procedure.PUSH(false, EBCRegisters.R6, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiBootServicesData" -> {
						procedure.MOVIxq(
							EBCRegisters.R6, false, null,
							EBCMoveTypes.DOUBLEWORD_32, EFIMemoryType.EfiBootServicesData.id.toULong()
						)
						procedure.PUSH(false, EBCRegisters.R6, false, null)
					}

					"java/lang/foreign/ValueLayout.JAVA_LONG" -> {
					}

					"java/lang/foreign/ValueLayout.JAVA_INT" -> {
					}

					"java/lang/foreign/ValueLayout.JAVA_SHORT" -> {
					}

					"java/lang/foreign/ValueLayout.JAVA_BYTE" -> {
					}

					else -> throw IllegalArgumentException("No translation for [$desc]")
				}

				is InvokeInstruction -> @Suppress("LongLine") when (
					val desc = element.owner().name().stringValue() + '.' + element.name() + element.type()
				) {
					"kotlin/jvm/internal/Intrinsics.checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.POPn(EBCRegisters.R6, false, null)
					}

					"java/lang/foreign/MemorySegment.address()J" -> {
					}

					$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/ValueLayout$OfLong;J)J" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.POPn(EBCRegisters.R5, false, null)
						procedure.ADD(
							true,
							EBCRegisters.R5, false,
							EBCRegisters.R6, false,
							null
						)
						procedure.PUSH(
							true,
							EBCRegisters.R5, true,
							null
						)
					}

					$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfInt;JI)V" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POPn(EBCRegisters.R4, false, null)
						procedure.ADD(
							true,
							EBCRegisters.R4, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.MOVdw(
							EBCRegisters.R4, true,
							EBCRegisters.R6, false,
							null, null
						)
					}

					$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfShort;JS)V" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POPn(EBCRegisters.R4, false, null)
						procedure.ADD(
							true,
							EBCRegisters.R4, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.MOVww(
							EBCRegisters.R4, true,
							EBCRegisters.R6, false,
							null, null
						)
					}

					$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfByte;JB)V" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.POP(EBCRegisters.R5, false, b64 = true, operand1Index = null)
						procedure.POPn(EBCRegisters.R4, false, null)
						procedure.ADD(
							true,
							EBCRegisters.R4, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.MOVbw(
							EBCRegisters.R4, true,
							EBCRegisters.R6, false,
							null, null
						)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getFirmwareVendor()Ljava/lang/foreign/MemorySegment;" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSHn(
							EBCRegisters.R6, true,
							naturalIndex16(
								false,
								0u, 24u
							)
						)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getFirmwareRevision()I" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSH(
							false,
							EBCRegisters.R6, true,
							naturalIndex16(
								false,
								1u, 24u
							)
						)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getHeader()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;" -> {
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFITableHeader.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getConOut()Lorg/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol;" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSHn(
							EBCRegisters.R6, true,
							naturalIndex16(
								false,
								4u, 32u
							)
						)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.getSegment()Ljava/lang/foreign/MemorySegment;" -> {
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.reset(Z)J" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
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
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSH(true, EBCRegisters.R7, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputString(Ljava/lang/String;)J",
					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputStringAt(Ljava/lang/foreign/MemorySegment;)J" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
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
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSH(true, EBCRegisters.R7, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISystemTable.getBootServices()Lorg/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable;" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSHn(
							EBCRegisters.R6, true,
							naturalIndex16(
								false,
								8u, 32u
							)
						)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.allocatePool(Lorg/bread_experts_group/api/compile/ebc/efi/EFIMemoryType;J)Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;" -> {
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null) // Size
						procedure.POP(EBCRegisters.R5, false, b64 = false, operand1Index = null) // PoolType
						procedure.POPn(EBCRegisters.R4, false, null) // BootServices
						procedure.MOVIxq(
							EBCRegisters.R3, false, null,
							EBCMoveTypes.QUADWORD_64, unInitBase
						)
						procedure.MOVqw(
							EBCRegisters.R3, false,
							EBCRegisters.R3, false,
							null, naturalIndex16(
								false,
								2u, 0u
							)
						)
						procedure.PUSHn(EBCRegisters.R3, false, null)
						procedure.PUSHn(EBCRegisters.R6, false, null)
						procedure.PUSHn(EBCRegisters.R5, false, null)
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
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.PUSH(true, EBCRegisters.R7, false, null)
						procedure.PUSHn(EBCRegisters.R3, true, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getData()Ljava/lang/foreign/MemorySegment;" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POP(EBCRegisters.R5, operand1Indirect = false, b64 = true, operand1Index = null)
						procedure.PUSHn(EBCRegisters.R6, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getStatus()J" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.getHeader()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;" -> {}
					"org/bread_experts_group/api/compile/ebc/efi/EFITableHeader.getSignature()J" -> {
						procedure.POPn(EBCRegisters.R6, false, null) // BootServices
						procedure.PUSH(
							true,
							EBCRegisters.R6, true,
							naturalIndex16(
								false,
								0u, 0u
							)
						)
					}

					else -> throw IllegalArgumentException("No translation for [$desc]")
				}

				is ConvertInstruction -> when (element.opcode()) {
					Opcode.I2L -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.EXTNDD(
							true,
							EBCRegisters.R6, false,
							EBCRegisters.R6, false,
							null
						)
						procedure.PUSH(true, EBCRegisters.R6, false, null)
					}

					Opcode.L2I -> {
						procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
						procedure.PUSH(false, EBCRegisters.R6, false, null)
					}


					Opcode.I2B -> {
						procedure.POP(EBCRegisters.R6, false, b64 = false, operand1Index = null)
						procedure.EXTNDB(
							false,
							EBCRegisters.R6, false,
							EBCRegisters.R6, false,
							null
						)
						procedure.PUSH(false, EBCRegisters.R6, false, null)
					}

					else -> throw IllegalArgumentException(element.opcode().toString())
				}

				is StackInstruction if element.opcode() == Opcode.POP2 -> {
					procedure.POP(EBCRegisters.R6, false, b64 = true, operand1Index = null)
				}

				is ReturnInstruction -> {
					procedure.POP(
						EBCRegisters.R7,
						operand1Indirect = false, b64 = true, operand1Index = null
					)
					procedure.RET()
				}

				is Label -> labelTargets[element] = procedure.output.size.toULong()
				is Instruction -> print("* ")
				else -> print("--- ")
			}.also { println("$element") }
			branchLocations.forEach { (location, label) ->
				val target = labelTargets[label]!!
				val data = EBCProcedure()
					.MOVIxq(
						EBCRegisters.R6, false, null,
						EBCMoveTypes.QUADWORD_64, target + codeBase
					)
					.output
				System.arraycopy(
					data, 0,
					procedure.output, location.toInt(),
					10
				)
			}
			return arrayOf(procedure.output, data)
		}
	}

	private val instructionBuffer: ByteBuffer = ByteBuffer.allocate(64).also {
		it.order(ByteOrder.LITTLE_ENDIAN)
	}

	private fun addInstruction() {
		instructionBuffer.flip()
		val data = ByteArray(instructionBuffer.limit())
		instructionBuffer.get(data)
		output += data
		instructionBuffer.clear()
	}

	fun ADD(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x0C or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun MUL(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x0E or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun AND(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x14 or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun SHR(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x18 or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CMPIlte(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		instructionBuffer.put((0x2E or (if (b64) 0b1000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or
					(if (operand1Index != null) 0b10000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun CMPIgte(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		instructionBuffer.put((0x2F or (if (b64) 0b1000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or
					(if (operand1Index != null) 0b10000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun CMPgte(
		b64: Boolean,
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x07 or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CMPlte(
		b64: Boolean,
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x06 or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun EXTNDB(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x1A or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun EXTNDD(
		b64: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x1C or (if (b64) 0b1000000 else 0) or (if (operand2Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun JMP32(
		conditional: Boolean,
		conditionSet: Boolean,
		relative: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UInt?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x01 or (if (operand1Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (if (relative) 0b10000 else 0)
					or (if (conditionSet) 0b1000000 else 0) or (if (conditional) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putInt(operand1Index.toInt())
		addInstruction()
	}

	fun MOVbw(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x1D or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun MOVww(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x1E or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun MOVdw(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x1F or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun MOVqw(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x20 or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun MOVIxq(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UShort?,
		move: EBCMoveTypes, immediate: ULong
	): EBCProcedure = this.also {
		instructionBuffer.put(0xF7.toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (move.ordinal shl 4)
					or (if (operand1Index != null) 0b1000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVnw(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x32 or (if (operand2Index != null) 0b01000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b00001000 else 0) or
					(operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CALL32(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		relative: Boolean,
		native: Boolean,
		immediate: UInt?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x03 or (if (immediate != null) (1 shl 7) else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (if (relative) 0b10000 else 0)
					or (if (native) 0b100000 else 0)).toByte()
		)
		if (immediate != null) instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun POP(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		b64: Boolean,
		operand1Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x2C or (if (b64) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun POPn(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		immediate: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x36 or (if (immediate != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (immediate != null) instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun PUSH(
		b64: Boolean,
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		immediate: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x2B or (if (b64) 0b1000000 else 0) or (if (immediate != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (immediate != null) instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun PUSHn(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		immediate: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x35 or (if (immediate != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (immediate != null) instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun RET(): EBCProcedure = this.also {
		instructionBuffer.put(0x04)
		instructionBuffer.put(0x00)
		addInstruction()
	}

	fun STORESP(
		operand1: EBCRegisters,
		operand2: EBCVMDedicatedRegisters
	): EBCProcedure = this.also {
		instructionBuffer.put(0x2A)
		instructionBuffer.put((operand1.ordinal or (operand2.ordinal shl 4)).toByte())
	}
}