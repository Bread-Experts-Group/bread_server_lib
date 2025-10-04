package org.bread_experts_group.api.secure.cryptography.feature

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class CryptographySystemFeatureImplementation<I : CryptographySystemFeatureImplementation<I>> :
	FeatureImplementation<I>, CheckedImplementation