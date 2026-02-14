package org.bread_experts_group.generic.numeric.geometry.point

import java.nio.ByteBuffer

data class Point2<T>(
	val x: T,
	val y: T
) : Point<T>(x) {
	override val elementCount: Int = 2
	override fun putInto(buffer: ByteBuffer, at: Int, vararg arrangement: PointLabels) {
		arrangement.forEach {
			when (it) {
				PointLabels.X -> this.byteBufferOperation(buffer, at, x)
				PointLabels.Y -> this.byteBufferOperation(buffer, at + elementSize, y)
				else -> throw IllegalArgumentException("Unsupported arrangement element $it")
			}
		}
	}

	operator fun plus(other: Point2<T>): Point2<T> = Point2(
		addOperation(x, other.x),
		addOperation(y, other.y)
	)

	operator fun minus(other: Point2<T>): Point2<T> = Point2(
		subOperation(x, other.x),
		subOperation(y, other.y)
	)

	operator fun times(other: Point2<T>): Point2<T> = Point2(
		mulOperation(x, other.x),
		mulOperation(y, other.y)
	)

	operator fun times(other: T): Point2<T> = Point2(
		mulOperation(x, other),
		mulOperation(y, other)
	)

	operator fun div(other: Point2<T>): Point2<T> = Point2(
		divOperation(x, other.x),
		divOperation(y, other.y)
	)

	operator fun div(other: T): Point2<T> = Point2(
		divOperation(x, other),
		divOperation(y, other)
	)

	override fun toString(): String = "2($x, $y)"
}