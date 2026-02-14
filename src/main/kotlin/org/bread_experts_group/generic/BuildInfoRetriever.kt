package org.bread_experts_group.generic

val buildInfo: Class<*> = MappedEnumeration::class.java.classLoader.loadClass("org.bread_experts_group.BuildInfo")
fun bslVersion() = buildInfo.getField("VERSION").get(null) as String
fun bslBuildDate() = buildInfo.getField("COMPILE_DATE").get(null) as String