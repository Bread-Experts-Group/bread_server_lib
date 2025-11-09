package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class SystemFeatureImplementation<I : SystemFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation