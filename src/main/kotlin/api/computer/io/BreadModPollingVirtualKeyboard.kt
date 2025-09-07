package org.bread_experts_group.api.computer.io

import org.bread_experts_group.api.computer.Computer
import java.awt.event.KeyEvent
import java.util.concurrent.LinkedBlockingQueue

class BreadModPollingVirtualKeyboard : IODevice {
	var modifiers: Int = 0
	var insert: Boolean = false
	val scancodes: LinkedBlockingQueue<UByte> = LinkedBlockingQueue()
	fun submitKeycode(keycode: Int, modifiers: Int) {
		this.modifiers = modifiers
		val code: UByte? = when (keycode) {
			KeyEvent.VK_INSERT -> {
				this.insert = !this.insert
				if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0xA2u
				else if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) 0x92u
				else 0x52u
			}

			KeyEvent.VK_ENTER -> if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0xA6u else 0x1Cu
			KeyEvent.VK_BACK_SPACE -> 0x0Eu
			KeyEvent.VK_SPACE -> 0x39u
			KeyEvent.VK_MINUS ->
				if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x82u
				else 0x0Cu

			KeyEvent.VK_COMMA ->
				if (
					(modifiers and
							(KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)) > 0
				) null
				else 0x33u

			KeyEvent.VK_SEMICOLON ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else 0x27u

			KeyEvent.VK_A -> 0x1Eu
			KeyEvent.VK_B -> 0x30u
			KeyEvent.VK_C -> 0x2Eu
			KeyEvent.VK_D -> 0x20u
			KeyEvent.VK_E -> 0x12u
			KeyEvent.VK_F -> 0x21u
			KeyEvent.VK_G -> 0x22u
			KeyEvent.VK_H -> 0x23u
			KeyEvent.VK_I -> 0x17u
			KeyEvent.VK_J -> 0x24u
			KeyEvent.VK_K -> 0x25u
			KeyEvent.VK_L -> 0x26u
			KeyEvent.VK_M -> 0x32u
			KeyEvent.VK_N -> 0x31u
			KeyEvent.VK_O -> 0x18u
			KeyEvent.VK_P -> 0x19u
			KeyEvent.VK_Q -> 0x10u
			KeyEvent.VK_R -> 0x13u
			KeyEvent.VK_S -> 0x1Fu
			KeyEvent.VK_T -> 0x14u
			KeyEvent.VK_U -> 0x16u
			KeyEvent.VK_V -> 0x2Fu
			KeyEvent.VK_W -> 0x11u
			KeyEvent.VK_X -> 0x2Du
			KeyEvent.VK_Y -> 0x15u
			KeyEvent.VK_Z -> 0x2Cu
			KeyEvent.VK_1 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x78u
				else 0x02u

			KeyEvent.VK_2 ->
				if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x79u
				else 0x03u

			KeyEvent.VK_3 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Au
				else 0x04u

			KeyEvent.VK_4 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Bu
				else 0x05u

			KeyEvent.VK_5 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Cu
				else 0x06u

			KeyEvent.VK_6 ->
				if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Du
				else 0x07u

			KeyEvent.VK_7 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Eu
				else 0x08u

			KeyEvent.VK_8 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Fu
				else 0x09u

			KeyEvent.VK_9 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x80u
				else 0x0Au

			KeyEvent.VK_0 ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x81u
				else 0x0Bu

			KeyEvent.VK_EQUALS ->
				if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) null
				else if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) 0x83u
				else 0x0Du

			KeyEvent.VK_PERIOD ->
				if (
					(modifiers and
							(KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)) > 0
				) null
				else 0x34u

			KeyEvent.VK_SLASH ->
				if (
					(modifiers and
							(KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)) > 0
				) null
				else 0x35u

			else -> throw UnsupportedOperationException("Key $keycode ?")
		}
		if (code != null) this.scancodes.add(code)
	}

	fun scancodeToChar(scancode: UByte): UByte {
		if ((modifiers and KeyEvent.CTRL_DOWN_MASK) > 0) return when (scancode.toUInt()) {
			0x0Cu -> 0x1Fu
			0x0Eu -> 0x7Fu
			0x1Cu -> 0x0Au
			0x20u -> 0x04u
			0x2Eu -> 0x03u
			0x39u -> 0x20u
			else -> throw UnsupportedOperationException("Code $scancode (ctrl)")
		}
		if ((modifiers and KeyEvent.ALT_DOWN_MASK) > 0) return when (scancode.toUInt()) {
			0x0Eu -> 0x00u
			0x20u -> 0x00u
			0x39u -> 0x20u
			0xA6u -> 0x00u
			else -> throw UnsupportedOperationException("Code $scancode (alt)")
		}
		if ((modifiers and KeyEvent.SHIFT_DOWN_MASK) > 0) return when (scancode.toUInt()) {
			0x02u -> 0x21u
			0x03u -> 0x40u
			0x04u -> 0x23u
			0x05u -> 0x24u
			0x06u -> 0x25u
			0x07u -> 0x5Eu
			0x08u -> 0x26u
			0x09u -> 0x2Au
			0x0Au -> 0x28u
			0x0Bu -> 0x29u
			0x0Cu -> 0x5Fu
			0x0Eu -> 0x08u
			0x10u -> 0x51u
			0x11u -> 0x57u
			0x12u -> 0x45u
			0x13u -> 0x52u
			0x14u -> 0x54u
			0x15u -> 0x59u
			0x16u -> 0x55u
			0x17u -> 0x49u
			0x18u -> 0x4Fu
			0x19u -> 0x50u
			0x1Cu -> 0x0Du
			0x1Eu -> 0x41u
			0x1Fu -> 0x53u
			0x20u -> 0x44u
			0x21u -> 0x46u
			0x22u -> 0x47u
			0x23u -> 0x48u
			0x24u -> 0x4Au
			0x25u -> 0x4Bu
			0x26u -> 0x4Cu
			0x27u -> 0x3Au
			0x2Cu -> 0x5Au
			0x2Du -> 0x58u
			0x2Eu -> 0x43u
			0x2Fu -> 0x56u
			0x30u -> 0x42u
			0x31u -> 0x4Eu
			0x32u -> 0x4Du
			0x39u -> 0x20u
			else -> throw UnsupportedOperationException("Code $scancode (shift)")
		}
		return when (scancode.toUInt()) {
			0x02u -> 0x31u
			0x03u -> 0x32u
			0x04u -> 0x33u
			0x05u -> 0x34u
			0x06u -> 0x35u
			0x07u -> 0x36u
			0x08u -> 0x37u
			0x09u -> 0x38u
			0x0Au -> 0x39u
			0x0Bu -> 0x30u
			0x0Cu -> 0x2Du
			0x0Du -> 0x3Du
			0x0Eu -> 0x08u
			0x10u -> 0x71u
			0x11u -> 0x77u
			0x12u -> 0x65u
			0x13u -> 0x72u
			0x14u -> 0x74u
			0x15u -> 0x79u
			0x16u -> 0x75u
			0x17u -> 0x69u
			0x18u -> 0x6Fu
			0x19u -> 0x70u
			0x1Cu -> 0x0Du
			0x1Eu -> 0x61u
			0x1Fu -> 0x73u
			0x20u -> 0x64u
			0x21u -> 0x66u
			0x22u -> 0x67u
			0x23u -> 0x68u
			0x24u -> 0x6Au
			0x25u -> 0x6Bu
			0x26u -> 0x6Cu
			0x27u -> 0x3Bu
			0x2Cu -> 0x7Au
			0x2Du -> 0x78u
			0x2Eu -> 0x63u
			0x2Fu -> 0x76u
			0x30u -> 0x62u
			0x31u -> 0x6Eu
			0x32u -> 0x6Du
			0x33u -> 0x2Cu
			0x34u -> 0x2Eu
			0x35u -> 0x2Fu
			0x39u -> 0x20u
			else -> throw UnsupportedOperationException("Code $scancode (normal)")
		}
	}

	override fun read(computer: Computer): UByte = throw UnsupportedOperationException()
	override fun write(computer: Computer, d: UByte) = throw UnsupportedOperationException()
}