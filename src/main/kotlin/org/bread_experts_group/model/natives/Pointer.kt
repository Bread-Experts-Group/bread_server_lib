package org.bread_experts_group.model.natives

import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.typeOf

interface Pointer<T> : BackingSegment {
	companion object {
		@Suppress("UNCHECKED_CAST")
		inline fun <reified T : Any> of(linker: Linker): Pointer<T> = this.of(linker, typeOf<T>()) as Pointer<T>

		@Suppress("LocalVariableName")
		@JvmStatic
		fun of(linker: Linker, type: KType): Pointer<*> {
			val Tclass = type.clazz
			val datatype = Tclass.findAnnotation<DatatypeBacked>()
			val segment = if (datatype != null) Arena.ofAuto().allocate(linker.canonicalLayouts()[datatype.datatype]!!)
			else if (Pointer::class.isSuperclassOf(Tclass) || Tclass == MemorySegment::class)
				Arena.ofAuto().allocate(ValueLayout.ADDRESS)
			else TODO("ALPHA")
			return PointerImpl<Any>(segment, type)
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
}