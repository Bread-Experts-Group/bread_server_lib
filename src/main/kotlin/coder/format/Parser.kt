package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.stream.FailQuickInputStream
import java.io.InputStream

abstract class Parser<K, T>(from: InputStream) : FailQuickInputStream(from) {
	abstract fun addParser(chunkIdentifier: K, parser: (InputStream) -> T)
	abstract fun readParsed(): RIFFChunk
	inline fun <reified R : T> readAllParsed(): Array<R> {
		val allRead = mutableListOf<R>()
		try {
			while (true) allRead.add(readParsed() as R)
		} catch (_: EndOfStream) {
		}
		return allRead.toTypedArray<R>()
	}
}