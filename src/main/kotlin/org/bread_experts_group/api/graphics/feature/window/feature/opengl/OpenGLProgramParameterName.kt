package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLProgramParameterName(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLProgramParameterName, UInt> {
	GL_LINK_STATUS(0x8B82u, "Link Status"),
	GL_INFO_LOG_LENGTH(0x8B84u, "Info Log Length");

	override fun toString(): String = stringForm()
}