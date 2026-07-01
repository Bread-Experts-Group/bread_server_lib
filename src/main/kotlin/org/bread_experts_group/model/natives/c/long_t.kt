package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("long")
abstract class long_t : Number(), Comparable<long_t>, Datatype