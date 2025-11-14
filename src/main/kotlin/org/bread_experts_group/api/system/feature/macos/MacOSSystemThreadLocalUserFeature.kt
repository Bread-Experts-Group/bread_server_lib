package org.bread_experts_group.api.system.feature.macos

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemThreadLocalUserFeature
import org.bread_experts_group.api.system.user.SystemUser
import org.bread_experts_group.api.system.user.macos.MacOSSystemUser

class MacOSSystemThreadLocalUserFeature : SystemThreadLocalUserFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	override val user: SystemUser
		get() = MacOSSystemUser()
}