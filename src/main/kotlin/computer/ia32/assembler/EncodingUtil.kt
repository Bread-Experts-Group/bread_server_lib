package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.computer.BinaryUtil.shl

fun modRmByte(memRM: AssemblerMemRM, reg: AssemblerRegister): Int {
	if (memRM.address != null) {
		println("BETA")
		return 0
	}
	var base: UByte = 0b11000000u
	base = base or (reg.regBits() shl 3)
	base = base or memRM.register!!.regBits()
	return base.toInt()
}