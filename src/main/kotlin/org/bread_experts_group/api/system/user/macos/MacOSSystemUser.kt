package org.bread_experts_group.api.system.user.macos

import org.bread_experts_group.api.system.user.SystemUser
import org.bread_experts_group.api.system.user.feature.SystemUserFeatureImplementation

class MacOSSystemUser : SystemUser() {
	override val features: MutableList<SystemUserFeatureImplementation<*>> = mutableListOf(
		MacOSSystemUserGetNameFeature(this)
	)
	/*
	init {
		// read utmpx entries
		// save name and logon time
	}
     */
}
