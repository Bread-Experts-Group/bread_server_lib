package org.bread_experts_group.computer.io

interface IODevice {
	fun read(): UByte
	fun write(d: UByte)
}