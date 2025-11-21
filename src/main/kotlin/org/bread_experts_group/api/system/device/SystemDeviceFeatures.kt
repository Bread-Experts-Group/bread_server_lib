package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.*

object SystemDeviceFeatures {
	val SYSTEM_TYPE_GUID = object : FeatureExpression<SystemDeviceSystemTypeGUIDFeature> {
		override val name: String = "Local System Type GUID"
	}

	val SYSTEM_IDENTIFIER = object : FeatureExpression<SystemDeviceSystemIdentityFeature> {
		override val name: String = "Local System Identifier"
	}

	val FRIENDLY_NAME = object : FeatureExpression<SystemDeviceFriendlyNameFeature> {
		override val name: String = "Friendly Name"
	}

	val SERIAL_PORT_NAME = object : FeatureExpression<SystemDeviceSerialPortNameFeature> {
		override val name: String = "Serial Port Name"
	}

	val IO_DEVICE = object : FeatureExpression<SystemDeviceIODeviceFeature> {
		override val name: String = "I/O Device"
	}

	// The following features are primarily specific to "Path Devices,"
	// such as entries on a file system; e.g. "C:\Windows\a.txt"

	val PARENT = object : FeatureExpression<SystemDeviceParentFeature> {
		override val name: String = "Parent Device"
	}

	val CHILDREN = object : FeatureExpression<SystemDeviceChildrenFeature> {
		override val name: String = "Children Devices"
	}

	val APPEND = object : FeatureExpression<SystemDevicePathAppendFeature> {
		override val name: String = "Path Appending"
	}

	val COPY = object : FeatureExpression<SystemDeviceCopyFeature> {
		override val name: String = "Copy Device"
	}

	val DELETE = object : FeatureExpression<SystemDeviceDeleteFeature> {
		override val name: String = "Delete Device"
	}

	val MOVE = object : FeatureExpression<SystemDeviceMoveFeature> {
		override val name: String = "Move Device"
	}

	val REPLACE = object : FeatureExpression<SystemDeviceReplaceFeature> {
		override val name: String = "Replace Device"
	}

	val SOFT_LINK = object : FeatureExpression<SystemDeviceSoftLinkFeature> {
		override val name: String = "Soft-Link To Device"
	}

	val HARD_LINK = object : FeatureExpression<SystemDeviceHardLinkFeature> {
		override val name: String = "Hard-Link To Device"
	}
}