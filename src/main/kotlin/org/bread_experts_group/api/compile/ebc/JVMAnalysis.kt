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
			ConstantDescs.CD_long -> StackElement.Primitive.LONG
			ConstantDescs.CD_double -> StackElement.Primitive.DOUBLE
			ConstantDescs.CD_float -> StackElement.Primitive.FLOAT
			ConstantDescs.CD_int, ConstantDescs.CD_short,
			ConstantDescs.CD_char, ConstantDescs.CD_byte,
			ConstantDescs.CD_boolean -> StackElement.Primitive.INT

			else -> LocalVariableElement.Reference(
				ClassLoader.getSystemClassLoader().loadClass(
					"${parameter.packageName()}.${parameter.displayName()}"
				), null
			)
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

			is IncrementInstruction, is NopInstruction -> {
				addVisited()
				workNextElement()
			}

			is ReturnInstruction -> addVisited()
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

				Opcode.IF_ICMPGE -> {
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

				else -> TODO("Branch: $opcode")
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

				else -> TODO("Call: $opcode")
			}

			else -> TODO("Analyze: $element")
		}
	}
	return visited.toSortedMap { bciA, bciB -> bciA.compareTo(bciB) }.map { (bci, frames) ->
		instructions[bci]!! to frames
	}
}