package org.bread_experts_group.ffi.posix.linux.x64

import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.posix.x64.*
import java.lang.foreign.SymbolLookup

private val cLookup: SymbolLookup? = globalArena.getLookup("libc.so.6")

val nativeGetCwd = posix64GetCwd(cLookup)
val nativeOpen_vInt = posix64Open_vInt(cLookup)
val nativeReadV = posix64ReadV(cLookup, `iovec*`)
val nativeWriteV = posix64WriteV(cLookup, `iovec*`)
val nativeLSeek = posix64LSeek(cLookup, off_t)
val nativeFTruncate = posix64FTruncate(cLookup, off_t)
val nativeFStat = posix64FStat(cLookup, `stat*`)
val nativeSocket = posix64Socket(cLookup)
val nativeClose = posix64Close(cLookup)