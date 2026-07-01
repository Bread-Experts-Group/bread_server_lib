package org.bread_experts_group.model

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

fun extToString(obj: Any?): String = when (obj) {
	is ByteArray -> "ByteArray(${obj.size}${if (obj.size < 256) "[${obj.toHexString()}]" else ""})"
	else -> "$obj"
}

@Suppress("UNCHECKED_CAST")
fun genericToString(obj: Any): String = "${obj::class.simpleName ?: ""}(" +
		(obj::class as KClass<Any>).memberProperties.joinToString(", ") {
			if (it.visibility == KVisibility.PUBLIC) "${it.name}=${extToString(it.getter(obj))}" else
				"${if (it.visibility != null) "(${it.visibility})" else ""}${it.name}"
		} + ')'