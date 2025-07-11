package org.bread_experts_group.coder.format.parse.png

data class Point2D(
	val x: Double,
	val y: Double
) {
	override fun toString(): String = "($x, $y)"
}