package org.bread_experts_group.numeric.geometry.point

import java.nio.ByteBuffer

data class Point3<T>(
	override val x: T,
	val y: T,
	val z: T
) : Point<T>(x) {
	override val elementCount: Int = 3
	override fun putInto(buffer: ByteBuffer, at: Int, vararg arrangement: PointLabels) {
		arrangement.forEach {
			when (it) {
				PointLabels.X -> this.byteBufferOperation(buffer, at, x)
				PointLabels.Y -> this.byteBufferOperation(buffer, at + elementSize, y)
				PointLabels.Z -> this.byteBufferOperation(buffer, at + (elementSize * 2), z)
				else -> throw IllegalArgumentException("Unsupported arrangement element $it")
			}
		}
	}

	operator fun plus(other: Point3<T>): Point3<T> = Point3(
		addOperation(x, other.x),
		addOperation(y, other.y),
		addOperation(z, other.z)
	)

	operator fun minus(other: Point3<T>): Point3<T> = Point3(
		subOperation(x, other.x),
		subOperation(y, other.y),
		addOperation(z, other.z)
	)

	operator fun times(other: Point3<T>): Point3<T> = Point3(
		mulOperation(x, other.x),
		mulOperation(y, other.y),
		addOperation(z, other.z)
	)

	operator fun times(other: T): Point3<T> = Point3(
		mulOperation(x, other),
		mulOperation(y, other),
		addOperation(z, other)
	)

	operator fun div(other: Point3<T>): Point3<T> = Point3(
		divOperation(x, other.x),
		divOperation(y, other.y),
		addOperation(z, other.z)
	)

	operator fun div(other: T): Point3<T> = Point3(
		divOperation(x, other),
		divOperation(y, other),
		addOperation(z, other)
	)

	override fun toString(): String = "3($x, $y, $z)"
}