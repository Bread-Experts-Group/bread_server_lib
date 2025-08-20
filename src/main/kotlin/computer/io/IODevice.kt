package org.bread_experts_group.computer.io

import org.bread_experts_group.computer.Computer

interface IODevice {
	fun read(computer: Computer): UByte
	fun write(computer: Computer, d: UByte)
}