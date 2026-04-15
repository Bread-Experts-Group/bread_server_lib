package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import org.bread_experts_group.generic.Mappable.Companion.id
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class DirectWriteLocalizedStrings(
	ptr: MemorySegment
) : IUnknown(
	ptr
), Collection<Pair<String, String>> {
	private var getCount: () -> Int = {
		val nativeGetCount: MethodHandle = getLocalVTblAddress(
			DirectWriteLocalizedStrings::class.java, 0
		).getDowncall(
			nativeLinker,
			UINT32,
			`void*`.withName("this")
		)
		getCount = {
			nativeGetCount.invokeExact(ptr) as Int
		}
		nativeGetCount.invokeExact(ptr) as Int
	}

	override val size: Int
		get() = getCount()

	private var getLocaleNameLength: (Int, MemorySegment) -> Int = { i, l ->
		val nativeGetLocaleNameLength: MethodHandle = getLocalVTblAddress(
			DirectWriteLocalizedStrings::class.java, 2
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			UINT32.withName("index"),
			PUINT32.withName("length")
		)
		getLocaleNameLength = { i, l ->
			nativeGetLocaleNameLength.invokeExact(ptr, i, l) as Int
		}
		nativeGetLocaleNameLength.invokeExact(ptr, i, l) as Int
	}

	private var getLocaleName: (Int, MemorySegment, Int) -> Int = { i, lN, s ->
		val nativeGetLocaleName: MethodHandle = getLocalVTblAddress(
			DirectWriteLocalizedStrings::class.java, 3
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			UINT32.withName("index"),
			PWCHAR.withName("localeName"),
			UINT32.withName("size")
		)
		getLocaleName = { i, lN, s ->
			nativeGetLocaleName.invokeExact(ptr, i, lN, s) as Int
		}
		nativeGetLocaleName.invokeExact(ptr, i, lN, s) as Int
	}

	private var getStringLength: (Int, MemorySegment) -> Int = { i, l ->
		val nativeGetStringLength: MethodHandle = getLocalVTblAddress(
			DirectWriteLocalizedStrings::class.java, 4
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			UINT32.withName("index"),
			PUINT32.withName("length")
		)
		getLocaleNameLength = { i, l ->
			nativeGetStringLength.invokeExact(ptr, i, l) as Int
		}
		nativeGetStringLength.invokeExact(ptr, i, l) as Int
	}

	private var getString: (Int, MemorySegment, Int) -> Int = { i, sB, s ->
		val nativeGetString: MethodHandle = getLocalVTblAddress(
			DirectWriteLocalizedStrings::class.java, 5
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			UINT32.withName("index"),
			PWCHAR.withName("stringBuffer"),
			UINT32.withName("size")
		)
		getString = { i, sB, s ->
			nativeGetString.invokeExact(ptr, i, sB, s) as Int
		}
		nativeGetString.invokeExact(ptr, i, sB, s) as Int
	}

	override fun containsAll(elements: Collection<Pair<String, String>>): Boolean {
		TODO("Not yet implemented")
	}

	override fun contains(element: Pair<String, String>): Boolean {
		TODO("Not yet implemented")
	}

	override fun isEmpty(): Boolean = size == 0
	override fun iterator(): Iterator<Pair<String, String>> = object : Iterator<Pair<String, String>> {
		var index = 0
		override fun hasNext(): Boolean = index < size
		override fun next(): Pair<String, String> {
			tryThrowWin32Error(getLocaleNameLength(index, threadLocalDWORD0))
			var localeNameLength = threadLocalDWORD0.get(DWORD, 0) + 1
			var localeNameSegment = autoArena.allocate(WCHAR, localeNameLength.toLong())
			while (true) {
				val status = getLocaleName(index, localeNameSegment, localeNameLength)
				if (
					WindowsLastError.entries.id(
						(status and 0xFFFF).toUInt()
					).enum == WindowsLastError.ERROR_INSUFFICIENT_BUFFER
				) {
					localeNameLength++
					localeNameSegment = autoArena.allocate(WCHAR, localeNameLength.toLong())
				} else {
					tryThrowWin32Error(status)
					break
				}
			}
			val localeName = localeNameSegment.getString(0, winCharsetWide)
			tryThrowWin32Error(getStringLength(index, threadLocalDWORD0))
			var stringLength = threadLocalDWORD0.get(DWORD, 0) + 1
			var stringSegment = autoArena.allocate(WCHAR, stringLength.toLong())
			while (true) {
				val status = getString(index, stringSegment, stringLength)
				if (
					WindowsLastError.entries.id(
						(status and 0xFFFF).toUInt()
					).enum == WindowsLastError.ERROR_INSUFFICIENT_BUFFER
				) {
					stringLength++
					stringSegment = autoArena.allocate(WCHAR, stringLength.toLong())
				} else {
					tryThrowWin32Error(status)
					break
				}
			}
			val string = stringSegment.getString(0, winCharsetWide)
			index++
			return localeName to string
		}
	}
}