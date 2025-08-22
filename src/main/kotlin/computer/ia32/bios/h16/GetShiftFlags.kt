package org.bread_experts_group.computer.ia32.bios.h16

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import java.awt.Toolkit
import java.awt.event.KeyEvent

object GetShiftFlags : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		var active: UByte = 0u
		Toolkit.getDefaultToolkit().apply {
			if (processor.computer.keyboard.insert) active = active or (1u shl 7).toUByte()
			if (getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) active = active or (1u shl 6).toUByte()
			if (getLockingKeyState(KeyEvent.VK_NUM_LOCK)) active = active or (1u shl 5).toUByte()
			if (getLockingKeyState(KeyEvent.VK_SCROLL_LOCK)) active = active or (1u shl 4).toUByte()
			if ((processor.computer.keyboard.modifiers and KeyEvent.ALT_DOWN_MASK) > 0)
				active = active or (1u shl 3).toUByte()
			if ((processor.computer.keyboard.modifiers and KeyEvent.CTRL_DOWN_MASK) > 0)
				active = active or (1u shl 2).toUByte()
			if ((processor.computer.keyboard.modifiers and KeyEvent.SHIFT_DOWN_MASK) > 0) {
				//  TODO: L/R shift
				active = active or (1u shl 1).toUByte()
				active = active or 1u.toUByte()
			}
		}
		processor.a.tl = active
	}
}