package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.api.graphics.feature.window.feature.opengl.OpenGLError.GL_NO_ERROR
import org.bread_experts_group.coder.Mappable
import org.bread_experts_group.coder.MappedEnumeration

enum class OpenGLError(override val id: UInt, override val tag: String) : Mappable<OpenGLError, UInt> {
	GL_NO_ERROR(0x0000u, "No Error"),
	GL_INVALID_VALUE(0x0501u, "Invalid Value"),
	GL_INVALID_OPERATION(0x0502u, "Invalid Operation"),
}

fun MappedEnumeration<UInt, OpenGLError>.checkAndThrow() {
	if (this.enum == GL_NO_ERROR) return
	throw OpenGLException(this)
}