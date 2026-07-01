package org.bread_experts_group.model.natives

import java.lang.classfile.ClassFile
import java.lang.constant.*
import java.lang.foreign.MemoryLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.StringConcatFactory
import java.lang.reflect.AccessFlag
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

interface Datatype {
	companion object {
		private val classFile = ClassFile.of()
		private val mhLookup = MethodHandles.lookup()

		private val loaded = mutableMapOf<KClass<*>, MutableMap<MemoryLayout, Class<*>>>()

		fun <T : Any> getLayout(layouts: Map<String, MemoryLayout>, forClass: KClass<T>): MemoryLayout {
			val datatypeBacked = forClass.findAnnotation<DatatypeBacked>() ?: throw IllegalArgumentException(
				"$forClass must define the ${DatatypeBacked::class.qualifiedName} annotation"
			)
			val layout = layouts[datatypeBacked.datatype] ?: throw IllegalArgumentException(
				"Provided layout map does not prescribe a layout for \"${datatypeBacked.datatype}\""
			)
			return layout
		}

		@Synchronized
		fun <T : Datatype> getDatatype(layouts: Map<String, MemoryLayout>, forClass: KClass<T>): Class<T> {
			val layout = getLayout(layouts, forClass)
			var layoutsLoaded = this.loaded[forClass]
			if (layoutsLoaded != null) {
				val loaded = layoutsLoaded[layout]
				@Suppress("UNCHECKED_CAST")
				if (loaded != null) return loaded as Class<T>
			} else {
				layoutsLoaded = mutableMapOf()
				this.loaded[forClass] = layoutsLoaded
			}

			val thisDesc = ClassDesc.of(
				Datatype::class.qualifiedName!!.substringBeforeLast('.') + ".$" + forClass.simpleName
			)
			val bytes = this.classFile.build(thisDesc) { classBuilder ->
				val superClass = ClassDesc.of(forClass.qualifiedName!!)
				classBuilder.withSuperclass(superClass)
				classBuilder.withMethodBody(
					"<init>", MethodTypeDesc.of(ConstantDescs.CD_void),
					AccessFlag.PUBLIC.mask()
				) { codeBuilder ->
					codeBuilder
						.aload(0)
						.invokespecial(
							superClass, "<init>",
							MethodTypeDesc.of(ConstantDescs.CD_void)
						)
						.return_()
				}
				when (layout) {
					ValueLayout.JAVA_INT -> {
						classBuilder.withField(
							"value", ConstantDescs.CD_int,
							AccessFlag.PRIVATE.mask()
						)
						classBuilder.withMethodBody(
							"toLong", MethodTypeDesc.of(ConstantDescs.CD_long),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_int
								)
								.i2l()
								.lreturn()
						}
						classBuilder.withMethodBody(
							"toInt", MethodTypeDesc.of(ConstantDescs.CD_int),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_int
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toString", MethodTypeDesc.of(ConstantDescs.CD_String),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							@Suppress("CAST_NEVER_SUCCEEDS")
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_int
								)
								.invokedynamic(
									DynamicCallSiteDesc.of(
										MethodHandleDesc.ofMethod(
											DirectMethodHandleDesc.Kind.STATIC,
											ClassDesc.of(StringConcatFactory::class.java.name),
											"makeConcatWithConstants",
											MethodTypeDesc.of(
												ConstantDescs.CD_CallSite,
												ConstantDescs.CD_MethodHandles_Lookup,
												ConstantDescs.CD_String,
												ConstantDescs.CD_MethodType,
												ConstantDescs.CD_String,
												ConstantDescs.CD_Object.arrayType()
											)
										),
										"toString",
										MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_int),
										"${forClass.simpleName} 32-bits: \u0001" as ConstantDesc
									)
								)
								.areturn()
						}
						classBuilder.withMethodBody(
							"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_int),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.dup()
								.invokespecial(
									superClass, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void)
								)
								.iload(1)
								.putfield(
									thisDesc, "value",
									ConstantDescs.CD_int
								)
								.return_()
						}
					}

					ValueLayout.JAVA_CHAR -> {
						classBuilder.withField(
							"value", ConstantDescs.CD_char,
							AccessFlag.PRIVATE.mask()
						)
						classBuilder.withMethodBody(
							"toInt", MethodTypeDesc.of(ConstantDescs.CD_int),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_char
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toString", MethodTypeDesc.of(ConstantDescs.CD_String),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							@Suppress("CAST_NEVER_SUCCEEDS")
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_char
								)
								.invokedynamic(
									DynamicCallSiteDesc.of(
										MethodHandleDesc.ofMethod(
											DirectMethodHandleDesc.Kind.STATIC,
											ClassDesc.of(StringConcatFactory::class.java.name),
											"makeConcatWithConstants",
											MethodTypeDesc.of(
												ConstantDescs.CD_CallSite,
												ConstantDescs.CD_MethodHandles_Lookup,
												ConstantDescs.CD_String,
												ConstantDescs.CD_MethodType,
												ConstantDescs.CD_String,
												ConstantDescs.CD_Object.arrayType()
											)
										),
										"toString",
										MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_char),
										"${forClass.simpleName} 16-bits: \u0001" as ConstantDesc
									)
								)
								.areturn()
						}
						classBuilder.withMethodBody(
							"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_char),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.dup()
								.invokespecial(
									superClass, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void)
								)
								.iload(1)
								.putfield(
									thisDesc, "value",
									ConstantDescs.CD_char
								)
								.return_()
						}
					}

					ValueLayout.JAVA_SHORT -> {
						classBuilder.withField(
							"value", ConstantDescs.CD_short,
							AccessFlag.PRIVATE.mask()
						)
						classBuilder.withMethodBody(
							"toInt", MethodTypeDesc.of(ConstantDescs.CD_int),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_short
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toShort", MethodTypeDesc.of(ConstantDescs.CD_short),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_short
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toString", MethodTypeDesc.of(ConstantDescs.CD_String),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							@Suppress("CAST_NEVER_SUCCEEDS")
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_short
								)
								.invokedynamic(
									DynamicCallSiteDesc.of(
										MethodHandleDesc.ofMethod(
											DirectMethodHandleDesc.Kind.STATIC,
											ClassDesc.of(StringConcatFactory::class.java.name),
											"makeConcatWithConstants",
											MethodTypeDesc.of(
												ConstantDescs.CD_CallSite,
												ConstantDescs.CD_MethodHandles_Lookup,
												ConstantDescs.CD_String,
												ConstantDescs.CD_MethodType,
												ConstantDescs.CD_String,
												ConstantDescs.CD_Object.arrayType()
											)
										),
										"toString",
										MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_short),
										"${forClass.simpleName} 16-bits: \u0001" as ConstantDesc
									)
								)
								.areturn()
						}
						classBuilder.withMethodBody(
							"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_short),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.dup()
								.invokespecial(
									superClass, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void)
								)
								.iload(1)
								.putfield(
									thisDesc, "value",
									ConstantDescs.CD_short
								)
								.return_()
						}
					}

					ValueLayout.JAVA_BYTE -> {
						classBuilder.withField(
							"value", ConstantDescs.CD_byte,
							AccessFlag.PRIVATE.mask()
						)
						classBuilder.withMethodBody(
							"toInt", MethodTypeDesc.of(ConstantDescs.CD_int),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_byte
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toByte", MethodTypeDesc.of(ConstantDescs.CD_byte),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_byte
								)
								.ireturn()
						}
						classBuilder.withMethodBody(
							"toString", MethodTypeDesc.of(ConstantDescs.CD_String),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							@Suppress("CAST_NEVER_SUCCEEDS")
							codeBuilder
								.aload(0)
								.getfield(
									thisDesc, "value",
									ConstantDescs.CD_byte
								)
								.invokedynamic(
									DynamicCallSiteDesc.of(
										MethodHandleDesc.ofMethod(
											DirectMethodHandleDesc.Kind.STATIC,
											ClassDesc.of(StringConcatFactory::class.java.name),
											"makeConcatWithConstants",
											MethodTypeDesc.of(
												ConstantDescs.CD_CallSite,
												ConstantDescs.CD_MethodHandles_Lookup,
												ConstantDescs.CD_String,
												ConstantDescs.CD_MethodType,
												ConstantDescs.CD_String,
												ConstantDescs.CD_Object.arrayType()
											)
										),
										"toString",
										MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_byte),
										"${forClass.simpleName} 8-bits: \u0001" as ConstantDesc
									)
								)
								.areturn()
						}
						classBuilder.withMethodBody(
							"<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_byte),
							AccessFlag.PUBLIC.mask()
						) { codeBuilder ->
							codeBuilder
								.aload(0)
								.dup()
								.invokespecial(
									superClass, "<init>",
									MethodTypeDesc.of(ConstantDescs.CD_void)
								)
								.iload(1)
								.putfield(
									thisDesc, "value",
									ConstantDescs.CD_byte
								)
								.return_()
						}
					}

					else -> TODO(layout.toString())
				}
			}
			val hidden = this.mhLookup.defineClass(bytes)
			layoutsLoaded[layout] = hidden
			@Suppress("UNCHECKED_CAST")
			return hidden as Class<T>
		}

		@JvmStatic
		fun <T : Datatype> getDatatype(layouts: Map<String, MemoryLayout>, forClass: Class<T>): Class<T> {
			return getDatatype(layouts, forClass.kotlin)
		}

		operator fun <T : Any> Class<T>.invoke(vararg args: Any?): T {
			@Suppress("UNCHECKED_CAST")
			return this.constructors.first { it.parameterCount == args.size }.newInstance(*args) as T
		}
	}
}