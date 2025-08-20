package org.bread_experts_group.protocol.old.posix.fuse

import org.bread_experts_group.protocol.old.posix.POSIXStat

data class FUSEDirectoryEntry(
	val name: String,
	val stat: POSIXStat
)