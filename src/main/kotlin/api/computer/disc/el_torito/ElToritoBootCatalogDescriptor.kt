package org.bread_experts_group.api.computer.disc.el_torito

import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequentialIOLayout

data class ElToritoBootCatalogDescriptor(val bootCatalogOffset: UInt) {
	companion object {
		const val BOOT_SYSTEM_IDENTIFIER = "EL TORITO SPECIFICATION" +
				"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"
		val layout = SequentialIOLayout(
			::ElToritoBootCatalogDescriptor,
			IOLayout.Companion.UNSIGNED_INT
		)
	}

	override fun toString(): String = "El Torito Boot Catalog Descriptor [@ $bootCatalogOffset]"
}