package org.bread_experts_group.api.secure.blob.feature.windows

import org.bread_experts_group.api.secure.blob.windows.WindowsSecureDataBlob
import org.bread_experts_group.ffi.windows.*
import kotlin.math.ceil

fun initializeOff(parent: WindowsSecureDataBlob, size: Long, flags: WindowsCryptProtectMemoryFlags) {
	val padded = (ceil(size / CRYPTPROTECTMEMORY_BLOCK_SIZE.toDouble()).toLong() * CRYPTPROTECTMEMORY_BLOCK_SIZE)
		.also {
			if (it > UInt.MAX_VALUE.toLong())
				throw IllegalArgumentException("Encryption block size too large, $it > ${UInt.MAX_VALUE.toLong()}")
		}
	parent.encrypt = {
		val r = nativeCryptProtectMemory!!.invokeExact(
			parent.managedSegment, padded.toInt(),
			flags.id.toInt()
		) as Int
		if (r == 0) decodeLastError(parent.arena)
	}
	parent.decrypt = {
		val r = nativeCryptUnprotectMemory!!.invokeExact(
			parent.managedSegment, padded.toInt(),
			flags.id.toInt()
		) as Int
		if (r == 0) decodeLastError(parent.arena)
	}
	parent.managedSegmentRealSize = padded
	parent.managedSegment = parent.arena.allocate(padded).reinterpret(size)
}