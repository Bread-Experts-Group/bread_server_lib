package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.generic.Mappable

enum class EFIMemoryType(override val id: UInt) : Mappable<EFIMemoryType, UInt> {
	EfiReservedMemoryType(0u),
	EfiLoaderCode(1u),
	EfiLoaderData(2u),
	EfiBootServicesCode(3u),
	EfiBootServicesData(4u),
	EfiRuntimeServicesCode(5u),
	EfiRuntimeServicesData(6u),
	EfiConventionalMemory(7u),
	EfiUnusableMemory(8u),
	EfiACPIReclaimMemory(9u),
	EfiACPIMemoryNVS(10u),
	EfiMemoryMappedIO(11u),
	EfiMemoryMappedIOPortSpace(12u),
	EfiPalCode(13u),
	EfiPersistentMemory(14u),
	EfiUnacceptedMemoryType(15u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}