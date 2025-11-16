package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.FeatureImplementation

abstract class SystemFeatureImplementation<I : SystemFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation