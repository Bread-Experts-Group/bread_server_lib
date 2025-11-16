package org.bread_experts_group.api.system.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemGetThreadLocalUserFeature
import org.bread_experts_group.api.system.user.SystemUser
import org.bread_experts_group.api.system.user.windows.WindowsSystemUser
import org.bread_experts_group.ffi.windows.CURRENT_THREAD_EFFECTIVE_TOKEN
import org.bread_experts_group.ffi.windows.nativeGetTokenInformation

class WindowsSystemGetThreadLocalUserFeature : SystemGetThreadLocalUserFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetTokenInformation != null

	override val user: SystemUser
		get() = WindowsSystemUser(CURRENT_THREAD_EFFECTIVE_TOKEN)
}