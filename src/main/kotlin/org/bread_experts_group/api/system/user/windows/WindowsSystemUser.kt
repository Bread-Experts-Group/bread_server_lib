package org.bread_experts_group.api.system.user.windows

import org.bread_experts_group.api.system.user.SystemUser
import org.bread_experts_group.api.system.user.feature.SystemUserFeatureImplementation
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemUser(accessToken: MemorySegment) : SystemUser() {
	override val features: MutableList<SystemUserFeatureImplementation<*>> = mutableListOf(
		WindowsSystemUserGetNameFeature(this),
		WindowsSystemUserGetLogonTimeFeature(this)
	)

	private val arena = Arena.ofAuto()
	val sid: MemorySegment
	val sessionLUID: MemorySegment

	init {
		var data = getTokenInformation(arena, accessToken, WindowsTokenInformationClass.TOKEN_USER)
		val user = TOKEN_USER_User.invokeExact(data, 0L) as MemorySegment
		sid = SID_AND_ATTRIBUTES_Sid.get(user, 0L) as MemorySegment
		data = getTokenInformation(arena, accessToken, WindowsTokenInformationClass.TOKEN_STATISTICS)
		sessionLUID = TOKEN_STATISTICS_AuthenticationId.invokeExact(data, 0L) as MemorySegment
	}
}