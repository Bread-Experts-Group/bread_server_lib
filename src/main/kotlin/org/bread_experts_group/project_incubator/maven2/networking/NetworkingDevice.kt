package org.bread_experts_group.project_incubator.maven2.networking

import java.util.*
import kotlin.reflect.full.isSubclassOf

interface NetworkingDevice {
	companion object {
		val providers = ServiceLoader.load(NetworkingProvider::class.java).filter {
			it.systemCompatible()
		}

		inline fun <reified T> new(): T? = providers.firstOrNull {
			it.produces.isSubclassOf(T::class)
		}?.new() as? T
	}
}