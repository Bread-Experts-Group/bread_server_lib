package org.bread_experts_group.api.system.user.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.user.feature.SystemUserGetNameFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemUserGetNameFeature(private val user: WindowsSystemUser) : SystemUserGetNameFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeLookupAccountSidW != null

	override val name: String
		get() = Arena.ofConfined().use {
			threadLocalDWORD0.set(DWORD, 0, 0)
			threadLocalDWORD1.set(DWORD, 0, 0)
			nativeLookupAccountSidW!!.invokeExact(
				capturedStateSegment,
				MemorySegment.NULL,
				user.sid,
				MemorySegment.NULL,
				threadLocalDWORD0,
				MemorySegment.NULL,
				threadLocalDWORD1,
				threadLocalDWORD2
			) as Int
			val accountName = it.allocate(
				WCHAR,
				(threadLocalDWORD0.get(DWORD, 0) + threadLocalDWORD1.get(DWORD, 0)).toLong()
			)
			val referencedDomainName = accountName.asSlice(WCHAR.byteSize() * threadLocalDWORD0.get(DWORD, 0))
			val status = nativeLookupAccountSidW.invokeExact(
				capturedStateSegment,
				MemorySegment.NULL,
				user.sid,
				accountName,
				threadLocalDWORD0,
				referencedDomainName,
				threadLocalDWORD1,
				threadLocalDWORD2
			) as Int
			if (status == 0) throwLastError()
			return referencedDomainName.getString(0, Charsets.UTF_16LE) + '\\' +
					accountName.getString(0, Charsets.UTF_16LE)
		}
}