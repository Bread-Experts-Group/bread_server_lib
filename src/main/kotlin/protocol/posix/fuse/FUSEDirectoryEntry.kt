package org.bread_experts_group.protocol.posix.fuse

import org.bread_experts_group.protocol.posix.POSIXStat

data class FUSEDirectoryEntry(
	val name: String,
	val stat: POSIXStat
)