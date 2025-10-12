package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLDataType(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLDataType, UInt> {
	GL_UNSIGNED_BYTE(0x1401u, "GLubyte"),
	GL_UNSIGNED_INT(0x1405u, "GLuint"),
	GL_FLOAT(0x1406u, "GLfloat");

	override fun toString(): String = stringForm()
}