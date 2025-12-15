package org.bread_experts_group.api.system.user.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.user.feature.SystemUserGetNameFeature
import org.bread_experts_group.ffi.windows.advapi.LookupAccountSidParameters
import org.bread_experts_group.ffi.windows.advapi.nativeLookupAccountSid

class WindowsSystemUserGetNameFeature(private val user: WindowsSystemUser) : SystemUserGetNameFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeLookupAccountSid != null

	override val name: String
		get() {
			val data = nativeLookupAccountSid!!(LookupAccountSidParameters(null, user.sid))
			return data.referencedDomainName + '\\' + data.name
		}
}