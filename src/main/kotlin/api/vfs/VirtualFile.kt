package org.bread_experts_group.api.vfs

import java.time.Instant

class VirtualFile(
	val size: ULong = 0uL,
	creationTime: Instant = Instant.now(),
	accessTime: Instant = Instant.now(),
	writeTime: Instant = Instant.now(),
	changeTime: Instant = Instant.now()
) : VirtualEntry(creationTime, accessTime, writeTime, changeTime)