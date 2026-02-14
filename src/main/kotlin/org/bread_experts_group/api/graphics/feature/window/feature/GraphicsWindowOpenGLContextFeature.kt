package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.graphics.feature.window.feature.opengl.*
import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.generic.numeric.geometry.matrix.Matrix4
import java.lang.foreign.MemorySegment

abstract class GraphicsWindowOpenGLContextFeature :
	GraphicsWindowFeatureImplementation<GraphicsWindowOpenGLContextFeature>() {
	final override val expresses: FeatureExpression<GraphicsWindowOpenGLContextFeature> =
		GraphicsWindowFeatures.OPENGL_CONTEXT
	var use: Boolean = false

	abstract fun glGetError(): MappedEnumeration<UInt, OpenGLError>
	abstract fun glViewport(x: Int, y: Int, w: Int, h: Int)
	abstract fun glClearColor(r: Float, g: Float, b: Float, a: Float)
	abstract fun glClear(vararg flags: OpenGLClearFlags)
	abstract fun glGenBuffers(n: Int, buffers: MemorySegment)
	abstract fun glGenVertexArrays(n: Int, arrays: MemorySegment)
	abstract fun glBindBuffer(target: OpenGLBufferTarget, buffer: Int)
	abstract fun glBindVertexArray(array: Int)
	abstract fun glBufferData(target: OpenGLBufferTarget, size: Int, data: MemorySegment, usage: OpenGLBufferUsage)
	abstract fun glCreateShader(type: OpenGLShaderType): Int
	abstract fun glShaderSource(shader: Int, count: Int, string: MemorySegment, length: MemorySegment)
	abstract fun glCompileShader(shader: Int)
	abstract fun glGetShaderInfoLog(shader: Int, maxLength: Int, length: MemorySegment, infoLog: MemorySegment)
	abstract fun glGetShaderiv(shader: Int, pname: OpenGLShaderParameterName, params: MemorySegment)
	abstract fun glCreateProgram(): Int
	abstract fun glGetProgramiv(program: Int, pname: OpenGLProgramParameterName, params: MemorySegment)
	abstract fun glGetProgramInfoLog(program: Int, maxLength: Int, length: MemorySegment, infoLog: MemorySegment)
	abstract fun glAttachShader(program: Int, shader: Int)
	abstract fun glLinkProgram(program: Int)
	abstract fun glUseProgram(program: Int)
	abstract fun glDeleteShader(shader: Int)
	abstract fun glDeleteProgram(program: Int)
	abstract fun glPolygonMode(face: OpenGLPolygonFace, mode: OpenGLPolygonMode)
	abstract fun glGetUniformLocation(program: Int, name: String): Int
	abstract fun glUniform(location: Int, v0: Int)
	abstract fun glUniform(location: Int, v0: Int, v1: Int)
	abstract fun glUniform(location: Int, v0: Float, v1: Float, v2: Float, v3: Float)
	abstract fun glUniformMatrix(location: Int, count: Int, transpose: Boolean, value: Matrix4<Float>)
	abstract fun glGenTextures(n: Int, textures: MemorySegment)
	abstract fun glBindTexture(target: OpenGLTextureTarget, texture: Int)
	abstract fun glTexImage2D(
		target: OpenGLTextureTarget, level: Int, internalFormat: OpenGLTextureInternalFormat,
		w: Int, h: Int, border: Int, format: OpenGLTextureFormat, type: OpenGLDataType,
		data: MemorySegment
	)

	abstract fun glGenerateMipmap(target: OpenGLTextureTarget)
	abstract fun glVertexAttribPointer(
		index: Int, size: Int, type: OpenGLDataType, normalized: Boolean, stride: Int, pointer: MemorySegment
	)

	abstract fun glEnableVertexAttribArray(index: Int)
	abstract fun glDrawArrays(mode: OpenGLPrimitiveRenderMode, first: Int, count: Int)
	abstract fun glDrawElements(
		mode: OpenGLPrimitiveRenderMode,
		count: Int, type: OpenGLDataType, indices: MemorySegment
	)

	abstract fun glEnable(cap: OpenGLCapability)
	abstract fun glTexParameter(target: OpenGLTextureTarget, pName: OpenGLTextureParameter, param: Int)

	abstract fun releaseContext()
	abstract fun acquireContext()
	abstract fun swapBuffers()

	fun bGLCreateShader(type: OpenGLShaderType) = OpenGLShader(this, type)
	fun bGLCreateProgram() = OpenGLProgram(this)
}