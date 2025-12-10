package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.system.socket.DeferredOperation
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

interface ReceiveFeature<F : D, D> {
	fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: F
	): DeferredOperation<D>

	fun receiveSegment(
		data: MemorySegment,
		vararg features: F
	): DeferredOperation<D> = gatherSegments(listOf(data), *features)

	fun gatherBytes(
		data: Collection<ByteArray>,
		vararg features: F
	): DeferredOperation<D> = Arena.ofConfined().use { tempArena ->
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
	): DeferredOperation<D> = Arena.ofConfined().use { tempArena ->
		val allocated = tempArena.allocate(data.size.toLong())
		val supported = receiveSegment(allocated, *features)
		MemorySegment.copy(
			allocated, ValueLayout.JAVA_BYTE, 0,
			data, 0, data.size
		)
		return supported
	}
}