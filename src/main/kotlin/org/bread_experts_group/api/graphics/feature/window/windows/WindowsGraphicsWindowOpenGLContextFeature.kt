package org.bread_experts_group.api.graphics.feature.window.windows

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowOpenGLContextFeature
import org.bread_experts_group.api.graphics.feature.window.feature.opengl.*
import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.numeric.geometry.matrix.Matrix4
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

class WindowsGraphicsWindowOpenGLContextFeature(
	private val window: WindowsGraphicsWindow
) : GraphicsWindowOpenGLContextFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private lateinit var hglrc: MemorySegment
	private val procedures: MutableMap<String, MethodHandle> = mutableMapOf()

	private val oglM = (nativeLoadLibraryExWide!!.invokeExact(
		capturedStateSegment,
		autoArena.allocateFrom("Opengl32.dll", winCharsetWide),
		MemorySegment.NULL,
		0
	) as MemorySegment).also {
		if (it == MemorySegment.NULL) throwLastError()
	}

	fun procedureAddress(name: String): MemorySegment {
		val namePCSTR = autoArena.allocateFrom(name, Charsets.US_ASCII)
		val address = nativeWGLGetProcAddress!!.invokeExact(capturedStateSegment, namePCSTR) as MemorySegment
		if (address == MemorySegment.NULL) {
			if (nativeGetLastError.get(capturedStateSegment, 0L) as Int != 0x7F) throwLastError()
			val oglAddress = nativeGetProcAddress!!.invokeExact(capturedStateSegment, oglM, namePCSTR) as MemorySegment
			if (oglAddress == MemorySegment.NULL) throwLastError()
			return oglAddress
		}
		return address
	}

	fun getHandleVoid(name: String, vararg layouts: ValueLayout) = procedures.getOrPut(name) {
		val addr = procedureAddress(name)
		addr.getDowncallVoid(nativeLinker, *layouts)
	}

	fun getHandle(name: String, rLayout: ValueLayout, vararg layouts: ValueLayout) = procedures.getOrPut(name) {
		val addr = procedureAddress(name)
		addr.getDowncall(nativeLinker, rLayout, *layouts)
	}

	override fun glGetError(): MappedEnumeration<UInt, OpenGLError> = OpenGLError.entries.id(
		(getHandle(
			"glGetError",
			ValueLayout.JAVA_INT
		).invokeExact() as Int).toUInt()
	)

	override fun glViewport(x: Int, y: Int, w: Int, h: Int) {
		getHandleVoid(
			"glViewport",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(x, y, w, h)
	}

	override fun glClearColor(r: Float, g: Float, b: Float, a: Float) {
		getHandleVoid(
			"glClearColor",
			ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT
		).invokeExact(r, g, b, a)
	}

	override fun glClear(vararg flags: OpenGLClearFlags) {
		getHandleVoid(
			"glClear",
			ValueLayout.JAVA_INT
		).invokeExact(flags.raw().toInt())
	}

	override fun glGenBuffers(n: Int, buffers: MemorySegment) {
		getHandleVoid(
			"glGenBuffers",
			ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(n, buffers)
	}

	override fun glGenVertexArrays(n: Int, arrays: MemorySegment) {
		getHandleVoid(
			"glGenVertexArrays",
			ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(n, arrays)
	}

	override fun glBindBuffer(target: OpenGLBufferTarget, buffer: Int) {
		getHandleVoid(
			"glBindBuffer",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(target.id.toInt(), buffer)
	}

	override fun glBindVertexArray(array: Int) {
		getHandleVoid(
			"glBindVertexArray",
			ValueLayout.JAVA_INT
		).invokeExact(array)
	}

	override fun glBufferData(target: OpenGLBufferTarget, size: Int, data: MemorySegment, usage: OpenGLBufferUsage) {
		getHandleVoid(
			"glBufferData",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT
		).invokeExact(target.id.toInt(), size, data, usage.id.toInt())
	}

	override fun glCreateShader(type: OpenGLShaderType) = getHandle(
		"glCreateShader",
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT
	).invokeExact(type.id.toInt()) as Int

	override fun glShaderSource(shader: Int, count: Int, string: MemorySegment, length: MemorySegment) {
		getHandleVoid(
			"glShaderSource",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS
		).invokeExact(shader, count, string, length)
	}

	override fun glCompileShader(shader: Int) {
		getHandleVoid(
			"glCompileShader",
			ValueLayout.JAVA_INT
		).invokeExact(shader)
	}

	override fun glGetShaderInfoLog(shader: Int, maxLength: Int, length: MemorySegment, infoLog: MemorySegment) {
		getHandleVoid(
			"glGetShaderInfoLog",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS
		).invokeExact(shader, maxLength, length, infoLog)
	}

	override fun glGetShaderiv(shader: Int, pname: OpenGLShaderParameterName, params: MemorySegment) {
		getHandleVoid(
			"glGetShaderiv",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(shader, pname.id.toInt(), params)
	}

	override fun glCreateProgram() = getHandle(
		"glCreateProgram",
		ValueLayout.JAVA_INT
	).invokeExact() as Int

	override fun glGetProgramiv(program: Int, pname: OpenGLProgramParameterName, params: MemorySegment) {
		getHandleVoid(
			"glGetProgramiv",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(program, pname.id.toInt(), params)
	}

	override fun glGetProgramInfoLog(program: Int, maxLength: Int, length: MemorySegment, infoLog: MemorySegment) {
		getHandleVoid(
			"glGetProgramInfoLog",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS
		).invokeExact(program, maxLength, length, infoLog)
	}

	override fun glAttachShader(program: Int, shader: Int) {
		getHandleVoid(
			"glAttachShader",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(program, shader)
	}

	override fun glLinkProgram(program: Int) {
		getHandleVoid(
			"glLinkProgram",
			ValueLayout.JAVA_INT
		).invokeExact(program)
	}

	override fun glUseProgram(program: Int) {
		getHandleVoid(
			"glUseProgram",
			ValueLayout.JAVA_INT
		).invokeExact(program)
	}

	override fun glDeleteShader(shader: Int) {
		getHandleVoid(
			"glDeleteShader",
			ValueLayout.JAVA_INT
		).invokeExact(shader)
	}

	override fun glDeleteProgram(program: Int) {
		getHandleVoid(
			"glDeleteProgram",
			ValueLayout.JAVA_INT
		).invokeExact(program)
	}

	override fun glVertexAttribPointer(
		index: Int, size: Int, type: OpenGLDataType, normalized: Boolean, stride: Int, pointer: MemorySegment
	) {
		getHandleVoid(
			"glVertexAttribPointer",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN,
			ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(index, size, type.id.toInt(), normalized, stride, pointer)
	}

	override fun glEnableVertexAttribArray(index: Int) {
		getHandleVoid(
			"glEnableVertexAttribArray",
			ValueLayout.JAVA_INT
		).invokeExact(index)
	}

	override fun glDrawArrays(mode: OpenGLPrimitiveRenderMode, first: Int, count: Int) {
		getHandleVoid(
			"glDrawArrays",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(mode.id.toInt(), first, count)
	}

	override fun glDrawElements(
		mode: OpenGLPrimitiveRenderMode,
		count: Int, type: OpenGLDataType, indices: MemorySegment
	) {
		getHandleVoid(
			"glDrawElements",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(mode.id.toInt(), count, type.id.toInt(), indices)
	}

	override fun glPolygonMode(face: OpenGLPolygonFace, mode: OpenGLPolygonMode) {
		getHandleVoid(
			"glPolygonMode",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(face.id.toInt(), mode.id.toInt())
	}

	override fun glGetUniformLocation(program: Int, name: String): Int = getHandle(
		"glGetUniformLocation",
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT, ValueLayout.ADDRESS
	).invokeExact(program, autoArena.allocateFrom(name, Charsets.US_ASCII)) as Int

	override fun glUniform(location: Int, v0: Int) {
		getHandleVoid(
			"glUniform1ui",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(location, v0)
	}

	override fun glUniform(location: Int, v0: Int, v1: Int) {
		getHandleVoid(
			"glUniform2ui",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(location, v0, v1)
	}

	override fun glUniform(location: Int, v0: Float, v1: Float, v2: Float, v3: Float) {
		getHandleVoid(
			"glUniform4f",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT,
			ValueLayout.JAVA_FLOAT
		).invokeExact(location, v0, v1, v2, v3)
	}

	override fun glUniformMatrix(location: Int, count: Int, transpose: Boolean, value: Matrix4<Float>) {
		val allocated = autoArena.allocate(ValueLayout.JAVA_FLOAT, 4 * 4)
//		value.fillArray(allocated, 0)
		TODO("M4F OGL")
		getHandleVoid(
			"glUniformMatrix4fv",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS
		).invokeExact(location, count, transpose, allocated)
	}

	override fun glGenTextures(n: Int, textures: MemorySegment) {
		getHandleVoid(
			"glGenTextures",
			ValueLayout.JAVA_INT, ValueLayout.ADDRESS
		).invokeExact(n, textures)
	}

	override fun glBindTexture(target: OpenGLTextureTarget, texture: Int) {
		getHandleVoid(
			"glBindTexture",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(target.id.toInt(), texture)
	}

	override fun glTexImage2D(
		target: OpenGLTextureTarget, level: Int, internalFormat: OpenGLTextureInternalFormat,
		w: Int, h: Int, border: Int, format: OpenGLTextureFormat, type: OpenGLDataType,
		data: MemorySegment
	) {
		getHandleVoid(
			"glTexImage2D",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
			ValueLayout.ADDRESS
		).invokeExact(
			target.id.toInt(), level, internalFormat.id.toInt(), w, h, border, format.id.toInt(),
			type.id.toInt(), data
		)
	}

	override fun glGenerateMipmap(target: OpenGLTextureTarget) {
		getHandleVoid(
			"glGenerateMipmap",
			ValueLayout.JAVA_INT
		).invokeExact(target.id.toInt())
	}

	override fun glEnable(cap: OpenGLCapability) {
		getHandleVoid(
			"glEnable",
			ValueLayout.JAVA_INT
		).invokeExact(cap.id.toInt())
	}

	override fun glTexParameter(target: OpenGLTextureTarget, pName: OpenGLTextureParameter, param: Int) {
		getHandleVoid(
			"glTexParameteri",
			ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
		).invokeExact(target.id.toInt(), pName.id.toInt(), param)
	}

	override fun releaseContext() {
		val makeStatus = nativeWGLMakeCurrent!!.invokeExact(
			capturedStateSegment,
			MemorySegment.NULL, MemorySegment.NULL
		) as Int
		if (makeStatus == 0) throwLastError()
	}

	override fun acquireContext() {
		val makeStatus = nativeWGLMakeCurrent!!.invokeExact(
			capturedStateSegment,
			window.hdc, hglrc
		) as Int
		if (makeStatus == 0) throwLastError()
	}

	override fun swapBuffers() {
		nativeSwapBuffers!!.invokeExact(window.hdc) as Int
	}

	override fun open() {
		if (!use) return
		hglrc = nativeWGLCreateContext!!.invokeExact(
			capturedStateSegment,
			window.hdc
		) as MemorySegment
		if (hglrc == MemorySegment.NULL) throwLastError()
	}

	override fun close() {
		nativeWGLMakeCurrent!!.invokeExact(MemorySegment.NULL, MemorySegment.NULL) as Boolean
		nativeWGLDeleteContext!!.invokeExact(hglrc) as Boolean
	}
}