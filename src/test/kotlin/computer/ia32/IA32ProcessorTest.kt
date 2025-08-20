package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import org.bread_experts_group.computer.ia32.register.Register
import org.bread_experts_group.getResource
import org.bread_experts_group.hex
import org.junit.jupiter.api.Test
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.PrintStream
import java.util.concurrent.CountDownLatch
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord

class IA32ProcessorTest {
	val processor: IA32Processor = IA32Processor()
	val ivt: MemoryModule = MemoryModule(0x400u)
	val bios: MemoryModule = MemoryModule(257u, 0x400u)
	val lowMemoryFree: MemoryModule = MemoryModule(0x7FB00u, 0x500u)
	val videoMemory: MemoryModule = MemoryModule(0xC0000u, 0x000A0000u)
	val computer: Computer = Computer(
		listOf(ivt, bios, lowMemoryFree, videoMemory),
		processor,
		StandardBIOS()
	)

	@Test
	fun helloWorld() {
//		computer.insertDisc(getResource("/computer/ia32/disc/Core-current.iso").toURL())
		computer.floppyURLs[0] = getResource("/computer/ia32/floppy/CPM86_10.img").toURL()
		computer.reset()
		val closeWait = CountDownLatch(1)
		val monitor = ProcessorVisualMonitor(computer)
		monitor.addKeyListener(object : KeyListener {
			override fun keyTyped(e: KeyEvent) {}
			override fun keyPressed(e: KeyEvent) {
				computer.keyboard.modifiers = e.modifiersEx
				if (e.keyChar == KeyEvent.CHAR_UNDEFINED) return
				val code: UByte? = when (e.keyCode) {
					KeyEvent.VK_ENTER -> if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0xA6u else 0x1Cu
					KeyEvent.VK_BACK_SPACE -> 0x0Eu
					KeyEvent.VK_SPACE -> 0x39u
					KeyEvent.VK_MINUS ->
						if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x82u
						else 0x0Cu

					KeyEvent.VK_COMMA ->
						if (
							(e.modifiersEx and
									(KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)) > 0
						) null
						else 0x33u

					KeyEvent.VK_SEMICOLON ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
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
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x78u
						else 0x02u

					KeyEvent.VK_2 ->
						if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x79u
						else 0x03u

					KeyEvent.VK_3 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Au
						else 0x04u

					KeyEvent.VK_4 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Bu
						else 0x05u

					KeyEvent.VK_5 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Cu
						else 0x06u

					KeyEvent.VK_6 ->
						if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Du
						else 0x07u

					KeyEvent.VK_7 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Eu
						else 0x08u

					KeyEvent.VK_8 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x7Fu
						else 0x09u

					KeyEvent.VK_9 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x80u
						else 0x0Au

					KeyEvent.VK_0 ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x81u
						else 0x0Bu

					KeyEvent.VK_EQUALS ->
						if ((e.modifiersEx and KeyEvent.CTRL_DOWN_MASK) > 0) null
						else if ((e.modifiersEx and KeyEvent.ALT_DOWN_MASK) > 0) 0x83u
						else 0x0Du

					KeyEvent.VK_PERIOD ->
						if (
							(e.modifiersEx and
									(KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)) > 0
						) null
						else 0x34u

					else -> throw UnsupportedOperationException("Key ${e.keyCode} '${e.keyChar}' ?")
				}
				if (code != null) computer.keyboard.scancodes.add(code)
			}

			override fun keyReleased(e: KeyEvent) {
				computer.keyboard.modifiers = e.modifiersEx
			}
		})
		monitor.addWindowListener(object : WindowAdapter() {
			override fun windowClosing(e: WindowEvent) {
				monitor.dispose()
			}

			override fun windowClosed(e: WindowEvent) {
				closeWait.countDown()
			}
		})
		monitor.isVisible = true
		monitor.requestFocus()
		val lrg = PrintStream((File("./cpu.log")))
		val registersDelta = mutableMapOf<String, ULong>()
		fun logRegister(r: Register) {
			if (registersDelta[r.name] != r.rx) registersDelta[r.name] = r.rx
			else return
			lrg.println("${r.name} Δ: ${hex(r.rx)}")
		}
		processor.logger.addHandler(
			object : Handler() {
				val written = mutableSetOf<Long>()
				override fun publish(record: LogRecord) {
					if (!written.add(record.sequenceNumber)) return
					lrg.println(record.message)
					logRegister(processor.a)
					logRegister(processor.b)
					logRegister(processor.c)
					logRegister(processor.d)
					logRegister(processor.sp)
					logRegister(processor.bp)
					logRegister(processor.di)
					logRegister(processor.si)
					logRegister(processor.cs)
					logRegister(processor.ds)
					logRegister(processor.es)
					logRegister(processor.fs)
					logRegister(processor.ss)
					logRegister(processor.gs)
					logRegister(processor.gdtrLimit)
					logRegister(processor.gdtrBase)
					logRegister(processor.idtrLimit)
					logRegister(processor.idtrBase)
					logRegister(processor.cr0)
					logRegister(processor.cr2)
					logRegister(processor.cr3)
					logRegister(processor.cr4)
					logRegister(processor.flags)
					record.thrown?.let { lrg.println(it.stackTraceToString()) }
				}

				override fun flush() {}
				override fun close() {}
			}
		)
		while (true) {
			try {
				processor.step()
			} catch (e: Exception) {
				processor.logger.log(Level.SEVERE, e) { "Error during step" }
				println(e.stackTraceToString())
				break
			}
			monitor.repaint()
		}
		closeWait.await()
	}
}