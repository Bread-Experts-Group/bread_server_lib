package org.bread_experts_group.api.graphics.feature.directwrite.factory

import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteFontWeight
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteNonCollectionTextFormatName
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormatDescriptor
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directwrite.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class GraphicsWindowDirectWriteFactory(
	handle: MemorySegment
) : IUnknown(
	handle
), GraphicsWindowDirectWriteFactoryData {
	var createTextFormat: (MemorySegment, MemorySegment, Int, Int, Int, Float, MemorySegment, MemorySegment) -> Int =
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
		font: DirectWriteTextFormatDescriptor,
		weight: DirectWriteFontWeight,
		style: DWriteFontStyle,
		stretch: DWriteFontStretch,
		size: Float
	): DirectWriteTextFormat = Arena.ofConfined().use { tA ->
		val fontFamilyName: MemorySegment
		val fontCollection: MemorySegment
		when (font) {
			is DirectWriteNonCollectionTextFormatName -> {
				fontFamilyName = tA.allocateFrom(font.name, Charsets.UTF_16LE)
				fontCollection = MemorySegment.NULL
			}

			else -> throw IllegalArgumentException()
		}
		tryThrowWin32Error(
			createTextFormat(
				fontFamilyName,
				fontCollection,
				weight.weight,
				style.id,
				stretch.id,
				size,
				tA.allocate(WCHAR, 1),
				threadLocalPTR
			)
		)
		DirectWriteTextFormat(threadLocalPTR.get(`void*`, 0))
	}
}