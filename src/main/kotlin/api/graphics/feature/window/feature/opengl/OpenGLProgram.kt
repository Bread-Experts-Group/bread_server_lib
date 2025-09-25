package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowOpenGLContextFeature
import org.bread_experts_group.ffi.readString
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout

class OpenGLProgram(private val from: GraphicsWindowOpenGLContextFeature) : AutoCloseable {
	val handle = from.glCreateProgram()

	fun attach(vararg shaders: OpenGLShader) {
		shaders.forEach { shader ->
			from.glAttachShader(handle, shader.handle)
			from.glGetError().checkAndThrow()
		}
	}

	fun link(): Boolean = Arena.ofConfined().use { arena ->
		from.glLinkProgram(handle)
		from.glGetError().checkAndThrow()
		val linkStatus = arena.allocate(ValueLayout.JAVA_BOOLEAN)
		from.glGetProgramiv(handle, OpenGLProgramParameterName.GL_LINK_STATUS, linkStatus)
		from.glGetError().checkAndThrow()
		linkStatus.get(ValueLayout.JAVA_BOOLEAN, 0)
	}

	fun linkInfoLog() = Arena.ofConfined().use { arena ->
		val length = arena.allocate(ValueLayout.JAVA_INT)
		from.glGetProgramiv(handle, OpenGLProgramParameterName.GL_INFO_LOG_LENGTH, length)
		from.glGetError().checkAndThrow()
		val infoLogLength = length.get(ValueLayout.JAVA_INT, 0)
		val infoLog = arena.allocate(infoLogLength.toLong())
		from.glGetProgramInfoLog(handle, infoLogLength, length, infoLog)
		from.glGetError().checkAndThrow()
		infoLog.readString(Charsets.US_ASCII)
	}

	override fun close() {
		from.glDeleteProgram(handle)
		from.glGetError().checkAndThrow()
	}
}