package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.generic.Mappable

enum class OpenGLPolygonMode(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLPolygonMode, UInt> {
	GL_POINT(0x1B00u, "Point"),
	GL_LINE(0x1B01u, "Line"),
	GL_FILL(0x1B02u, "Fill"),
}