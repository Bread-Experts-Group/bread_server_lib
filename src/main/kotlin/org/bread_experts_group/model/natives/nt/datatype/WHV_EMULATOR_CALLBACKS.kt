package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_EMULATOR_CALLBACKS : Structure<WHV_EMULATOR_CALLBACKS> {
	@Order(0)
	abstract var Size: UINT32

	@Order(1)
	abstract var Reserved: UINT32

	@Order(2)
	abstract var WHvEmulatorIoPortCallback: WHV_EMULATOR_IO_PORT_CALLBACK

	@Order(3)
	abstract var WHvEmulatorMemoryCallback: WHV_EMULATOR_MEMORY_CALLBACK

	@Order(4)
	abstract var WHvEmulatorGetVirtualProcessorRegisters: WHV_EMULATOR_GET_VIRTUAL_PROCESSOR_REGISTERS_CALLBACK

	@Order(5)
	abstract var WHvEmulatorSetVirtualProcessorRegisters: WHV_EMULATOR_SET_VIRTUAL_PROCESSOR_REGISTERS_CALLBACK

	@Order(6)
	abstract var WHvEmulatorTranslateGvaPage: WHV_EMULATOR_TRANSLATE_GVA_PAGE_CALLBACK
}