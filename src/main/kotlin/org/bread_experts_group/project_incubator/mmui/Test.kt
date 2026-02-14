package org.bread_experts_group.project_incubator.mmui

import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.api.graphics.feature.window.icon.Image2D
import org.bread_experts_group.api.graphics.feature.window.icon.ImagePlaneType
import org.bread_experts_group.api.graphics.feature.window.icon.IntImagePlane
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowIcon
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowName
import org.bread_experts_group.api.graphics.feature.window.open.StandardGraphicsWindowOpenFeatures
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

fun main() {
	val windowing = GraphicsProvider.get(GraphicsFeatures.GUI_WINDOW)
	val img = ImageIO.read(
		File("C:\\Users\\Adenosine3Phosphate\\Downloads\\photo_2025-08-11_17-27-07.jpg")
	)
	val w = img.width
	val h = img.height
	val r = IntArray(((w * h) / 4) + 1)
	val g = IntArray(((w * h) / 4) + 1)
	val b = IntArray(((w * h) / 4) + 1)
	val a = IntArray(((w * h) / 4) + 1)
	var y = h
	var p = 0
	while (--y > 0) {
		var x = 0
		while (x < w) {
			val argb = img.getRGB(x++, y)
			a[p ushr 2] = a[p ushr 2] or (((argb ushr 24) and 0xFF) shl ((p and 0b11) shl 3))
			r[p ushr 2] = r[p ushr 2] or (((argb ushr 16) and 0xFF) shl ((p and 0b11) shl 3))
			g[p ushr 2] = g[p ushr 2] or (((argb ushr 8) and 0xFF) shl ((p and 0b11) shl 3))
			b[p ushr 2] = b[p ushr 2] or ((argb and 0xFF) shl ((p and 0b11) shl 3))
			p++
		}
	}
	val window = windowing.open(
		StandardGraphicsWindowOpenFeatures.VISIBLE,
		StandardGraphicsWindowOpenFeatures.SIZING_BORDER,
		StandardGraphicsWindowOpenFeatures.SYSTEM_MAXIMIZE_BUTTON,
		StandardGraphicsWindowOpenFeatures.SYSTEM_MINIMIZE_BUTTON,
		StandardGraphicsWindowOpenFeatures.SYSTEM_CLOSE_BUTTON,
		GraphicsWindowIcon(
			Image2D(
				arrayOf(
					IntImagePlane(ImagePlaneType.RED_8, r),
					IntImagePlane(ImagePlaneType.GREEN_8, g),
					IntImagePlane(ImagePlaneType.BLUE_8, b),
					IntImagePlane(ImagePlaneType.ALPHA_8, a)
				),
				w.toUInt(), h.toUInt()
			)
		)
	).firstNotNullOf { it as? GraphicsWindow }
	while (true) {
		Thread.sleep(1)
		window.get(GraphicsWindowFeatures.WINDOW_NAME).set(GraphicsWindowName(Random.nextInt().toString()))
	}
}