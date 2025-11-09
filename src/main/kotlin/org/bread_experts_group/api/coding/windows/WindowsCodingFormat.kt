package org.bread_experts_group.api.coding.windows

import org.bread_experts_group.api.coding.CodingFormat
import org.bread_experts_group.api.coding.CodingFormatDescriptors
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.CPINFOEXW
import org.bread_experts_group.ffi.windows.CPINFOEXW_CodePageName
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeGetCPInfoExW
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsCodingFormat(val pageNr: UInt) : CodingFormat() {
	override val systemName: String
	override val descriptor: CodingFormatDescriptors = when (pageNr) {
		437u -> CodingFormatDescriptors.CODE_PAGE_437
		65001u -> CodingFormatDescriptors.UTF_8
		else -> CodingFormatDescriptors.OTHER
	}

	init {
		Arena.ofConfined().use {
			val cpInfoEx = it.allocate(CPINFOEXW)
			val status = nativeGetCPInfoExW!!.invokeExact(
				capturedStateSegment,
				pageNr.toInt(),
				0,
				cpInfoEx
			) as Int
			if (status == 0) decodeLastError()
			systemName = (CPINFOEXW_CodePageName.invokeExact(cpInfoEx, 0L) as MemorySegment)
				.getString(0, Charsets.UTF_16LE)
		}
	}
}