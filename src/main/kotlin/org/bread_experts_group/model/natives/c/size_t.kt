package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("size_t")
abstract class size_t : Number(), Comparable<size_t>, Datatype