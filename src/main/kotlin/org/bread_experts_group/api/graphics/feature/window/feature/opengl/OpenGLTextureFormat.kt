package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.generic.Mappable

enum class OpenGLTextureFormat(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLTextureFormat, UInt> {
	GL_RED(0x1903u, "Red"),
	GL_RGB(0x1907u, "Red, Green, Blue"),
	GL_RGBA(0x1908u, "Red, Green, Blue, Alpha");

	override fun toString(): String = stringForm()
}