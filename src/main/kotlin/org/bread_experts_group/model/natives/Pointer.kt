package org.bread_experts_group.model.natives

import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import java.lang.foreign.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.typeOf

interface Pointer<T> : BackingSegment {
	companion object {
		@Suppress("UNCHECKED_CAST")
		inline fun <reified T : Any> of(linker: Linker): Pointer<T> = this.of(linker, typeOf<T>()) as Pointer<T>

		@Suppress("LocalVariableName", "UNCHECKED_CAST")
		@JvmStatic
		fun of(linker: Linker, type: KType): Pointer<*> {
			val Tclass = type.clazz
			val datatype = Tclass.findAnnotation<DatatypeBacked>()
			return if (datatype != null) linker.canonicalLayouts().let {
				DatatypePointerImpl<Any>(
					Arena.ofAuto().allocate(it[datatype.datatype]!!),
					Datatype.getDatatype(it, type.clazz as KClass<Datatype>),
					Datatype.getLayout(it, type.clazz)
				)
			} else if (Pointer::class.isSuperclassOf(Tclass) || Tclass == MemorySegment::class)
				PointerImpl<Any>(Arena.ofAuto().allocate(ValueLayout.ADDRESS), type)
			else TODO("ALPHA")
		}
	}

	fun deref(): T

	class PointerImpl<T : Any>(private val pointer: MemorySegment, private val type: KType) : Pointer<T> {
		override fun getSegment(): MemorySegment = pointer
		override fun deref(): T {
			@Suppress("UNCHECKED_CAST")
			return when (type.clazz) {
				Pointer::class -> PointerImpl<Any>(
					getSegment().get(ValueLayout.ADDRESS, 0),
					type.arguments.first().type!!
				) as T

				MemorySegment::class -> getSegment().get(ValueLayout.ADDRESS, 0) as T

				else -> TODO("$type")
			}
		}
	}

	class DatatypePointerImpl<T>(
		private val pointer: MemorySegment,
		private val datatype: Class<*>,
		private val layout: MemoryLayout
	) : Pointer<T> {
		override fun getSegment(): MemorySegment = pointer

		@Suppress("UNCHECKED_CAST")
		override fun deref(): T = when (layout) {
			ValueLayout.JAVA_INT -> datatype(pointer.get(ValueLayout.JAVA_INT, 0)) as T
			else -> TODO("$layout")
		}
	}
}