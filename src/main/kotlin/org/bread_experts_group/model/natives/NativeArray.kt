package org.bread_experts_group.model.natives

import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import java.lang.foreign.Arena
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.reflect.KClass

sealed class NativeArray<T : Any>(
	protected var backingSegment: MemorySegment
) : Iterable<T>, Pointer<NativeArray<T>> {
	companion object {
		@JvmStatic
		fun <T> ofClass(
			layouts: Map<String, MemoryLayout>,
			size: Long,
			clazz: KClass<T>
		): NativeArray<T> where T : Datatype, T : Number = when (val layout = Datatype.getLayout(layouts, clazz)) {
			ValueLayout.JAVA_BYTE -> Byte(size, Datatype.getDatatype(layouts, clazz))
			ValueLayout.JAVA_CHAR -> Char(size, Datatype.getDatatype(layouts, clazz))
			ValueLayout.JAVA_INT -> I4(size, Datatype.getDatatype(layouts, clazz))
			else -> TODO(layout.toString())
		}

		@JvmStatic
		fun <T> fromClass(
			layouts: Map<String, MemoryLayout>,
			segment: MemorySegment,
			clazz: Class<T>
		): NativeArray<T> where T : Datatype, T : Number = when (
			val layout = Datatype.getLayout(layouts, clazz.kotlin)
		) {
			ValueLayout.JAVA_BYTE -> Byte(segment, Datatype.getDatatype(layouts, clazz.kotlin))
			ValueLayout.JAVA_CHAR -> Char(segment, Datatype.getDatatype(layouts, clazz.kotlin))
			ValueLayout.JAVA_INT -> I4(segment, Datatype.getDatatype(layouts, clazz.kotlin))
			else -> TODO(layout.toString())
		}

		inline fun <reified T> of(
			layouts: Map<String, MemoryLayout>,
			size: Long
		): NativeArray<T> where T : Datatype, T : Number = ofClass(layouts, size, T::class)

		inline fun <reified T> of(
			layouts: Map<String, MemoryLayout>,
			vararg elements: T
		): NativeArray<T> where T : Datatype, T : Number {
			val array = of<T>(layouts, elements.size.toLong())
			elements.forEachIndexed(array::set)
			return array
		}
	}

	override fun deref(): NativeArray<T> = this

	sealed class Value<T>(
		segment: MemorySegment,
		private val layout: MemoryLayout
	) : NativeArray<T>(segment) where T : Datatype, T : Number {
		override var size: Long = segment.byteSize() / layout.byteSize()
			protected set

		override fun reinterpret(size: Long): NativeArray<T> {
			this.backingSegment = this.backingSegment.reinterpret(layout.byteSize() * size)
			this.size = size
			return this
		}

		override fun getSegment(): MemorySegment = this.backingSegment

		override fun iterator(): Iterator<T> = object : Iterator<T> {
			var index = 0L
			override fun hasNext(): Boolean = index < size
			override fun next(): T = this@Value[index++]
		}
	}

	class Byte<T>(
		segment: MemorySegment,
		private val datatype: Class<T>
	) : Value<T>(segment, ValueLayout.JAVA_BYTE) where T : Datatype, T : Number {
		constructor(
			size: Long,
			datatype: Class<T>
		) : this(Arena.ofAuto().allocate(ValueLayout.JAVA_BYTE, size), datatype)

		override fun get(index: Long): T = datatype(
			this.backingSegment.getAtIndex(ValueLayout.JAVA_BYTE, index)
		)

		override fun set(index: Long, value: T): Unit = this.backingSegment.setAtIndex(
			ValueLayout.JAVA_BYTE, index, value.toByte()
		)
	}

	class Char<T>(
		segment: MemorySegment,
		private val datatype: Class<T>
	) : Value<T>(segment, ValueLayout.JAVA_CHAR) where T : Datatype, T : Number {
		constructor(
			size: Long,
			datatype: Class<T>
		) : this(Arena.ofAuto().allocate(ValueLayout.JAVA_CHAR, size), datatype)

		override fun get(index: Long): T = datatype(
			this.backingSegment.getAtIndex(ValueLayout.JAVA_CHAR, index)
		)

		override fun set(index: Long, value: T): Unit = this.backingSegment.setAtIndex(
			ValueLayout.JAVA_CHAR, index, Char(value.toInt())
		)
	}

	class I4<T>(
		segment: MemorySegment,
		private val datatype: Class<T>
	) : Value<T>(segment, ValueLayout.JAVA_INT) where T : Datatype, T : Number {
		constructor(
			size: Long,
			datatype: Class<T>
		) : this(Arena.ofAuto().allocate(ValueLayout.JAVA_INT, size), datatype)

		override fun get(index: Long): T = datatype(
			this.backingSegment.getAtIndex(ValueLayout.JAVA_INT, index)
		)

		override fun set(index: Long, value: T): Unit = this.backingSegment.setAtIndex(
			ValueLayout.JAVA_INT, index, value.toInt()
		)
	}

	abstract val size: Long

	abstract fun reinterpret(size: Long): NativeArray<T>

	abstract operator fun get(index: Long): T
	abstract operator fun set(index: Long, value: T)
	abstract override operator fun iterator(): Iterator<T>

	operator fun get(index: Int): T = this[index.toLong()]
	operator fun set(index: Int, value: T): Unit = this.set(index.toLong(), value)
}