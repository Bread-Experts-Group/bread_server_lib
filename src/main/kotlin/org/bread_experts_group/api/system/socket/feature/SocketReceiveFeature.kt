package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

abstract class SocketReceiveFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketReceiveFeature<F, D>>
) : SocketFeatureImplementation<SocketReceiveFeature<F, D>>() {
	abstract fun gatherS(
		data: Collection<MemorySegment>,
		vararg features: F
	): List<D>

	fun receiveS(
		data: MemorySegment,
		vararg features: F
	): List<D> = gatherS(listOf(data), *features)

	fun gather(
		data: Collection<ByteArray>,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		val allocated = data.associateWith { tempArena.allocate(it.size.toLong()) }
		val supported = gatherS(allocated.values, *features)
		allocated.forEach { (array, segment) ->
			MemorySegment.copy(
				segment, ValueLayout.JAVA_BYTE, 0,
				array, 0, array.size
			)
		}
		return supported
	}

	fun receive(
		data: ByteArray,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		val allocated = tempArena.allocate(data.size.toLong())
		val supported = receiveS(allocated, *features)
		MemorySegment.copy(
			allocated, ValueLayout.JAVA_BYTE, 0,
			data, 0, data.size
		)
		return supported
	}
}