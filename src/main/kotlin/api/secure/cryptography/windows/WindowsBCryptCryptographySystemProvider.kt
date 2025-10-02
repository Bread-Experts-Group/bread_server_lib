package org.bread_experts_group.api.secure.cryptography.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.cryptography.CryptographySystem
import org.bread_experts_group.api.secure.cryptography.CryptographySystemFeatures
import org.bread_experts_group.api.secure.cryptography.CryptographySystemProvider
import org.bread_experts_group.api.secure.cryptography.windows.feature.WindowsHashingFeature
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.ffi.windows.COMException
import org.bread_experts_group.ffi.windows.ULONG
import org.bread_experts_group.ffi.windows.WindowsNTStatus
import org.bread_experts_group.ffi.windows.bcrypt.*
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
			val status = WindowsNTStatus.entries.id(
				((nativeBCryptEnumRegisteredProviders ?: return false).invokeExact(
					bufferSz,
					bufferLoc
				) as Int).toUInt()
			)
			if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
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
				val status = WindowsNTStatus.entries.id(
					((nativeBCryptQueryProviderRegistration ?: return@use false).invokeExact(
						allocatedStrings[providerName],
						WindowsBCryptProviderMode.CRYPT_ANY.id.toInt(),
						bcInterface.id.toInt(),
						bufferSz,
						bufferLoc
					) as Int).toUInt()
				)
				if (status.enum != WindowsNTStatus.STATUS_SUCCESS) throw COMException(status.toString())
				val provider = BCryptProviderDescription(
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
				"SHA1" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA1,
						function, providers.first()
					)
				)

				"SHA256" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA256,
						function, providers.first()
					)
				)

				"SHA384" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA384,
						function, providers.first()
					)
				)

				"SHA512" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA512,
						function, providers.first()
					)
				)

				"MD5" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_MD5,
						function, providers.first()
					)
				)

				"MD4" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_MD4,
						function, providers.first()
					)
				)

				"MD2" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_MD2,
						function, providers.first()
					)
				)

				"SHA3-256" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA3_256,
						function, providers.first()
					)
				)

				"SHA3-384" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA3_384,
						function, providers.first()
					)
				)

				"SHA3-512" -> system.exposedFeatures.add(
					WindowsHashingFeature(
						CryptographySystemFeatures.HASHING_SHA3_512,
						function, providers.first()
					)
				)

				else -> println(" $iface: Needs implementation: $function")
			}

			else -> println("Needs implementation $iface")
		}
		return system
	}
}