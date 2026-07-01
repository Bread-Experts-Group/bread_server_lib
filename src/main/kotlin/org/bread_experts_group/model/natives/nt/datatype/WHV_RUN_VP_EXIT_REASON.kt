package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_RUN_VP_EXIT_REASON(override val id: Int) : Mappable<WHV_RUN_VP_EXIT_REASON, Int> {
	WHvRunVpExitReasonNone(0x00000000),

	// Standard exits caused by operations of the virtual processor
	WHvRunVpExitReasonMemoryAccess(0x00000001),
	WHvRunVpExitReasonX64IoPortAccess(0x00000002),
	WHvRunVpExitReasonUnrecoverableException(0x00000004),
	WHvRunVpExitReasonInvalidVpRegisterValue(0x00000005),
	WHvRunVpExitReasonUnsupportedFeature(0x00000006),
	WHvRunVpExitReasonX64InterruptWindow(0x00000007),
	WHvRunVpExitReasonX64Halt(0x00000008),
	WHvRunVpExitReasonX64ApicEoi(0x00000009),

	// Additional exits that can be configured through partition properties
	WHvRunVpExitReasonX64MsrAccess(0x00001000),
	WHvRunVpExitReasonX64Cpuid(0x00001001),
	WHvRunVpExitReasonException(0x00001002),
	WHvRunVpExitReasonX64Rdtsc(0x00001003),

	// Exits caused by the host
	WHvRunVpExitReasonCanceled(0x00002001);

	override val tag: String = name
}