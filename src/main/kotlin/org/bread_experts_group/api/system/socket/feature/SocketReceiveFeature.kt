package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

abstract class SocketReceiveFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketReceiveFeature<F, D>>
) : SocketFeatureImplementation<SocketReceiveFeature<F, D>>() {
	abstract fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: F
	): List<D>

	fun receiveSegment(
		data: MemorySegment,
		vararg features: F
	): List<D> = gatherSegments(listOf(data), *features)

	fun gatherBytes(
		data: Collection<ByteArray>,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		val allocated = data.associateWith { tempArena.allocate(it.size.toLong()) }
		val supported = gatherSegments(allocated.values, *features)
		allocated.forEach { (array, segment) ->
			MemorySegment.copy(
				segment, ValueLayout.JAVA_BYTE, 0,
				array, 0, array.size
			)
		}
		return supported
	}

	fun receiveBytes(
		data: ByteArray,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		val allocated = tempArena.allocate(data.size.toLong())
		val supported = receiveSegment(allocated, *features)
		MemorySegment.copy(
			allocated, ValueLayout.JAVA_BYTE, 0,
			data, 0, data.size
		)
		return supported
	}
}