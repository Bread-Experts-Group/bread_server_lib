package org.bread_experts_group.computer.io

class Diagnostics : IODevice {
	val read: MutableList<UByte> = mutableListOf()
	override fun read(): UByte = 0u
	override fun write(d: UByte) {
		this.read.add(d)
	}
}