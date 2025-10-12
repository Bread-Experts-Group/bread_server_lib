package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.Mappable

enum class PEMachineTypes(
	override val id: UShort,
	override val tag: String
) : Mappable<PEMachineTypes, UShort> {
	IMAGE_FILE_MACHINE_UNKNOWN(0x0000u, "Any machine type"),
	IMAGE_FILE_MACHINE_ALPHA(0x0184u, "Alpha (32-bits)"),
	IMAGE_FILE_MACHINE_ALPHA64(0x0284u, "Alpha (64-bits)"),
	IMAGE_FILE_MACHINE_AM33(0x01D3u, "Matsushita AM33 (32-bits)"),
	IMAGE_FILE_MACHINE_AMD64(0x8664u, "x86 (64-bits)"),
	IMAGE_FILE_MACHINE_ARM(0x01C0u, "ARM little-endian (32-bits)"),
	IMAGE_FILE_MACHINE_ARM64(0xAA64u, "ARM little-endian (64-bits)"),
	IMAGE_FILE_MACHINE_ARM64EC(0xA641u, "ARM / x86 common ABI (64-bits)"),
	IMAGE_FILE_MACHINE_ARM64X(0xA64Eu, "ARM / x86 common ABI (64-bits) w/ ARM (64-bits)"),
	IMAGE_FILE_MACHINE_ARMNT(0x01C4u, "ARM Thumb-2 little-endian (32-bits)"),
	IMAGE_FILE_MACHINE_AXP64(0x0284u, "Alpha (64-bits)"),
	IMAGE_FILE_MACHINE_EBC(0x0EBCu, "UEFI Bytecode (64-bits)"),
	IMAGE_FILE_MACHINE_I386(0x014Cu, "x86 (32-bits)"),
	IMAGE_FILE_MACHINE_IA64(0x0200u, "Intel Itanium (64-bits)"),
	IMAGE_FILE_MACHINE_LOONGARCH32(0x6232u, "LoongArch (32-bits)"),
	IMAGE_FILE_MACHINE_LOONGARCH64(0x6264u, "LoongArch (64-bits)"),
	IMAGE_FILE_MACHINE_M32R(0x9041u, "Mitsubishi M32R little-endian (32-bits)"),
	IMAGE_FILE_MACHINE_R3000BE(0x0160u, "MIPS I big-endian (32-bits)"),
	IMAGE_FILE_MACHINE_R3000(0x0162u, "MIPS I little-endian (32-bits)"),
	IMAGE_FILE_MACHINE_R4000(0x0162u, "MIPS III little-endian (64-bits)"),
	IMAGE_FILE_MACHINE_R10000(0x0168u, "MIPS IV little-endian (64-bits)"),
	IMAGE_FILE_MACHINE_WCEMIPSV2(0x0169u, "MIPS little-endian (32-bits) w/ WCE v2"),
	IMAGE_FILE_MACHINE_MIPS16(0x0266u, "MIPS (16-bits)"),
	IMAGE_FILE_MACHINE_MIPSFPU(0x0366u, "MIPS (32-bits) w/ FPU"),
	IMAGE_FILE_MACHINE_MIPSFPU16(0x0466u, "MIPS (16-bits) w/ FPU"),
	IMAGE_FILE_MACHINE_POWERPC(0x01F0u, "PowerPC little-endian (32-bits)"),
	IMAGE_FILE_MACHINE_POWERPCFP(0x1F1u, "PowerPC little-endian (32-bits) w/ FPU"),
	IMAGE_FILE_MACHINE_RISCV32(0x5032u, "RISC-V (32-bits)"),
	IMAGE_FILE_MACHINE_RISCV64(0x5064u, "RISC-V (64-bits)"),
	IMAGE_FILE_MACHINE_RISCV128(0x5128u, "RISC-V (128-bits)"),
	IMAGE_FILE_MACHINE_SH3(0x01A2u, "Hitachi SH3 (32-bits)"),
	IMAGE_FILE_MACHINE_SH3DSP(0x01A3u, "Hitachi SH3 (32-bits) w/ DSP"),
	IMAGE_FILE_MACHINE_SH4(0x01A6u, "Hitachi SH4 (32-bits)"),
	IMAGE_FILE_MACHINE_SH5(0x01A8u, "Hitachi SH5 (64-bits)"),
	IMAGE_FILE_MACHINE_THUMB(0x01C2u, "ARM Thumb little-endian (16-bits)")
}