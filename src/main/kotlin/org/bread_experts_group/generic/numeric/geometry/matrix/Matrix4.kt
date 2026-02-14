package org.bread_experts_group.generic.numeric.geometry.matrix

import org.bread_experts_group.generic.numeric.geometry.point.Point4
import kotlin.math.tan

class Matrix4<T>(
	val m0: Point4<T>,
	val m1: Point4<T>,
	val m2: Point4<T>,
	val m3: Point4<T>
) {
	companion object {
		fun perspective(fovYRadians: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4<Float> {
			val f = 1f / tan(fovYRadians / 2f)
			return Matrix4(
				Point4(f / aspect, 0f, 0f, 0f),
				Point4(0f, f, 0f, 0f),
				Point4(0f, 0f, (zFar + zNear) / (zNear - zFar), -1f),
				Point4(0f, 0f, (2f * zFar * zNear) / (zNear - zFar), 0f)
			)
		}

		fun fromFloat(d: Float) = Matrix4(
			Point4(d, 0f, 0f, 0f),
			Point4(0f, d, 0f, 0f),
			Point4(0f, 0f, d, 0f),
			Point4(0f, 0f, 0f, d)
		)
	}

//	fun fillArray(segment: MemorySegment, offset: Long) {
//		val offs = Point4F.byteSize()
//		m0.fillArray(segment, offset)
//		m1.fillArray(segment, offset + offs)
//		m2.fillArray(segment, offset + (offs * 2))
//		m3.fillArray(segment, offset + (offs * 3))
//	}
//
//	operator fun plus(v: Point4F): Matrix4F = this * Matrix4F(
//		Point4F(1f, 0f, 0f, 0f),
//		Point4F(0f, 1f, 0f, 0f),
//		Point4F(0f, 0f, 1f, 0f),
//		Point4F(v.x, v.y, v.z, v.w)
//	)
//
//	operator fun times(m: Matrix4F): Matrix4F = Matrix4F(
//		Point4F(
//			m0.x * m.m0.x + m1.x * m.m0.y + m2.x * m.m0.z + m3.x * m.m0.w,
//			m0.y * m.m0.x + m1.y * m.m0.y + m2.y * m.m0.z + m3.y * m.m0.w,
//			m0.z * m.m0.x + m1.z * m.m0.y + m2.z * m.m0.z + m3.z * m.m0.w,
//			m0.w * m.m0.x + m1.w * m.m0.y + m2.w * m.m0.z + m3.w * m.m0.w
//		),
//		Point4F(
//			m0.x * m.m1.x + m1.x * m.m1.y + m2.x * m.m1.z + m3.x * m.m1.w,
//			m0.y * m.m1.x + m1.y * m.m1.y + m2.y * m.m1.z + m3.y * m.m1.w,
//			m0.z * m.m1.x + m1.z * m.m1.y + m2.z * m.m1.z + m3.z * m.m1.w,
//			m0.w * m.m1.x + m1.w * m.m1.y + m2.w * m.m1.z + m3.w * m.m1.w
//		),
//		Point4F(
//			m0.x * m.m2.x + m1.x * m.m2.y + m2.x * m.m2.z + m3.x * m.m2.w,
//			m0.y * m.m2.x + m1.y * m.m2.y + m2.y * m.m2.z + m3.y * m.m2.w,
//			m0.z * m.m2.x + m1.z * m.m2.y + m2.z * m.m2.z + m3.z * m.m2.w,
//			m0.w * m.m2.x + m1.w * m.m2.y + m2.w * m.m2.z + m3.w * m.m2.w
//		),
//		Point4F(
//			m0.x * m.m3.x + m1.x * m.m3.y + m2.x * m.m3.z + m3.x * m.m3.w,
//			m0.y * m.m3.x + m1.y * m.m3.y + m2.y * m.m3.z + m3.y * m.m3.w,
//			m0.z * m.m3.x + m1.z * m.m3.y + m2.z * m.m3.z + m3.z * m.m3.w,
//			m0.w * m.m3.x + m1.w * m.m3.y + m2.w * m.m3.z + m3.w * m.m3.w
//		)
//	)
//
//	fun rotateX(radians: Float): Matrix4F {
//		val cos = cos(radians)
//		val sin = sin(radians)
//		return Matrix4F(
//			Point4F(1f, 0f, 0f, 0f),
//			Point4F(0f, cos, sin, 0f),
//			Point4F(0f, -sin, cos, 0f),
//			Point4F(0f, 0f, 0f, 1f),
//		) * this
//	}
//
//	fun rotateY(radians: Float): Matrix4F {
//		val cos = cos(radians)
//		val sin = sin(radians)
//		return Matrix4F(
//			Point4F(cos, 0f, -sin, 0f),
//			Point4F(0f, 1f, 0f, 0f),
//			Point4F(sin, 0f, cos, 0f),
//			Point4F(0f, 0f, 0f, 1f),
//		) * this
//	}
//
//	fun rotateZ(radians: Float): Matrix4F {
//		val cos = cos(radians)
//		val sin = sin(radians)
//		return Matrix4F(
//			Point4F(cos, -sin, 0f, 0f),
//			Point4F(sin, cos, 0f, 0f),
//			Point4F(0f, 0f, 1f, 0f),
//			Point4F(0f, 0f, 0f, 1f),
//		) * this
//	}
}