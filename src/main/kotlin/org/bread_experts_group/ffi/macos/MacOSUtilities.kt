package org.bread_experts_group.ffi.macos

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

val MACOS_PTR: AddressLayout = ValueLayout.ADDRESS
val MACOS_NULLPTR: MemorySegment = MemorySegment.NULL
val MACOS_INT: ValueLayout.OfInt = ValueLayout.JAVA_INT

val MACOS_STDIN_FILENO: Int = 0
val MACOS_STDOUT_FILENO: Int = 1
val MACOS_STDERR_FILENO: Int = 2