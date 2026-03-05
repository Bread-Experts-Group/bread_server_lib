package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.autoArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.time.Duration

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
	): DeferredOperation<D> {
		val allocated = data.associateWith { autoArena.allocate(it.size.toLong()) }
		val defer = gatherSegments(allocated.values, *features)
		return object : DeferredOperation<D> {
			override fun block(duration: Duration): List<D> {
				val supported = defer.block(duration)
				allocated.forEach { (array, segment) ->
					MemorySegment.copy(
						segment, ValueLayout.JAVA_BYTE, 0,
						array, 0, array.size
					)
				}
				return supported
			}
		}
	}

	fun receiveBytes(
		data: ByteArray,
		vararg features: F
	): DeferredOperation<D> {
		val allocated = autoArena.allocate(data.size.toLong())
		val defer = receiveSegment(allocated, *features)
		return object : DeferredOperation<D> {
			override fun block(duration: Duration): List<D> {
				val supported = defer.block(duration)
				MemorySegment.copy(
					allocated, ValueLayout.JAVA_BYTE, 0,
					data, 0, data.size
				)
				return supported
			}
		}
	}
}