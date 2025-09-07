package org.bread_experts_group.api.computer.ia32

import org.bread_experts_group.api.computer.Computer
import org.bread_experts_group.hex
import java.awt.Color
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel

class IA32ProcessorStateMonitor(computer: Computer) : JFrame() {
	fun addRegister(name: String, read: () -> ULong) {
		val a = object : JLabel() {
			override fun getText(): String? = "$name: ${hex(read())}"
		}
		a.foreground = Color.WHITE
		a.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
		add(a)
	}

	init {
		computer.processor as IA32Processor
		title = "Processor State Monitor"
		defaultCloseOperation = EXIT_ON_CLOSE
		isResizable = false
		layout = BoxLayout(this.contentPane, BoxLayout.Y_AXIS)
		addRegister("a", computer.processor.a::rx)
		addRegister("b", computer.processor.b::rx)
		addRegister("c", computer.processor.c::rx)
		addRegister("d", computer.processor.d::rx)
		addRegister("sp", computer.processor.sp::rx)
		addRegister("bp", computer.processor.bp::rx)
		addRegister("di", computer.processor.di::rx)
		addRegister("si", computer.processor.si::rx)
		addRegister("cs", computer.processor.cs::rx)
		addRegister("ds", computer.processor.ds::rx)
		addRegister("ss", computer.processor.ss::rx)
		addRegister("es", computer.processor.es::rx)
		addRegister("fs", computer.processor.fs::rx)
		addRegister("gs", computer.processor.gs::rx)
		addRegister("flags", computer.processor.flags::rx)
		addRegister("ip", computer.processor.ip::rx)
		contentPane.background = Color.BLACK
		pack()
	}
}