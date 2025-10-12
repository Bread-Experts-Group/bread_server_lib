package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLShaderType(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLShaderType, UInt> {
	GL_VERTEX_SHADER(0x8B31u, "Vertex Shader"),
	GL_FRAGMENT_SHADER(0x8B30u, "Fragment Shader");

	override fun toString(): String = stringForm()
}