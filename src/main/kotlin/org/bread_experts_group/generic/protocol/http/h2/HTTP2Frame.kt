package org.bread_experts_group.generic.protocol.http.h2

import org.bread_experts_group.generic.MappedEnumeration

data class HTTP2Frame(
	val length: Int,
	val type: MappedEnumeration<Int, HTTP2StandardFrameTypes>,
	val flags: Int,
	val streamIdentifier: Int
) : HTTP2ReadFrameData