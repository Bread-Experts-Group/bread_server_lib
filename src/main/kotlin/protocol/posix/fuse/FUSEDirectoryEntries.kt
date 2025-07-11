package org.bread_experts_group.protocol.posix.fuse

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class FUSEDirectoryEntries : ArrayList<FUSEDirectoryEntry>() {
	fun toBuffer(arena: Arena, handle: MemorySegment): MemorySegment {
		val nativeEntries = buildList {
			this@FUSEDirectoryEntries.forEach {
				add(
					Pair(
						// TODO JDK 24
						arena.allocateUtf8String(it.name),
						it.stat.toBuffer(arena)
					)
				)
			}
		}
		val calculatedSizes = buildList {
			nativeEntries.forEach { (nameSeg, statSeg) ->
				add(
					nativeFuseAddDirEntry.invokeExact(
						handle, MemorySegment.NULL, 0,
						nameSeg, statSeg, 0
					) as Long
				)
			}
		}
		val buffer = arena.allocate(calculatedSizes.sum())
		return buffer
	}
}