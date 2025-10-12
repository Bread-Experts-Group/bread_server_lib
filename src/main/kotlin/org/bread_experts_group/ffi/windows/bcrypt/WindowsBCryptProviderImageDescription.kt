package org.bread_experts_group.ffi.windows.bcrypt

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsBCryptProviderImageDescription(ptr: MemorySegment) {
	val moduleFile: String = (CRYPT_IMAGE_REG_pszImage.get(ptr, 0) as MemorySegment)
		.reinterpret(Long.MAX_VALUE)
		.getString(0, Charsets.UTF_16LE)
	val interfaces: List<WindowsBCryptProviderInterfaceDescription> = run {
		val ptr = ptr.reinterpret(CRYPT_IMAGE_REG.byteSize())
		val count = CRYPT_IMAGE_REG_cInterfaces.get(ptr, 0) as Int
		val interfaceArray = (CRYPT_IMAGE_REG_rgpInterfaces.get(ptr, 0) as MemorySegment)
			.reinterpret(ValueLayout.ADDRESS.byteSize() * count)
		var offset = 0L
		List(count) {
			val interfacePtr = interfaceArray.get(ValueLayout.ADDRESS, offset)
				.reinterpret(CRYPT_INTERFACE_REG.byteSize())
			offset += ValueLayout.ADDRESS.byteSize()
			WindowsBCryptProviderInterfaceDescription(interfacePtr)
		}
	}

	override fun toString(): String = "Provider Image Description" +
			"\n\tModule File: $moduleFile" +
			"\n\tInterfaces: ${interfaces.toString().replace("\n", "\n\t")}"
}