package org.bread_experts_group.api.secure.cryptography.feature

import org.bread_experts_group.api.secure.blob.SecuredByteArray

abstract class HashingFeature : CryptographySystemFeatureImplementation<HashingFeature>() {
	abstract operator fun plusAssign(b: Byte)
	abstract operator fun plusAssign(b: ByteArray)
	operator fun plusAssign(b: SecuredByteArray) = plusAssign(b.around)

	abstract fun flush(): ByteArray
	fun flushSecure(): SecuredByteArray = SecuredByteArray(flush())
}