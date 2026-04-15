package org.bread_experts_group.api.graphics.feature.directwrite.factory

import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.directwrite.fontcollection.DirectWriteFontCollection
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteFontWeight
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.api.graphics.feature.directwrite.textlayout.DirectWriteTextLayout
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directwrite.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class GraphicsWindowDirectWriteFactory(
	handle: MemorySegment
) : IUnknown(
	handle
), GraphicsWindowDirectWriteFactoryData {
	private var getSystemFontCollection: (MemorySegment, Int) -> Int = { fC, cFU ->
		val nativeGetSystemFontCollection: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DFactory::class.java, 0
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PIDWriteFontCollection.withName("fontCollection"),
			BOOL.withName("checkForUpdates")
		)
		getSystemFontCollection = { fC, cFU ->
			nativeGetSystemFontCollection.invokeExact(ptr, fC, cFU) as Int
		}
		nativeGetSystemFontCollection.invokeExact(ptr, fC, cFU) as Int
	}

	fun getSystemFontCollection(checkForUpdates: Boolean): DirectWriteFontCollection {
		tryThrowWin32Error(
			getSystemFontCollection(
				threadLocalPTR,
				if (checkForUpdates) 1 else 0
			)
		)
		return DirectWriteFontCollection(threadLocalPTR.get(`void*`, 0))
	}

	private var createTextFormat: (MemorySegment, MemorySegment, Int, Int, Int, Float, MemorySegment, MemorySegment) -> Int =
		{ fFN, fC, fW, fS, fSt, fSi, lN, tF ->
			val nativeCreateTextFormat: MethodHandle = getLocalVTblAddress(
				GraphicsWindowDirect2DFactory::class.java, 12
			).getDowncall(
				nativeLinker,
				HRESULT,
				`void*`.withName("this"),
				PWCHAR.withName("fontFamilyName"),
				PIDWriteFontCollection.withName("fontCollection"),
				DWRITE_FONT_WEIGHT.withName("fontWeight"),
				DWRITE_FONT_STYLE.withName("fontStyle"),
				DWRITE_FONT_STRETCH.withName("fontStretch"),
				FLOAT.withName("fontSize"),
				PWCHAR.withName("localeName"),
				PIDWriteTextFormat.withName("textFormat")
			)
			createTextFormat = { fFN, fC, fW, fS, fSt, fSi, lN, tF ->
				nativeCreateTextFormat.invokeExact(ptr, fFN, fC, fW, fS, fSt, fSi, lN, tF) as Int
			}
			nativeCreateTextFormat.invokeExact(ptr, fFN, fC, fW, fS, fSt, fSi, lN, tF) as Int
		}

	fun createTextFormat(
		font: String,
		collection: DirectWriteFontCollection?,
		weight: DirectWriteFontWeight,
		style: DWriteFontStyle,
		stretch: DWriteFontStretch,
		size: Float,
		localeName: String?
	): DirectWriteTextFormat {
		tryThrowWin32Error(
			createTextFormat(
				autoArena.allocateFrom(font, winCharsetWide),
				collection?.ptr ?: MemorySegment.NULL,
				weight.weight,
				style.id,
				stretch.id,
				size,
				if (localeName != null) autoArena.allocateFrom(localeName, winCharsetWide)
				else autoArena.allocate(WCHAR, 1),
				threadLocalPTR
			)
		)
		return DirectWriteTextFormat(threadLocalPTR.get(`void*`, 0))
	}

	private var createTextLayout: (MemorySegment, Int, MemorySegment, Float, Float, MemorySegment) -> Int =
		{ s, sL, tF, mW, mH, tL ->
			val nativeCreateTextLayout: MethodHandle = getLocalVTblAddress(
				GraphicsWindowDirect2DFactory::class.java, 15
			).getDowncall(
				nativeLinker,
				HRESULT,
				`void*`.withName("this"),
				PWCHAR.withName("string"),
				UINT32.withName("stringLength"),
				PIDWriteTextFormat.withName("textFormat"),
				FLOAT.withName("maxWidth"),
				FLOAT.withName("maxHeight"),
				PIDWriteTextLayout.withName("textLayout")
			)
			createTextLayout = { s, sL, tF, mW, mH, tL ->
				nativeCreateTextLayout.invokeExact(ptr, s, sL, tF, mW, mH, tL) as Int
			}
			nativeCreateTextLayout.invokeExact(ptr, s, sL, tF, mW, mH, tL) as Int
		}

	fun createTextLayout(
		string: String,
		textFormat: DirectWriteTextFormat,
		maxWidth: Float,
		maxHeight: Float
	): DirectWriteTextLayout {
		tryThrowWin32Error(
			createTextLayout(
				autoArena.allocateFrom(string, winCharsetWide),
				string.length,
				textFormat.ptr,
				maxWidth,
				maxHeight,
				threadLocalPTR
			)
		)
		return DirectWriteTextLayout(threadLocalPTR.get(`void*`, 0))
	}
}