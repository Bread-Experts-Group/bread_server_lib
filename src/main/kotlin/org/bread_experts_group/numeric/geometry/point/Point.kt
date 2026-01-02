package org.bread_experts_group.numeric.geometry.point

import java.nio.ByteBuffer

abstract class Point<T>(open val x: T) {
	protected val elementSize = when (x) {
		is Byte -> 1
		is Short -> 2
		is Int, Float -> 4
		is Long, Double -> 8
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}

	protected val byteBufferOperation = when (x) {
		is Byte -> { buffer: ByteBuffer, at: Int, e: T -> buffer.put(at, e as Byte) }
		is Short -> { buffer: ByteBuffer, at: Int, e: T -> buffer.putShort(at, e as Short) }
		is Int -> { buffer: ByteBuffer, at: Int, e: T -> buffer.putInt(at, e as Int) }
		is Float -> { buffer: ByteBuffer, at: Int, e: T -> buffer.putFloat(at, e as Float) }
		is Long -> { buffer: ByteBuffer, at: Int, e: T -> buffer.putLong(at, e as Long) }
		is Double -> { buffer: ByteBuffer, at: Int, e: T -> buffer.putDouble(at, e as Double) }
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}

	abstract val elementCount: Int
	abstract fun putInto(buffer: ByteBuffer, at: Int, vararg arrangement: PointLabels)
	fun putInto(buffer: ByteBuffer, vararg arrangement: PointLabels) {
		this.putInto(buffer, buffer.position(), *arrangement)
		buffer.position(buffer.position() + (elementCount * elementSize))
	}

	@Suppress("UNCHECKED_CAST")
	protected val addOperation: (T, T) -> T = when (x) {
		is Byte -> { a: T, b: T -> ((a as Byte) + (b as Byte)).toByte() as T }
		is Short -> { a: T, b: T -> ((a as Short) + (b as Short)).toShort() as T }
		is Int -> { a: T, b: T -> ((a as Int) + (b as Int)) as T }
		is Float -> { a: T, b: T -> ((a as Float) + (b as Float)) as T }
		is Long -> { a: T, b: T -> ((a as Long) + (b as Long)) as T }
		is Double -> { a: T, b: T -> ((a as Double) + (b as Double)) as T }
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}

	@Suppress("UNCHECKED_CAST")
	protected val subOperation: (T, T) -> T = when (x) {
		is Byte -> { a: T, b: T -> ((a as Byte) - (b as Byte)).toByte() as T }
		is Short -> { a: T, b: T -> ((a as Short) - (b as Short)).toShort() as T }
		is Int -> { a: T, b: T -> ((a as Int) - (b as Int)) as T }
		is Float -> { a: T, b: T -> ((a as Float) - (b as Float)) as T }
		is Long -> { a: T, b: T -> ((a as Long) - (b as Long)) as T }
		is Double -> { a: T, b: T -> ((a as Double) - (b as Double)) as T }
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}

	@Suppress("UNCHECKED_CAST")
	protected val mulOperation: (T, T) -> T = when (x) {
		is Byte -> { a: T, b: T -> ((a as Byte) * (b as Byte)).toByte() as T }
		is Short -> { a: T, b: T -> ((a as Short) * (b as Short)).toShort() as T }
		is Int -> { a: T, b: T -> ((a as Int) * (b as Int)) as T }
		is Float -> { a: T, b: T -> ((a as Float) * (b as Float)) as T }
		is Long -> { a: T, b: T -> ((a as Long) * (b as Long)) as T }
		is Double -> { a: T, b: T -> ((a as Double) * (b as Double)) as T }
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}

	@Suppress("UNCHECKED_CAST")
	protected val divOperation: (T, T) -> T = when (x) {
		is Byte -> { a: T, b: T -> ((a as Byte) / (b as Byte)).toByte() as T }
		is Short -> { a: T, b: T -> ((a as Short) / (b as Short)).toShort() as T }
		is Int -> { a: T, b: T -> ((a as Int) / (b as Int)) as T }
		is Float -> { a: T, b: T -> ((a as Float) / (b as Float)) as T }
		is Long -> { a: T, b: T -> ((a as Long) / (b as Long)) as T }
		is Double -> { a: T, b: T -> ((a as Double) / (b as Double)) as T }
		null -> throw IllegalStateException()
		else -> throw IllegalArgumentException("Unsupported element ${x!!::class.qualifiedName}")
	}
}