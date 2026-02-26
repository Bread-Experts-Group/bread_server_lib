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

	val GET_SIZE = object : FeatureExpression<IODeviceGetSizeFeature> {
		override val name: String = "Get Data Size"
	}

	val SET_SIZE = object : FeatureExpression<IODeviceSetSizeFeature> {
		override val name: String = "Set Data Size"
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

	val GET_DEVICE_GEOMETRY = object : FeatureExpression<IODeviceGetDeviceGeometryFeature> {
		override val name: String = "Get Device Geometry"
	}

	val GET_DEVICE_FIRMWARE_INFO = object : FeatureExpression<IODeviceGetDeviceFirmwareInfoFeature> {
		override val name: String = "Get Device Firmware Information"
	}

	val BYPASS_FS_DRIVER_BOUNDS_CHECKS = object : FeatureExpression<IODeviceBypassFSDriverBoundsChecksFeature> {
		override val name: String = "Bypass Filesystem Driver for Boundary Checking on Device Access"
	}
}