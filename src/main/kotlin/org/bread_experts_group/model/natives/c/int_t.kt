package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
abstract class int_t : Number(), Comparable<int_t>, Datatype