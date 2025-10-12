package org.bread_experts_group.numeric.geometry

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class Vector4F(
	val x: Float, val y: Float, val z: Float, val w: Float
) {
	companion object {
		fun byteSize(): Long = ValueLayout.JAVA_FLOAT.byteSize() * 4
	}

	fun fillArray(segment: MemorySegment, offset: Long) {
		val offs = ValueLayout.JAVA_FLOAT.byteSize()
		segment.set(ValueLayout.JAVA_FLOAT, offset, x)
		segment.set(ValueLayout.JAVA_FLOAT, offset + offs, y)
		segment.set(ValueLayout.JAVA_FLOAT, offset + (offs * 2), z)
		segment.set(ValueLayout.JAVA_FLOAT, offset + (offs * 3), w)
	}
}