package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_PARTITION_PROPERTY_CODE(override val id: Int) : Mappable<WHV_PARTITION_PROPERTY_CODE, Int> {
	WHvPartitionPropertyCodeExtendedVmExits(0x00000001),

	WHvPartitionPropertyCodeExceptionExitBitmap(0x00000002), // AMD 64

	WHvPartitionPropertyCodeSeparateSecurityDomain(0x00000003),
	WHvPartitionPropertyCodeNestedVirtualization(0x00000004),

	WHvPartitionPropertyCodeX64MsrExitBitmap(0x00000005), // AMD 64

	WHvPartitionPropertyCodePrimaryNumaNode(0x00000006),
	WHvPartitionPropertyCodeCpuReserve(0x00000007),
	WHvPartitionPropertyCodeCpuCap(0x00000008),
	WHvPartitionPropertyCodeCpuWeight(0x00000009),
	WHvPartitionPropertyCodeCpuGroupId(0x0000000A),
	WHvPartitionPropertyCodeProcessorFrequencyCap(0x0000000B),
	WHvPartitionPropertyCodeAllowDeviceAssignment(0x0000000C),
	WHvPartitionPropertyCodeDisableSmt(0x0000000D),
	WHvPartitionPropertyCodeProcessorFeatures(0x00001001),
	WHvPartitionPropertyCodeProcessorClFlushSize(0x00001002),

	WHvPartitionPropertyCodeCpuidExitList(0x00001003), // AMD 64
	WHvPartitionPropertyCodeCpuidResultList(0x00001004), // AMD 64
	WHvPartitionPropertyCodeLocalApicEmulationMode(0x00001005), // AMD 64
	WHvPartitionPropertyCodeProcessorXsaveFeatures(0x00001006), // AMD 64

	WHvPartitionPropertyCodeProcessorClockFrequency(0x00001007),

	WHvPartitionPropertyCodeInterruptClockFrequency(0x00001008), // AMD 64
	WHvPartitionPropertyCodeApicRemoteReadSupport(0x00001009), // AMD 64

	WHvPartitionPropertyCodeProcessorFeaturesBanks(0x0000100A),
	WHvPartitionPropertyCodeReferenceTime(0x0000100B),
	WHvPartitionPropertyCodeSyntheticProcessorFeaturesBanks(0x0000100C),

	WHvPartitionPropertyCodeCpuidResultList2(0x0000100D), // AMD 64
	WHvPartitionPropertyCodeProcessorPerfmonFeatures(0x0000100E), // AMD 64
	WHvPartitionPropertyCodeMsrActionList(0x0000100F), // AMD 64
	WHvPartitionPropertyCodeUnimplementedMsrAction(0x00001010), // AMD 64

	WHvPartitionPropertyCodePhysicalAddressWidth(0x00001011),

	WHvPartitionPropertyCodeArm64IcParameters(0x00001012), // ARM64

	WHvPartitionPropertyCodeProcessorCount(0x00001fff);

	override val tag: String = name
}