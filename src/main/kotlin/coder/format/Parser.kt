package org.bread_experts_group.coder.format

import org.bread_experts_group.stream.FailQuickInputStream
import java.io.InputStream

abstract class Parser<K, T>(from: InputStream) : FailQuickInputStream(from) {
	abstract fun addParser(chunkIdentifier: K, parser: (InputStream) -> T)
	abstract fun readParsed(): T
	fun readAllParsed() = buildList {
		try {
			while (true) add(readParsed())
		} catch (_: EndOfStream) {
		}
	}
}