package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLTextureParameter(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLTextureParameter, UInt> {
	GL_TEXTURE_MIN_FILTER(0x2801u, "Minification Filter"),
	GL_TEXTURE_MAG_FILTER(0x2800u, "Magnification Filter");

	override fun toString(): String = stringForm()
}