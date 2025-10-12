package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLPolygonFace(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLPolygonFace, UInt> {
	GL_FRONT(0x0404u, "Front"),
	GL_BACK(0x0405u, "Back"),
	GL_FRONT_AND_BACK(0x0408u, "Front And Back"),
}