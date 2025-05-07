package org.bread_experts_group.posix

import java.lang.foreign.MemoryLayout
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val posixTimespecStructure: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_LONG.withName("tv_sec"),
	ValueLayout.JAVA_LONG.withName("tv_nsec")
)
val timespecTvSecHandle: VarHandle = posixTimespecStructure.varHandle(MemoryLayout.PathElement.groupElement("tv_sec"))
val timespecTvNsecHandle: VarHandle = posixTimespecStructure.varHandle(MemoryLayout.PathElement.groupElement("tv_nsec"))

val posixStatStructure: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_LONG.withName("st_dev"),
	ValueLayout.JAVA_LONG.withName("st_ino"),
	ValueLayout.JAVA_INT.withName("st_mode"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.JAVA_LONG.withName("st_nlink"),
	ValueLayout.JAVA_INT.withName("st_uid"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.JAVA_INT.withName("st_gid"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.JAVA_LONG.withName("st_rdev"),
	ValueLayout.JAVA_LONG.withName("st_size"),
	ValueLayout.JAVA_LONG.withName("st_blksize"),
	ValueLayout.JAVA_LONG.withName("st_blocks"),
	posixTimespecStructure.withName("st_atim"),
	posixTimespecStructure.withName("st_mtim"),
	posixTimespecStructure.withName("st_ctim"),
)
val statStDevHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_dev"))
val statStInoHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_ino"))
val statStModeHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_mode"))
val statStNLinkHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_nlink"))
val statStUidHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_uid"))
val statStGidHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_gid"))
val statStRDevHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_rdev"))
val statStSizeHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_size"))
val statStBlkSizeHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_blksize"))
val statStBlocksHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_blocks"))
val statStAtimHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_atim"))
val statStMitmHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_mtim"))
val statStCtimHandle: VarHandle = posixStatStructure.varHandle(MemoryLayout.PathElement.groupElement("st_ctim"))