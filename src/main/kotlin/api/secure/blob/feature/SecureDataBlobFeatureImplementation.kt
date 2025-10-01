package org.bread_experts_group.api.secure.blob.feature

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class SecureDataBlobFeatureImplementation<I : SecureDataBlobFeatureImplementation<I>> :
	FeatureImplementation<I>,
	CheckedImplementation