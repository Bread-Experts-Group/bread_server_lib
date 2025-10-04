package org.bread_experts_group.api.secure.cryptography.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.CryptographySystem
import org.bread_experts_group.api.secure.cryptography.CryptographySystemFeatures
import org.bread_experts_group.api.secure.cryptography.CryptographySystemProvider
import org.bread_experts_group.api.secure.cryptography.windows.feature.hash.*
import org.bread_experts_group.ffi.windows.ULONG
import org.bread_experts_group.ffi.windows.bcrypt.*
import org.bread_experts_group.ffi.windows.returnsNTRESULT
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsBCryptCryptographySystemProvider : CryptographySystemProvider() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private var providerInterfaceMap: Map<WindowsBCryptInterface, MutableMap<String, MutableSet<String>>> = emptyMap()
	override fun supported(): Boolean = Arena.ofConfined().use { arena ->
		val bufferSz = arena.allocate(ULONG)
		val bufferLoc = arena.allocate(ValueLayout.ADDRESS)
		val readStrings = buildList {
			(nativeBCryptEnumRegisteredProviders ?: return false).returnsNTRESULT(
				bufferSz,
				bufferLoc
			)
			val buffer = bufferLoc.get(ValueLayout.ADDRESS, 0).reinterpret(
				bufferSz.get(ULONG, 0).toUInt().toLong(),
				arena
			) {
				nativeBCryptFreeBuffer!!.invokeExact(it)
			}
			val count = buffer.get(ULONG, 0).toUInt().toLong()
			if (count == 0L) return@use false
			val array = buffer.get(ValueLayout.ADDRESS, 8).reinterpret(ValueLayout.ADDRESS.byteSize() * count)
			var offset = 0L
			(0..<count).forEach { _ ->
				val string = array.get(ValueLayout.ADDRESS, offset)
				add(string.reinterpret(Long.MAX_VALUE).getString(0, Charsets.UTF_16LE))
				offset += ValueLayout.ADDRESS.byteSize()
			}
		}
		// INTERFACE <FUNCTION, <IMPLEMENTING PROVIDERS>>
		val allocatedStrings = readStrings.associateWith { arena.allocateFrom(it, Charsets.UTF_16LE) }
		providerInterfaceMap = WindowsBCryptInterface.entries.associateWith { bcInterface ->
			val functionMap = mutableMapOf<String, MutableSet<String>>()
			for (providerName in readStrings) {
				bufferSz.set(ULONG, 0, 0)
				bufferLoc.set(ValueLayout.ADDRESS, 0, MemorySegment.NULL)
				(nativeBCryptQueryProviderRegistration ?: return@use false).returnsNTRESULT(
					allocatedStrings[providerName]!!,
					WindowsBCryptProviderMode.CRYPT_ANY.id.toInt(),
					bcInterface.id.toInt(),
					bufferSz,
					bufferLoc
				)
				val provider = WindowsBCryptProviderDescription(
					bufferLoc.get(ValueLayout.ADDRESS, 0).reinterpret(
						bufferSz.get(ULONG, 0).toUInt().toLong(),
						arena
					) {
						nativeBCryptFreeBuffer!!.invokeExact(it)
					}
				)
				provider.userMode?.interfaces?.forEach {
					for (function in it.bcFunctions) {
						val aliasMap = functionMap.getOrPut(function) { mutableSetOf() }
						aliasMap.addAll(provider.aliases)
						aliasMap.add(providerName)
					}
				}
			}
			functionMap
		}
		return true
	}

	override fun new(): CryptographySystem {
		val system = WindowsBCryptCryptographySystem()
		for ((iface, functions) in providerInterfaceMap) when (iface) {
			WindowsBCryptInterface.BCRYPT_HASH_INTERFACE -> for ((function, providers) in functions) when (function) {
				"MD2" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_MD2,
							BCRYPT_MD2_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD2_SIMD,
							BCRYPT_MD2_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_MD2_HMAC,
							BCRYPT_HMAC_MD2_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD2_HMAC_SIMD,
							BCRYPT_HMAC_MD2_ALG_HANDLE
						)
					)
				}

				"MD4" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_MD4,
							BCRYPT_MD4_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD4_SIMD,
							BCRYPT_MD4_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_MD4_HMAC,
							BCRYPT_HMAC_MD4_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD4_HMAC_SIMD,
							BCRYPT_HMAC_MD4_ALG_HANDLE
						)
					)
				}

				"MD5" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_MD5,
							BCRYPT_MD5_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD5_SIMD,
							BCRYPT_MD5_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_MD5_HMAC,
							BCRYPT_HMAC_MD5_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_MD5_HMAC_SIMD,
							BCRYPT_HMAC_MD5_ALG_HANDLE
						)
					)
				}

				"SHA1" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA1,
							BCRYPT_SHA1_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA1_SIMD,
							BCRYPT_SHA1_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA1_HMAC,
							BCRYPT_HMAC_SHA1_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA1_HMAC_SIMD,
							BCRYPT_HMAC_SHA1_ALG_HANDLE
						)
					)
				}

				"SHA256" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA256,
							BCRYPT_SHA256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA256_SIMD,
							BCRYPT_SHA256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA256_HMAC,
							BCRYPT_HMAC_SHA256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA256_HMAC_SIMD,
							BCRYPT_HMAC_SHA256_ALG_HANDLE
						)
					)
				}

				"SHA384" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA384,
							BCRYPT_SHA384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA384_SIMD,
							BCRYPT_SHA384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA384_HMAC,
							BCRYPT_HMAC_SHA384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA384_HMAC_SIMD,
							BCRYPT_HMAC_SHA384_ALG_HANDLE
						)
					)
				}

				"SHA512" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA512,
							BCRYPT_SHA512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA512_SIMD,
							BCRYPT_SHA512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA512_HMAC,
							BCRYPT_HMAC_SHA512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA512_HMAC_SIMD,
							BCRYPT_HMAC_SHA512_ALG_HANDLE
						)
					)
				}

				"SHA3-256" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_256,
							BCRYPT_SHA3_256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_256_SIMD,
							BCRYPT_SHA3_256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_256_HMAC,
							BCRYPT_HMAC_SHA3_256_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_256_HMAC_SIMD,
							BCRYPT_HMAC_SHA3_256_ALG_HANDLE
						)
					)
				}

				"SHA3-384" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_384,
							BCRYPT_SHA3_384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_384_SIMD,
							BCRYPT_SHA3_384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_384_HMAC,
							BCRYPT_HMAC_SHA3_384_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_384_HMAC_SIMD,
							BCRYPT_HMAC_SHA3_384_ALG_HANDLE
						)
					)
				}

				"SHA3-512" -> {
					system.exposedFeatures.add(
						WindowsHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_512,
							BCRYPT_SHA3_512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_512_SIMD,
							BCRYPT_SHA3_512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_512_HMAC,
							BCRYPT_HMAC_SHA3_512_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_SHA3_512_HMAC_SIMD,
							BCRYPT_HMAC_SHA3_512_ALG_HANDLE
						)
					)
				}

				"SHAKE128" -> Arena.ofShared().let {
					val algorithm = createBCryptAlgorithm(function, providers.first(), it)
					system.exposedFeatures.add(
						WindowsXOFHashingFeature(
							CryptographySystemFeatures.HASHING_SHAKE128,
							algorithm, it
						)
					)
				}

				"SHAKE256" -> Arena.ofShared().let {
					val algorithm = createBCryptAlgorithm(function, providers.first(), it)
					system.exposedFeatures.add(
						WindowsXOFHashingFeature(
							CryptographySystemFeatures.HASHING_SHAKE256,
							algorithm, it
						)
					)
				}

				"CSHAKE128" -> system.exposedFeatures.add(
					WindowsCSHAKEXOFHashingFeature(
						CryptographySystemFeatures.HASHING_CSHAKE128,
						BCRYPT_CSHAKE128_ALG_HANDLE
					)
				)

				"CSHAKE256" -> system.exposedFeatures.add(
					WindowsCSHAKEXOFHashingFeature(
						CryptographySystemFeatures.HASHING_CSHAKE256,
						BCRYPT_CSHAKE256_ALG_HANDLE
					)
				)

				"AES-CMAC" -> {
					system.exposedFeatures.add(
						WindowsMACHashingFeature(
							CryptographySystemFeatures.HASHING_AES_CMAC,
							BCRYPT_AES_CMAC_ALG_HANDLE
						)
					)
					system.exposedFeatures.add(
						WindowsMACSIMDHashingFeature(
							CryptographySystemFeatures.HASHING_AES_CMAC_SIMD,
							BCRYPT_AES_CMAC_ALG_HANDLE
						)
					)
				}

				"AES-GMAC" -> {}

				"KMAC128" -> {
					system.exposedFeatures.add(
						WindowsKMACXOFHashingFeature(
							CryptographySystemFeatures.HASHING_KMAC128,
							BCRYPT_KMAC128_ALG_HANDLE
						)
					)
				}

				"KMAC256" -> {
					system.exposedFeatures.add(
						WindowsKMACXOFHashingFeature(
							CryptographySystemFeatures.HASHING_KMAC256,
							BCRYPT_KMAC256_ALG_HANDLE
						)
					)
				}

				else -> println(" $iface: Needs implementation: $function")
			}

			else -> println("Needs implementation $iface")
		}
		return system
	}
}