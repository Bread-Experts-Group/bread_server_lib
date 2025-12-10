package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.feature.FeatureExpression

object GraphicsConsoleIOFeatures {
	val DEVICE_GET = object : FeatureExpression<GraphicsConsoleIODeviceGetFeature> {
		override val name: String = "Console I/O Device Retrieval"
	}

//	val DEVICE_SET = object : FeatureExpression<> {
//		override val name: String = "Console I/O Device Redirection"
//	}

	val MODE_GET = object : FeatureExpression<GraphicsConsoleIOModeGetFeature> {
		override val name: String = "Console I/O Mode Retrieval"
	}

	val MODE_SET = object : FeatureExpression<GraphicsConsoleIOModeSetFeature> {
		override val name: String = "Console I/O Mode Modification"
	}

	val GET_CODE_PAGE = object : FeatureExpression<GraphicsConsoleIOGetCodePageFeature> {
		override val name: String = "Console I/O Code Page Retrieval"
	}

	val SET_CODE_PAGE = object : FeatureExpression<GraphicsConsoleIOSetCodePageFeature> {
		override val name: String = "Console I/O Code Page Modification"
	}

	val EVENT_GET = object : FeatureExpression<GraphicsConsoleIOEventGetFeature> {
		override val name: String = "Console I/O Event Retrieval"
	}
}
