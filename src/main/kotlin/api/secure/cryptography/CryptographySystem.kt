package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.FeatureProvisioner
import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation
import java.lang.ref.Cleaner

abstract class CryptographySystem : FeatureProvisioner<CryptographySystemFeatureImplementation<*>>(
	CryptographySystemFeatureImplementation::class.java
), AutoCloseable {
	private companion object {
		val csCleaner: Cleaner = Cleaner.create()
	}

	private val cleanOp = csCleaner.register(this) { this.clean() }
	abstract fun clean()
	final override fun close() {
		cleanOp.clean()
	}
}