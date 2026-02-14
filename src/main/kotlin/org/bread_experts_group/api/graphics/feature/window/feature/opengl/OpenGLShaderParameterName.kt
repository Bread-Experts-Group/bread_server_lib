package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.generic.Mappable

enum class OpenGLShaderParameterName(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLProgramParameterName, UInt> {
	GL_INFO_LOG_LENGTH(0x8B84u, "Info Log Length"),
	GL_COMPILE_STATUS(0x8B81u, "Compile Status");

	override fun toString(): String = stringForm()
}