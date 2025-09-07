package org.bread_experts_group.api.computer.io

import org.bread_experts_group.api.computer.Computer

interface IODevice {
	fun read(computer: Computer): UByte
	fun write(computer: Computer, d: UByte)
}