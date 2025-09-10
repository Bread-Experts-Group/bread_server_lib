package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.coder.Mappable

enum class OpenGLTextureInternalFormat(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLTextureInternalFormat, UInt> {
	GL_RGB8(0x8051u, "RGB, 8 bit/sample"),
	GL_RGBA8(0x8058u, "RGBA, 8 bit/sample");

	override fun toString(): String = stringForm()
}