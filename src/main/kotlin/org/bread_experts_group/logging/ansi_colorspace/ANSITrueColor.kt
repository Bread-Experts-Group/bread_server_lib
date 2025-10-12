package org.bread_experts_group.logging.ansi_colorspace

import org.bread_experts_group.logging.ansiEscape

/**
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
data class ANSITrueColor(
	val r: UByte,
	val g: UByte,
	val b: UByte,
	val background: Boolean = false
) : ANSIColorSpace {
	override val trailer: String = ansiEscape + (if (background) "48" else "38") + ";2;" + r.toString() + ';' +
			g.toString() + ';' + b.toString() + 'm'
}