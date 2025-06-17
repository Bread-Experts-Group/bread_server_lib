package org.bread_experts_group.computer.bios

import org.bread_experts_group.computer.Computer

interface BIOSProvider {
	fun initialize(computer: Computer)
}