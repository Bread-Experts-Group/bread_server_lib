package org.bread_experts_group.api.secure.blob

import java.lang.AutoCloseable
import java.lang.ref.Cleaner
import java.util.*
import kotlin.ByteArray

/**
 * A thin wrapper around a [ByteArray] for use with [SecureDataBlob]. Includes [AutoCloseable] to securely erase
 *  data when no longer needed.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class SecuredByteArray(val around: ByteArray) : AutoCloseable {
	companion object {
		private val saCleaner = Cleaner.create()
	}

	private val cleanOp = saCleaner.register(this) { Arrays.fill(around, 0) }
	override fun close() {
		cleanOp.clean()
	}
}