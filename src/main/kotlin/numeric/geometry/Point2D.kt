package org.bread_experts_group.numeric.geometry

data class Point2D(
	val x: Double,
	val y: Double
) {
	override fun toString(): String = "2D($x, $y)"
}