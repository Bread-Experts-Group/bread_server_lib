package org.bread_experts_group.numeric.geometry

import java.lang.foreign.MemorySegment
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Matrix4F(
	val m0: Vector4F = Vector4F(0f, 0f, 0f, 0f),
	val m1: Vector4F = Vector4F(0f, 0f, 0f, 0f),
	val m2: Vector4F = Vector4F(0f, 0f, 0f, 0f),
	val m3: Vector4F = Vector4F(0f, 0f, 0f, 0f)
) {
	companion object {
		fun byteSize(): Long = Vector4F.byteSize() * 4
		fun perspective(fovYRadians: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4F {
			val f = 1f / tan(fovYRadians / 2f)
			return Matrix4F(
				Vector4F(f / aspect, 0f, 0f, 0f),
				Vector4F(0f, f, 0f, 0f),
				Vector4F(0f, 0f, (zFar + zNear) / (zNear - zFar), -1f),
				Vector4F(0f, 0f, (2f * zFar * zNear) / (zNear - zFar), 0f)
			)
		}
	}

	constructor(d: Float) : this(
		Vector4F(d, 0f, 0f, 0f),
		Vector4F(0f, d, 0f, 0f),
		Vector4F(0f, 0f, d, 0f),
		Vector4F(0f, 0f, 0f, d)
	)

	fun fillArray(segment: MemorySegment, offset: Long) {
		val offs = Vector4F.byteSize()
		m0.fillArray(segment, offset)
		m1.fillArray(segment, offset + offs)
		m2.fillArray(segment, offset + (offs * 2))
		m3.fillArray(segment, offset + (offs * 3))
	}

	operator fun plus(v: Vector4F): Matrix4F = this * Matrix4F(
		Vector4F(1f, 0f, 0f, 0f),
		Vector4F(0f, 1f, 0f, 0f),
		Vector4F(0f, 0f, 1f, 0f),
		Vector4F(v.x, v.y, v.z, v.w)
	)

	operator fun times(m: Matrix4F): Matrix4F = Matrix4F(
		Vector4F(
			m0.x * m.m0.x + m1.x * m.m0.y + m2.x * m.m0.z + m3.x * m.m0.w,
			m0.y * m.m0.x + m1.y * m.m0.y + m2.y * m.m0.z + m3.y * m.m0.w,
			m0.z * m.m0.x + m1.z * m.m0.y + m2.z * m.m0.z + m3.z * m.m0.w,
			m0.w * m.m0.x + m1.w * m.m0.y + m2.w * m.m0.z + m3.w * m.m0.w
		),
		Vector4F(
			m0.x * m.m1.x + m1.x * m.m1.y + m2.x * m.m1.z + m3.x * m.m1.w,
			m0.y * m.m1.x + m1.y * m.m1.y + m2.y * m.m1.z + m3.y * m.m1.w,
			m0.z * m.m1.x + m1.z * m.m1.y + m2.z * m.m1.z + m3.z * m.m1.w,
			m0.w * m.m1.x + m1.w * m.m1.y + m2.w * m.m1.z + m3.w * m.m1.w
		),
		Vector4F(
			m0.x * m.m2.x + m1.x * m.m2.y + m2.x * m.m2.z + m3.x * m.m2.w,
			m0.y * m.m2.x + m1.y * m.m2.y + m2.y * m.m2.z + m3.y * m.m2.w,
			m0.z * m.m2.x + m1.z * m.m2.y + m2.z * m.m2.z + m3.z * m.m2.w,
			m0.w * m.m2.x + m1.w * m.m2.y + m2.w * m.m2.z + m3.w * m.m2.w
		),
		Vector4F(
			m0.x * m.m3.x + m1.x * m.m3.y + m2.x * m.m3.z + m3.x * m.m3.w,
			m0.y * m.m3.x + m1.y * m.m3.y + m2.y * m.m3.z + m3.y * m.m3.w,
			m0.z * m.m3.x + m1.z * m.m3.y + m2.z * m.m3.z + m3.z * m.m3.w,
			m0.w * m.m3.x + m1.w * m.m3.y + m2.w * m.m3.z + m3.w * m.m3.w
		)
	)

	fun rotateX(radians: Float): Matrix4F {
		val cos = cos(radians)
		val sin = sin(radians)
		return Matrix4F(
			Vector4F(1f, 0f, 0f, 0f),
			Vector4F(0f, cos, sin, 0f),
			Vector4F(0f, -sin, cos, 0f),
			Vector4F(0f, 0f, 0f, 1f),
		) * this
	}

	fun rotateY(radians: Float): Matrix4F {
		val cos = cos(radians)
		val sin = sin(radians)
		return Matrix4F(
			Vector4F(cos, 0f, -sin, 0f),
			Vector4F(0f, 1f, 0f, 0f),
			Vector4F(sin, 0f, cos, 0f),
			Vector4F(0f, 0f, 0f, 1f),
		) * this
	}

	fun rotateZ(radians: Float): Matrix4F {
		val cos = cos(radians)
		val sin = sin(radians)
		return Matrix4F(
			Vector4F(cos, -sin, 0f, 0f),
			Vector4F(sin, cos, 0f, 0f),
			Vector4F(0f, 0f, 1f, 0f),
			Vector4F(0f, 0f, 0f, 1f),
		) * this
	}
}