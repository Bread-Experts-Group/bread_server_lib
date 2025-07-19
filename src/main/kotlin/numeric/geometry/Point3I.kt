package org.bread_experts_group.numeric.geometry

data class Point3I(
	val x: Int,
	val y: Int,
	val z: Int
) {
	override fun toString(): String = "3I($x, $y, $z)"
}