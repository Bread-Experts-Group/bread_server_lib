package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.FeatureProvisioner
import org.bread_experts_group.api.graphics.feature.GraphicsFeatureImplementation

object GraphicsProvider : FeatureProvisioner<GraphicsFeatureImplementation<*>>(
	GraphicsFeatureImplementation::class.java
)