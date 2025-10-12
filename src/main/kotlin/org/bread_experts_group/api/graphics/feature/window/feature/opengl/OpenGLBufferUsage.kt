package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLBufferUsage(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLBufferUsage, UInt> {
	GL_STATIC_DRAW(0x88E4u, "Static Draw");

	override fun toString(): String = stringForm()
}