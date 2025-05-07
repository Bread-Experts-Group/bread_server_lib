package org.bread_experts_group.fuse

import org.bread_experts_group.posix.POSIXStat

data class FUSEDirectoryEntry(
	val name: String,
	val stat: POSIXStat
)