package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.Mappable

enum class PEWindowsSubsystem(
	override val id: UShort,
	override val tag: String
) : Mappable<PEWindowsSubsystem, UShort> {
	IMAGE_SUBSYSTEM_UNKNOWN(0u, "Unknown subsystem"),
	IMAGE_SUBSYSTEM_NATIVE(1u, "Device driver / native Windows process"),
	IMAGE_SUBSYSTEM_WINDOWS_GUI(2u, "Windows GUI subsystem"),
	IMAGE_SUBSYSTEM_WINDOWS_CUI(3u, "Windows Character UI subsystem"),
	IMAGE_SUBSYSTEM_OS2_CUI(5u, "OS/2 Character UI subsystem"),
	IMAGE_SUBSYSTEM_POSIX_CUI(7u, "POSIX Character UI subsystem"),
	IMAGE_SUBSYSTEM_NATIVE_WINDOWS(8u, "Native Win9x driver"),
	IMAGE_SUBSYSTEM_WINDOWS_CE_GUI(9u, "Windows CE GUI subsystem"),
	IMAGE_SUBSYSTEM_EFI_APPLICATION(10u, "EFI application"),
	IMAGE_SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER(11u, "EFI boot services driver"),
	IMAGE_SUBSYSTEM_EFI_RUNTIME_DRIVER(12u, "EFI runtime services driver"),
	IMAGE_SUBSYSTEM_EFI_ROM(13u, "EFI ROM image"),
	IMAGE_SUBSYSTEM_XBOX(14u, "XBOX application"),
	IMAGE_SUBSYSTEM_WINDOWS_BOOT_APPLICATION(16u, "Windows boot application")
}