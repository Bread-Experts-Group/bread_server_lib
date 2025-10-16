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
			procedure.MOVIqq(
				EBCRegisters.R6, false, null,
				unInitBase
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
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						unInitBase
					)
					val (natural, constant) = variables.getValue(element.slot())
					when (element.typeKind()) {
						TypeKind.INT -> procedure.PUSH32(
							EBCRegisters.R6, true,
							naturalIndex16(false, natural, constant)
						)

						TypeKind.LONG -> procedure.PUSH64(
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
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						unInitBase
					)
					when (element.typeKind()) {
						TypeKind.INT -> {
							val (natural, constant) = variables.getOrPut(element.slot()) {
								val free = variablesFreeNatural to variablesFreeConstant
								variablesFreeConstant += 4u
								free
							}
							procedure.POP32(EBCRegisters.R5, false, null)
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
							procedure.POP64(EBCRegisters.R5, false, operand1Index = null)
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
						procedure.POP32(EBCRegisters.R5, false, null)
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.ADD32(
							EBCRegisters.R6, false,
							EBCRegisters.R5, false, null
						)
						procedure.PUSH32(EBCRegisters.R6, false, null)
					}

					Opcode.LADD -> {
						procedure.POP64(EBCRegisters.R5, false, null)
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.ADD64(
							EBCRegisters.R6, false,
							EBCRegisters.R5, false, null
						)
						procedure.PUSH64(EBCRegisters.R6, false, null)
					}

					Opcode.LMUL -> {
						procedure.POP64(EBCRegisters.R5, false, null)
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.MUL64(
							EBCRegisters.R6, false,
							EBCRegisters.R5, false, null
						)
						procedure.PUSH64(EBCRegisters.R6, false, null)
					}

					Opcode.LAND -> {
						procedure.POP64(EBCRegisters.R5, false, null)
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.AND64(
							EBCRegisters.R6, false,
							EBCRegisters.R5, false,
							null
						)
						procedure.PUSH64(EBCRegisters.R6, false, null)
					}

					Opcode.LUSHR -> {
						procedure.POP32(EBCRegisters.R5, false, null)
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
					}

					else -> throw IllegalArgumentException("Unknown operator opcode: ${element.opcode()}")
				}

				is BranchInstruction -> when (element.opcode()) {
					Opcode.IF_ICMPGE -> {
						procedure.POP32(EBCRegisters.R6, false, null)
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
						procedure.POP32(EBCRegisters.R6, false, null)
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

					Opcode.IFGT -> {
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

					Opcode.IFLE -> {
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
				}

				is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.INT -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						(element.constantValue() as Integer).toInt().toUInt()
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.INT -> {
					procedure.MOVIdd(
						EBCRegisters.R6, false, null,
						(element.constantValue() as Integer).toInt().toUInt()
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.IntrinsicConstantInstruction if element.typeKind() == TypeKind.LONG -> {
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						(element.constantValue() as java.lang.Long).toLong().toULong()
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.LoadConstantInstruction if element.typeKind() == TypeKind.LONG -> {
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						(element.constantEntry() as LongEntry).longValue().toULong()
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
				}

				is ConstantInstruction.LoadConstantInstruction if element.constantEntry() is StringEntry -> {
					val dataPosition = strings.getOrPut((element.constantEntry() as StringEntry).stringValue()) {
						val saved = initBase + data.size.toULong()
						data += ((element.constantEntry() as StringEntry).stringValue() + "\u0000")
							.toByteArray(Charsets.UTF_16LE)
						saved
					}
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						dataPosition
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
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
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIMemoryType.EfiBootServicesData" -> {
						procedure.MOVIdd(
							EBCRegisters.R6, false, null,
							EFIMemoryType.EfiBootServicesData.id
						)
						procedure.PUSH32(EBCRegisters.R6, false, null)
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
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R6, false, null)
					}

					"java/lang/foreign/MemorySegment.address()J" -> {
					}

					$$"java/lang/foreign/MemorySegment.get(Ljava/lang/foreign/ValueLayout$OfLong;J)J" -> {
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.POPn(EBCRegisters.R5, false, null)
						procedure.ADD64(
							EBCRegisters.R5, false,
							EBCRegisters.R6, false, null
						)
						procedure.PUSH64(EBCRegisters.R5, true, null)
					}

					$$"java/lang/foreign/MemorySegment.set(Ljava/lang/foreign/ValueLayout$OfInt;JI)V" -> {
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.POP64(EBCRegisters.R5, false, null)
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
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.POP64(EBCRegisters.R5, false, null)
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
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.POP64(EBCRegisters.R5, false, null)
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
						procedure.PUSH32(
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
						procedure.POP32(EBCRegisters.R6, false, null)
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
						procedure.PUSH64(EBCRegisters.R7, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputString(Ljava/lang/String;)J",
					"org/bread_experts_group/api/compile/ebc/efi/EFISimpleTextOutputProtocol.outputStringAt(Ljava/lang/foreign/MemorySegment;)J" -> {
						procedure.POP64(EBCRegisters.R6, false, null)
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
						procedure.PUSH64(EBCRegisters.R7, false, null)
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
						procedure.POP64(EBCRegisters.R6, false, null) // Size
						procedure.POP32(EBCRegisters.R5, false, null) // PoolType
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
						procedure.PUSH64(EBCRegisters.R7, false, null)
						procedure.PUSHn(EBCRegisters.R3, true, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getData()Ljava/lang/foreign/MemorySegment;" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
						procedure.POP64(EBCRegisters.R5, false, null)
						procedure.PUSHn(EBCRegisters.R6, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1.getStatus()J" -> {
						procedure.POPn(EBCRegisters.R6, false, null)
					}

					"org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable.getHeader()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;" -> {}
					"org/bread_experts_group/api/compile/ebc/efi/EFITableHeader.getSignature()J" -> {
						procedure.POPn(EBCRegisters.R6, false, null) // BootServices
						procedure.PUSH64(
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
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.EXTNDD64(
							EBCRegisters.R6, false,
							EBCRegisters.R6, false,
							null
						)
						procedure.PUSH64(EBCRegisters.R6, false, null)
					}

					Opcode.L2I -> {
						procedure.POP64(EBCRegisters.R6, false, null)
						procedure.PUSH32(EBCRegisters.R6, false, null)
					}


					Opcode.I2B -> {
						procedure.POP32(EBCRegisters.R6, false, null)
						procedure.EXTNDB32(
							EBCRegisters.R6, false,
							EBCRegisters.R6, false,
							null
						)
						procedure.PUSH32(EBCRegisters.R6, false, null)
					}

					else -> throw IllegalArgumentException(element.opcode().toString())
				}

				is StackInstruction if element.opcode() == Opcode.POP2 -> {
					procedure.POP64(EBCRegisters.R6, false, null)
				}

				is ReturnInstruction -> {
					procedure.POP64(EBCRegisters.R7, false, null)
					procedure.RET()
				}

				is Label -> labelTargets[element] = procedure.output.size.toULong()
				is Instruction -> print("* ")
				else -> print("--- ")
			}.also { println("$element") }
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

	private fun arithmeticBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	) {
		instructionBuffer.put((opcode or (if (operand2Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun ADD32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x0C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun ADD64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x4C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MUL32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x0E, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MUL64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x4E, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun AND32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x14, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun AND64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x54, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SHR32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x18, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SHR64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x58, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	private fun cmpIwBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	) {
		instructionBuffer.put(opcode.toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or
					(if (operand1Index != null) 0b10000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun CMPI32weq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2D, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64weq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6D, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wlte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2E, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wlte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6E, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wgte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2F, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wgte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6F, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wulte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x30, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wulte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x70, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wugte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x31, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wugte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x71, operand1, operand1Indirect, operand1Index, immediate)
	}

	private fun cmpBase(
		opcode: Int,
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	) {
		instructionBuffer.put((opcode or (if (operand2Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CMP32eq(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x05, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64eq(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x45, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32lte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x06, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64lte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x46, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32gte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x07, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64gte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x47, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32ulte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x08, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64ulte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x48, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32ugte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x09, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64ugte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x49, operand1, operand2, operand2Indirect, operand2Index)
	}

	private fun extendBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	) {
		instructionBuffer.put((opcode or (if (operand2Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun EXTNDB32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x1A, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDB64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x5A, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDW32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x1B, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDW64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x5B, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDD32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x1C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDD64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		extendBase(0x5C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
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

	private fun MOVIBase(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UShort?,
		immediateDataLength: EBCImmediateDataLength,
		move: EBCMoveTypes
	) {
		instructionBuffer.put((0x37 or ((immediateDataLength.ordinal + 1) shl 6)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (move.ordinal shl 4)
					or (if (operand1Index != null) 0b1000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
	}

	fun MOVIbw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIbd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIbq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIww(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIwd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIwq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIdw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIdd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIdq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIqw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_64_QUADWORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIqd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_64_QUADWORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIqq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_64_QUADWORD
		)
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

	fun POP32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x2C or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun POP64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x6C or (if (operand1Index != null) 0b10000000 else 0)).toByte())
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

	fun PUSH32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x2B or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun PUSH64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x6B or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
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
}