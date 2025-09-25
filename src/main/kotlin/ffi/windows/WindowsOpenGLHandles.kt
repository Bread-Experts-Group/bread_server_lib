package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.*
import java.lang.invoke.MethodHandle

val HGLRC: AddressLayout = ValueLayout.ADDRESS

private val handleArena = Arena.ofAuto()
private val opengl32Lookup: SymbolLookup = handleArena.getLookup("OpenGL32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeWGLCreateContext: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglCreateContext",
	arrayOf(
		HGLRC,
		HDC
	),
	listOf(
		gleCapture
	)
)

val nativeWGLDeleteContext: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglDeleteContext", BOOL,
	HGLRC
)

val nativeWGLMakeCurrent: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglMakeCurrent",
	arrayOf(
		BOOL,
		HDC, HGLRC
	),
	listOf(
		gleCapture
	)
)

val nativeWGLGetProcAddress: MethodHandle = opengl32Lookup.getDowncall(
	linker, "wglGetProcAddress",
	arrayOf(
		PROC,
		LPCSTR
	),
	listOf(
		gleCapture
	)
)