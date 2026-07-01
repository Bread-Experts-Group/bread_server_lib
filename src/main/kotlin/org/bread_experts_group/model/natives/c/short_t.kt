package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("short")
abstract class short_t : Number(), Comparable<short_t>, Datatype