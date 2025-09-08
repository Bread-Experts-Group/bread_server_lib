package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val opengl32Lookup: SymbolLookup = handleArena.getLookup("OpenGL32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeWGLCreateContext: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglCreateContext", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeWGLDeleteContext: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglDeleteContext", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS
)

val nativeWGLMakeCurrent: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglMakeCurrent", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeWGLGetProcAddress: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglGetProcAddress", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)