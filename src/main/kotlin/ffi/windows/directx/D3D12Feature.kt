package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.coder.Mappable

enum class D3D12Feature(
	override val id: UInt,
	override val tag: String
) : Mappable<D3D12Feature, UInt> {
	// https://github.com/microsoft/DirectX-Headers/blob/main/include/directx/d3d12.h#L2328
	D3D12_FEATURE_D3D12_OPTIONS(0u, "Feature Level #1"),
	D3D12_FEATURE_ARCHITECTURE(1u, "Architecture #0 [Superseded]"),
	D3D12_FEATURE_FEATURE_LEVELS(2u, "Supported Feature Levels"),
	D3D12_FEATURE_FORMAT_SUPPORT(3u, "Data Format Support"),
	D3D12_FEATURE_MULTISAMPLE_QUALITY_LEVELS(4u, "Multisample Quality Level Support"),
	D3D12_FEATURE_FORMAT_INFO(5u, "DXGI Data Format"),
	D3D12_FEATURE_GPU_VIRTUAL_ADDRESS_SUPPORT(6u, "GPU Virtual Address Space Limits"),
	D3D12_FEATURE_SHADER_MODEL(7u, "Supported Shader Model"),
	D3D12_FEATURE_D3D12_OPTIONS1(8u, "Feature Level #1"),
	D3D12_FEATURE_PROTECTED_RESOURCE_SESSION_SUPPORT(10u, "Protected Resource Session Support"),
	D3D12_FEATURE_ROOT_SIGNATURE(12u, "Root Signature Support"),
	D3D12_FEATURE_ARCHITECTURE1(16u, "Architecture #1"),
	D3D12_FEATURE_D3D12_OPTIONS2(18u, "Features #2"),
	D3D12_FEATURE_SHADER_CACHE(19u, "Shader Cache Support"),
	D3D12_FEATURE_COMMAND_QUEUE_PRIORITY(20u, "Command Queue Type Support"),
	D3D12_FEATURE_D3D12_OPTIONS3(21u, "Features #3"),
	D3D12_FEATURE_EXISTING_HEAPS(22u, "System-backed Heap Creation Support"),
	D3D12_FEATURE_D3D12_OPTIONS4(23u, "Features #4"),
	D3D12_FEATURE_SERIALIZATION(24u, "Heap Serialization Support"),
	D3D12_FEATURE_CROSS_NODE(25u, "Cross-Node Resource Sharing Support"),
	D3D12_FEATURE_D3D12_OPTIONS5(27u, "Features #5"),
	D3D12_FEATURE_DISPLAYABLE(28u, "Displayable"), // ???
	D3D12_FEATURE_D3D12_OPTIONS6(30u, "Features #6"),
	D3D12_FEATURE_QUERY_META_COMMAND(31u, "Meta Command Query Support"),
	D3D12_FEATURE_D3D12_OPTIONS7(32u, "Features #7"),
	D3D12_FEATURE_PROTECTED_RESOURCE_SESSION_TYPE_COUNT(33u, "Protected Resource Session Type Count"),
	D3D12_FEATURE_PROTECTED_RESOURCE_SESSION_TYPES(34u, "Protected Resource Session Types"),
	D3D12_FEATURE_D3D12_OPTIONS8(36u, "Features #8"),
	D3D12_FEATURE_D3D12_OPTIONS9(37u, "Features #9"),
	D3D12_FEATURE_D3D12_OPTIONS10(39u, "Features #10"),
	D3D12_FEATURE_D3D12_OPTIONS11(40u, "Features #11");

	override fun toString(): String = stringForm()
}