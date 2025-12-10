package org.bread_experts_group.api.system.io

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.feature.*

object IODeviceFeatures {
	val READ = object : FeatureExpression<IODeviceReadFeature> {
		override val name: String = "Read Data"
	}

	val READ_CALLBACK = object : FeatureExpression<IODeviceReadCallbackFeature> {
		override val name: String = "Read Data, callback-oriented"
	}

	val WRITE = object : FeatureExpression<IODeviceWriteFeature> {
		override val name: String = "Write Data"
	}

	val FLUSH = object : FeatureExpression<IODeviceFlushFeature> {
		override val name: String = "Flush Data"
	}

	val SEEK = object : FeatureExpression<IODeviceSeekFeature> {
		override val name: String = "Seek Data"
	}

	val RELEASE = object : FeatureExpression<IODeviceReleaseFeature> {
		override val name: String = "Release Device"
	}

	val REOPEN = object : FeatureExpression<IODeviceReopenFeature> {
		override val name: String = "Reopen Device"
	}

	val DATA_RANGE_LOCK = object : FeatureExpression<IODeviceDataRangeLockFeature> {
		override val name: String = "Device Data Range Lock"
	}
}