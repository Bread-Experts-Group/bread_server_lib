package org.bread_experts_group.api.secure.cryptography.windows.feature.random

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.feature.random.RandomFeature
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsRandomFeature(
	override val expresses: FeatureExpression<RandomFeature>,
	private val algorithm: MemorySegment?,
) : RandomFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = Arena.ofConfined().use { tempArena ->
		fillRandom(algorithm, tempArena.allocate(16))
		true
	}

	override fun nextByte(): Byte = Arena.ofConfined().use { tempArena ->
		val area = tempArena.allocate(ValueLayout.JAVA_BYTE)
		fillRandom(algorithm, area)
		area.get(ValueLayout.JAVA_BYTE, 0)
	}

	override fun nextShort(): Short = Arena.ofConfined().use { tempArena ->
		val area = tempArena.allocate(ValueLayout.JAVA_SHORT)
		fillRandom(algorithm, area)
		area.get(ValueLayout.JAVA_SHORT, 0)
	}

	override fun nextInt(): Int = Arena.ofConfined().use { tempArena ->
		val area = tempArena.allocate(ValueLayout.JAVA_INT)
		fillRandom(algorithm, area)
		area.get(ValueLayout.JAVA_INT, 0)
	}

	override fun nextLong(): Long = Arena.ofConfined().use { tempArena ->
		val area = tempArena.allocate(ValueLayout.JAVA_LONG)
		fillRandom(algorithm, area)
		area.get(ValueLayout.JAVA_LONG, 0)
	}

	override fun fill(b: ByteArray, offset: Int, length: Int) = Arena.ofConfined().use { tempArena ->
		val area = tempArena.allocate(length.toLong())
		fillRandom(algorithm, area)
		MemorySegment.copy(area, ValueLayout.JAVA_BYTE, 0, b, offset, length)
	}
}