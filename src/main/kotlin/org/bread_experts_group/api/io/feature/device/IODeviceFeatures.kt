package org.bread_experts_group.api.io.feature.device

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.io.feature.device.feature.IODeviceReadFeature
import org.bread_experts_group.api.io.feature.device.feature.IODeviceWriteFeature

object IODeviceFeatures {
	val READ = object : FeatureExpression<IODeviceReadFeature> {
		override val name: String = "Read Data"
	}

	val WRITE = object : FeatureExpression<IODeviceWriteFeature> {
		override val name: String = "Write Data"
	}
}