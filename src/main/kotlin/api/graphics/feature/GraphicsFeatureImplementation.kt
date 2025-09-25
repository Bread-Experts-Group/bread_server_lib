package org.bread_experts_group.api.graphics.feature

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.FeatureImplementation

abstract class GraphicsFeatureImplementation<I : GraphicsFeatureImplementation<I>> : FeatureImplementation<I>,
	CheckedImplementation