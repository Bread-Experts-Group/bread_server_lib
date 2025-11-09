package org.bread_experts_group.api.io

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.io.feature.serial.IOSerialFeatureImplementation

object IOFeatures {
	val SERIAL = object : FeatureExpression<IOSerialFeatureImplementation> {
		override val name: String = "Serial I/O"
	}
}