package org.bread_experts_group.api.secure.blob

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.secure.blob.feature.CrossProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.feature.LocalProcessEncryptedSecureDataBlobFeature
import org.bread_experts_group.api.secure.blob.feature.LocalUserEncryptedSecureDataBlobFeature

object SecureDataBlobFeatures {
	/**
	 * Data stored in a [SecureDataBlob] will only able to be decrypted by local process.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	val LOCAL_PROCESS_ENCRYPTED = object : FeatureExpression<LocalProcessEncryptedSecureDataBlobFeature> {
		override val name: String = "Local-Process Specific Decryption"
	}

	/**
	 * Data stored in a [SecureDataBlob] will be able to be decrypted by other processes.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	val CROSS_PROCESS_ENCRYPTED = object : FeatureExpression<CrossProcessEncryptedSecureDataBlobFeature> {
		override val name: String = "Cross-Process Specific Decryption"
	}

	/**
	 * Data stored in a [SecureDataBlob] will be able to be decrypted by processes running under the local user.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	val LOCAL_USER_ENCRYPTED = object : FeatureExpression<LocalUserEncryptedSecureDataBlobFeature> {
		override val name: String = "Local-User Specific Decryption"
	}
}