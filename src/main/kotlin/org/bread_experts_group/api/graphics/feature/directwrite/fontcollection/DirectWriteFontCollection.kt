package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directwrite.PIDWriteFontFamily
import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class DirectWriteFontCollection(
	handle: MemorySegment
) : IUnknown(
	handle
), Iterable<DirectWriteFontFamily> {
	private var findFamilyName: (MemorySegment, MemorySegment, MemorySegment) -> Int = { fN, i, e ->
		val nativeFindFamilyName: MethodHandle = getLocalVTblAddress(
			DirectWriteFontCollection::class.java, 2
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PWCHAR.withName("familyName"),
			PUINT32.withName("index"),
			PBOOL.withName("exists")
		)
		findFamilyName = { fN, i, e ->
			nativeFindFamilyName.invokeExact(ptr, fN, i, e) as Int
		}
		nativeFindFamilyName.invokeExact(ptr, fN, i, e) as Int
	}

	fun findFamilyName(familyName: String): Int? {
		tryThrowWin32Error(
			findFamilyName(
				autoArena.allocateFrom(familyName, winCharsetWide),
				threadLocalDWORD0,
				threadLocalDWORD1
			)
		)
		return if (threadLocalDWORD1.get(DWORD, 0) != 0) threadLocalDWORD0.get(DWORD, 0)
		else null
	}

	private var getFamilyName: (Int, MemorySegment) -> Int = { i, fF ->
		val nativeGetFamilyName: MethodHandle = getLocalVTblAddress(
			DirectWriteFontCollection::class.java, 1
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			UINT32.withName("index"),
			PIDWriteFontFamily.withName("fontFamily")
		)
		getFamilyName = { i, fF ->
			nativeGetFamilyName.invokeExact(ptr, i, fF) as Int
		}
		nativeGetFamilyName.invokeExact(ptr, i, fF) as Int
	}

	operator fun get(index: Int): DirectWriteFontFamily {
		tryThrowWin32Error(
			getFamilyName(
				index,
				threadLocalPTR
			)
		)
		return DirectWriteFontFamily(threadLocalPTR.get(PIDWriteFontFamily, 0))
	}

	override fun iterator(): Iterator<DirectWriteFontFamily> {
		TODO("Not yet implemented")
	}
}