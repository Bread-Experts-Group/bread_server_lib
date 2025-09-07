package org.bread_experts_group.ffi.wayland

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout

private val handleArena = Arena.ofAuto()
private val waylandClientLookup: SymbolLookup = handleArena.getLookup("libwayland-client.so")
private val linker: Linker = Linker.nativeLinker()

val nativeWLDisplayConnect = waylandClientLookup.getDowncall(
	linker, "wl_display_connect", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeWLDisplayDisconnect = waylandClientLookup.getDowncallVoid(
	linker, "wl_display_disconnect", ValueLayout.ADDRESS
)