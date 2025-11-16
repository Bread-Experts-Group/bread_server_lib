package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.FeatureImplementation

abstract class GraphicsFeatureImplementation<I : GraphicsFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation