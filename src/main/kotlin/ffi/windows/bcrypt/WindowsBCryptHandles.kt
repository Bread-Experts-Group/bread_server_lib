package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.windows.LPCWSTR
import org.bread_experts_group.ffi.windows.NTSTATUS
import org.bread_experts_group.ffi.windows.PVOID
import org.bread_experts_group.ffi.windows.ULONG
import java.lang.foreign.*

val BCRYPT_MD2_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000001)
val BCRYPT_MD4_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000011)
val BCRYPT_MD5_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000021)
val BCRYPT_SHA1_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000031)
val BCRYPT_SHA256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000041)
val BCRYPT_SHA384_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000051)
val BCRYPT_SHA512_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000061)
val BCRYPT_HMAC_MD5_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000091)
val BCRYPT_HMAC_SHA1_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000000A1)
val BCRYPT_HMAC_SHA256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000000B1)
val BCRYPT_HMAC_SHA384_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000000C1)
val BCRYPT_HMAC_SHA512_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000000D1)
val BCRYPT_AES_CMAC_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000101)
val BCRYPT_HMAC_MD2_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000121)
val BCRYPT_HMAC_MD4_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000131)
val BCRYPT_SHA3_256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000003B1)
val BCRYPT_SHA3_384_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000003C1)
val BCRYPT_SHA3_512_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000003D1)
val BCRYPT_HMAC_SHA3_256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000003E1)
val BCRYPT_HMAC_SHA3_384_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x000003F1)
val BCRYPT_HMAC_SHA3_512_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000401)
val BCRYPT_CSHAKE128_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000411)
val BCRYPT_CSHAKE256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000421)
val BCRYPT_KMAC128_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000431)
val BCRYPT_KMAC256_ALG_HANDLE: MemorySegment = MemorySegment.ofAddress(0x00000441)

private val handleArena = Arena.ofAuto()
private val bcrypt32Lookup: SymbolLookup = handleArena.getLookup("Bcrypt.dll")
private val linker: Linker = Linker.nativeLinker()

val BCRYPT_HANDLE: AddressLayout = ValueLayout.ADDRESS
val BCRYPT_ALG_HANDLE = BCRYPT_HANDLE
val BCRYPT_HASH_HANDLE = BCRYPT_HANDLE

val nativeBCryptEnumRegisteredProviders = bcrypt32Lookup.getDowncall(
	linker, "BCryptEnumRegisteredProviders", NTSTATUS,
	ValueLayout.ADDRESS /* of ULONG */, ValueLayout.ADDRESS // of PCRYPT_PROVIDERS
)

val nativeBCryptQueryProviderRegistration = bcrypt32Lookup.getDowncall(
	linker, "BCryptQueryProviderRegistration", NTSTATUS,
	LPCWSTR, ULONG, ULONG, ValueLayout.ADDRESS /* of ULONG */, ValueLayout.ADDRESS // of PCRYPT_PROVIDER_REG
)

val nativeBCryptOpenAlgorithmProvider = bcrypt32Lookup.getDowncall(
	linker, "BCryptOpenAlgorithmProvider", NTSTATUS,
	ValueLayout.ADDRESS /* of BCRYPT_ALG_HANDLE  */, LPCWSTR, LPCWSTR, ULONG
)

val nativeBCryptCloseAlgorithmProvider = bcrypt32Lookup.getDowncall(
	linker, "BCryptCloseAlgorithmProvider", NTSTATUS,
	BCRYPT_ALG_HANDLE, ULONG
)

val nativeBCryptGetProperty = bcrypt32Lookup.getDowncall(
	linker, "BCryptGetProperty", NTSTATUS,
	BCRYPT_HANDLE, LPCWSTR, ValueLayout.ADDRESS, ULONG, ValueLayout.ADDRESS /* of ULONG */, ULONG
)

val nativeBCryptSetProperty = bcrypt32Lookup.getDowncall(
	linker, "BCryptSetProperty", NTSTATUS,
	BCRYPT_HANDLE, LPCWSTR, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptCreateMultiHash = bcrypt32Lookup.getDowncall(
	linker, "BCryptCreateMultiHash", NTSTATUS,
	BCRYPT_ALG_HANDLE, BCRYPT_HASH_HANDLE, ULONG, ValueLayout.ADDRESS, ULONG, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptProcessMultiOperations = bcrypt32Lookup.getDowncall(
	linker, "BCryptProcessMultiOperations", NTSTATUS,
	BCRYPT_HANDLE, BCRYPT_MULTI_OPERATION_TYPE, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptCreateHash = bcrypt32Lookup.getDowncall(
	linker, "BCryptCreateHash", NTSTATUS,
	BCRYPT_ALG_HANDLE, BCRYPT_HASH_HANDLE, ValueLayout.ADDRESS, ULONG, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptHashData = bcrypt32Lookup.getDowncall(
	linker, "BCryptHashData", NTSTATUS,
	BCRYPT_HASH_HANDLE, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptFinishHash = bcrypt32Lookup.getDowncall(
	linker, "BCryptFinishHash", NTSTATUS,
	BCRYPT_HASH_HANDLE, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptDestroyHash = bcrypt32Lookup.getDowncall(
	linker, "BCryptDestroyHash", NTSTATUS,
	BCRYPT_HASH_HANDLE
)

val nativeBCryptDuplicateHash = bcrypt32Lookup.getDowncall(
	linker, "BCryptDuplicateHash", NTSTATUS,
	BCRYPT_HASH_HANDLE, BCRYPT_HASH_HANDLE, ValueLayout.ADDRESS, ULONG, ULONG
)

val nativeBCryptFreeBuffer = bcrypt32Lookup.getDowncallVoid(
	linker, "BCryptFreeBuffer", PVOID
)