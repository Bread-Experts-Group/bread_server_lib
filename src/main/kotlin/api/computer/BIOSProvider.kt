package org.bread_experts_group.api.computer

interface BIOSProvider {
	fun initialize(computer: Computer)
}