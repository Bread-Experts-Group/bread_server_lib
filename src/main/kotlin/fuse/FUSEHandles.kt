package org.bread_experts_group.fuse

import org.bread_experts_group.getDowncall
import org.bread_experts_group.getDowncallVoid
import org.bread_experts_group.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val fuseLookup: SymbolLookup = handleArena.getLookup("libfuse3.so.3")
private val linker: Linker = Linker.nativeLinker()

val nativeFuseAddDirEntry: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_add_direntry", ValueLayout.JAVA_LONG,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG
)

val nativeFuseReplyBuffer: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_reply_buf", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG
)

val nativeFuseReplyEntry: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_reply_entry", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeFuseReplyError: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_reply_err", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.JAVA_INT
)

val nativeFuseNewSession: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_session_new", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS
)

val nativeFuseMount: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_session_mount", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeFuseSessionLoop: MethodHandle = fuseLookup.getDowncall(
	linker, "fuse_session_loop", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeFuseSessionUnmount = fuseLookup.getDowncallVoid(
	linker, "fuse_session_unmount", ValueLayout.ADDRESS
)

val nativeFuseSessionExit = fuseLookup.getDowncallVoid(
	linker, "fuse_session_exit", ValueLayout.ADDRESS
)

val nativeFuseSessionDestroy = fuseLookup.getDowncallVoid(
	linker, "fuse_session_destroy", ValueLayout.ADDRESS
)