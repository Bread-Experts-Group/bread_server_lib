@file:Suppress("DANGEROUS_CHARACTERS", "ObjectPropertyName")

package org.bread_experts_group.ffi.posix.linux.x64

import org.bread_experts_group.ffi.posix.x64.int
import org.bread_experts_group.ffi.posix.x64.int64_t
import org.bread_experts_group.ffi.posix.x64.size_t
import org.bread_experts_group.ffi.posix.x64.`void*`
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.VarHandle

val __SQUAD_TYPE = int64_t
val __U32_TYPE = int
val __SYSCALL_ULONG_TYPE = __SQUAD_TYPE
val __SYSCALL_SLONG_TYPE = __SQUAD_TYPE
val ino_t = __SYSCALL_ULONG_TYPE
val off_t = __SYSCALL_SLONG_TYPE
val mode_t = __U32_TYPE
val nlink_t = __SYSCALL_ULONG_TYPE
val uid_t = __U32_TYPE
val gid_t = __U32_TYPE
val dev_t = __SQUAD_TYPE
val blksize_t = __SYSCALL_SLONG_TYPE
val blkcnt_t = __SYSCALL_SLONG_TYPE
val time_t = __SYSCALL_SLONG_TYPE

val iovec: StructLayout = MemoryLayout.structLayout(
	`void*`.withName("iov_base"),
	size_t.withName("iov_len")
)
val `iovec*` = `void*` // iovec
val iovec_iov_base: VarHandle = iovec.varHandle(groupElement("iov_base"))
val iovec_iov_len: VarHandle = iovec.varHandle(groupElement("iov_len"))

val stat: StructLayout = MemoryLayout.structLayout(
	dev_t.withName("st_dev"),
	ino_t.withName("st_ino"),
	nlink_t.withName("st_nlink"),
	mode_t.withName("st_mode"),
	uid_t.withName("st_uid"),
	gid_t.withName("st_gid"),
	int.withName("__pad0"),
	dev_t.withName("st_rdev"),
	off_t.withName("st_size"),
	blksize_t.withName("st_blksize"),
	blkcnt_t.withName("st_blocks"),
	time_t.withName("st_atime"),
	__SYSCALL_ULONG_TYPE.withName("st_atimensec"),
	time_t.withName("st_mtime"),
	__SYSCALL_ULONG_TYPE.withName("st_mtimensec"),
	time_t.withName("st_ctime"),
	__SYSCALL_ULONG_TYPE.withName("st_ctimensec"),
	MemoryLayout.sequenceLayout(3, __SYSCALL_SLONG_TYPE).withName("__glibc_reserved")
)
val `stat*` = `void*`
val stat_st_size: VarHandle = stat.varHandle(groupElement("st_size"))