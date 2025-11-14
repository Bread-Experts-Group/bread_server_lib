package org.bread_experts_group.api.system.user.macos

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.user.feature.SystemUserGetNameFeature
import org.bread_experts_group.ffi.macos.nativeGetlogin
import java.lang.foreign.MemorySegment

class MacOSSystemUserGetNameFeature(private val user: MacOSSystemUser) : SystemUserGetNameFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true

	override val name: String
		get() {
			val name = nativeGetlogin!!.invokeExact() as MemorySegment
			return name.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_8)
		}
}