package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsBCryptProviderInterfaceDescription(ptr: MemorySegment) {
	val bcInterface: MappedEnumeration<UInt, WindowsBCryptInterface> = WindowsBCryptInterface.entries.id(
		(CRYPT_INTERFACE_REG_dwInterface.get(ptr, 0) as Int).toUInt()
	)
	val bcFlags: MappedEnumeration<UInt, WindowsBCryptInterfaceFlags> = WindowsBCryptInterfaceFlags.entries.id(
		(CRYPT_INTERFACE_REG_dwFlags.get(ptr, 0) as Int).toUInt()
	)
	val bcFunctions = run {
		val count = CRYPT_INTERFACE_REG_cFunctions.get(ptr, 0) as Int
		val aliasArray = (CRYPT_INTERFACE_REG_rgpszFunctions.get(ptr, 0) as MemorySegment)
			.reinterpret(ValueLayout.ADDRESS.byteSize() * count)
		var offset = 0L
		List(count) {
			val string = aliasArray.get(ValueLayout.ADDRESS, offset).reinterpret(Long.MAX_VALUE)
			offset += ValueLayout.ADDRESS.byteSize()
			string.getString(0, Charsets.UTF_16LE)
		}
	}

	override fun toString(): String = "Interface Description" +
			"\n\tInterface: $bcInterface" +
			"\n\tFlags: $bcFlags" +
			"\n\tFunctions: $bcFunctions"
}