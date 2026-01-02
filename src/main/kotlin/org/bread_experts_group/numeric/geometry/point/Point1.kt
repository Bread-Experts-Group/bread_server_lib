package org.bread_experts_group.numeric.geometry.point

import java.nio.ByteBuffer

data class Point1<T>(override val x: T) : Point<T>(x) {
	override val elementCount: Int = 1
	override fun putInto(buffer: ByteBuffer, at: Int, vararg arrangement: PointLabels) {
		arrangement.forEach {
			if (it != PointLabels.X) throw IllegalArgumentException("Unsupported arrangement element $it")
			this.byteBufferOperation(buffer, at, x)
		}
	}

	operator fun plus(other: Point1<T>): Point1<T> = Point1(addOperation(x, other.x))
	operator fun minus(other: Point1<T>): Point1<T> = Point1(subOperation(x, other.x))
	operator fun times(other: Point1<T>): Point1<T> = Point1(mulOperation(x, other.x))
	operator fun times(other: T): Point1<T> = Point1(mulOperation(x, other))
	operator fun div(other: Point1<T>): Point1<T> = Point1(divOperation(x, other.x))
	operator fun div(other: T): Point1<T> = Point1(divOperation(x, other))

	override fun toString(): String = "1($x)"
}