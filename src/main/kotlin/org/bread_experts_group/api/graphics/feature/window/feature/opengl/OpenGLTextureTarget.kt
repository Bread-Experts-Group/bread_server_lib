package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.generic.Mappable

enum class OpenGLTextureTarget(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLTextureTarget, UInt> {
	GL_TEXTURE_2D(0x0DE1u, "2D Texture");

	override fun toString(): String = stringForm()
}