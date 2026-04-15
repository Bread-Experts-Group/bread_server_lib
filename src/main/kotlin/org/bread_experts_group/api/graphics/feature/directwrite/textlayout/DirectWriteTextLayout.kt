package org.bread_experts_group.api.graphics.feature.directwrite.textlayout

import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directwrite.*
import org.bread_experts_group.generic.numeric.geometry.point.Point2
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class DirectWriteTextLayout(
	handle: MemorySegment
) : DirectWriteTextFormat(
	handle
) {
	private var hitTestTextPosition: (Int, Int, MemorySegment, MemorySegment, MemorySegment) -> Int =
		{ tP, iTH, pX, pY, hTM ->
			val nativeHitTestTextPosition: MethodHandle = getLocalVTblAddress(
				DirectWriteTextLayout::class.java, 37
			).getDowncall(
				nativeLinker,
				HRESULT,
				`void*`.withName("this"),
				UINT32.withName("textPosition"),
				BOOL.withName("isTrailingHit"),
				PFLOAT.withName("pointX"),
				PFLOAT.withName("pointY"),
				PDWRITE_HIT_TEST_METRICS.withName("hitTestMetrics")
			)
			hitTestTextPosition = { tP, iTH, pX, pY, hTM ->
				nativeHitTestTextPosition.invokeExact(ptr, tP, iTH, pX, pY, hTM) as Int
			}
			nativeHitTestTextPosition.invokeExact(ptr, tP, iTH, pX, pY, hTM) as Int
		}

	fun hitTestTextPosition(
		textPosition: Int,
		trailingHit: Boolean
	): Pair<Point2<Float>, DirectWriteHitTestMetrics> {
		val htm = autoArena.allocate(DWRITE_HIT_TEST_METRICS)
		tryThrowWin32Error(
			hitTestTextPosition(
				textPosition,
				if (trailingHit) 1 else 0,
				threadLocalDWORD0,
				threadLocalDWORD1,
				htm
			)
		)
		return Point2(
			Float.fromBits(threadLocalDWORD0.get(DWORD, 0)),
			Float.fromBits(threadLocalDWORD1.get(DWORD, 0))
		) to DirectWriteHitTestMetrics(
			DWRITE_HIT_TEST_METRICS_textPosition.get(htm, 0) as Int,
			DWRITE_HIT_TEST_METRICS_length.get(htm, 0) as Int,
			DWRITE_HIT_TEST_METRICS_left.get(htm, 0) as Float,
			DWRITE_HIT_TEST_METRICS_top.get(htm, 0) as Float,
			DWRITE_HIT_TEST_METRICS_width.get(htm, 0) as Float,
			DWRITE_HIT_TEST_METRICS_height.get(htm, 0) as Float,
			DWRITE_HIT_TEST_METRICS_bidiLevel.get(htm, 0) as Int,
			DWRITE_HIT_TEST_METRICS_isText.get(htm, 0) as Int != 0,
			DWRITE_HIT_TEST_METRICS_isTrimmed.get(htm, 0) as Int != 0,
		)
	}
}