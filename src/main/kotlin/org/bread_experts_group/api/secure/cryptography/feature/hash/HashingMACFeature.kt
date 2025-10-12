package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class HashingMACFeature : CryptographySystemFeatureImplementation<HashingMACFeature>(), HashingCommon,
	MACCommon