package org.bread_experts_group.protocol.old.posix

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

data class POSIXStat(
	val deviceID: Long = 0,
	val inode: Long = 0,
	val mode: Int = 0,
	val hardLinkCount: Long = 0,
	val ownerUserID: Int = 0,
	val ownerGroupID: Int = 0,
	val specialDeviceID: Long = 0,
	val size: Long = 0,
	val blockSize: Long = 0,
	val allocatedBlocks: Long = 0,
	val lastAccess: POSIXTimespec = POSIXTimespec(),
	val lastModification: POSIXTimespec = POSIXTimespec(),
	val lastStatusChange: POSIXTimespec = POSIXTimespec()
) {
	fun toBuffer(arena: Arena): MemorySegment {
		return arena.allocate(posixStatStructure)
	}
}