package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.user.SystemUser

abstract class SystemThreadLocalUserFeature : SystemFeatureImplementation<SystemThreadLocalUserFeature>() {
	override val expresses: FeatureExpression<SystemThreadLocalUserFeature> = SystemFeatures.THREAD_LOCAL_USER
	abstract val user: SystemUser
}