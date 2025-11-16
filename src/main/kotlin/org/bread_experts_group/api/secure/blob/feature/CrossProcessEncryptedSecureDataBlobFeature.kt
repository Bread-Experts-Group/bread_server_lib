package org.bread_experts_group.api.secure.blob.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.secure.blob.SecureDataBlobFeatures

abstract class CrossProcessEncryptedSecureDataBlobFeature :
	EncryptedSecureDataBlobFeature<CrossProcessEncryptedSecureDataBlobFeature>() {
	override val expresses: FeatureExpression<CrossProcessEncryptedSecureDataBlobFeature> =
		SecureDataBlobFeatures.CROSS_PROCESS_ENCRYPTED
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}