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
import java.lang.reflect.Constructor
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaMethod

interface Structure<T : Structure<T>> : Pointer<T> {
	companion object {
		private val classFile = ClassFile.of()
		private val mhLookup = MethodHandles.lookup()

		fun layoutParametersGeneral(
			layouts: Map<String, MemoryLayout>,
			type: KType
		): Pair<Long, Long> {
			val clazz = type.classifier as KClass<*>
			val localAlignment: Long
			val localSize: Long
			if (clazz == MemorySegment::class || clazz == Pointer::class || Function::class.isSuperclassOf(clazz)) {
				localAlignment = ValueLayout.ADDRESS.byteAlignment()
				localSize = ValueLayout.ADDRESS.byteSize()
			} else if (clazz == Long::class) {
				localAlignment = ValueLayout.JAVA_LONG.byteAlignment()
				localSize = ValueLayout.JAVA_LONG.byteSize()
			} else if (Structure::class.isSuperclassOf(clazz)) {
				@Suppress("UNCHECKED_CAST")
				val (otherSize, otherAlignment) = layoutParameters(layouts, type)
				val structAlignment = clazz.findAnnotation<Align>()
				localAlignment = structAlignment?.bytes ?: otherAlignment
				localSize = otherSize
			} else if (clazz.hasAnnotation<DatatypeBacked>()) {
				val layout = Datatype.getLayout(layouts, clazz)
				localAlignment = layout.byteAlignment()
				localSize = layout.byteSize()
			} else if (clazz == IndexedEnumSet::class) {
				return layoutParametersGeneral(layouts, type.arguments.first().type!!)
			} else TODO(clazz.toString())
			return localSize to localAlignment
		}

		fun layoutParameters(
			layouts: Map<String, MemoryLayout>,
			type: KType
		): Triple<Long, Long, Map<KProperty<*>, Long>> {
			var offset = 0L
			var alignment = 0L
			val fieldMap = mutableMapOf<KProperty<*>, Long>()
			val forClass = type.classifier as KClass<*>
			val properties = forClass.declaredMemberProperties.toList().groupBy { property ->
				property.findAnnotation<Order>()!!.index
			}
			properties.keys.sortedBy { it }.forEach { i ->
				val group = properties[i]!!
				var groupMaxAlignment: Long = 0
				var groupMaxSize: Long = 0
				group.forEach {
					val classifier = it.returnType.classifier as KClass<*>
					val localAlignment: Long
					val localSize: Long
					if (classifier == NativeArray::class) {
						val size = it.returnType.findAnnotation<ArraySize>() ?: throw IllegalArgumentException(
							"$forClass: $it must have ${ArraySize::class} annotation"
						)
						val (otherSize, otherAlignment) = layoutParametersGeneral(
							layouts,
							it.returnType.arguments.first().type!!
						)
						localAlignment = otherAlignment
						localSize = size.size * otherSize
					} else {
						val (otherSize, otherAlignment) = layoutParametersGeneral(layouts, it.returnType)
						localAlignment = otherAlignment
						localSize = otherSize
					}

					if (localAlignment > groupMaxAlignment) groupMaxAlignment = localAlignment
					if (localSize > groupMaxSize) groupMaxSize = localSize
				}

				val distance = offset % groupMaxAlignment
				if (distance > 0) offset += groupMaxAlignment - distance
				if (groupMaxAlignment > alignment) alignment = groupMaxAlignment
				group.forEach { fieldMap[it] = offset }
				offset += groupMaxSize
			}
			return Triple(offset, alignment, fieldMap)
		}

		val loaded: MutableMap<KType, Class<*>> = mutableMapOf<KType, Class<*>>()
		fun getStructureOfType(linker: Linker, type: KType): Class<*> {
			this.loaded[type]?.let { return it }
			val forClass = type.classifier as KClass<*>
			val layouts = linker.canonicalLayouts()
			val (size, alignment, fieldMap) = layoutParameters(layouts, type)
			val thisDesc = ClassDesc.of(
				Structure::class.qualifiedName!!.substringBeforeLast('.') + ".$" + forClass.simpleName
			)
			val bytes = this.classFile.build(thisDesc) { classBuilder ->
				classBuilder.withSuperclass(forClass.desc)

				classBuilder.withField(
					"segment", MemorySegment::class.desc,
					AccessFlag.PRIVATE.mask() or AccessFlag.FINAL.mask()
				)

				classBuilder.withMethodBody(
					"getSegment", MethodTypeDesc.of(MemorySegment::class.desc),
					AccessFlag.PUBLIC.mask()
				) { codeBuilder ->
					codeBuilder
						.aload(0)
						.getfield(
							thisDesc, "segment",
							MemorySegment::class.desc
						)
						.areturn()
				}

				fieldMap.forEach { (property, offset) ->
					val getter = property.getter.javaMethod!!

					val propertyType = property.returnType
					val propertyClass = propertyType.clazz

					classBuilder.withMethodBody(
						getter.name, MethodTypeDesc.of(propertyClass.desc),
						AccessFlag.PUBLIC.mask()
					) { codeBuilder ->
						@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
						if (propertyClass.hasAnnotation<DatatypeBacked>() || propertyClass == IndexedEnumSet::class) {
							val layout = if (propertyClass == IndexedEnumSet::class) Datatype.getLayout(
								layouts,
								property.getter.returnType.arguments.first().type!!.classifier as KClass<*>
							) else Datatype.getLayout(layouts, propertyClass)

							val layoutName: String
							val layoutDesc: ClassDesc
							val typeDesc: ClassDesc
							val typeKind: TypeKind

							when (layout) {
								ValueLayout.JAVA_INT -> {
									layoutName = "JAVA_INT"
									layoutDesc = ValueLayout.OfInt::class.desc
									typeDesc = ConstantDescs.CD_int
									typeKind = TypeKind.INT
								}

								ValueLayout.JAVA_SHORT -> {
									layoutName = "JAVA_SHORT"
									layoutDesc = ValueLayout.OfShort::class.desc
									typeDesc = ConstantDescs.CD_short
									typeKind = TypeKind.SHORT
								}

								ValueLayout.JAVA_BYTE -> {
									layoutName = "JAVA_BYTE"
									layoutDesc = ValueLayout.OfByte::class.desc
									typeDesc = ConstantDescs.CD_byte
									typeKind = TypeKind.BYTE
								}

								else -> TODO(layout.toString())
							}
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.getstatic(
									ValueLayout::class.desc, layoutName,
									layoutDesc
								)
								.ldc(offset as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "get",
									MethodTypeDesc.of(
										typeDesc,
										layoutDesc,
										ConstantDescs.CD_long
									)
								)
							if (Datatype::class.isSuperclassOf(propertyClass)) {
								val datatype = Datatype.getDatatype(layouts, propertyClass as KClass<Datatype>)
								val slot = codeBuilder.allocateLocal(typeKind)
								codeBuilder
									.storeLocal(typeKind, slot)
									.new_(datatype.desc)
									.dup()
									.loadLocal(typeKind, slot)
									.invokespecial(
										datatype.desc, "<init>",
										MethodTypeDesc.of(ConstantDescs.CD_void, typeDesc)
									)
							} else {
								if (Mappable::class.isSuperclassOf(propertyClass)) {
									val valueSlot = codeBuilder.allocateLocal(typeKind)
									val indexSlot = codeBuilder.allocateLocal(TypeKind.INT)
									codeBuilder
										.storeLocal(typeKind, valueSlot)
										.iconst_0()
										.istore(indexSlot)
										.ldc(propertyClass.desc)
										.invoke(Class<*>::getEnumConstants)
										.checkcast(propertyClass.desc.arrayType())
										.block { blockBuilder ->
											blockBuilder
												.dup()
												.iload(indexSlot)
												.aaload()
												.dup()
												.invoke(Mappable<*, *>::id.getter)
												.checkcast(ConstantDescs.CD_Integer)
												.invokevirtual(
													ConstantDescs.CD_Number, "intValue",
													MethodTypeDesc.of(ConstantDescs.CD_int)
												)
												.iload(valueSlot)
												.if_icmpeq(blockBuilder.endLabel())
												.pop()
												.iinc(indexSlot, 1)
												.goto_(blockBuilder.startLabel())
										}
								} else if (propertyClass == IndexedEnumSet::class) {
									val enumClass = property.getter.returnType.arguments.first().type!!
										.classifier as KClass<*>
									val valueSlot = codeBuilder.allocateLocal(typeKind)
									val indexSlot = codeBuilder.allocateLocal(TypeKind.INT)
									val flagSlot = codeBuilder.allocateLocal(TypeKind.REFERENCE)
									codeBuilder
										.storeLocal(typeKind, valueSlot)
										.iconst_0()
										.istore(indexSlot)
										.ldc(enumClass.desc)
										.dup()
										.getstatic(
											IndexedEnumSet::class.desc, "Companion",
											IndexedEnumSet.Companion::class.desc
										)
										.swap()
										.invoke(IndexedEnumSet.Companion::noneOf)
										.astore(flagSlot)
										.invoke(Class<*>::getEnumConstants)
										.checkcast(enumClass.desc.arrayType())
										.block { blockBuilder ->
											val enumSlot = blockBuilder.allocateLocal(TypeKind.REFERENCE)
											blockBuilder
												.dup()
												.arraylength()
												.iload(indexSlot)
												.if_icmpeq(blockBuilder.endLabel())

												.dup()
												.iload(indexSlot)
												.aaload()
												.dup()
												.astore(enumSlot)
												.invoke(Flaggable::position.getter)
												.iload(valueSlot)
												.i2l()
												.land()
												.lconst_0()
												.lcmp()
												.iinc(indexSlot, 1)
												.ifeq(blockBuilder.startLabel())

												.aload(flagSlot)
												.aload(enumSlot)
												.invoke(IndexedEnumSet<*>::add)
												.pop()
												.goto_(blockBuilder.startLabel())
										}
										.aload(flagSlot)
								} else TODO(propertyClass.toString())
							}
							codeBuilder.areturn()
						} else if (propertyClass == Long::class) {
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.getstatic(
									ValueLayout::class.desc, "JAVA_LONG",
									ValueLayout.OfLong::class.desc
								)
								.ldc(offset as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "get",
									MethodTypeDesc.of(
										ConstantDescs.CD_long,
										ValueLayout.OfLong::class.desc,
										ConstantDescs.CD_long
									)
								)
								.lreturn()
						} else if (Structure::class.isSuperclassOf(propertyClass)) {
							val structure = getStructureOfType(linker, propertyType)
							codeBuilder
								.ldc(structure.desc)
								.iconst_1()
								.anewarray(ConstantDescs.CD_Class)
								.dup()
								.iconst_0()
								.ldc(MemorySegment::class.desc)
								.aastore()
								.invoke(Class<*>::getConstructor)
								.iconst_1()
								.anewarray(MemorySegment::class.desc)
								.dup()
								.iconst_0()
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.ldc(offset as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "asSlice",
									MethodTypeDesc.of(
										MemorySegment::class.desc,
										ConstantDescs.CD_long
									)
								)
								.aastore()
								.invoke(Constructor<*>::newInstance)
								.checkcast(propertyClass.desc)
								.areturn()
						} else if (propertyClass == MemorySegment::class) {
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.getstatic(
									ValueLayout::class.desc, "ADDRESS",
									AddressLayout::class.desc
								)
								.ldc(offset as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "get",
									MethodTypeDesc.of(
										MemorySegment::class.desc,
										AddressLayout::class.desc,
										ConstantDescs.CD_long
									)
								)
								.areturn()
						} else if (propertyClass == Pointer::class) {
							val pointedType = property.getter.returnType.arguments.first().type!!
							val pointedClass = pointedType.clazz
							if (pointedClass == NativeArray::class) {
								val arrayClass = pointedType.arguments.first().type!!.clazz
								codeBuilder
									.getstatic(
										thisDesc, "linker",
										Linker::class.desc
									)
									.invoke(Linker::canonicalLayouts)
									.aload(0)
									.getfield(
										thisDesc, "segment",
										MemorySegment::class.desc
									)
									.getstatic(
										ValueLayout::class.desc, "ADDRESS",
										AddressLayout::class.desc
									)
									.ldc(offset as ConstantDesc)
									.invokeinterface(
										MemorySegment::class.desc, "get",
										MethodTypeDesc.of(
											MemorySegment::class.desc,
											AddressLayout::class.desc,
											ConstantDescs.CD_long
										)
									)
									.ldc(arrayClass.desc)
									.invokestatic(
										NativeArray::class.desc, "fromClass",
										MethodTypeDesc.of(
											NativeArray::class.desc,
											ConstantDescs.CD_Map,
											MemorySegment::class.desc,
											ConstantDescs.CD_Class
										)
									)
									.areturn()
							} else if (Structure::class.isSuperclassOf(pointedClass)) {
								val structure = getStructureOfType(linker, pointedType)
								codeBuilder
									.ldc(structure.desc)
									.iconst_1()
									.anewarray(ConstantDescs.CD_Class)
									.dup()
									.iconst_0()
									.ldc(MemorySegment::class.desc)
									.aastore()
									.invoke(Class<*>::getConstructor)
									.iconst_1()
									.anewarray(MemorySegment::class.desc)
									.dup()
									.iconst_0()
									.aload(0)
									.getfield(
										thisDesc, "segment",
										MemorySegment::class.desc
									)
									.getstatic(
										ValueLayout::class.desc, "ADDRESS",
										AddressLayout::class.desc
									)
									.ldc(offset as ConstantDesc)
									.invokeinterface(
										MemorySegment::class.desc, "get",
										MethodTypeDesc.of(
											MemorySegment::class.desc,
											AddressLayout::class.desc,
											ConstantDescs.CD_long
										)
									)
									.aastore()
									.invoke(Constructor<*>::newInstance)
									.checkcast(propertyClass.desc)
									.areturn()
							} else {
								TODO("$pointedClass")
							}
						} else if (propertyClass == NativeArray::class) {
							val clazz = property.getter.returnType.arguments.first().type!!.classifier as KClass<*>
							val size = property.getter.returnType.findAnnotation<ArraySize>()!!.size
							codeBuilder
								.getstatic(
									thisDesc, "linker",
									Linker::class.desc
								)
								.invoke(Linker::canonicalLayouts)
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.ldc(offset as ConstantDesc)
								.ldc(size as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "asSlice",
									MethodTypeDesc.of(
										MemorySegment::class.desc,
										ConstantDescs.CD_long, ConstantDescs.CD_long
									)
								)
								.ldc(clazz.desc)
								.invokestatic(
									NativeArray::class.desc, "fromClass",
									MethodTypeDesc.of(
										NativeArray::class.desc,
										ConstantDescs.CD_Map,
										MemorySegment::class.desc,
										ConstantDescs.CD_Class
									)
								)
								.areturn()
						} else if (Function::class.isSuperclassOf(propertyClass)) {
							val parameterTypes = propertyType.arguments.map { it.type!! }.toMutableList()
							val returnType = parameterTypes.removeLast()

							val parameterClasses = parameterTypes.map { it.classifier as KClass<*> }
							val returnClass = returnType.classifier as KClass<*>

							val lambdaDesc = thisDesc.nested("Lambda$${property.name}")
							val lambdaBytes = classFile.build(lambdaDesc) { lambdaBuilder ->
								lambdaBuilder.withSuperclass(ConstantDescs.CD_Object)
								lambdaBuilder.withInterfaceSymbols(propertyClass.desc)
								lambdaBuilder.withField(
									"handle", ConstantDescs.CD_MethodHandle,
									AccessFlag.PUBLIC.mask()
								)

								lambdaBuilder.withMethodBody(
									"invoke", MethodTypeDesc.of(
										ConstantDescs.CD_Object,
										List(parameterClasses.size) { ConstantDescs.CD_Object }
									),
									AccessFlag.PUBLIC.mask()
								) { invokeCodeBuilder ->
									invokeCodeBuilder
										.aload(0)
										.getfield(
											lambdaDesc, "handle",
											ConstantDescs.CD_MethodHandle
										)
									parameterClasses.forEachIndexed { index, clazz ->
										invokeCodeBuilder.aload(index + 1)
										if (BackingSegment::class.isSuperclassOf(clazz)) {
											invokeCodeBuilder.invoke(BackingSegment::getSegment)
										} else if (Datatype::class.isSuperclassOf(clazz)) {
											val datatype = clazz.findAnnotation<DatatypeBacked>()!!.datatype
											when (val layout = linker.canonicalLayouts()[datatype]!!) {
												ValueLayout.JAVA_INT -> invokeCodeBuilder
													.checkcast(ConstantDescs.CD_Number)
													.invokevirtual(
														ConstantDescs.CD_Number, "intValue",
														MethodTypeDesc.of(ConstantDescs.CD_int)
													)

												else -> TODO(layout.toString())
											}
										} else if (clazz == Long::class) {
											invokeCodeBuilder
												.checkcast(ConstantDescs.CD_Number)
												.invokevirtual(
													ConstantDescs.CD_Number, "longValue",
													MethodTypeDesc.of(ConstantDescs.CD_long)
												)

										} else if (clazz != MemorySegment::class) TODO(clazz.toString())
									}
									invokeCodeBuilder.invokevirtual(
										ConstantDescs.CD_MethodHandle, "invokeExact",
										MethodTypeDesc.of(
											nativeDesc(linker, returnClass),
											parameterClasses.map { nativeDesc(linker, it) }
										)
									)
									if (Datatype::class.isSuperclassOf(returnClass)) {
										val layout = Datatype.getLayout(layouts, returnClass)
										val datatype = Datatype.getDatatype(layouts, returnClass as KClass<Datatype>)
										when (layout) {
											ValueLayout.JAVA_INT -> invokeCodeBuilder
												.new_(datatype.desc)
												.dup_x1()
												.swap()
												.invokespecial(
													datatype.desc, "<init>",
													MethodTypeDesc.of(
														ConstantDescs.CD_void,
														ConstantDescs.CD_int
													)
												)

											else -> TODO("$layout")
										}
									} else if (returnClass == Long::class) {
										invokeCodeBuilder
											.invokestatic(
												ConstantDescs.CD_Long, "valueOf",
												MethodTypeDesc.of(ConstantDescs.CD_Long, ConstantDescs.CD_long)
											)
									} else TODO(returnClass.toString())
									invokeCodeBuilder.areturn()
								}

								lambdaBuilder.withMethodBody(
									"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_MethodHandle),
									AccessFlag.PUBLIC.mask()
								) { lambdaCodeBuilder ->
									lambdaCodeBuilder
										.aload(0)
										.dup()
										.invokespecial(
											ConstantDescs.CD_Object, "<init>",
											MethodTypeDesc.of(ConstantDescs.CD_void)
										)
										.aload(1)
										.putfield(
											lambdaDesc, "handle",
											ConstantDescs.CD_MethodHandle
										)
										.return_()
								}
							}
							this.mhLookup.defineClass(lambdaBytes)

							codeBuilder
								.new_(lambdaDesc)
								.dup()
								.getstatic(
									thisDesc, "linker",
									Linker::class.desc
								)
								.aload(0)
								.getfield(
									thisDesc, "segment",
									MemorySegment::class.desc
								)
								.getstatic(
									ValueLayout::class.desc, "ADDRESS",
									AddressLayout::class.desc
								)
								.ldc(offset as ConstantDesc)
								.invokeinterface(
									MemorySegment::class.desc, "get",
									MethodTypeDesc.of(
										MemorySegment::class.desc,
										AddressLayout::class.desc,
										ConstantDescs.CD_long
									)
								)
								.dup()
								.invoke(MemorySegment::address)
								.lconst_0()
								.lcmp()
								.ifThen(Opcode.IFEQ) { block ->
									block
										.aconst_null()
										.areturn()
								}
								.functionDescriptor(
									{
										codeBuilder
											.getstatic(
												thisDesc, "linker",
												Linker::class.desc
											)
											.invoke(Linker::canonicalLayouts)
									},
									returnType,
									parameterTypes
								)
								.bipush(0)
								.anewarray(Linker.Option::class.desc)
								.invokeinterface(
									Linker::class.desc, "downcallHandle",
									MethodTypeDesc.of(
										ConstantDescs.CD_MethodHandle,
										MemorySegment::class.desc,
										FunctionDescriptor::class.desc,
										Linker.Option::class.desc.arrayType()
									)
								)
								.invokespecial(
									lambdaDesc, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_MethodHandle)
								)
								.areturn()
						} else {
							codeBuilder
								.new_(NotImplementedError::class.desc)
								.dup()
								.ldc("$propertyClass" as ConstantDesc)
								.invokespecial(
									NotImplementedError::class.desc, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_String)
								)
								.athrow()
						}
					}

					if (property is KMutableProperty<*>) {
						val setter = property.setter.javaMethod!!
						classBuilder.withMethodBody(
							setter.name, MethodTypeDesc.of(ConstantDescs.CD_void, propertyClass.desc),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
							if (Datatype::class.isSuperclassOf(propertyClass)) {
								when (val layout = Datatype.getLayout(layouts, propertyClass as KClass<Datatype>)) {
									ValueLayout.JAVA_INT -> {
										codeBuilder
											.aload(0)
											.getfield(
												thisDesc, "segment",
												MemorySegment::class.desc
											)
											.getstatic(
												ValueLayout::class.desc, "JAVA_INT",
												ValueLayout.OfInt::class.desc
											)
											.ldc(offset as ConstantDesc)
											.aload(1)
											.checkcast(ConstantDescs.CD_Number)
											.invokevirtual(
												ConstantDescs.CD_Number, "intValue",
												MethodTypeDesc.of(ConstantDescs.CD_int)
											)
											.invokeinterface(
												MemorySegment::class.desc, "set",
												MethodTypeDesc.of(
													ConstantDescs.CD_void,
													ValueLayout.OfInt::class.desc,
													ConstantDescs.CD_long,
													ConstantDescs.CD_int
												)
											)
									}

									ValueLayout.JAVA_SHORT -> {
										codeBuilder
											.aload(0)
											.getfield(
												thisDesc, "segment",
												MemorySegment::class.desc
											)
											.getstatic(
												ValueLayout::class.desc, "JAVA_SHORT",
												ValueLayout.OfShort::class.desc
											)
											.ldc(offset as ConstantDesc)
											.aload(1)
											.checkcast(ConstantDescs.CD_Number)
											.invokevirtual(
												ConstantDescs.CD_Number, "shortValue",
												MethodTypeDesc.of(ConstantDescs.CD_short)
											)
											.invokeinterface(
												MemorySegment::class.desc, "set",
												MethodTypeDesc.of(
													ConstantDescs.CD_void,
													ValueLayout.OfShort::class.desc,
													ConstantDescs.CD_long,
													ConstantDescs.CD_short
												)
											)
									}

									ValueLayout.JAVA_BYTE -> {
										codeBuilder
											.aload(0)
											.getfield(
												thisDesc, "segment",
												MemorySegment::class.desc
											)
											.getstatic(
												ValueLayout::class.desc, "JAVA_BYTE",
												ValueLayout.OfByte::class.desc
											)
											.ldc(offset as ConstantDesc)
											.aload(1)
											.checkcast(ConstantDescs.CD_Number)
											.invokevirtual(
												ConstantDescs.CD_Number, "byteValue",
												MethodTypeDesc.of(ConstantDescs.CD_byte)
											)
											.invokeinterface(
												MemorySegment::class.desc, "set",
												MethodTypeDesc.of(
													ConstantDescs.CD_void,
													ValueLayout.OfByte::class.desc,
													ConstantDescs.CD_long,
													ConstantDescs.CD_byte
												)
											)
									}

									else -> TODO(layout.toString())
								}
							} else if (Function::class.isSuperclassOf(propertyClass)) {
								val parameters = propertyType.arguments.toMutableList()
								val returnType = parameters.removeLast()
								codeBuilder.lambda(
									propertyClass, thisDesc, linker, offset,
									returnType.type!!,
									parameters.map { it.type!! }
								)
							} else if (BackingSegment::class.isSuperclassOf(propertyClass)) {
								codeBuilder
									.aload(0)
									.getfield(
										thisDesc, "segment",
										MemorySegment::class.desc
									)
									.getstatic(
										ValueLayout::class.desc, "ADDRESS",
										AddressLayout::class.desc
									)
									.ldc(offset as ConstantDesc)
									.aload(1)
									.invoke(BackingSegment::getSegment)
									.invokeinterface(
										MemorySegment::class.desc, "set",
										MethodTypeDesc.of(
											ConstantDescs.CD_void,
											AddressLayout::class.desc,
											ConstantDescs.CD_long,
											MemorySegment::class.desc
										)
									)
							} else if (propertyClass == Long::class) {
								codeBuilder
									.aload(0)
									.getfield(
										thisDesc, "segment",
										MemorySegment::class.desc
									)
									.getstatic(
										ValueLayout::class.desc, "JAVA_LONG",
										ValueLayout.OfLong::class.desc
									)
									.ldc(offset as ConstantDesc)
									.lload(1)
									.invokeinterface(
										MemorySegment::class.desc, "set",
										MethodTypeDesc.of(
											ConstantDescs.CD_void,
											ValueLayout.OfLong::class.desc,
											ConstantDescs.CD_long,
											ConstantDescs.CD_long
										)
									)
							} else if (propertyClass == MemorySegment::class) {
								codeBuilder
									.aload(0)
									.getfield(
										thisDesc, "segment",
										MemorySegment::class.desc
									)
									.getstatic(
										ValueLayout::class.desc, "ADDRESS",
										AddressLayout::class.desc
									)
									.ldc(offset as ConstantDesc)
									.aload(1)
									.invokeinterface(
										MemorySegment::class.desc, "set",
										MethodTypeDesc.of(
											ConstantDescs.CD_void,
											AddressLayout::class.desc,
											ConstantDescs.CD_long,
											MemorySegment::class.desc
										)
									)
							} else if (Mappable::class.isSuperclassOf(propertyClass)) {
								when (
									val layout = linker.canonicalLayouts()
										[propertyClass.findAnnotation<DatatypeBacked>()!!.datatype]
								) {
									ValueLayout.JAVA_INT -> {
										codeBuilder
											.aload(0)
											.getfield(
												thisDesc, "segment",
												MemorySegment::class.desc
											)
											.getstatic(
												ValueLayout::class.desc, "JAVA_INT",
												ValueLayout.OfInt::class.desc
											)
											.ldc(offset as ConstantDesc)
											.aload(1)
											.invoke(Mappable<*, *>::id.getter)
											.checkcast(ConstantDescs.CD_Integer)
											.invokevirtual(
												ConstantDescs.CD_Number, "intValue",
												MethodTypeDesc.of(ConstantDescs.CD_int)
											)
											.invokeinterface(
												MemorySegment::class.desc, "set",
												MethodTypeDesc.of(
													ConstantDescs.CD_void,
													ValueLayout.OfInt::class.desc,
													ConstantDescs.CD_long,
													ConstantDescs.CD_int
												)
											)
									}

									else -> TODO("$layout")
								}
							} else {
								codeBuilder
									.new_(NotImplementedError::class.desc)
									.dup()
									.ldc("$propertyClass" as ConstantDesc)
									.invokespecial(
										NotImplementedError::class.desc, "<init>",
										MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_String)
									)
									.athrow()
								return@withMethodBody
							}
							codeBuilder.return_()
						}
					}
				}

				classBuilder.withField(
					"linker", Linker::class.desc,
					AccessFlag.STATIC.mask() or AccessFlag.PUBLIC.mask()
				)

				classBuilder.withMethodBody(
					"deref", MethodTypeDesc.of(ConstantDescs.CD_Object),
					AccessFlag.PUBLIC.mask()
				) { codeBuilder ->
					codeBuilder
						.aload(0)
						.areturn()
				}

				classBuilder.withMethodBody(
					"<init>", MethodTypeDesc.of(ConstantDescs.CD_void),
					AccessFlag.PUBLIC.mask()
				) { codeBuilder ->
					@Suppress("CAST_NEVER_SUCCEEDS")
					codeBuilder
						.aload(0)
						.dup()
						.dup()
						.invokespecial(
							forClass.desc, "<init>",
							MethodTypeDesc.of(ConstantDescs.CD_void)
						)
						.invoke(Arena::ofAuto)
						.ldc(size as ConstantDesc)
						.ldc(alignment as ConstantDesc)
						.invokeinterface(
							Arena::class.desc, "allocate",
							MethodTypeDesc.of(MemorySegment::class.desc, ConstantDescs.CD_long, ConstantDescs.CD_long)
						)
						.putfield(
							thisDesc, "segment",
							MemorySegment::class.desc
						)
						.return_()
				}

				classBuilder.withMethodBody(
					"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, MemorySegment::class.desc),
					AccessFlag.PUBLIC.mask()
				) { codeBuilder ->
					@Suppress("CAST_NEVER_SUCCEEDS")
					codeBuilder
						.aload(0)
						.dup()
						.dup()
						.invokespecial(
							forClass.desc, "<init>",
							MethodTypeDesc.of(ConstantDescs.CD_void)
						)
						.aload(1)
						.ldc(Long.MAX_VALUE as ConstantDesc) // TODO : SAFER
						.invokeinterface(
							MemorySegment::class.desc, "reinterpret",
							MethodTypeDesc.of(MemorySegment::class.desc, ConstantDescs.CD_long)
						)
						.putfield(
							thisDesc, "segment",
							MemorySegment::class.desc
						)
						.return_()
				}
			}
			val hidden = this.mhLookup.defineClass(bytes)
			hidden!!.getField("linker").set(null, linker)
			this.loaded[type] = hidden
			@Suppress("UNCHECKED_CAST")
			return hidden
		}

		@Suppress("UNCHECKED_CAST")
		inline fun <reified T : Structure<T>> getStructure(linker: Linker): Class<T> = getStructureOfType(
			linker, typeOf<T>()
		) as Class<T>

		@JvmStatic
		fun <T : Structure<T>> getStructure(linker: Linker, forClass: Class<T>): Class<T> {
			@Suppress("UNCHECKED_CAST")
			return getStructureOfType(linker, forClass.kotlin.createType()) as Class<T>
		}
	}
}