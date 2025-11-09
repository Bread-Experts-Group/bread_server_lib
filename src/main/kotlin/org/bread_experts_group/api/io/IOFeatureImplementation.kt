package org.bread_experts_group.api.io

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class IOFeatureImplementation<I : IOFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation