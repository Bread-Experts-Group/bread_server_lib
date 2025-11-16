package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.FeatureImplementation

abstract class SystemDeviceFeatureImplementation<I : SystemDeviceFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation