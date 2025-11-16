package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.FeatureExpression
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

	val PARENT = object : FeatureExpression<SystemDeviceParentFeature> {
		override val name: String = "Parent Device"
	}

	val CHILDREN = object : FeatureExpression<SystemDeviceChildrenFeature> {
		override val name: String = "Children Devices"
	}
}