package org.bread_experts_group.project_incubator

import org.bread_experts_group.model.natives.*
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.c.int_t
import org.bread_experts_group.model.natives.c.long_t
import org.bread_experts_group.model.natives.nt.datatype.*
import org.bread_experts_group.model.natives.nt.datatype.hresult.HRESULT
import org.bread_experts_group.model.natives.nt.library.*
import org.bread_experts_group.project_incubator.sim3a.IOMapped
import org.bread_experts_group.project_incubator.sim3a.Intel82441FXPMC
import org.bread_experts_group.project_incubator.sim3a.PCIFunction
import java.awt.EventQueue
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import javax.swing.JFrame
import kotlin.reflect.typeOf

fun main() {
	val linker = Linker.nativeLinker()
	val layouts = linker.canonicalLayouts()

	val k32 = Library.getLibrary(linker, Arena.global(), Kernel32::class)
	val u32 = Library.getLibrary(linker, Arena.global(), User32::class)
	val g32 = Library.getLibrary(linker, Arena.global(), Gdi32::class)
	val ro = Library.getLibrary(linker, Arena.global(), RuntimeObject::class)
	val winHv = Library.getLibrary(linker, Arena.global(), WinHvPlatform::class)
	val winHvE = Library.getLibrary(linker, Arena.global(), WinHvEmulation::class)

	val int = Datatype.getDatatype(layouts, int_t::class)
	val long = Datatype.getDatatype(layouts, long_t::class)

	fun handleHRESULT(result: org.bread_experts_group.model.natives.nt.datatype.HRESULT) {
		val result = HRESULT.of(result.toInt(), linker, Arena.global())
		if (result !is SystemStatus.OK) throw result as SystemStatus
	}

	System.setOut(PrintStream(FileOutputStream(FileDescriptor.out), true, "UTF-8"))
	System.setErr(PrintStream(FileOutputStream(FileDescriptor.err), true, "UTF-8"))

	val partition = Pointer.of<WHV_PARTITION_HANDLE>(linker)
	handleHRESULT(winHv.WHvCreatePartition(partition))
	val processorCount = Pointer.of<UINT32>(linker)
	processorCount.getSegment().set(ValueLayout.JAVA_INT, 0, 1)
	handleHRESULT(
		winHv.WHvSetPartitionProperty(
			partition.deref(),
			WHV_PARTITION_PROPERTY_CODE.WHvPartitionPropertyCodeProcessorCount,
			processorCount.getSegment(),
			int(processorCount.getSegment().byteSize().toInt())
		)
	)
	handleHRESULT(winHv.WHvSetupPartition(partition.deref()))

	val pmc = Intel82441FXPMC(
		mapOf(
			0 to mapOf(
				1 to mapOf(
					0 to PCIFunction()
				)
			)
		),
		Arena.global().allocate(4 * 1024 * 1024, 4096)
	)

	Thread.ofPlatform().start {
		val frame = JFrame("VGA")

		val panel = VgaTextPanel(pmc.memory)

		frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
		frame.contentPane.add(panel)
		frame.pack()
		frame.setLocationRelativeTo(null)
		frame.isVisible = true

		while (frame.isDisplayable) {
			EventQueue.invokeLater {
				panel.repaint()
			}

			Thread.sleep(16)
		}
	}

	handleHRESULT(winHv.WHvCreateVirtualProcessor(partition.deref(), int(0), int(0)))
	val exitContext = Structure.getStructure<WHV_RUN_VP_EXIT_CONTEXT>(linker)()

	pmc.memory.asByteBuffer().put(pmc.bios.asByteBuffer())

	handleHRESULT(winHv.WHvMapGpaRange(partition.deref(), pmc.memory, 0, pmc.memory.byteSize(), int(0x7)))
	handleHRESULT(
		winHv.WHvMapGpaRange(
			partition.deref(), pmc.bios, 0x100000000 - pmc.bios.byteSize(), pmc.bios.byteSize(), int(0x5)
		)
	)
	handleHRESULT(
		winHv.WHvMapGpaRange(
			partition.deref(), Arena.global().allocate(4096, 4096), 0xFEE00000, 4096, int(0x7)
		)
	)

	val io = mutableMapOf<Int, IOMapped>()

	val callbacks = Structure.getStructure<WHV_EMULATOR_CALLBACKS>(linker)()
	callbacks.Size = int(Structure.layoutParameters(layouts, typeOf<WHV_EMULATOR_CALLBACKS>()).first.toInt())
	callbacks.WHvEmulatorIoPortCallback = ioPortCallback@{ _, access ->
		if (access.Direction.toInt() == 1) {
			val value = when (access.AccessSize.toInt()) {
				1 -> access.Data.getSegment().get(ValueLayout.JAVA_BYTE, 0).toInt() and 0xFF
				2 -> access.Data.getSegment().get(ValueLayout.JAVA_SHORT, 0).toInt() and 0xFFFF
				4 -> access.Data.getSegment().get(ValueLayout.JAVA_INT, 0)
				else -> throw IllegalStateException()
			}
			val device = io[access.Port.toInt()] as? IOMapped.Write<*>
			if (device != null) when (device) {
				is IOMapped.Write.`8` -> device.write(value.toByte())
				is IOMapped.Write.`16` -> device.write(value.toShort())
				is IOMapped.Write.`32` -> device.write(value)
			} else {
				println("BSL: unknown write (${value.toHexString()}) to I/O port ${access.Port.toInt().toHexString()}")
			}
		} else {
			val device = io[access.Port.toInt()] as? IOMapped.Read<*>
			val value = if (device != null) when (device) {
				is IOMapped.Read.`8` -> device.read()
				is IOMapped.Read.`16` -> device.read()
				is IOMapped.Read.`32` -> device.read()
			} else {
				println("BSL: unknown read from I/O port ${access.Port.toInt().toHexString()}")
				-1
			}
			when (access.AccessSize.toInt()) {
				1 -> access.Data.getSegment().set(ValueLayout.JAVA_BYTE, 0, value.toByte())
				2 -> access.Data.getSegment().set(ValueLayout.JAVA_SHORT, 0, value.toShort())
				4 -> access.Data.getSegment().set(ValueLayout.JAVA_INT, 0, value.toInt())
				else -> throw IllegalStateException()
			}
		}
		long(0)
	}
	callbacks.WHvEmulatorMemoryCallback = { _, access ->
		if (access.Direction.toInt() == 1) TODO("W")
//		when (access.GpaAddress) {
//			in 0x0F0000..0x0FFFFF -> MemorySegment.copy(
//				pmc.bios, access.GpaAddress,
//				access.Data.getSegment(), 0,
//				access.AccessSize.toInt().toLong()
//			)
//
//			else -> TODO(access.GpaAddress.toHexString())
//		}
		TODO(access.GpaAddress.toHexString())
		long(0)
	}
	callbacks.WHvEmulatorGetVirtualProcessorRegisters = { _, names, count, values ->
		winHv.WHvGetVirtualProcessorRegisters(partition.deref(), int(0), names, count, values)
	}
	callbacks.WHvEmulatorSetVirtualProcessorRegisters = { _, names, count, values ->
		winHv.WHvSetVirtualProcessorRegisters(partition.deref(), int(0), names, count, values)
	}
	callbacks.WHvEmulatorTranslateGvaPage = { _, gva, flags, result, gpa ->
		winHv.WHvTranslateGva(partition.deref(), int(0), gva, flags, result, gpa)
	}
	val emulator = Pointer.of<WHV_EMULATOR_HANDLE>(linker)
	handleHRESULT(winHvE.WHvEmulatorCreateEmulator(callbacks, emulator))

	while (true) {
		handleHRESULT(
			winHv.WHvRunVirtualProcessor(
				partition.deref(), int(0),
				exitContext.getSegment(), int(exitContext.getSegment().byteSize().toInt())
			)
		)
		when (exitContext.ExitReason) {
			WHV_RUN_VP_EXIT_REASON.WHvRunVpExitReasonX64IoPortAccess -> {
				val ptr = Pointer.of<WHV_EMULATOR_STATUS>(linker)
				handleHRESULT(
					winHvE.WHvEmulatorTryIoEmulation(
						emulator.deref(),
						MemorySegment.NULL,
						exitContext.VpContext,
						exitContext.Union.IoPortAccess,
						ptr
					)
				)
				if (ptr.getSegment().get(ValueLayout.JAVA_INT, 0) != 1) println(
					"! ${
						ptr.getSegment().get(ValueLayout.JAVA_INT, 0).toString(2)
					}"
				)
			}

			WHV_RUN_VP_EXIT_REASON.WHvRunVpExitReasonMemoryAccess -> {
				val ptr = Pointer.of<WHV_EMULATOR_STATUS>(linker)
				handleHRESULT(
					winHvE.WHvEmulatorTryMmioEmulation(
						emulator.deref(),
						MemorySegment.NULL,
						exitContext.VpContext,
						exitContext.Union.MemoryAccess,
						ptr
					)
				)
				if (ptr.getSegment().get(ValueLayout.JAVA_INT, 0) != 1) println(
					"! ${
						ptr.getSegment().get(ValueLayout.JAVA_INT, 0).toString(2)
					}"
				)
			}

			WHV_RUN_VP_EXIT_REASON.WHvRunVpExitReasonX64Halt -> {
				println("Halted")
				break
			}

			WHV_RUN_VP_EXIT_REASON.WHvRunVpExitReasonUnrecoverableException -> {
				println(exitContext.Union.VpException)
				break
			}

			else -> TODO("${exitContext.ExitReason}")
		}
	}
//	ro.RoInitialize(RO_INIT_TYPE.RO_INIT_MULTITHREADED)
//	val h = Pointer.of<HSTRING>(linker)
//	ro.WindowsCreateString(
//		WCHAR.nullTerminated(layouts, RuntimeClass_Windows_Graphics_Capture_GraphicsCaptureItem),
//		uint(RuntimeClass_Windows_Graphics_Capture_GraphicsCaptureItem.length),
//		h
//	)
//	val h2 = Pointer.of<MemorySegment>(linker)
//	ro.RoGetActivationFactory(h.deref(), GUID.fromUuid(IID_IGraphicsCaptureItemInterop, linker), h2)
//	val h3 = Structure.getStructure<IGraphicsCaptureItemInterop>(linker)(h2.deref())
//	val h4 = Pointer.of<MemorySegment>(linker)
//	println(h4.deref())
//	println(u32.FindWindowW(WCHAR.nullTerminated(layouts, "CabinetWClass"), null))
//	println(
//		h3.lpVtbl.deref()
//			.CreateForWindow(
//				h3,
//				u32.FindWindowW(WCHAR.nullTerminated(layouts, "CabinetWClass"), null),
//				GUID.fromUuid(IID_IGraphicsCaptureItem, linker),
//				h4
//			)
//	)
//	println(h4.deref())

//	val reference = Structure.getStructure<WNDCLASSEXW>(linker)()
//	reference.cbSize = uint(reference.getSegment().byteSize().toInt())
//	reference.lpfnWndProc = { hwnd, uMsg, wParam, lParam ->
//		when (uMsg.toInt()) {
//			WM_DESTROY -> {
//				u32.PostQuitMessage(int_t(0))
//				0
//			}
//
//			else -> u32.DefWindowProcW(hwnd, uMsg, wParam, lParam)
//		}
//	}
//	reference.hInstance = k32.GetModuleHandleW(null)
//	reference.lpszClassName = WCHAR.nullTerminated(layouts, "Sample Window Class")
//	u32.RegisterClassExW(reference)
//	val hwnd = u32.CreateWindowExW(
//		dword(0),
//		reference.lpszClassName,
//		WCHAR.nullTerminated(layouts, "Learn to Program Windows"),
//		dword(WS_OVERLAPPEDWINDOW),
//
//		int_t(CW_USEDEFAULT), int_t(CW_USEDEFAULT), int_t(CW_USEDEFAULT), int_t(CW_USEDEFAULT),
//
//		null, null, reference.hInstance, null
//	)
//	u32.ShowWindow(hwnd, int_t(1))
//
//	val msg = Structure.getStructure<MSG>(linker)()
//	while (u32.GetMessageW(msg, null, uint(0), uint(0)).toInt() > 0) {
//		u32.TranslateMessage(msg)
//		u32.DispatchMessageW(msg)
//	}
}