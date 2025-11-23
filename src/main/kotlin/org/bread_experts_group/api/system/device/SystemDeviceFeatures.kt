package org.bread_experts_group.api.system.device

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.*

object SystemDeviceFeatures {
	val SYSTEM_IDENTIFIER = object : FeatureExpression<SystemDeviceBasicIdentifierFeature> {
		override val name: String = "Local System Identifier"
	}

	val SYSTEM_TYPE_IDENTIFIER = object : FeatureExpression<SystemDeviceBasicIdentifierFeature> {
		override val name: String = "Local System Type Identifier"
	}

	val FRIENDLY_NAME = object : FeatureExpression<SystemDeviceFriendlyNameFeature> {
		override val name: String = "Friendly Name"
	}

	val IO_DEVICE = object : FeatureExpression<SystemDeviceIODeviceFeature> {
		override val name: String = "I/O Device"
	}

	val SERIAL_PORT_NAME = object : FeatureExpression<SystemDeviceSerialPortNameFeature> {
		override val name: String = "Serial Port Name"
	}

	// The following features are primarily specific to "Path Devices,"
	// such as entries on a file system; e.g. "C:\Windows\a.txt"

	val PATH_PARENT = object : FeatureExpression<SystemDeviceParentFeature> {
		override val name: String = "Parent Path Device"
	}

	val PATH_CHILDREN = object : FeatureExpression<SystemDeviceChildrenFeature> {
		override val name: String = "Children Path Devices"
	}

	val PATH_CHILDREN_STREAMS = object : FeatureExpression<SystemDeviceChildrenStreamsFeature> {
		override val name: String = "Children Path Device Stream Devices"
	}

	val PATH_APPEND = object : FeatureExpression<SystemDevicePathAppendFeature> {
		override val name: String = "Path Device Appending"
	}

	val PATH_COPY = object : FeatureExpression<SystemDeviceCopyFeature> {
		override val name: String = "Copy Path Device"
	}

	val PATH_DELETE = object : FeatureExpression<SystemDeviceDeleteFeature> {
		override val name: String = "Delete Path Device"
	}

	val PATH_MOVE = object : FeatureExpression<SystemDeviceMoveFeature> {
		override val name: String = "Move Path Device"
	}

	val PATH_REPLACE = object : FeatureExpression<SystemDeviceReplaceFeature> {
		override val name: String = "Replace Path Device"
	}

	val PATH_SOFT_LINK = object : FeatureExpression<SystemDeviceSoftLinkFeature> {
		override val name: String = "Soft-Link To Path Device"
	}

	val PATH_HARD_LINK = object : FeatureExpression<SystemDeviceHardLinkFeature> {
		override val name: String = "Hard-Link To Path Device"
	}

	val PATH_ENABLE_TRANSPARENT_ENCRYPT = object : FeatureExpression<SystemDeviceTransparentEncryptionEnableFeature> {
		override val name: String = "Path Device Transparent Encryption Enable"
	}

	val PATH_DISABLE_TRANSPARENT_ENCRYPT = object : FeatureExpression<SystemDeviceTransparentEncryptionDisableFeature> {
		override val name: String = "Path Device Transparent Encryption Disable"
	}

	val PATH_QUERY_TRANSPARENT_ENCRYPT = object : FeatureExpression<SystemDeviceQueryTransparentEncryptionFeature> {
		override val name: String = "Query Path Device Transparent Encryption"
	}

	val PATH_TRANSPARENT_ENCRYPT_RAW_IO_DEVICE =
		object : FeatureExpression<SystemDeviceTransparentEncryptionRawIODeviceFeature> {
			override val name: String = "Path Device Raw I/O Device thru Transparent Encryption"
		}

	// Path Device Attributes

	val PATH_GET_CREATION_TIME = object : FeatureExpression<SystemDeviceGetTimeFeature> {
		override val name: String = "Path Device Get Creation Time"
	}

	val PATH_GET_LAST_ACCESS_TIME = object : FeatureExpression<SystemDeviceGetTimeFeature> {
		override val name: String = "Path Device Get Last-Access Time"
	}

	val PATH_GET_LAST_WRITE_TIME = object : FeatureExpression<SystemDeviceGetTimeFeature> {
		override val name: String = "Path Device Get Last-Write Time"
	}

	val PATH_GET_LAST_METADATA_WRITE_TIME = object : FeatureExpression<SystemDeviceGetTimeFeature> {
		override val name: String = "Path Device Get Last-Metadata-Write Time"
	}

//	val PATH_GET_ATTRIBUTES = object : FeatureExpression<SystemDeviceGetTimeFeature> {
//		override val name: String = "Path Device Get Attributes"
//	}
}