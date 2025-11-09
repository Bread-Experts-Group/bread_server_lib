package org.bread_experts_group.ffi.wayland

import org.bread_experts_group.ffi.*
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout

private val waylandClientLookup: SymbolLookup? = globalArena.getLookup("libwayland-client.so")

val nativeWLDisplayConnect = waylandClientLookup.getDowncall(
	nativeLinker, "wl_display_connect", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeWLDisplayDisconnect = waylandClientLookup.getDowncallVoid(
	nativeLinker, "wl_display_disconnect", ValueLayout.ADDRESS
)