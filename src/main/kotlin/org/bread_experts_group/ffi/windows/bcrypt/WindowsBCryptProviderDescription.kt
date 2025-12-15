package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.ffi.windows.winCharsetWide
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsBCryptProviderDescription(ptr: MemorySegment) {
	val aliases = run {
		val count = CRYPT_PROVIDER_REG_cAliases.get(ptr, 0) as Int
		val aliasArray = (CRYPT_PROVIDER_REG_rgpszAliases.get(ptr, 0) as MemorySegment)
			.reinterpret(ValueLayout.ADDRESS.byteSize() * count)
		var offset = 0L
		List(count) {
			val string = aliasArray.get(ValueLayout.ADDRESS, offset).reinterpret(Long.MAX_VALUE)
			offset += ValueLayout.ADDRESS.byteSize()
			string.getString(0, winCharsetWide)
		}
	}
	val userMode = (CRYPT_PROVIDER_REG_pUM.get(ptr, 0) as MemorySegment).let {
		if (it != MemorySegment.NULL) WindowsBCryptProviderImageDescription(it.reinterpret(CRYPT_IMAGE_REG.byteSize()))
		else null
	}
	val kernelMode = (CRYPT_PROVIDER_REG_pKM.get(ptr, 0) as MemorySegment).let {
		if (it != MemorySegment.NULL) WindowsBCryptProviderImageDescription(it.reinterpret(CRYPT_IMAGE_REG.byteSize()))
		else null
	}

	override fun toString(): String = "Provider Description" +
			"\n\tAliases: $aliases" +
			"\n\tUser Mode: ${userMode.toString().replace("\n", "\n\t")}" +
			"\n\tKernel Mode: ${kernelMode.toString().replace("\n", "\n\t")}"
}