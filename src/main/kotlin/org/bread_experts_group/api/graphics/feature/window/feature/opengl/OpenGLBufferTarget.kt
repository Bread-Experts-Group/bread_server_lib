package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLBufferTarget(
	override val id: UInt,
	override val tag: String
) : Mappable<OpenGLBufferTarget, UInt> {
	GL_ARRAY_BUFFER(0x8892u, "Array Buffer"),
	GL_ELEMENT_ARRAY_BUFFER(0x8893u, "Element Array Buffer");

	override fun toString(): String = stringForm()
}