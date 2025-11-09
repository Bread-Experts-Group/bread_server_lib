package org.bread_experts_group.api.system.user.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.user.feature.SystemUserGetLogonTimeFeature
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout
import java.time.Instant

class WindowsSystemUserGetLogonTimeFeature(private val user: WindowsSystemUser) : SystemUserGetLogonTimeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeLsaGetLogonSessionData != null

	override val logonTime: Instant
		get() {
			nativeLsaGetLogonSessionData!!.returnsNTSTATUS(
				user.sessionLUID,
				threadLocalPTR
			)
			Arena.ofConfined().use {
				val data = threadLocalPTR.get(ValueLayout.ADDRESS, 0).reinterpret(
					SECURITY_LOGON_SESSION_DATA.byteSize(),
					it
				) { m ->
					nativeLsaFreeReturnBuffer!!.returnsNTSTATUS(m)
				}
				return Instant.ofEpochMilli(
					FILETIMEToUnixMs(SECURITY_LOGON_SESSION_DATA_LogonTime.get(data, 0L) as Long)
				)
			}
		}
}