package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureProvider
import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation
import org.bread_experts_group.logging.ColoredHandler
import java.lang.ref.Cleaner
import java.util.*
import java.util.logging.Logger

abstract class CryptographySystem : FeatureProvider<CryptographySystemFeatureImplementation<*>>, AutoCloseable {
	private companion object {
		val csCleaner: Cleaner = Cleaner.create()
	}

	override val logger: Logger = ColoredHandler.newLogger("TMP logger")
	override val features: MutableList<CryptographySystemFeatureImplementation<*>> = ServiceLoader.load(
		CryptographySystemFeatureImplementation::class.java
	).toMutableList()
	override val supportedFeatures: MutableMap<
			FeatureExpression<out CryptographySystemFeatureImplementation<*>>,
			MutableList<CryptographySystemFeatureImplementation<*>>
			> = mutableMapOf()

	private val cleanOp = csCleaner.register(this) { this.clean() }
	abstract fun clean()
	final override fun close() {
		cleanOp.clean()
	}
}