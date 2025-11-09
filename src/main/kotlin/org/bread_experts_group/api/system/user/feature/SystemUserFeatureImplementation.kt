package org.bread_experts_group.api.system.user.feature

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class SystemUserFeatureImplementation<I : SystemUserFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation