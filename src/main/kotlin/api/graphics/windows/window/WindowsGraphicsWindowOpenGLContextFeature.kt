package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.FeatureImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowOpenGLContextFeature
import org.bread_experts_group.api.graphics.feature.window.feature.opengl.*
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.windows.*
import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import javax.imageio.ImageIO

class WindowsGraphicsWindowOpenGLContextFeature(
	private val window: WindowsGraphicsWindow
) : GraphicsWindowOpenGLContextFeature() {
	private val arena = Arena.ofShared()
	override val source: FeatureImplementationSource = FeatureImplementationSource.SYSTEM_NATIVE
	private lateinit var hglrc: MemorySegment
	private val procedures: MutableMap<String, MethodHandle> = mutableMapOf()

	private val linker = Linker.nativeLinker()
	private val oglM = (nativeLoadLibraryExW.invokeExact(
		stringToPCWSTR(arena, "Opengl32.dll"),
		MemorySegment.NULL,
		0
	) as MemorySegment).also {
		if (it == MemorySegment.NULL) decodeLastError(arena)
	}

	fun procedureAddress(name: String): MemorySegment {
		val address = nativeWGLGetProcAddress.invokeExact(stringToPCSTR(arena, name)) as MemorySegment
		if (address == MemorySegment.NULL) {
			if (nativeGetLastError.invokeExact() as Int != 0x7F) decodeLastError(arena)
			val oglAddress = nativeGetProcAddress.invokeExact(oglM, stringToPCSTR(arena, name)) as MemorySegment
			if (oglAddress == MemorySegment.NULL) decodeLastError(arena)
			return oglAddress
		}
		return address
	}

	fun getHandleVoid(name: String, vararg layouts: ValueLayout) = procedures.getOrPut(name) {
		val addr = procedureAddress(name)
		addr.getDowncallVoid(linker, *layouts)
	}

	fun getHandle(name: String, rLayout: ValueLayout, vararg layouts: ValueLayout) = procedures.getOrPut(name) {
		val addr = procedureAddress(name)
		addr.getDowncall(linker, rLayout, *layouts)
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

	fun glGetUniformLocation(program: Int, name: String): Int = getHandle(
		"glGetUniformLocation",
		ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT, ValueLayout.ADDRESS
	).invokeExact(program, stringToPCSTR(arena, name)) as Int

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

	override fun open() {
		if (!use) return
		hglrc = nativeWGLCreateContext.invokeExact(window.hdc) as MemorySegment
		if (hglrc == MemorySegment.NULL) decodeLastError(arena)
		val makeStatus = nativeWGLMakeCurrent.invokeExact(window.hdc, hglrc) as Boolean
		if (!makeStatus) decodeLastError(arena)
		window.procedures[WindowsMessageTypes.WM_SIZE.position.toInt()] = { _, _, _, clientSize ->
			glViewport(
				0, 0,
				clientSize.toInt() and 0xFFFF, (clientSize.toInt() shr 16) and 0xFFFF
			)
			0
		}
		// Shaders
		val vertex = bGLCreateShader(OpenGLShaderType.GL_VERTEX_SHADER)
		vertex.source = arrayOf(
			"#version 330 core\nlayout (location = 0) in vec3 aPos;\nlayout (location = 1) in vec3 aColor;\nlayout (location = 2) in vec2 aTexCoord;\nout vec3 vColor;\nout vec2 vTexCoord;\nvoid main(){\ngl_Position = vec4(aPos, 1.0);\nvColor = aColor;\nvTexCoord = aTexCoord;\n}"
		)
		if (!vertex.compile()) println("V:C: ${vertex.compileInfoLog()}")
		val fragment = bGLCreateShader(OpenGLShaderType.GL_FRAGMENT_SHADER)
		fragment.source = arrayOf(
			"#version 330 core\nin vec3 vColor;\nin vec2 vTexCoord;\nout vec4 FragColor;\nuniform uint time;\nuniform sampler2D tTexture;\nvoid main(){\nFragColor = texture(tTexture, vTexCoord);\n}"
		)
		if (!fragment.compile()) println("F:C: ${fragment.compileInfoLog()}")
		val program = bGLCreateProgram()
		program.attach(vertex, fragment)
		if (!program.link()) println("P:L: ${program.linkInfoLog()}")
		// Buffers
		val vertices = arena.allocateArray(
			ValueLayout.JAVA_FLOAT,
			// positions          // colors           // texture coords
			0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
			0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
			-0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
			-0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
		)
		val indices = arena.allocateArray(
			ValueLayout.JAVA_INT,
			0, 1, 3, // first triangle
			1, 2, 3  // second triangle
		)
		val vao = arena.allocate(ValueLayout.JAVA_INT)
		val vbo = arena.allocate(ValueLayout.JAVA_INT)
		val ebo = arena.allocate(ValueLayout.JAVA_INT)
		glGenVertexArrays(1, vao)
		glGenBuffers(1, vbo)
		glGenBuffers(1, ebo)

		glBindVertexArray(vao.get(ValueLayout.JAVA_INT, 0))

		glBindBuffer(
			OpenGLBufferTarget.GL_ARRAY_BUFFER,
			vbo.get(ValueLayout.JAVA_INT, 0)
		)
		glBufferData(
			OpenGLBufferTarget.GL_ARRAY_BUFFER,
			vertices.byteSize().toInt(),
			vertices,
			OpenGLBufferUsage.GL_STATIC_DRAW
		)

		glBindBuffer(
			OpenGLBufferTarget.GL_ELEMENT_ARRAY_BUFFER,
			ebo.get(ValueLayout.JAVA_INT, 0)
		)
		glBufferData(
			OpenGLBufferTarget.GL_ELEMENT_ARRAY_BUFFER,
			indices.byteSize().toInt(),
			indices,
			OpenGLBufferUsage.GL_STATIC_DRAW
		)

		glVertexAttribPointer(
			0, 3, OpenGLDataType.GL_FLOAT, false, 8 * 4,
			MemorySegment.NULL
		)
		glEnableVertexAttribArray(0)

		glVertexAttribPointer(
			1, 3, OpenGLDataType.GL_FLOAT, false, 8 * 4,
			MemorySegment.ofAddress(3 * 4)
		)
		glEnableVertexAttribArray(1)

		glVertexAttribPointer(
			2, 2, OpenGLDataType.GL_FLOAT, false, 8 * 4,
			MemorySegment.ofAddress(6 * 4)
		)
		glEnableVertexAttribArray(2)

		glBindBuffer(OpenGLBufferTarget.GL_ARRAY_BUFFER, 0)
		glBindVertexArray(0)
		// Texture
		val texture = arena.allocate(ValueLayout.JAVA_INT)
		glGenTextures(1, texture)
		glBindTexture(OpenGLTextureTarget.GL_TEXTURE_2D, texture.get(ValueLayout.JAVA_INT, 0))
		val random = arena.allocate(300 * 300 * 3)
		val image = ImageIO.read(File("C:\\Users\\Adenosine3Phosphate\\Desktop\\185694081.png"))
		for (pixel in 0 until (image.width * image.height)) {
			val x = pixel % image.width
			val y = image.height - 1 - (pixel / image.width)
			val rgb = image.getRGB(x, y)

			val base = pixel * 3L
			random.set(ValueLayout.JAVA_BYTE, base, (rgb shr 16).toByte()) // R
			random.set(ValueLayout.JAVA_BYTE, base + 1, (rgb shr 8).toByte())  // G
			random.set(ValueLayout.JAVA_BYTE, base + 2, rgb.toByte())          // B
		}

		glTexImage2D(
			OpenGLTextureTarget.GL_TEXTURE_2D, 0,
			OpenGLTextureInternalFormat.GL_RGB8,
			300, 300, 0,
			OpenGLTextureFormat.GL_RGB,
			OpenGLDataType.GL_UNSIGNED_BYTE,
			random
		)
		glGenerateMipmap(OpenGLTextureTarget.GL_TEXTURE_2D)
		// Render
		glPolygonMode(OpenGLPolygonFace.GL_FRONT_AND_BACK, OpenGLPolygonMode.GL_FILL)
		window.procedures[WindowsMessageTypes.WM_PAINT.position.toInt()] = { _, _, _, _ ->
			glClearColor(0f, 0f, 0.5f, 1f)
			glClear(OpenGLClearFlags.GL_COLOR_BUFFER_BIT, OpenGLClearFlags.GL_DEPTH_BUFFER_BIT)
			glBindTexture(OpenGLTextureTarget.GL_TEXTURE_2D, texture.get(ValueLayout.JAVA_INT, 0))
			glUseProgram(program.handle)
//			val location = glGetUniformLocation(program.handle, "time")
//			glUniform(location, (System.currentTimeMillis() / 1000).toInt())
			glBindVertexArray(vao.get(ValueLayout.JAVA_INT, 0))
			glDrawElements(
				OpenGLPrimitiveRenderMode.GL_TRIANGLES,
				6, OpenGLDataType.GL_UNSIGNED_INT, MemorySegment.NULL
			)
			nativeSwapBuffers.invokeExact(window.hdc) as Boolean
			0
		}
	}

	override fun close() {
		nativeWGLMakeCurrent.invokeExact(MemorySegment.NULL, MemorySegment.NULL) as Boolean
		nativeWGLDeleteContext.invokeExact(hglrc) as Boolean
		arena.close()
	}
}