package org.bread_experts_group.protocol.old.http.html

import java.util.concurrent.atomic.LongAdder

data class ComputedDirectoryStatistics(
	val errored: LongAdder = LongAdder(),
	val unreadable: LongAdder = LongAdder(),
	val calculatedSize: LongAdder = LongAdder(),
	val files: LongAdder = LongAdder(),
	val loops: LongAdder = LongAdder(),
	val directories: LongAdder = LongAdder()
) {
	fun merge(other: ComputedDirectoryStatistics) {
		this.errored.add(other.errored.sum())
		this.unreadable.add(other.unreadable.sum())
		this.calculatedSize.add(other.calculatedSize.sum())
		this.files.add(other.files.sum())
		this.loops.add(other.loops.sum())
		this.directories.add(other.directories.sum())
	}
}