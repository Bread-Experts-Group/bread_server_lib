package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.Flaggable

enum class OpenGLClearFlags(override val position: Long) : Flaggable {
	GL_COLOR_BUFFER_BIT(0x00004000),
	GL_DEPTH_BUFFER_BIT(0x00000100)
}