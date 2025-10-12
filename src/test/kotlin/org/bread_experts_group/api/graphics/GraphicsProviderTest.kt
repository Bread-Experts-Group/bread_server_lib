package org.bread_experts_group.org.bread_experts_group.api.graphics

import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.api.graphics.feature.window.feature.opengl.*
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.numeric.geometry.Matrix4F
import org.bread_experts_group.numeric.geometry.Vector4F
import org.bread_experts_group.org.bread_experts_group.getResource
import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.concurrent.LinkedBlockingQueue
import javax.imageio.ImageIO
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphicsProviderTest {
	val logger = ColoredHandler.newLogger("tmp logger")

	fun window(): GraphicsWindow {
		val windowing = GraphicsProvider.get(GraphicsFeatures.WINDOW, false)
		val template = windowing.createTemplate()
		val window = windowing.createWindow(template)
		return window
	}

	@Test
	fun directXWindow() {
		val window = window()
		val windowName = window.get(GraphicsWindowFeatures.WINDOW_NAME)
		windowName?.name = "Hello World"
		window.get(GraphicsWindowFeatures.DIRECTX_CONTEXT)?.use = true
		assert(windowName?.name == "Hello World")
		window.open()
		assert(windowName?.name == "Hello World")
		window.processingLock.acquire()
	}

	@Test
	fun openGLWindow() {
		val window = window()
		val windowName = window.get(GraphicsWindowFeatures.WINDOW_NAME)
		windowName.name = "Hello World"
		window.get(GraphicsWindowFeatures.OPENGL_CONTEXT).use = true
		assertEquals("Hello World", windowName.name)
		window.open()
		assertEquals("Hello World", windowName.name)
		window.get(GraphicsWindowFeatures.OPENGL_CONTEXT).apply {
			acquireContext()
			val arena = Arena.ofConfined()
			// Shaders
			val vertex = bGLCreateShader(OpenGLShaderType.GL_VERTEX_SHADER)
			vertex.source = arrayOf(getResource("/api/graphics/basic.vsh").readText())
			if (!vertex.compile()) logger.info("V:C: ${vertex.compileInfoLog()}")
			val fragment = bGLCreateShader(OpenGLShaderType.GL_FRAGMENT_SHADER)
			fragment.source = arrayOf(getResource("/api/graphics/basic.fsh").readText())
			if (!fragment.compile()) logger.info("F:C: ${fragment.compileInfoLog()}")
			val program = bGLCreateProgram()
			program.attach(vertex, fragment)
			if (!program.link()) logger.info("P:L: ${program.linkInfoLog()}")
			// Buffers
			val vertices = arena.allocateFrom(
				ValueLayout.JAVA_FLOAT,
				-0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
				0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
				0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
				0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
				-0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
				-0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

				-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
				0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
				0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
				0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
				-0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
				-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

				-0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
				-0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
				-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
				-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
				-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
				-0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

				0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
				0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
				0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
				0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
				0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
				0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

				-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
				0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
				0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
				0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
				-0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
				-0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

				-0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
				0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
				0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
				0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
				-0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
				-0.5f, 0.5f, -0.5f, 0.0f, 1.0f
			)
			val indices = arena.allocateFrom(
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
				0, 3, OpenGLDataType.GL_FLOAT, false, 5 * 4,
				MemorySegment.NULL
			)
			glEnableVertexAttribArray(0)

//			glVertexAttribPointer(
//				1, 4, OpenGLDataType.GL_FLOAT, false, 9 * 4,
//				MemorySegment.ofAddress(3 * 4)
//			)
//			glEnableVertexAttribArray(1)

			glVertexAttribPointer(
				2, 2, OpenGLDataType.GL_FLOAT, false, 5 * 4,
				MemorySegment.ofAddress(3 * 4)
			)
			glEnableVertexAttribArray(2)

			glBindBuffer(OpenGLBufferTarget.GL_ARRAY_BUFFER, 0)
			glBindVertexArray(0)
			// Texture
			val texture = arena.allocate(ValueLayout.JAVA_INT)
			glGenTextures(1, texture)
			glBindTexture(OpenGLTextureTarget.GL_TEXTURE_2D, texture.get(ValueLayout.JAVA_INT, 0))
			val random = arena.allocate(768 * 975 * 4)
			val image = ImageIO.read(File("C:\\Users\\Adenosine3Phosphate\\Desktop\\photo_2025-09-21_10-32-11.jpg"))
			for (pixel in 0 until (image.width * image.height)) {
				val x = pixel % image.width
				val y = image.height - 1 - (pixel / image.width)
				val rgb = image.getRGB(x, y)

				val base = pixel * 4L
				random.set(ValueLayout.JAVA_BYTE, base, (rgb shr 16).toByte()) // R
				random.set(ValueLayout.JAVA_BYTE, base + 1, (rgb shr 8).toByte())  // G
				random.set(ValueLayout.JAVA_BYTE, base + 2, rgb.toByte())          // B
				random.set(ValueLayout.JAVA_BYTE, base + 3, (rgb shr 24).toByte())
			}

			glTexParameter(OpenGLTextureTarget.GL_TEXTURE_2D, OpenGLTextureParameter.GL_TEXTURE_MAG_FILTER, 0x2600)
			glTexImage2D(
				OpenGLTextureTarget.GL_TEXTURE_2D, 0,
				OpenGLTextureInternalFormat.GL_RGBA8,
				768, 975, 0,
				OpenGLTextureFormat.GL_RGBA,
				OpenGLDataType.GL_UNSIGNED_BYTE,
				random
			)
			glGenerateMipmap(OpenGLTextureTarget.GL_TEXTURE_2D)
			// Render
			glPolygonMode(OpenGLPolygonFace.GL_FRONT_AND_BACK, OpenGLPolygonMode.GL_FILL)
			// Uniform
			val projectionHandle = glGetUniformLocation(program.handle, "uProjection")
			val projection = Matrix4F.perspective(
				Math.toRadians(45.0).toFloat(),
				800f / 600f,
				0.1f,
				100f
			)
			val modelHandle = glGetUniformLocation(program.handle, "uModel")
			val model = Matrix4F(1f).rotateX(Math.toRadians(-55.0).toFloat())
			val viewHandle = glGetUniformLocation(program.handle, "uView")
			val view = Matrix4F(1f) + Vector4F(0f, 0f, -3f, 1f)
			var i = 0f
			glEnable(OpenGLCapability.GL_DEPTH_TEST)
			val glTasking = LinkedBlockingQueue<Runnable>(1)
			window.get(GraphicsWindowFeatures.RESIZE_LAMBDA)?.lambda = { w, h ->
				glTasking.put {
					glViewport(0, 0, w.toInt(), h.toInt())
				}
			}
			window.get(GraphicsWindowFeatures.RENDER_LAMBDA)?.lambda = {
				glTasking.put {
					glClearColor(0f, 0f, 0.5f, 1f)
					glClear(OpenGLClearFlags.GL_COLOR_BUFFER_BIT, OpenGLClearFlags.GL_DEPTH_BUFFER_BIT)
					glBindTexture(OpenGLTextureTarget.GL_TEXTURE_2D, texture.get(ValueLayout.JAVA_INT, 0))
					glUseProgram(program.handle)
					glUniformMatrix(projectionHandle, 1, false, projection)
					i += Math.toRadians(1.0).toFloat()
					glUniformMatrix(
						modelHandle, 1, false,
						model.rotateY(i).rotateX(i / 2)
					)
					glUniformMatrix(viewHandle, 1, false, view)
					glBindVertexArray(vao.get(ValueLayout.JAVA_INT, 0))
					glDrawArrays(
						OpenGLPrimitiveRenderMode.GL_TRIANGLES,
						0, 36
					)
					swapBuffers()
				}
			}
			while (!window.processingLock.tryAcquire()) {
				glTasking.poll()?.run()
			}
		}
	}
}