package org.bread_experts_group.api.compile.ebc

import java.lang.classfile.Instruction
import java.lang.classfile.Opcode
import java.lang.classfile.TypeKind
import java.lang.classfile.attribute.CodeAttribute
import java.lang.classfile.instruction.*
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

fun analyze(
	parameters: List<ClassDesc>,
	code: CodeAttribute
): List<Pair<Instruction, List<ExecutionFrame>>> {
	val workList = ArrayDeque<Pair<Int, ExecutionFrame>>()
	val initialParameters = mutableMapOf<Int, LocalVariableElement>()
	var pI = 0
	for (parameter in parameters) {
		val type = when (parameter) {
			ConstantDescs.CD_long, ConstantDescs.CD_double -> {
				initialParameters[pI] = if (parameters == ConstantDescs.CD_long) StackElement.Primitive.LONG
				else StackElement.Primitive.DOUBLE
				pI += 2
				continue
			}

			ConstantDescs.CD_float -> StackElement.Primitive.FLOAT
			ConstantDescs.CD_int, ConstantDescs.CD_short,
			ConstantDescs.CD_char, ConstantDescs.CD_byte,
			ConstantDescs.CD_boolean -> StackElement.Primitive.INT

			else -> LocalVariableElement.Reference(parameter, null)
		}
		initialParameters[pI++] = type
	}
	workList.add(0 to ExecutionFrame(initialParameters))
	var bci = 0
	val instructions = mutableMapOf<Int, Instruction>()
	for (element in code) {
		if (element !is Instruction) continue
		instructions[bci] = element
		bci += element.sizeInBytes()
	}
	val visited = mutableMapOf<Int, MutableList<ExecutionFrame>>()
	while (workList.isNotEmpty()) {
		val (bci, frame) = workList.removeFirst()
		println(instructions[bci]!!)
		fun frameCopy(): ExecutionFrame {
			val variables = HashMap(frame.variables)
			val stack = ArrayList(frame.stack)
			var i = 0
			while (i < variables.size) {
				val variable = variables[i]
				if (variable !is LocalVariableElement.Reference) {
					i++
					continue
				}
				val replacement = LocalVariableElement.Reference(variable.clazz, variable.isNull)
				variables[i] = replacement
				i++
				var i = 0
				while (i < stack.size) {
					val element = stack[i]
					if (element is StackElement.VariableReference && element.ref === variable)
						stack[i] = StackElement.VariableReference(replacement)
					i++
				}
			}
			return ExecutionFrame(variables, stack)
		}

		fun addVisited() = visited.getOrPut(bci) { mutableListOf() }.add(frameCopy())

		val element = instructions[bci]!!
		fun workNextElement() = workList.add(bci + element.sizeInBytes() to frameCopy())
		fun workBranch(element: BranchInstruction) {
			val target = code.labelToBci(element.target())
			val frame = frameCopy()
			if (visited[target]?.any { it == frame } == true) return
			workList.add(target to frame)
		}

		when (element) {
			is LoadInstruction -> {
				addVisited()
				frame.stack.add(
					when (val variable = frame.variables[element.slot()]) {
						is StackElement.Primitive -> variable
						is LocalVariableElement.Reference -> StackElement.VariableReference(variable)
						null -> throw InternalError()
					}
				)
				workNextElement()
			}

			is StoreInstruction -> {
				addVisited()
				frame.variables[element.slot()] = when (val element = frame.stack.removeLast()) {
					is StackElement.Primitive -> element
					is StackElement.VariableReference -> element.ref
				}
				workNextElement()
			}

			is ConstantInstruction -> {
				addVisited()
				frame.stack.add(
					when (element.typeKind()) {
						TypeKind.REFERENCE -> StackElement.Primitive.REFERENCE
						TypeKind.LONG -> StackElement.Primitive.LONG
						TypeKind.DOUBLE -> StackElement.Primitive.DOUBLE
						TypeKind.INT, TypeKind.SHORT,
						TypeKind.BYTE, TypeKind.CHAR, TypeKind.BOOLEAN -> StackElement.Primitive.INT

						TypeKind.FLOAT -> StackElement.Primitive.FLOAT
						TypeKind.VOID -> throw InternalError()
					}
				)
				workNextElement()
			}

			is OperatorInstruction -> when (val opcode = element.opcode()) {
				Opcode.LADD, Opcode.LSUB, Opcode.LMUL, Opcode.LDIV, Opcode.LREM,
				Opcode.LAND, Opcode.LOR, Opcode.LXOR -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.LONG)
					workNextElement()
				}

				Opcode.IADD, Opcode.ISUB, Opcode.IMUL, Opcode.IDIV, Opcode.IREM,
				Opcode.IAND, Opcode.IOR, Opcode.IXOR,
				Opcode.IUSHR, Opcode.ISHR, Opcode.ISHL -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.INT)
					workNextElement()
				}

				Opcode.LCMP -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.INT)
					workNextElement()
				}

				Opcode.ARRAYLENGTH -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.INT)
					workNextElement()
				}

				else -> throw NotImplementedError("Operator: $opcode")
			}

			is IncrementInstruction, is NopInstruction -> {
				addVisited()
				workNextElement()
			}

			is NewPrimitiveArrayInstruction, is NewReferenceArrayInstruction -> {
				addVisited()
				frame.stack.removeLast()
				frame.stack.add(StackElement.Primitive.REFERENCE)
				workNextElement()
			}

			is ArrayStoreInstruction -> {
				addVisited()
				frame.stack.removeLast()
				frame.stack.removeLast()
				frame.stack.removeLast()
				workNextElement()
			}

			is ArrayLoadInstruction -> {
				addVisited()
				frame.stack.removeLast()
				frame.stack.removeLast()
				frame.stack.add(
					when (element.typeKind()) {
						TypeKind.REFERENCE -> StackElement.Primitive.REFERENCE
						TypeKind.LONG -> StackElement.Primitive.LONG
						TypeKind.DOUBLE -> StackElement.Primitive.DOUBLE
						TypeKind.INT, TypeKind.SHORT,
						TypeKind.BYTE, TypeKind.CHAR, TypeKind.BOOLEAN -> StackElement.Primitive.INT

						TypeKind.FLOAT -> StackElement.Primitive.FLOAT
						TypeKind.VOID -> throw InternalError()
					}
				)
				workNextElement()
			}

			is StackInstruction -> when (val opcode = element.opcode()) {
				Opcode.POP -> {
					addVisited()
					frame.stack.removeLast()
					workNextElement()
				}

				else -> throw NotImplementedError("Stack: $opcode")
			}

			is ReturnInstruction -> addVisited()
			is ConvertInstruction -> when (val opcode = element.opcode()) {
				Opcode.I2C, Opcode.I2S, Opcode.I2B -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.INT)
					workNextElement()
				}

				Opcode.I2L -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.LONG)
					workNextElement()
				}

				Opcode.L2I -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.add(StackElement.Primitive.INT)
					workNextElement()
				}

				else -> throw NotImplementedError("Convert: $opcode")
			}

			is BranchInstruction -> when (val opcode = element.opcode()) {
				Opcode.IFNULL, Opcode.IFNONNULL -> {
					addVisited()
					val reference = frame.stack.removeLast()
					val isNull = opcode == Opcode.IFNULL
					if (reference is StackElement.VariableReference) reference.ref.isNull = isNull
					workBranch(element)
					if (reference is StackElement.VariableReference) reference.ref.isNull = !isNull
					workNextElement()
				}

				Opcode.IFEQ, Opcode.IFNE,
				Opcode.IFLT, Opcode.IFLE,
				Opcode.IFGT, Opcode.IFGE -> {
					addVisited()
					frame.stack.removeLast()
					workBranch(element)
					workNextElement()
				}

				Opcode.IF_ICMPEQ, Opcode.IF_ICMPNE,
				Opcode.IF_ICMPLT, Opcode.IF_ICMPLE,
				Opcode.IF_ICMPGT, Opcode.IF_ICMPGE -> {
					addVisited()
					frame.stack.removeLast()
					frame.stack.removeLast()
					workBranch(element)
					workNextElement()
				}

				Opcode.GOTO -> {
					addVisited()
					workBranch(element)
				}

				else -> throw NotImplementedError("Branch: $opcode")
			}

			is InvokeInstruction -> when (val opcode = element.opcode()) {
				Opcode.INVOKESTATIC -> {
					addVisited()
					var parameters = element.typeSymbol().parameterList().size
					while (parameters > 0) {
						frame.stack.removeLast()
						parameters--
					}
					when (element.typeSymbol().returnType()) {
						ConstantDescs.CD_long -> frame.stack.add(StackElement.Primitive.LONG)
						ConstantDescs.CD_double -> frame.stack.add(StackElement.Primitive.DOUBLE)
						ConstantDescs.CD_int, ConstantDescs.CD_short,
						ConstantDescs.CD_byte, ConstantDescs.CD_char,
						ConstantDescs.CD_boolean -> frame.stack.add(StackElement.Primitive.INT)

						ConstantDescs.CD_float -> frame.stack.add(StackElement.Primitive.FLOAT)
						ConstantDescs.CD_void -> {}
						else -> frame.stack.add(StackElement.Primitive.REFERENCE)
					}
					workList.add(bci + element.sizeInBytes() to frameCopy())
				}

				else -> throw NotImplementedError("Call: $opcode")
			}

			is TypeCheckInstruction -> {
				addVisited()
				println("*** TODO: TYPECHECK")
				workNextElement()
			}

			else -> throw NotImplementedError("Analyze: $element")
		}
	}
	return visited.toSortedMap { bciA, bciB -> bciA.compareTo(bciB) }.map { (bci, frames) ->
		instructions[bci]!! to frames
	}
}