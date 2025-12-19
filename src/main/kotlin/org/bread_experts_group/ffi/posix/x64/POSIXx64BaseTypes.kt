@file:Suppress("DANGEROUS_CHARACTERS", "ObjectPropertyName")

package org.bread_experts_group.ffi.posix.x64

import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.AddressLayout
import java.lang.foreign.ValueLayout

private val mappings = nativeLinker.canonicalLayouts()

val char = mappings["char"]!! as ValueLayout.OfByte
val size_t = mappings["size_t"]!! as ValueLayout.OfLong
val int64_t = mappings["long long"]!! as ValueLayout.OfLong
val ssize_t = size_t
val int = mappings["int"]!! as ValueLayout.OfInt
val short = mappings["short"]!! as ValueLayout.OfShort
val `void*` = mappings["void*"]!! as AddressLayout

val `char*` = `void*` // char