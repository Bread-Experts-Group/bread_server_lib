package org.bread_experts_group

import org.bread_experts_group.logging.ColoredHandler

val buildInfo: Class<*> = ColoredHandler::class.java.classLoader.loadClass("org.bread_experts_group.BuildInfo")
fun version() = buildInfo.getField("VERSION").get(null) as String
fun buildDate() = buildInfo.getField("COMPILE_DATE").get(null) as String