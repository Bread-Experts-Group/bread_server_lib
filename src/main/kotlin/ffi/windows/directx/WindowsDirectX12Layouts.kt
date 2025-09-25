package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.windows.BOOL
import org.bread_experts_group.ffi.windows.UINT
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle
import java.util.*

val D3D12_COMMAND_QUEUE_DESC: StructLayout = MemoryLayout.structLayout(
	D3D12_COMMAND_LIST_TYPE.withName("Type"),
	D3D12_COMMAND_QUEUE_PRIORITY.withName("Priority"),
	D3D12_COMMAND_QUEUE_FLAGS.withName("Flags"),
	UINT.withName("NodeMask")
)

val D3D12_FEATURE_DATA_D3D12_OPTIONS: StructLayout = MemoryLayout.structLayout(
	BOOL.withName("DoublePrecisionFloatShaderOps"),
	BOOL.withName("OutputMergerLogicOp"),
	D3D12_SHADER_MIN_PRECISION_SUPPORT.withName("MinPrecisionSupport"),
	D3D12_TILED_RESOURCES_TIER.withName("TiledResourcesTier"),
	D3D12_RESOURCE_BINDING_TIER.withName("ResourceBindingTier"),
	BOOL.withName("PSSpecifiedStencilRefSupported"),
	BOOL.withName("TypedUAVLoadAdditionalFormats"),
	BOOL.withName("ROVsSupported"),
	D3D12_CONSERVATIVE_RASTERIZATION_TIER.withName("ConservativeRasterizationTier"),
	UINT.withName("MaxGPUVirtualAddressBitsPerResource"), // Intentionally not supported for this struct
	BOOL.withName("StandardSwizzle64KBSupported"),
	D3D12_CROSS_NODE_SHARING_TIER.withName("CrossNodeSharingTier"),
	BOOL.withName("CrossAdapterRowMajorTextureSupported"),
	BOOL.withName("VPAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation"),
	D3D12_RESOURCE_HEAP_TIER.withName("ResourceHeapTier")
)
val D3D12_FEATURE_DATA_D3D12_OPTIONS_DoublePrecisionFloatShaderOps: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("DoublePrecisionFloatShaderOps"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_OutputMergerLogicOp: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("OutputMergerLogicOp"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_MinPrecisionSupport: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("MinPrecisionSupport"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_TiledResourcesTier: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("TiledResourcesTier"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_ResourceBindingTier: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("ResourceBindingTier"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_PSSpecifiedStencilRefSupported: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("PSSpecifiedStencilRefSupported"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_TypedUAVLoadAdditionalFormats: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("TypedUAVLoadAdditionalFormats"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_ROVsSupported: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("ROVsSupported"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_ConservativeRasterizationTier: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("ConservativeRasterizationTier"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_StandardSwizzle64KBSupported: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("StandardSwizzle64KBSupported"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_CrossNodeSharingTier: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("CrossNodeSharingTier"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_CrossAdapterRowMajorTextureSupported: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("CrossAdapterRowMajorTextureSupported"))

@Suppress("LongLine")
val D3D12_FEATURE_DATA_D3D12_OPTIONS_VPAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("VPAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation"))
val D3D12_FEATURE_DATA_D3D12_OPTIONS_ResourceHeapTier: VarHandle =
	D3D12_FEATURE_DATA_D3D12_OPTIONS.varHandle(groupElement("ResourceHeapTier"))

class D3D12FeatureDataD3D12Options(
	val ptr: MemorySegment
) : D3D12FeatureData(D3D12Feature.D3D12_FEATURE_D3D12_OPTIONS) {
	val doublePrecisionFloatShaderOps: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_DoublePrecisionFloatShaderOps.get(ptr) as Int != 0
	val outputMergerLogicOp: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_OutputMergerLogicOp.get(ptr) as Int != 0
	val minPrecisionSupport: EnumSet<D3D12ShaderMinPrecisionSupport>
		get() = D3D12ShaderMinPrecisionSupport.entries.from(
			D3D12_FEATURE_DATA_D3D12_OPTIONS_MinPrecisionSupport.get(ptr) as Int
		)
	val tiledResourcesTier: MappedEnumeration<UInt, D3D12TiledResourcesTier>
		get() = D3D12TiledResourcesTier.entries.id(
			(D3D12_FEATURE_DATA_D3D12_OPTIONS_TiledResourcesTier.get(ptr) as Int).toUInt()
		)
	val resourceBindingTier: MappedEnumeration<UInt, D3D12ResourceBindingTier>
		get() = D3D12ResourceBindingTier.entries.id(
			(D3D12_FEATURE_DATA_D3D12_OPTIONS_ResourceBindingTier.get(ptr) as Int).toUInt()
		)
	val psSpecifiedStencilRefSupported: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_PSSpecifiedStencilRefSupported.get(ptr) as Int != 0
	val typedUAVLoadAdditionalFormats: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_TypedUAVLoadAdditionalFormats.get(ptr) as Int != 0
	val rovsSupported: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_ROVsSupported.get(ptr) as Int != 0
	val conservativeRasterizationTier: MappedEnumeration<UInt, D3D12ConservativeRasterizationTier>
		get() = D3D12ConservativeRasterizationTier.entries.id(
			(D3D12_FEATURE_DATA_D3D12_OPTIONS_ConservativeRasterizationTier.get(ptr) as Int).toUInt()
		)
	val standardSwizzle64KBSupported: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_StandardSwizzle64KBSupported.get(ptr) as Int != 0
	val crossNodeSharingTier: MappedEnumeration<UInt, D3D12CrossNodeSharingTier>
		get() = D3D12CrossNodeSharingTier.entries.id(
			(D3D12_FEATURE_DATA_D3D12_OPTIONS_CrossNodeSharingTier.get(ptr) as Int).toUInt()
		)
	val crossAdapterRowMajorTextureSupported: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_CrossAdapterRowMajorTextureSupported.get(ptr) as Int != 0

	@Suppress("LongLine")
	val vpAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation: Boolean
		get() = D3D12_FEATURE_DATA_D3D12_OPTIONS_VPAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation.get(
			ptr
		) as Int != 0
	val resourceHeapTier: MappedEnumeration<UInt, D3D12ResourceHeapTier>
		get() = D3D12ResourceHeapTier.entries.id(
			(D3D12_FEATURE_DATA_D3D12_OPTIONS_ResourceHeapTier.get(ptr) as Int).toUInt()
		)

	@Suppress("LongLine")
	override fun expandToString(): String = " DoublePrecisionFloatShaderOps: $doublePrecisionFloatShaderOps\n" +
			" OutputMergerLogicOp: $outputMergerLogicOp\n" +
			" MinPrecisionSupport: $minPrecisionSupport\n" +
			" TiledResourcesTier: $tiledResourcesTier\n" +
			" ResourceBindingTier: $resourceBindingTier\n" +
			" PSSpecifiedStencilRefSupported: $psSpecifiedStencilRefSupported\n" +
			" TypedUAVLoadAdditionalFormats: $typedUAVLoadAdditionalFormats\n" +
			" ROVsSupported: $rovsSupported\n" +
			" ConservativeRasterizationTier: $conservativeRasterizationTier\n" +
			" StandardSwizzle64KBSupported: $standardSwizzle64KBSupported\n" +
			" CrossNodeSharingTier: $crossNodeSharingTier\n" +
			" CrossAdapterRowMajorTextureSupported: $crossAdapterRowMajorTextureSupported\n" +
			" VPAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation: $vpAndRTArrayIndexFromAnyShaderFeedingRasterizerSupportedWithoutGSEmulation\n" +
			" ResourceHeapTier: $resourceHeapTier"
}

val D3D12_FEATURE_DATA_FEATURE_LEVELS: StructLayout = MemoryLayout.structLayout(
	UINT.withName("NumFeatureLevels"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("pFeatureLevelsRequested"),
	D3D_FEATURE_LEVEL.withName("MaxSupportedFeatureLevel"),
	MemoryLayout.paddingLayout(4)
)
val D3D12_FEATURE_DATA_FEATURE_LEVELS_NumFeatureLevels: VarHandle =
	D3D12_FEATURE_DATA_FEATURE_LEVELS.varHandle(groupElement("NumFeatureLevels"))
val D3D12_FEATURE_DATA_FEATURE_LEVELS_pFeatureLevelsRequested: VarHandle =
	D3D12_FEATURE_DATA_FEATURE_LEVELS.varHandle(groupElement("pFeatureLevelsRequested"))
val D3D12_FEATURE_DATA_FEATURE_LEVELS_MaxSupportedFeatureLevel: VarHandle =
	D3D12_FEATURE_DATA_FEATURE_LEVELS.varHandle(groupElement("MaxSupportedFeatureLevel"))

class D3D12FeatureDataFeatureLevels(
	val ptr: MemorySegment
) : D3D12FeatureData(D3D12Feature.D3D12_FEATURE_FEATURE_LEVELS) {
	var numFeatureLevels: UInt
		get() = (D3D12_FEATURE_DATA_FEATURE_LEVELS_NumFeatureLevels.get(ptr) as Int).toUInt()
		set(value) {
			D3D12_FEATURE_DATA_FEATURE_LEVELS_NumFeatureLevels.set(ptr, value.toInt())
		}
	var pFeatureLevelsRequested: MemorySegment
		get() = D3D12_FEATURE_DATA_FEATURE_LEVELS_pFeatureLevelsRequested.get(ptr) as MemorySegment
		set(value) {
			D3D12_FEATURE_DATA_FEATURE_LEVELS_pFeatureLevelsRequested.set(ptr, value)
		}
	val maxSupportedFeatureLevel: MappedEnumeration<UInt, D3DFeatureLevel>
		get() = D3DFeatureLevel.entries.id(
			(D3D12_FEATURE_DATA_FEATURE_LEVELS_MaxSupportedFeatureLevel.get(ptr) as Int).toUInt()
		)

	override fun expandToString(): String = " NumFeatureLevels: $numFeatureLevels\n" +
			" pFeatureLevelsRequested: $pFeatureLevelsRequested\n" +
			" MaxSupportedFeatureLevel: $maxSupportedFeatureLevel"
}

val D3D12_FEATURE_DATA_FORMAT_SUPPORT: StructLayout = MemoryLayout.structLayout(
	DXGI_FORMAT.withName("Format"),
	D3D12_FORMAT_SUPPORT1.withName("Support1"),
	D3D12_FORMAT_SUPPORT2.withName("Support2")
)
val D3D12_FEATURE_DATA_FORMAT_SUPPORT_Format: VarHandle =
	D3D12_FEATURE_DATA_FORMAT_SUPPORT.varHandle(groupElement("Format"))
val D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support1: VarHandle =
	D3D12_FEATURE_DATA_FORMAT_SUPPORT.varHandle(groupElement("Support1"))
val D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support2: VarHandle =
	D3D12_FEATURE_DATA_FORMAT_SUPPORT.varHandle(groupElement("Support2"))

class D3D12FeatureDataFormatSupport(
	val ptr: MemorySegment
) : D3D12FeatureData(D3D12Feature.D3D12_FEATURE_FORMAT_SUPPORT) {
	var format: MappedEnumeration<UInt, DXGIFormat>
		get() = DXGIFormat.entries.id(
			(D3D12_FEATURE_DATA_FORMAT_SUPPORT_Format.get(ptr) as Int).toUInt()
		)
		set(value) {
			D3D12_FEATURE_DATA_FORMAT_SUPPORT_Format.set(ptr, value.raw.toInt())
		}
	var support1: EnumSet<D3D12FormatSupport1>
		get() = D3D12FormatSupport1.entries.from(
			D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support1.get(ptr) as Int
		)
		set(value) {
			D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support1.set(ptr, value.raw().toInt())
		}
	var support2: EnumSet<D3D12FormatSupport2>
		get() = D3D12FormatSupport2.entries.from(
			D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support2.get(ptr) as Int
		)
		set(value) {
			D3D12_FEATURE_DATA_FORMAT_SUPPORT_Support2.set(ptr, value.raw().toInt())
		}

	override fun expandToString(): String = " Format: $format\n" +
			"  Support 1: $support1\n" +
			"  Support 2: $support2"
}

val D3D12_DESCRIPTOR_HEAP_DESC: StructLayout = MemoryLayout.structLayout(
	D3D12_DESCRIPTOR_HEAP_TYPE.withName("Type"),
	UINT.withName("NumDescriptors"),
	D3D12_DESCRIPTOR_HEAP_FLAGS.withName("Flags"),
	UINT.withName("NodeMask")
)
val D3D12_DESCRIPTOR_HEAP_DESC_Type: VarHandle =
	D3D12_DESCRIPTOR_HEAP_DESC.varHandle(groupElement("Type"))
val D3D12_DESCRIPTOR_HEAP_DESC_NumDescriptors: VarHandle =
	D3D12_DESCRIPTOR_HEAP_DESC.varHandle(groupElement("NumDescriptors"))
val D3D12_DESCRIPTOR_HEAP_DESC_Flags: VarHandle =
	D3D12_DESCRIPTOR_HEAP_DESC.varHandle(groupElement("Flags"))
val D3D12_DESCRIPTOR_HEAP_DESC_NodeMask: VarHandle =
	D3D12_DESCRIPTOR_HEAP_DESC.varHandle(groupElement("NodeMask"))

class D3D12DescriptorHeapDesc(
	val ptr: MemorySegment
) {
	var type: MappedEnumeration<UInt, D3D12DescriptorHeapType>
		get() = D3D12DescriptorHeapType.entries.id(
			(D3D12_DESCRIPTOR_HEAP_DESC_Type.get(ptr) as Int).toUInt()
		)
		set(value) {
			D3D12_DESCRIPTOR_HEAP_DESC_Type.set(ptr, 0, value.raw.toInt())
		}
	var numDescriptors: UInt
		get() = (D3D12_DESCRIPTOR_HEAP_DESC_NumDescriptors.get(ptr, 0) as Int).toUInt()
		set(value) {
			D3D12_DESCRIPTOR_HEAP_DESC_NumDescriptors.set(ptr, 0, value.toInt())
		}
	var flags: EnumSet<D3D12DescriptorHeapFlags>
		get() = D3D12DescriptorHeapFlags.entries.from(
			D3D12_DESCRIPTOR_HEAP_DESC_Flags.get(ptr, 0) as Int
		)
		set(value) {
			D3D12_DESCRIPTOR_HEAP_DESC_Flags.set(ptr, 0, value.raw().toInt())
		}
	var nodeMask: UInt
		get() = (D3D12_DESCRIPTOR_HEAP_DESC_NodeMask.get(ptr, 0) as Int).toUInt()
		set(value) {
			D3D12_DESCRIPTOR_HEAP_DESC_NodeMask.set(ptr, 0, value.toInt())
		}
}