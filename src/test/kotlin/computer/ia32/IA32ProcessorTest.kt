package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.bread_experts_group.computer.ia32.bios.Read.FloppyGeometry.Companion.floppy5_14_160K
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
import java.nio.file.Files
import java.nio.file.StandardOpenOption
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
		val temp = getResource("/computer/ia32/floppy/CPM86_10.img")
		computer.floppies[0] = Files.newByteChannel(
			temp,
			StandardOpenOption.WRITE, StandardOpenOption.READ
		) to floppy5_14_160K
		computer.reset()
		val closeWait = CountDownLatch(1)
		val monitor = ProcessorVisualMonitor(computer)
		monitor.addKeyListener(object : KeyListener {
			override fun keyTyped(e: KeyEvent) {}
			override fun keyPressed(e: KeyEvent) {
				if (e.keyChar == KeyEvent.CHAR_UNDEFINED) return
				computer.keyboard.submitKeycode(e.keyCode, e.modifiersEx)
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