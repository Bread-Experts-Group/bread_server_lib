package org.bread_experts_group.numeric.geometry

import java.nio.ByteBuffer

data class Point3D(
	val x: Double,
	val y: Double,
	val z: Double
) {
	override fun toString(): String = "3D($x, $y, $z)"
}

fun ByteBuffer.put3D(p: Point3D): ByteBuffer {
	this.putDouble(p.x)
	this.putDouble(p.y)
	this.putDouble(p.z)
	return this
}