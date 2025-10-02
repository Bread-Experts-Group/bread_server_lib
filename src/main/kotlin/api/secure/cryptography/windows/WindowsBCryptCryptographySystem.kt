package org.bread_experts_group.api.secure.cryptography.windows

import org.bread_experts_group.api.secure.cryptography.CryptographySystem
import java.lang.foreign.Arena

class WindowsBCryptCryptographySystem() : CryptographySystem() {
	private val arena = Arena.ofShared()
	internal val exposedFeatures = features

	override fun clean() {
		arena.close()
	}
}