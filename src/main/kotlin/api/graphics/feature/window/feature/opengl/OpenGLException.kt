package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.coder.MappedEnumeration

class OpenGLException(
	val error: MappedEnumeration<UInt, OpenGLError>
) : RuntimeException(error.toString())