package org.bread_experts_group.api.io.feature.device

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class IODeviceFeatureImplementation<I : IODeviceFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation