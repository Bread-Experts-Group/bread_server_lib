package org.bread_experts_group.computer.ia32.register

import org.bread_experts_group.computer.BinaryUtil.hex
import java.util.logging.Logger

class ControlRegister0(logger: Logger, name: String, vararg flags: FlagType) : Register(
	logger,
	name,
	run {
		var sum: ULong = 0u
		flags.forEach { sum = sum or it.position }
		sum
	}
) {
	enum class FlagType(val position: ULong) {
		PROTECTED_MODE_ENABLE(0x0000_0000_0000_0001u),
		MONITOR_CO_PROCESSOR(0x0000_0000_0000_0002u),
		SOFT_X87_EMULATION(0x0000_0000_0000_0004u),
		TASK_CONTEXT_SAVING(0x0000_0000_0000_0008u),
		FPU_80387_OR_HIGHER(0x0000_0000_0000_0010u),
		INTERNAL_X87_EXCEPTIONS(0x0000_0000_0000_0020u),
		NO_WRITE_TO_READ_ONLY_PAGES(0x0000_0000_0001_0000u),
		ALIGNMENT_CHECK(0x0000_0000_0004_0000u),
		WRITE_THROUGH_CACHE_DISABLED(0x0000_0000_2000_0000u),
		MEMORY_CACHE_DISABLED(0x0000_0000_4000_0000u),
		PAGING_ENABLED(0x0000_0000_8000_0000u),
	}

	override var rx: ULong = super.rx
		set(value) {
			if (field == value) return
			field = value
			var flags = ""
			FlagType.entries.forEach {
				flags += if (this.getFlag(it)) ((if (flags.isNotEmpty()) ", " else "") + it.name) else ""
			}
			this.logger.warning("${this.name} set ${hex(value)} [$flags]")
		}

	fun setFlag(flag: FlagType, state: Boolean) {
		var extracted = this.rx and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.rx = extracted
	}

	fun getFlag(flag: FlagType): Boolean = (this.rx and flag.position) > 0u
}