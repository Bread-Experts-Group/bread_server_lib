package org.bread_experts_group.api.system.user.macos

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.user.feature.SystemUserGetLogonTimeFeature
import java.time.Instant

class MacOSSystemUserGetLogonTimeFeature(private val user: MacOSSystemUser) : SystemUserGetLogonTimeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO: implement using utmpx

	private val placeholder: Instant = Instant.now()

	override val logonTime: Instant
		get() = placeholder
}