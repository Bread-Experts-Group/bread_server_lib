package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowOpenGLContextFeature
import org.bread_experts_group.ffi.windows.stringToPCSTR
import org.bread_experts_group.ffi.windows.wPCSTRToString
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class OpenGLShader(private val from: GraphicsWindowOpenGLContextFeature, type: OpenGLShaderType) : AutoCloseable {
	val handle = from.glCreateShader(type)
	var source = arrayOf<String>()

	fun compile() = Arena.ofConfined().use { arena ->
		val sourceArray = arena.allocateArray(ValueLayout.ADDRESS, source.size.toLong())
		source.forEachIndexed { i, source ->
			sourceArray.set(ValueLayout.ADDRESS, i.toLong(), stringToPCSTR(arena, source))
		}
		from.glShaderSource(
			handle, source.size,
			sourceArray,
			MemorySegment.NULL
		)
		from.glGetError().checkAndThrow()
		from.glCompileShader(handle)
		from.glGetError().checkAndThrow()
		val compileStatus = arena.allocate(ValueLayout.JAVA_BOOLEAN)
		from.glGetShaderiv(handle, OpenGLShaderParameterName.GL_COMPILE_STATUS, compileStatus)
		from.glGetError().checkAndThrow()
		compileStatus.get(ValueLayout.JAVA_BOOLEAN, 0)
	}

	fun compileInfoLog() = Arena.ofConfined().use { arena ->
		val length = arena.allocate(ValueLayout.JAVA_INT)
		from.glGetShaderiv(handle, OpenGLShaderParameterName.GL_INFO_LOG_LENGTH, length)
		from.glGetError().checkAndThrow()
		val infoLogLength = length.get(ValueLayout.JAVA_INT, 0)
		val infoLog = arena.allocate(infoLogLength.toLong())
		from.glGetShaderInfoLog(handle, infoLogLength, length, infoLog)
		from.glGetError().checkAndThrow()
		wPCSTRToString(infoLog)
	}

	override fun close() {
		println("Being closed")
		from.glDeleteShader(handle)
		from.glGetError().checkAndThrow()
	}
}