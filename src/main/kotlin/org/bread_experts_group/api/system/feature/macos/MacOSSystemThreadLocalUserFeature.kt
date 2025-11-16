package org.bread_experts_group.api.system.feature.macos

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemGetThreadLocalUserFeature
import org.bread_experts_group.api.system.user.SystemUser
import org.bread_experts_group.api.system.user.macos.MacOSSystemUser

class MacOSSystemThreadLocalUserFeature : SystemGetThreadLocalUserFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	override val user: SystemUser
		get() = MacOSSystemUser()
}