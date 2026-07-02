package org.bread_experts_group.model.natives

import java.lang.classfile.ClassBuilder
import java.lang.classfile.CodeBuilder
import java.lang.classfile.Opcode
import java.lang.classfile.TypeKind
import java.lang.classfile.constantpool.InterfaceMethodRefEntry
import java.lang.constant.*
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.AccessFlag
import java.lang.reflect.Method
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

fun ClassBuilder.property(property: KProperty<*>, getter: (CodeBuilder) -> Unit) {
	withMethodBody(
		property.getter.javaMethod!!.name,
		MethodTypeDesc.of(property.returnType.clazz.desc),
		property.getter.javaMethod!!.modifiers and (AccessFlag.ABSTRACT.mask().inv())
	) { methodBuilder -> getter(methodBuilder) }
}

fun ClassBuilder.property(
	property: KMutableProperty<*>,
	getter: CodeBuilder.() -> Unit, setter: (CodeBuilder) -> Unit
) {
	this.property(property as KProperty<*>, getter)
	withMethodBody(
		property.setter.javaMethod!!.name,
		MethodTypeDesc.of(ConstantDescs.CD_void, property.returnType.clazz.desc),
		property.setter.javaMethod!!.modifiers and (AccessFlag.ABSTRACT.mask().inv())
	) { methodBuilder -> setter(methodBuilder) }
}

fun CodeBuilder.get(field: KProperty<*>): CodeBuilder {
	val jField = field.javaField
	if (jField != null) TODO("F")
	val jMethod = field.getter.javaMethod!!
	return if (jMethod.modifiers and AccessFlag.STATIC.mask() != 0) this.invokestatic(
		jMethod.declaringClass.desc, jMethod.name,
		MethodTypeDesc.of(field.returnType.clazz.desc),
		jMethod.declaringClass.isInterface
	) else if (jMethod.declaringClass.isInterface) this.invokeinterface(
		jMethod.declaringClass.desc, jMethod.name,
		MethodTypeDesc.of(field.returnType.clazz.desc)
	) else this.invokevirtual(
		jMethod.declaringClass.desc, jMethod.name,
		MethodTypeDesc.of(field.returnType.clazz.desc)
	)
}

fun CodeBuilder.invoke(method: Method): CodeBuilder {
	val access = method.accessFlags()
	val ref = if (method.declaringClass.isInterface) this.constantPool().interfaceMethodRefEntry(
		method.declaringClass.desc, method.name,
		MethodType.methodType(method.returnType, method.parameterTypes).describeConstable().get()
	) else this.constantPool().methodRefEntry(
		method.declaringClass.desc, method.name,
		MethodType.methodType(method.returnType, method.parameterTypes).describeConstable().get()
	)

	return this.invoke(
		if (access.contains(AccessFlag.STATIC)) Opcode.INVOKESTATIC
		else if (ref is InterfaceMethodRefEntry) Opcode.INVOKEINTERFACE
		else Opcode.INVOKEVIRTUAL,
		ref
	)
}

fun CodeBuilder.invoke(method: KFunction<*>): CodeBuilder = invoke(method.javaMethod!!)

fun nativeDesc(linker: Linker, clazz: KClass<*>): ClassDesc {
	if (
		clazz == MemorySegment::class ||
		clazz == KMutableProperty::class ||
		clazz.java.isArray ||
		BackingSegment::class.isSuperclassOf(clazz)
	) return MemorySegment::class.desc
	val datatypeBacked = clazz.findAnnotation<DatatypeBacked>()
	if (datatypeBacked != null) return when (val layout = linker.canonicalLayouts()[datatypeBacked.datatype]!!) {
		ValueLayout.JAVA_INT -> ConstantDescs.CD_int
		ValueLayout.JAVA_BYTE -> ConstantDescs.CD_byte
		else -> TODO("$layout")
	}
	return when (clazz) {
		Unit::class -> ConstantDescs.CD_void
		Long::class -> ConstantDescs.CD_long
		else -> TODO("$clazz")
	}
}

@Suppress("CAST_NEVER_SUCCEEDS")
fun CodeBuilder.layout(type: KType, layoutStub: CodeBuilder.() -> CodeBuilder): CodeBuilder {
	val clazz = type.classifier!! as KClass<*>
	if (
		clazz == Pointer::class ||
		clazz == MemorySegment::class ||
		Function::class.isSuperclassOf(clazz)
	) {
		this.getstatic(
			ValueLayout::class.desc, "ADDRESS",
			AddressLayout::class.desc
		)
		return this
	}
	if (clazz == IndexedEnumSet::class) return this.layout(
		type.arguments[0].type!!,
		layoutStub
	)
	if (clazz == NativeArray::class) {
		val size = type.findAnnotation<ArraySize>()
		if (size != null) return this
			.ldc(size.size as ConstantDesc)
			.layout(type.arguments.first().type!!, layoutStub)
			.invoke(MemoryLayout::sequenceLayout)
		else {
			this.getstatic(
				ValueLayout::class.desc, "ADDRESS",
				AddressLayout::class.desc
			)
			return this
		}
	}
	if (Structure::class.isSuperclassOf(clazz)) {
		val properties = clazz.memberProperties.sortedBy { it.findAnnotation<Order>()!!.index }
		val offsetSlot = this.allocateLocal(TypeKind.LONG)
		val alignmentSlot = this.allocateLocal(TypeKind.LONG)
		val layoutSlot = this.allocateLocal(TypeKind.LONG)
		this
			.lconst_0()
			.lconst_0()
			.lstore(offsetSlot)
			.lstore(alignmentSlot)
			.new_(ArrayList::class.desc)
			.dup()
			.invokespecial(
				ArrayList::class.desc, "<init>",
				MethodTypeDesc.of(ConstantDescs.CD_void)
			)
		properties.groupBy { it.findAnnotation<Order>()!!.index }.forEach { (_, properties) ->
			val union = properties.size > 1
			if (union) {
				this
					.dup()
					.lload(offsetSlot)
					.new_(ArrayList::class.desc)
					.dup()
					.invokespecial(
						ArrayList::class.desc, "<init>",
						MethodTypeDesc.of(ConstantDescs.CD_void)
					)
				properties.forEach { property ->
					this
						.dup()
						.layout(
							if (property.returnType.clazz == Pointer::class) typeOf<MemorySegment>()
							else property.returnType,
							layoutStub
						)
						.invokevirtual(
							ArrayList::class.desc, "add",
							MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
						)
						.pop()
				}
				this
					.iconst_0()
					.anewarray(ConstantDescs.CD_Object)
					.invokevirtual(
						ArrayList::class.desc, "toArray",
						MethodTypeDesc.of(
							ConstantDescs.CD_Object.arrayType(),
							ConstantDescs.CD_Object.arrayType()
						)
					)
					.invoke(MemoryLayout::unionLayout)
					.dup()
					.astore(layoutSlot)
					.invoke(MemoryLayout::byteAlignment)
					.dup2()
					.lload(alignmentSlot)
					.lcmp()
					.ifThen(Opcode.IFGT) { thenBuilder ->
						thenBuilder
							.dup2()
							.lstore(alignmentSlot)
					}
					.lrem()
					.dup2()
					.lconst_0()
					.lcmp()
					.ifThenElse(Opcode.IFGT, { thenBuilder ->
						thenBuilder
							.aload(layoutSlot)
							.invoke(MemoryLayout::byteAlignment)
							.dup2_x2()
							.pop2()
							.lsub()
							.dup2()
							.lload(offsetSlot)
							.ladd()
							.lstore(offsetSlot)
							.invoke(MemoryLayout::paddingLayout)
							.dup2()
							.pop()
							.swap()
							.invokevirtual(
								ArrayList::class.desc, "add",
								MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
							)
							.pop()
					}, { elseBuilder ->
						elseBuilder.pop2()
					})
					.aload(layoutSlot)
					.dup()
					.invoke(MemoryLayout::byteSize)
					.lload(offsetSlot)
					.ladd()
					.lstore(offsetSlot)
					.invokevirtual(
						ArrayList::class.desc, "add",
						MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
					)
					.pop()
			} else {
				properties.forEach { property ->
					this
						.dup()
						.lload(offsetSlot)
						.layout(
							if (property.returnType.clazz == Pointer::class) typeOf<MemorySegment>()
							else property.returnType,
							layoutStub
						)
						.dup()
						.astore(layoutSlot)
						.invoke(MemoryLayout::byteAlignment)
						.dup2()
						.lload(alignmentSlot)
						.lcmp()
						.ifThen(Opcode.IFGT) { thenBuilder ->
							thenBuilder
								.dup2()
								.lstore(alignmentSlot)
						}
						.lrem()
						.dup2()
						.lconst_0()
						.lcmp()
						.ifThenElse(Opcode.IFGT, { thenBuilder ->
							thenBuilder
								.aload(layoutSlot)
								.invoke(MemoryLayout::byteAlignment)
								.dup2_x2()
								.pop2()
								.lsub()
								.dup2()
								.lload(offsetSlot)
								.ladd()
								.lstore(offsetSlot)
								.invoke(MemoryLayout::paddingLayout)
								.dup2()
								.pop()
								.swap()
								.invokevirtual(
									ArrayList::class.desc, "add",
									MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
								)
								.pop()
						}, { elseBuilder ->
							elseBuilder.pop2()
						})
						.aload(layoutSlot)
						.dup()
						.invoke(MemoryLayout::byteSize)
						.lload(offsetSlot)
						.ladd()
						.lstore(offsetSlot)
						.invokevirtual(
							ArrayList::class.desc, "add",
							MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
						)
						.pop()
				}
			}
		}
		this
			.lload(offsetSlot)
			.lload(alignmentSlot)
			.lrem()
			.dup2()
			.lconst_0()
			.lcmp()
			.ifThenElse(Opcode.IFGT, { thenBuilder ->
				thenBuilder
					.lload(alignmentSlot)
					.dup2_x2()
					.pop2()
					.lsub()
					.invoke(MemoryLayout::paddingLayout)
					.swap()
					.dup_x1()
					.swap()
					.invokevirtual(
						ArrayList::class.desc, "add",
						MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object)
					)
					.pop()
			}, { elseBuilder ->
				elseBuilder.pop2()
			})
			.iconst_0()
			.anewarray(ConstantDescs.CD_Object)
			.invokevirtual(
				ArrayList::class.desc, "toArray",
				MethodTypeDesc.of(
					ConstantDescs.CD_Object.arrayType(),
					ConstantDescs.CD_Object.arrayType()
				)
			)
			.invoke(MemoryLayout::structLayout)
		return this
	}
	val datatypeBacked = clazz.findAnnotation<DatatypeBacked>()
	if (datatypeBacked != null) {
		@Suppress("CAST_NEVER_SUCCEEDS")
		this.layoutStub()
			.ldc(datatypeBacked.datatype as ConstantDesc)
			.invokeinterface(
				ConstantDescs.CD_Map, "get",
				MethodTypeDesc.of(
					ConstantDescs.CD_Object,
					ConstantDescs.CD_Object
				)
			)
			.checkcast(MemoryLayout::class.desc)
		return this
	}
	when (clazz) {
		Long::class -> this.getstatic(
			ValueLayout::class.desc, "JAVA_LONG",
			ValueLayout.OfLong::class.desc
		)

		else -> TODO("$type")
	}
	return this
}

fun CodeBuilder.functionDescriptor(
	layoutStub: CodeBuilder.() -> CodeBuilder,
	returnType: KType, parameters: List<KType>
): CodeBuilder {
	val returnClass = returnType.classifier as KClass<*>
	if (returnClass == Unit::class) {
		this
			.sipush(parameters.size)
			.anewarray(MemoryLayout::class.desc)
		for ((i, element) in parameters.withIndex()) {
			this.dup().sipush(i)
			layout(element, layoutStub)
			this.aastore()
		}
		this.invoke(FunctionDescriptor::ofVoid)
	} else {
		layout(returnType, layoutStub)
		this
			.sipush(parameters.size)
			.anewarray(MemoryLayout::class.desc)
		for (i in parameters.indices) {
			this
				.dup()
				.sipush(i)
			layout(parameters[i], layoutStub)
			this.aastore()
		}
		this.invoke(FunctionDescriptor::of)
	}
	return this
}

enum class PointerPointed {
	STRUCTURE,
	SEGMENT
}

fun CodeBuilder.lambda(
	clazz: KClass<*>,

	thisDesc: ClassDesc,
	linker: Linker,
	offset: Long,

	returnType: KType,
	parameters: List<KType>
) {
	val returnClass = returnType.classifier!! as KClass<*>
	val parameterClasses = parameters.map { it.classifier as KClass<*> }
	@Suppress("CAST_NEVER_SUCCEEDS")
	this
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
		.getstatic(
			thisDesc, "linker",
			Linker::class.desc
		)
		.invoke(MethodHandles::lookup)
		.ldc(clazz.desc)
		.ldc("invoke" as ConstantDesc)
		.ldc(
			MethodTypeDesc.of(
				ConstantDescs.CD_Object,
				List(parameters.size) { ConstantDescs.CD_Object }
			)
		)
		.invoke(MethodHandles.Lookup::findVirtual)
		.aload(1)
		.invoke(MethodHandle::bindTo)
		.ldc(
			MethodTypeDesc.of(
				returnClass.desc,
				parameterClasses.map { it.desc }
			)
		)
		.invoke(MethodHandle::asType)
		.bipush(0)
		.bipush(parameters.size)
		.anewarray(ConstantDescs.CD_MethodHandle)
	parameterClasses.forEachIndexed { i, clazz ->
		this.dup().bipush(i)

		var clazz = clazz
		var pointing: PointerPointed? = null
		if (clazz == Pointer::class) {
			val pointerClass = parameters[i].arguments.first().type!!.clazz
			pointing = if (Structure::class.isSuperclassOf(pointerClass)) {
				clazz = pointerClass
				PointerPointed.STRUCTURE
			} else if (pointerClass == MemorySegment::class) {
				PointerPointed.SEGMENT
			} else TODO("$pointerClass")
		}

		if (pointing == PointerPointed.STRUCTURE || Structure::class.isSuperclassOf(clazz)) {
			this
				.invoke(MethodHandles::lookup)
				.getstatic(
					thisDesc, "linker",
					Linker::class.desc
				)
				.ldc(clazz.desc)
				.invokestatic(
					Structure::class.desc, "getStructure",
					MethodTypeDesc.of(ConstantDescs.CD_Class, Linker::class.desc, ConstantDescs.CD_Class),
					true
				)
				.ldc(
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						MemorySegment::class.desc
					)
				)
				.invoke(MethodHandles.Lookup::findConstructor)
				.dup()
				.invoke(MethodHandle::type)
				.ldc(clazz.desc)
				.invoke(MethodType::changeReturnType)
				.invoke(MethodHandle::asType)
		} else if (Datatype::class.isSuperclassOf(clazz)) {
			val datatype = linker.canonicalLayouts()[clazz.findAnnotation<DatatypeBacked>()!!.datatype]!!
			this
				.invoke(MethodHandles::lookup)
				.getstatic(
					thisDesc, "linker",
					Linker::class.desc
				)
				.invoke(Linker::canonicalLayouts)
				.ldc(clazz.desc)
				.invokestatic(
					Datatype::class.desc, "getDatatype",
					MethodTypeDesc.of(ConstantDescs.CD_Class, ConstantDescs.CD_Map, ConstantDescs.CD_Class),
					true
				)
				.ldc(
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						when (datatype) {
							ValueLayout.JAVA_INT -> ConstantDescs.CD_int
							else -> TODO(datatype.toString())
						}
					)
				)
				.invoke(MethodHandles.Lookup::findConstructor)
				.dup()
				.invoke(MethodHandle::type)
				.ldc(clazz.desc)
				.invoke(MethodType::changeReturnType)
				.invoke(MethodHandle::asType)
		} else if (clazz == NativeArray::class) {
			val arrayType = parameters[i].arguments[0]
			@Suppress("CAST_NEVER_SUCCEEDS")
			this
				.invoke(MethodHandles::lookup)
				.ldc(NativeArray::class.desc)
				.ldc("fromClass" as ConstantDesc)
				.ldc(
					MethodTypeDesc.of(
						NativeArray::class.desc,
						ConstantDescs.CD_Map, MemorySegment::class.desc, ConstantDescs.CD_Class
					)
				)
				.invoke(MethodHandles.Lookup::findStatic)
				.ldc(
					MethodTypeDesc.of(
						NativeArray::class.desc,
						ConstantDescs.CD_Map, ConstantDescs.CD_Class, MemorySegment::class.desc
					)
				)
				.bipush(3)
				.newarray(TypeKind.INT)
				.dup()
				.bipush(0)
				.bipush(0)
				.iastore()
				.dup()
				.bipush(1)
				.bipush(2)
				.iastore()
				.dup()
				.bipush(2)
				.bipush(1)
				.iastore()
				.invoke(MethodHandles::permuteArguments)
				.bipush(0)
				.bipush(2)
				.anewarray(ConstantDescs.CD_Object)
				.dup()
				.bipush(1)
				.getstatic(
					thisDesc, "linker",
					Linker::class.desc
				)
				.invoke(Linker::canonicalLayouts)
				.aastore()
				.dup()
				.bipush(1)
				.ldc((arrayType.type!!.classifier as KClass<*>).desc)
				.aastore()
				.invoke(MethodHandles::insertArguments)
		} else if (clazz == Long::class || clazz == MemorySegment::class) {
			this.aconst_null()
		} else if (pointing == PointerPointed.SEGMENT) {
			this.aconst_null()
//			TODO("Setting")
		} else {
			TODO("* $clazz")
		}
		this.aastore()
	}
	this.invoke(MethodHandles::filterArguments)
	if (returnClass != Unit::class) {
		if (Datatype::class.isSuperclassOf(returnClass)) {
			@Suppress("UNCHECKED_CAST")
			val layout = Datatype.getLayout(linker.canonicalLayouts(), returnClass)
			when (layout) {
				ValueLayout.JAVA_INT -> this.loadConstant(
					MethodHandleDesc.ofMethod(
						DirectMethodHandleDesc.Kind.VIRTUAL,
						returnClass.desc,
						"intValue",
						MethodTypeDesc.of(ConstantDescs.CD_int)
					)
				)

				else -> TODO("$layout")
			}
			this.invoke(MethodHandles::filterReturnValue)
		} else if (returnClass == Long::class) {
//			this.loadConstant(
//				MethodHandleDesc.ofMethod(
//					DirectMethodHandleDesc.Kind.VIRTUAL,
//					ConstantDescs.CD_Long,
//					"longValue",
//					MethodTypeDesc.of(ConstantDescs.CD_long)
//				)
//			)
		} else {
			TODO("$returnClass")
		}
	}
	this
		.functionDescriptor(
			{
				this
					.getstatic(
						thisDesc, "linker",
						Linker::class.desc
					)
					.invoke(Linker::canonicalLayouts)
			},
			returnType,
			parameters
		)
		.invoke(Arena::global) // TODO better arena
		.bipush(0)
		.anewarray(Linker.Option::class.desc)
		.invoke(Linker::upcallStub)
		.invokeinterface(
			MemorySegment::class.desc, "set",
			MethodTypeDesc.of(
				ConstantDescs.CD_void,
				AddressLayout::class.desc,
				ConstantDescs.CD_long,
				MemorySegment::class.desc
			)
		)
}