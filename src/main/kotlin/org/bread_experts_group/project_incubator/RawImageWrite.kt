package org.bread_experts_group.project_incubator

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures
import org.bread_experts_group.generic.io.reader.BSLWriter
import java.io.File
import javax.imageio.ImageIO

fun main() {
	val file = "C:\\Users\\Adenosine3Phosphate\\Desktop\\HG0mKPRaIAAzHdn.jpg"
	val img = ImageIO.read(File(file))
	val fileOut = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE).device.get(
		SystemDeviceFeatures.PATH_APPEND
	).append("out.bin").get(
		SystemDeviceFeatures.IO_DEVICE
	).open(
		StandardIOOpenFeatures.CREATE,
		FileIOOpenFeatures.TRUNCATE,
		FileIOReOpenFeatures.WRITE,
		FileIOReOpenFeatures.SHARE_READ
	).firstNotNullOf { it as? IODevice }
	val writer = BSLWriter(fileOut.get(IODeviceFeatures.WRITE))
	var y = 0
	while (y < img.height) {
		var x = 0
		while (x < img.width) {
			val rgb = img.getRGB(x, y)
			writer.write8i(rgb and 0xFF)
			writer.write8i((rgb ushr 8) and 0xFF)
			writer.write8i((rgb ushr 16) and 0xFF)
			writer.write8i(0)
			x++
		}
		y++
	}
	writer.flush()
}