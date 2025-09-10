package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.coder.Mappable

enum class OpenGLPrimitiveRenderMode(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLPrimitiveRenderMode, UInt> {
	GL_TRIANGLES(0x0004u, "Triangles");

	override fun toString(): String = stringForm()
}