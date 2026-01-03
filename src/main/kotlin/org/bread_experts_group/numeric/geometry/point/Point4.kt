package org.bread_experts_group.numeric.geometry.point

import java.nio.ByteBuffer

data class Point4<T>(
	val x: T,
	val y: T,
	val z: T,
	val w: T
) : Point<T>(x) {
	override val elementCount: Int = 3
	override fun putInto(buffer: ByteBuffer, at: Int, vararg arrangement: PointLabels) {
		arrangement.forEach {
			when (it) {
				PointLabels.X -> this.byteBufferOperation(buffer, at, x)
				PointLabels.Y -> this.byteBufferOperation(buffer, at + elementSize, y)
				PointLabels.Z -> this.byteBufferOperation(buffer, at + (elementSize * 2), z)
				PointLabels.W -> this.byteBufferOperation(buffer, at + (elementSize * 3), w)
			}
		}
	}

	operator fun plus(other: Point4<T>): Point4<T> = Point4(
		addOperation(x, other.x),
		addOperation(y, other.y),
		addOperation(z, other.z),
		addOperation(w, other.w)
	)

	operator fun minus(other: Point4<T>): Point4<T> = Point4(
		subOperation(x, other.x),
		subOperation(y, other.y),
		addOperation(z, other.z),
		addOperation(w, other.w)
	)

	operator fun times(other: Point4<T>): Point4<T> = Point4(
		mulOperation(x, other.x),
		mulOperation(y, other.y),
		addOperation(z, other.z),
		addOperation(w, other.w)
	)

	operator fun times(other: T): Point4<T> = Point4(
		mulOperation(x, other),
		mulOperation(y, other),
		addOperation(z, other),
		addOperation(w, other)
	)

	operator fun div(other: Point4<T>): Point4<T> = Point4(
		divOperation(x, other.x),
		divOperation(y, other.y),
		addOperation(z, other.z),
		addOperation(w, other.w)
	)

	operator fun div(other: T): Point4<T> = Point4(
		divOperation(x, other),
		divOperation(y, other),
		addOperation(z, other),
		addOperation(w, other)
	)

	override fun toString(): String = "4($x, $y, $z, $w)"
}