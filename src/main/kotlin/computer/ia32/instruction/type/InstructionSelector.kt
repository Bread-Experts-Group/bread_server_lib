package org.bread_experts_group.computer.ia32.instruction.type

import org.bread_experts_group.computer.ia32.IA32Processor

abstract class InstructionSelector(opcode: UInt) : Instruction(opcode, "") {
	protected lateinit var processor: IA32Processor
	fun initialize(processor: IA32Processor) {
		this.processor = processor
	}

	private fun getReg(): UInt {
		val saved = this.processor.ip.rx
		val read = this.processor.decoding.getComponents(this.processor.decoding.readFetch()).second
		this.processor.ip.rx = saved
		return read
	}

	private fun getIns(): Instruction = this.instructions[this.getReg()]
		?: throw ArrayIndexOutOfBoundsException("${this.getReg()} in ${this.instructions} is out of bounds...")

	override fun operands(processor: IA32Processor): String = this.getIns().operands(processor)
	override fun handle(processor: IA32Processor): Unit = this.getIns().handle(processor)

	override val mnemonic: String
		get() {
			val savedIP = this.processor.ip.rx
			val name = this.getIns().mnemonic
			this.processor.ip.rx = savedIP
			return name
		}

	abstract val instructions: Map<UInt, Instruction>
}