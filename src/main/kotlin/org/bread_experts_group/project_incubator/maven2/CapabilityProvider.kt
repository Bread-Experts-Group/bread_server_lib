package org.bread_experts_group.project_incubator.maven2

import kotlin.reflect.KClass

interface CapabilityProvider<P : Any> {
	fun systemCompatible(): Boolean
	fun new(): P

	val produces: KClass<out P>
}