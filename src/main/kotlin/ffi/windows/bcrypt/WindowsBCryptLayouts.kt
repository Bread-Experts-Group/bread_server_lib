package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.ffi.windows.PWSTR
import org.bread_experts_group.ffi.windows.ULONG
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val CRYPT_INTERFACE_REG: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("dwInterface"),
	ULONG.withName("dwFlags"),
	ULONG.withName("cFunctions"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("rgpszFunctions") // *PWSTR
)
val CRYPT_INTERFACE_REG_dwInterface: VarHandle = CRYPT_INTERFACE_REG.varHandle(groupElement("dwInterface"))
val CRYPT_INTERFACE_REG_dwFlags: VarHandle = CRYPT_INTERFACE_REG.varHandle(groupElement("dwFlags"))
val CRYPT_INTERFACE_REG_cFunctions: VarHandle = CRYPT_INTERFACE_REG.varHandle(groupElement("cFunctions"))
val CRYPT_INTERFACE_REG_rgpszFunctions: VarHandle = CRYPT_INTERFACE_REG.varHandle(groupElement("rgpszFunctions"))

val CRYPT_IMAGE_REG: StructLayout = MemoryLayout.structLayout(
	PWSTR.withName("pszImage"),
	ULONG.withName("cInterfaces"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("rgpInterfaces") // *CRYPT_INTERFACE_REG
)
val CRYPT_IMAGE_REG_pszImage: VarHandle = CRYPT_IMAGE_REG.varHandle(groupElement("pszImage"))
val CRYPT_IMAGE_REG_cInterfaces: VarHandle = CRYPT_IMAGE_REG.varHandle(groupElement("cInterfaces"))
val CRYPT_IMAGE_REG_rgpInterfaces: VarHandle = CRYPT_IMAGE_REG.varHandle(groupElement("rgpInterfaces"))

val CRYPT_PROVIDER_REG: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("cAliases"),
	MemoryLayout.paddingLayout(4),
	PWSTR.withName("rgpszAliases"),
	ValueLayout.ADDRESS.withName("pUM"), // CRYPT_IMAGE_REG
	ValueLayout.ADDRESS.withName("pKM") // CRYPT_IMAGE_REG
)
val CRYPT_PROVIDER_REG_cAliases: VarHandle = CRYPT_PROVIDER_REG.varHandle(groupElement("cAliases"))
val CRYPT_PROVIDER_REG_rgpszAliases: VarHandle = CRYPT_PROVIDER_REG.varHandle(groupElement("rgpszAliases"))
val CRYPT_PROVIDER_REG_pUM: VarHandle = CRYPT_PROVIDER_REG.varHandle(groupElement("pUM"))
val CRYPT_PROVIDER_REG_pKM: VarHandle = CRYPT_PROVIDER_REG.varHandle(groupElement("pKM"))

val BCRYPT_MULTI_HASH_OPERATION: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("iHash"),
	BCRYPT_HASH_OPERATION_TYPE.withName("hashOperation"),
	ValueLayout.ADDRESS.withName("pbBuffer").withByteAlignment(4),
	ULONG.withName("cbBuffer"),
	MemoryLayout.paddingLayout(4)
)
val BCRYPT_MULTI_HASH_OPERATION_iHash: VarHandle = BCRYPT_MULTI_HASH_OPERATION.varHandle(groupElement("iHash"))
val BCRYPT_MULTI_HASH_OPERATION_hashOperation: VarHandle = BCRYPT_MULTI_HASH_OPERATION.varHandle(
	groupElement("hashOperation")
)
val BCRYPT_MULTI_HASH_OPERATION_pbBuffer: VarHandle = BCRYPT_MULTI_HASH_OPERATION.varHandle(groupElement("pbBuffer"))
val BCRYPT_MULTI_HASH_OPERATION_cbBuffer: VarHandle = BCRYPT_MULTI_HASH_OPERATION.varHandle(groupElement("cbBuffer"))