package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class SystemDeviceFeatureImplementation<I : SystemDeviceFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation