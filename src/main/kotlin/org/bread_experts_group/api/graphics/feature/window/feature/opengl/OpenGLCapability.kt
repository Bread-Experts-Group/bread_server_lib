package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Mappable

enum class OpenGLCapability(override val id: UInt, override val tag: String) : Mappable<OpenGLCapability, UInt> {
	GL_DEPTH_TEST(0x0B71u, "Depth Test");

	override fun toString(): String = stringForm()
}