package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_MAP_GPA_RANGE_FLAGS : Flaggable {
	WHvMapGpaRangeFlagRead,
	WHvMapGpaRangeFlagWrite,
	WHvMapGpaRangeFlagExecute,
	WHvMapGpaRangeFlagTrackDirtyPages;

	override val position: Long = 1L shl ordinal
}