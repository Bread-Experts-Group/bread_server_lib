package org.bread_experts_group.api.graphics.feature.window.feature.opengl

import org.bread_experts_group.generic.MappedEnumeration

class OpenGLException(
	val error: MappedEnumeration<UInt, OpenGLError>
) : RuntimeException(error.toString())