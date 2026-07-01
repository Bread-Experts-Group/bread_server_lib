package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.*
import java.lang.foreign.MemorySegment

@Suppress("FunctionName")
@LookupBacked("WinHvEmulation.dll")
abstract class WinHvEmulation internal constructor() : Library {
	// TODO: Windows date
	abstract fun WHvEmulatorCreateEmulator(
		Callbacks: Pointer<WHV_EMULATOR_CALLBACKS>,
		Emulator: Pointer<WHV_EMULATOR_HANDLE>
	): HRESULT

	abstract fun WHvEmulatorTryIoEmulation(
		Emulator: WHV_EMULATOR_HANDLE,
		Context: MemorySegment,
		VpContext: Pointer<WHV_VP_EXIT_CONTEXT>,
		IoInstructionContext: Pointer<WHV_X64_IO_PORT_ACCESS_CONTEXT>,
		EmulatorReturnStatus: Pointer<WHV_EMULATOR_STATUS>
	): HRESULT

	abstract fun WHvEmulatorTryMmioEmulation(
		Emulator: WHV_EMULATOR_HANDLE,
		Context: MemorySegment,
		VpContext: Pointer<WHV_VP_EXIT_CONTEXT>,
		MmioInstructionContext: Pointer<WHV_MEMORY_ACCESS_CONTEXT>,
		EmulatorReturnStatus: Pointer<WHV_EMULATOR_STATUS>
	): HRESULT
}