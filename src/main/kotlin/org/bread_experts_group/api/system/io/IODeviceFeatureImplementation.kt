package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.FeatureImplementation

abstract class IODeviceFeatureImplementation<I : IODeviceFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation