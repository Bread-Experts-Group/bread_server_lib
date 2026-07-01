package org.bread_experts_group.model.natives

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.generic.Mappable
import java.lang.classfile.ClassFile
import java.lang.classfile.Opcode
import java.lang.classfile.TypeKind
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.*
import java.lang.invoke.MethodHandles
import java.lang.reflect.AccessFlag
import java.util.*
import kotlin.io.path.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.superclasses

interface Library {
	companion object {
		private val classFile = ClassFile.of()
		private val mhLookup = MethodHandles.lookup()

		private val loaded = mutableMapOf<KClass<*>, Library>()

		@Synchronized
		fun <T : Library> getLibrary(linker: Linker, arena: Arena, forClass: KClass<T>): T {
			val loaded = this.loaded[forClass]
			@Suppress("UNCHECKED_CAST")
			if (loaded != null) return loaded as T
			if (
				forClass.superclasses.size != 2 ||
				forClass.superclasses[0] != Library::class ||
				forClass.superclasses[1] != Any::class
			) throw IllegalArgumentException("Library classes must only inherit from ${Library::class.qualifiedName}")
			val lookupBacked = forClass.findAnnotation<LookupBacked>() ?: throw IllegalArgumentException(
				"$forClass must define the ${LookupBacked::class.qualifiedName} annotation"
			)
			val lookup = if (lookupBacked.path) SymbolLookup.libraryLookup(Path(lookupBacked.library), arena)
			else SymbolLookup.libraryLookup(lookupBacked.library, arena)

			val thisClass = ClassDesc.of(
				Library::class.qualifiedName!!.substringBeforeLast('.') + ".$" + forClass.simpleName
			)
			val bytes = this.classFile.build(thisClass) { classBuilder ->
				classBuilder.withSuperclass(forClass.desc)
				classBuilder.withMethodBody(
					"<init>",
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						SymbolLookup::class.desc, Linker::class.desc
					),
					0
				) { codeBuilder ->
					codeBuilder
						.aload(0)
						.invokespecial(
							forClass.desc, "<init>",
							MethodTypeDesc.of(ConstantDescs.CD_void)
						)
						.aload(2)
						.invoke(Linker::canonicalLayouts)
					val canonicalLayouts = linker.canonicalLayouts()
					val canonicalLayoutsSlot = codeBuilder.allocateLocal(TypeKind.REFERENCE)
					codeBuilder.astore(canonicalLayoutsSlot)
					forClass.declaredMemberFunctions.forEach { function ->
						classBuilder.withField(
							function.name, ConstantDescs.CD_MethodHandle,
							AccessFlag.FINAL.mask()
						)
						@Suppress("CAST_NEVER_SUCCEEDS")
						codeBuilder
							.aload(1)
							.ldc(function.name as ConstantDesc)
							.invoke(SymbolLookup::find)
							.aconst_null()
							.invokevirtual(
								Optional::class.desc, "orElse",
								MethodTypeDesc.of(
									ConstantDescs.CD_Object,
									ConstantDescs.CD_Object,
								)
							)
							.checkcast(MemorySegment::class.desc)
							.block { upper ->
								upper.block { lower ->
									lower
										.dup()
										.ifnull(lower.endLabel())
										.aload(2)
										.swap()
										.functionDescriptor(
											{ lower.aload(canonicalLayoutsSlot) },
											function.returnType,
											function.parameters.drop(1).map { it.type }
										)
										.iconst_0()
										.anewarray(ConstantDescs.CD_Object)
										.invokeinterface(
											Linker::class.desc, "downcallHandle",
											MethodTypeDesc.of(
												ConstantDescs.CD_MethodHandle,
												MemorySegment::class.desc,
												FunctionDescriptor::class.desc,
												Linker.Option::class.desc.arrayType()
											)
										)
										.aload(0)
										.swap()
										.putfield(
											thisClass, function.name,
											ConstantDescs.CD_MethodHandle
										)
										.goto_(upper.endLabel())
								}.pop()
							}
						classBuilder.withMethodBody(
							function.name, MethodTypeDesc.of(
								function.returnType.clazz.let {
									if (it == Unit::class) ConstantDescs.CD_void
									else it.desc
								},
								function.parameters.drop(1).map { p -> p.type.clazz.desc }
							),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisClass, function.name,
									ConstantDescs.CD_MethodHandle
								)
							val returnDesc: ClassDesc = when (function.returnType.clazz) {
								MemorySegment::class -> MemorySegment::class.desc
								Long::class -> ConstantDescs.CD_long
								Unit::class -> ConstantDescs.CD_void
								else -> {
									val annotation = function.returnType.clazz.findAnnotation<DatatypeBacked>() ?: TODO(
										function.returnType.toString()
									)
									when (val layout = canonicalLayouts[annotation.datatype]) {
										ValueLayout.JAVA_INT -> ConstantDescs.CD_int
										ValueLayout.JAVA_SHORT -> ConstantDescs.CD_short

										else -> TODO(layout.toString())
									}
								}
							}
							var offset = 1
							val parameterDescs: List<ClassDesc> = function.parameters.drop(1)
								.mapIndexed { i, parameter ->
									val parameterClass = parameter.type.clazz
									if (parameterClass == MemorySegment::class) {
										codeBuilder.aload(i + offset)
										codeBuilder.ifThenElse(
											Opcode.IFNULL,
											{ thenBlock ->
												thenBlock.getstatic(
													MemorySegment::class.desc, "NULL",
													MemorySegment::class.desc
												)
											},
											{ elseBlock ->
												elseBlock.aload(i + offset)
											}
										)
										return@mapIndexed MemorySegment::class.desc
									}
									if (parameterClass == Pointer::class) {
										codeBuilder.aload(i + offset)
										codeBuilder.ifThenElse(
											Opcode.IFNULL,
											{ thenBlock ->
												thenBlock.getstatic(
													MemorySegment::class.desc, "NULL",
													MemorySegment::class.desc
												)
											},
											{ elseBlock ->
												elseBlock
													.aload(i + offset)
													.invokeinterface(
														BackingSegment::class.desc, "getSegment",
														MethodTypeDesc.of(MemorySegment::class.desc)
													)
											}
										)
										return@mapIndexed MemorySegment::class.desc
									}
									if (BackingSegment::class.isSuperclassOf(parameterClass)) {
										codeBuilder.aload(i + offset)
										codeBuilder.ifThenElse(
											Opcode.IFNULL,
											{ thenBlock ->
												thenBlock.getstatic(
													MemorySegment::class.desc, "NULL",
													MemorySegment::class.desc
												)
											},
											{ elseBlock ->
												elseBlock
													.aload(i + offset)
													.invoke(BackingSegment::getSegment)
											}
										)
										return@mapIndexed MemorySegment::class.desc
									}
									if (parameterClass == Long::class) {
										codeBuilder.lload(i + offset++)
										return@mapIndexed ConstantDescs.CD_long
									}
									if (parameterClass == IndexedEnumSet::class) {
										val typeClass = parameter.type.arguments.first().type!!.clazz
										if (Flaggable::class.isSuperclassOf(typeClass)) {
											val annotation = typeClass.findAnnotation<DatatypeBacked>()
												?: TODO("$typeClass")
											when (val layout = canonicalLayouts[annotation.datatype]) {
												ValueLayout.JAVA_INT -> {
													val indexSlot = codeBuilder.allocateLocal(TypeKind.INT)
													val valueSlot = codeBuilder.allocateLocal(TypeKind.INT)
													codeBuilder
														.iconst_0()
														.istore(valueSlot)
														.aload(i + offset)
														.invoke(IndexedEnumSet<*>::size.getter)
														.istore(indexSlot)
														.block { blockBuilder ->
															blockBuilder
																.iinc(indexSlot, -1)
																.iload(indexSlot)
																.iconst_m1()
																.if_icmpeq(blockBuilder.endLabel())
																.aload(i + offset)
																.iload(indexSlot)
																.invoke(IndexedEnumSet<*>::get)
																.checkcast(Flaggable::class.desc)
																.invoke(Flaggable::position.getter)
																.l2i()
																.iload(valueSlot)
																.ior()
																.istore(valueSlot)
																.goto_(blockBuilder.startLabel())
														}
														.iload(valueSlot)
													return@mapIndexed ConstantDescs.CD_int
												}

												else -> TODO(layout.toString())
											}
										} else TODO("$typeClass")
									}
									val annotation = parameterClass.findAnnotation<DatatypeBacked>()
										?: TODO("$parameterClass")
									when (val layout = canonicalLayouts[annotation.datatype]) {
										ValueLayout.JAVA_INT -> {
											if (Number::class.isSuperclassOf(parameterClass)) {
												codeBuilder
													.aload(i + offset)
													.invokevirtual(
														ConstantDescs.CD_Number, "intValue",
														MethodTypeDesc.of(ConstantDescs.CD_int)
													)
											} else if (Mappable::class.isSuperclassOf(parameterClass)) {
												codeBuilder
													.aload(i + offset)
													.invoke(Mappable<*, *>::id.getter)
													.checkcast(ConstantDescs.CD_Number)
													.invokevirtual(
														ConstantDescs.CD_Number, "intValue",
														MethodTypeDesc.of(ConstantDescs.CD_int)
													)
											} else TODO("$parameterClass")
											ConstantDescs.CD_int
										}

										ValueLayout.JAVA_BYTE -> {
											if (Number::class.isSuperclassOf(parameterClass)) {
												codeBuilder
													.aload(i + offset)
													.invokevirtual(
														ConstantDescs.CD_Number, "byteValue",
														MethodTypeDesc.of(ConstantDescs.CD_byte)
													)
											} else TODO("$parameterClass")
											ConstantDescs.CD_byte
										}

										else -> TODO(layout.toString())
									}
								}
							codeBuilder
								.invokevirtual(
									ConstantDescs.CD_MethodHandle, "invokeExact",
									MethodTypeDesc.of(
										returnDesc,
										parameterDescs
									)
								)
							when (val returnType = function.returnType.clazz) {
								MemorySegment::class -> codeBuilder.areturn()
								Long::class -> codeBuilder.lreturn()
								Unit::class -> codeBuilder.return_()
								else -> {
									val annotation = returnType.findAnnotation<DatatypeBacked>() ?: TODO("$returnType")

									@Suppress("UNCHECKED_CAST")
									val a = Datatype.getDatatype(canonicalLayouts, returnType as KClass<Datatype>)
									when (val layout = canonicalLayouts[annotation.datatype]) {
										ValueLayout.JAVA_INT -> {
											if (Number::class.isSuperclassOf(returnType)) {
												val resultSlot = codeBuilder.allocateLocal(TypeKind.INT)
												codeBuilder
													.istore(resultSlot)
													.new_(ClassDesc.ofDescriptor(a.descriptorString()))
													.dup()
													.iload(resultSlot)
													.invokespecial(
														ClassDesc.ofDescriptor(a.descriptorString()), "<init>",
														MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_int)
													)
													.areturn()
											}
											ConstantDescs.CD_int
										}

										ValueLayout.JAVA_SHORT -> {
											if (Number::class.isSuperclassOf(returnType)) {
												val resultSlot = codeBuilder.allocateLocal(TypeKind.SHORT)
												codeBuilder
													.istore(resultSlot)
													.new_(ClassDesc.ofDescriptor(a.descriptorString()))
													.dup()
													.iload(resultSlot)
													.invokespecial(
														ClassDesc.ofDescriptor(a.descriptorString()), "<init>",
														MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_short)
													)
													.areturn()
											}
											ConstantDescs.CD_short
										}

										else -> TODO(layout.toString())
									}
								}
							}
						}
					}
					codeBuilder.return_()
				}
			}
			val hidden = this.mhLookup.defineHiddenClass(bytes, true)

			@Suppress("UNCHECKED_CAST")
			val new = hidden.lookupClass().getDeclaredConstructor(
				SymbolLookup::class.java,
				Linker::class.java
			).newInstance(lookup, linker) as T
			this.loaded[forClass] = new
			return new
		}
	}
}