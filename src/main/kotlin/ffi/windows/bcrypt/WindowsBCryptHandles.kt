package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.windows.LPCWSTR
import org.bread_experts_group.ffi.windows.NTSTATUS
import org.bread_experts_group.ffi.windows.PVOID
import org.bread_experts_group.ffi.windows.ULONG
import java.lang.foreign.*

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

val nativeBCryptFreeBuffer = bcrypt32Lookup.getDowncallVoid(
	linker, "BCryptFreeBuffer", PVOID
)