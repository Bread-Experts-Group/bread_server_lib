package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactory
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directwrite.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.MappedEnumeration
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class DirectWriteFont(
	ptr: MemorySegment
) : IUnknown(
	ptr
) {
	private var getFontFamily: (MemorySegment) -> Int = {
		val nativeGetFontFamily: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 0
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PIDWriteFontFamily.withName("fontFamily")
		)
		getFontFamily = {
			nativeGetFontFamily.invokeExact(ptr, it) as Int
		}
		nativeGetFontFamily.invokeExact(ptr, it) as Int
	}

	fun getFontFamily(): DirectWriteFontFamily {
		tryThrowWin32Error(getFontFamily(threadLocalPTR))
		return DirectWriteFontFamily(threadLocalPTR.get(`void*`, 0))
	}

	private var getWeightI: () -> Int = {
		val nativeGetWeight: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 1
		).getDowncall(
			nativeLinker,
			DWRITE_FONT_WEIGHT,
			`void*`.withName("this")
		)
		getWeightI = {
			nativeGetWeight.invokeExact(ptr) as Int
		}
		nativeGetWeight.invokeExact(ptr) as Int
	}

	fun getWeight(): MappedEnumeration<Int, DWriteFontWeight> = DWriteFontWeight.entries.id(
		getWeightI()
	)

	private var getStretchI: () -> Int = {
		val nativeGetStretch: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 2
		).getDowncall(
			nativeLinker,
			DWRITE_FONT_STRETCH,
			`void*`.withName("this")
		)
		getStretchI = {
			nativeGetStretch.invokeExact(ptr) as Int
		}
		nativeGetStretch.invokeExact(ptr) as Int
	}

	fun getStretch(): MappedEnumeration<Int, DWriteFontStretch> = DWriteFontStretch.entries.id(
		getStretchI()
	)

	private var getStyleI: () -> Int = {
		val nativeGetStyle: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 3
		).getDowncall(
			nativeLinker,
			DWRITE_FONT_STYLE,
			`void*`.withName("this")
		)
		getStyleI = {
			nativeGetStyle.invokeExact(ptr) as Int
		}
		nativeGetStyle.invokeExact(ptr) as Int
	}

	fun getStyle(): MappedEnumeration<Int, DWriteFontStyle> = DWriteFontStyle.entries.id(
		getStyleI()
	)

	private var getFaceNames: (MemorySegment) -> Int = {
		val nativeGetFaceNames: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 5
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PIDWriteLocalizedStrings.withName("names")
		)
		getFaceNames = {
			nativeGetFaceNames.invokeExact(ptr, it) as Int
		}
		nativeGetFaceNames.invokeExact(ptr, it) as Int
	}

	fun getFaceNames(): DirectWriteLocalizedStrings {
		tryThrowWin32Error(
			getFaceNames(threadLocalPTR)
		)
		return DirectWriteLocalizedStrings(threadLocalPTR.get(`void*`, 0))
	}

	private var getInformationalStrings: (Int, MemorySegment, MemorySegment) -> Int = { iSI, iS, e ->
		val nativeGetInformationalStrings: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 6
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			DWRITE_INFORMATIONAL_STRING_ID.withName("informationalStringID"),
			PIDWriteLocalizedStrings.withName("informationalStrings"),
			PBOOL.withName("exists")
		)
		getInformationalStrings = { iSI, iS, e ->
			nativeGetInformationalStrings.invokeExact(ptr, iSI, iS, e) as Int
		}
		nativeGetInformationalStrings.invokeExact(ptr, iSI, iS, e) as Int
	}

	fun getInformationalStrings(
		informationalStringID: DWriteInformationalStringID
	): DirectWriteLocalizedStrings? {
		tryThrowWin32Error(
			getInformationalStrings(
				informationalStringID.id,
				threadLocalPTR,
				threadLocalDWORD0
			)
		)
		return if (threadLocalDWORD0.get(DWORD, 0) != 0)
			DirectWriteLocalizedStrings(threadLocalPTR.get(`void*`, 0))
		else null
	}

	private var getMetrics: (MemorySegment) -> Unit = { fM ->
		val nativeGetMetrics: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 8
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PDWRITE_FONT_METRICS.withName("fontMetrics")
		)
		getMetrics = { fM ->
			nativeGetMetrics.invokeExact(ptr, fM)
		}
		nativeGetMetrics.invokeExact(ptr, fM)
	}

	fun getMetrics(): DirectWriteMetrics {
		val metrics = autoArena.allocate(DWRITE_FONT_METRICS)
		getMetrics(metrics)
		return DirectWriteMetrics(
			(DWRITE_FONT_METRICS_designUnitsPerEm.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_ascent.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_descent.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_lineGap.get(metrics, 0) as Int).toShort(),
			(DWRITE_FONT_METRICS_capHeight.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_xHeight.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_underlinePosition.get(metrics, 0) as Int).toShort(),
			(DWRITE_FONT_METRICS_underlineThickness.get(metrics, 0) as Int).toUShort(),
			(DWRITE_FONT_METRICS_strikethroughPosition.get(metrics, 0) as Int).toShort(),
			(DWRITE_FONT_METRICS_strikethroughThickness.get(metrics, 0) as Int).toUShort()
		)
	}

	private var createFontFace: (MemorySegment) -> Int = { fF ->
		val nativeCreateFontFace: MethodHandle = getLocalVTblAddress(
			DirectWriteFont::class.java, 10
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PIDWriteFontFace.withName("fontFace")
		)
		createFontFace = { fF ->
			nativeCreateFontFace.invokeExact(ptr, fF) as Int
		}
		nativeCreateFontFace.invokeExact(ptr, fF) as Int
	}

	fun createFontFace(): DirectWriteFontFace {
		tryThrowWin32Error(
			createFontFace(
				threadLocalPTR
			)
		)
		return DirectWriteFontFace(threadLocalPTR.get(`void*`, 0))
	}

	fun createTextFormat(
		factory: GraphicsWindowDirectWriteFactory,
		size: Float
	): DirectWriteTextFormat {
		val (locale, familyName) = this.getInformationalStrings(
			DWriteInformationalStringID.DWRITE_INFORMATIONAL_STRING_WIN32_FAMILY_NAMES
		)!!.first()
		return factory.createTextFormat(
			familyName,
			getFontFamily().getFontCollection(),
			getWeight().enum!!,
			getStyle().enum!!,
			getStretch().enum!!,
			size,
			locale
		)
	}
}