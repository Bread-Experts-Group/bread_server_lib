package org.bread_experts_group.api.system.device.io

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.device.io.feature.IODeviceReadFeature
import org.bread_experts_group.api.system.device.io.feature.IODeviceWriteFeature

object IODeviceFeatures {
	val READ = object : FeatureExpression<IODeviceReadFeature> {
		override val name: String = "Read Data"
	}

	val WRITE = object : FeatureExpression<IODeviceWriteFeature> {
		override val name: String = "Write Data"
	}
}