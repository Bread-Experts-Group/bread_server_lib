package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.generic.logging.LevelLogger
import java.lang.ref.Cleaner
import java.util.*

abstract class CryptographySystem : FeatureProvider<CryptographySystemFeatureImplementation<*>>, AutoCloseable {
	private companion object {
		val csCleaner: Cleaner = Cleaner.create()
	}

	override val logger = LevelLogger("crypto", SystemProvider.logger)
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