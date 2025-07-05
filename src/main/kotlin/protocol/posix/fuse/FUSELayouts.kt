package org.bread_experts_group.protocol.posix.fuse

import java.lang.foreign.*
import java.lang.invoke.VarHandle

val fuseEntryParamStructure: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_LONG.withName("ino"),
	ValueLayout.JAVA_LONG.withName("generation"),
	MemoryLayout.paddingLayout(144).withName("attr"),
	ValueLayout.JAVA_DOUBLE.withName("attr_timeout"),
	ValueLayout.JAVA_DOUBLE.withName("entry_timeout"),
)
val inoHandle: VarHandle = fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("ino"))
val generationHandle: VarHandle = fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("generation"))
val attrTimeoutHandle: VarHandle = fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("attr_timeout"))
val entrTimeoutHandle: VarHandle = fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("entry_timeout"))

val fuseArgumentsStructure: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("argc"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.ADDRESS.withName("argv"),
	ValueLayout.JAVA_LONG.withName("allocated"),
	MemoryLayout.paddingLayout(4)
)
val argumentsActualStructure: SequenceLayout = MemoryLayout.sequenceLayout(1, AddressLayout.ADDRESS)
val argdHandle: VarHandle = argumentsActualStructure.varHandle(MemoryLayout.PathElement.sequenceElement())
val argcHandle: VarHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("argc"))
val argvHandle: VarHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("argv"))
val allocHandle: VarHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("allocated"))

val fuseCallbacksStructure: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("init"),
	ValueLayout.ADDRESS.withName("destroy"),
	ValueLayout.ADDRESS.withName("lookup"),
	ValueLayout.ADDRESS.withName("mkdir"),
	ValueLayout.ADDRESS.withName("readdir"),
	ValueLayout.ADDRESS.withName("statfs"),
)
val callbacksInitHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("init"))
val callbacksDestroyHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("destroy"))
val callbacksLookupHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("lookup"))
val callbacksMkdirHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("mkdir"))
val callbacksReaddirHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("readdir"))
val callbacksStatfsHandle: VarHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("statfs"))