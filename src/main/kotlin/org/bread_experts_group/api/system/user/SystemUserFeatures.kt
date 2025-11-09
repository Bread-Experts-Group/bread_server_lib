package org.bread_experts_group.api.system.user

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.user.feature.SystemUserGetLogonTimeFeature
import org.bread_experts_group.api.system.user.feature.SystemUserGetNameFeature

object SystemUserFeatures {
	val NAME_GET = object : FeatureExpression<SystemUserGetNameFeature> {
		override val name: String = "User Name Retrieval"
	}

	val LOGON_TIME_GET = object : FeatureExpression<SystemUserGetLogonTimeFeature> {
		override val name: String = "Log-on Time Retrieval"
	}
}