package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.charset.Charset

abstract class SocketSendFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketSendFeature<F, D>>
) : SocketFeatureImplementation<SocketSendFeature<F, D>>() {
	abstract fun scatterS(
		data: Collection<MemorySegment>,
		vararg features: F
	): List<D>

	fun sendS(
		data: MemorySegment,
		vararg features: F
	): List<D> = scatterS(listOf(data), *features)

	fun scatter(
		data: Collection<ByteArray>,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		return scatterS(
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

	fun send(
		data: ByteArray,
		vararg features: F
	): List<D> = Arena.ofConfined().use { tempArena ->
		val segment = tempArena.allocate(data.size.toLong())
		MemorySegment.copy(
			data, 0,
			segment, ValueLayout.JAVA_BYTE, 0,
			data.size
		)
		return sendS(segment, *features)
	}

	fun scatter(
		data: Collection<String>,
		charset: Charset,
		vararg features: F
	): List<D> = scatter(data.map { it.toByteArray(charset) }, *features)

	fun send(
		data: String,
		charset: Charset,
		vararg features: F
	): List<D> = send(data.toByteArray(charset), *features)
}