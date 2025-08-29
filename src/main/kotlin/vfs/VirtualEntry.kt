package org.bread_experts_group.vfs

import java.time.Instant

open class VirtualEntry(
	val creationTime: Instant,
	val accessTime: Instant,
	val writeTime: Instant,
	val changeTime: Instant
)