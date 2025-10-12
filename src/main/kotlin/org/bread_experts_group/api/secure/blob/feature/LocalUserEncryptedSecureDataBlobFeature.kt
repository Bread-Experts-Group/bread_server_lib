package org.bread_experts_group.api.secure.blob.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.secure.blob.SecureDataBlobFeatures

abstract class LocalUserEncryptedSecureDataBlobFeature :
	EncryptedSecureDataBlobFeature<LocalUserEncryptedSecureDataBlobFeature>() {
	override val expresses: FeatureExpression<LocalUserEncryptedSecureDataBlobFeature> =
		SecureDataBlobFeatures.LOCAL_USER_ENCRYPTED
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}