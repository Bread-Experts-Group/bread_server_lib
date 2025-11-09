package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.AddressLayout
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

val HGLRC: AddressLayout = ValueLayout.ADDRESS

private val opengl32Lookup: SymbolLookup? = globalArena.getLookup("OpenGL32.dll")

val nativeWGLCreateContext: MethodHandle? = opengl32Lookup.getDowncall(
	nativeLinker, "wglCreateContext",
	arrayOf(
		HGLRC,
		HDC
	),
	listOf(
		gleCapture
	)
)

val nativeWGLDeleteContext: MethodHandle? = opengl32Lookup.getDowncall(
	nativeLinker, "wglDeleteContext", BOOL,
	HGLRC
)

val nativeWGLMakeCurrent: MethodHandle? = opengl32Lookup.getDowncall(
	nativeLinker, "wglMakeCurrent",
	arrayOf(
		BOOL,
		HDC, HGLRC
	),
	listOf(
		gleCapture
	)
)

val nativeWGLGetProcAddress: MethodHandle? = opengl32Lookup.getDowncall(
	nativeLinker, "wglGetProcAddress",
	arrayOf(
		PROC,
		LPCSTR
	),
	listOf(
		gleCapture
	)
)