package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.system.socket.DeferredOperation
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.charset.Charset

interface SendFeature<F : D, D> {
	fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: F
	): DeferredOperation<D>

	fun sendSegment(
		data: MemorySegment,
		vararg features: F
	): DeferredOperation<D> = scatterSegments(listOf(data), *features)

	fun scatterBytes(
		data: Collection<ByteArray>,
		vararg features: F
	): DeferredOperation<D> = Arena.ofConfined().use { tempArena ->
		return scatterSegments(
			data.map {
				val segment = tempArena.allocate(it.size.toLong())
				MemorySegment.copy(
					it, 0,
					segment, ValueLayout.JAVA_BYTE, 0,
					it.size
				)
				segment
			},
			*features
		)
	}

	fun sendBytes(
		data: ByteArray,
		vararg features: F
	): DeferredOperation<D> = Arena.ofConfined().use { tempArena ->
		val segment = tempArena.allocate(data.size.toLong())
		MemorySegment.copy(
			data, 0,
			segment, ValueLayout.JAVA_BYTE, 0,
			data.size
		)
		return sendSegment(segment, *features)
	}

	fun scatterStrings(
		data: Collection<String>,
		charset: Charset,
		vararg features: F
	): DeferredOperation<D> = scatterBytes(data.map { it.toByteArray(charset) }, *features)

	fun sendString(
		data: String,
		charset: Charset,
		vararg features: F
	): DeferredOperation<D> = sendBytes(data.toByteArray(charset), *features)
}