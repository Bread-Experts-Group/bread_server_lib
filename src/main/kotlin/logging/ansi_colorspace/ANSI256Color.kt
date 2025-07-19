package org.bread_experts_group.logging.ansi_colorspace

import org.bread_experts_group.logging.ansiEscape

/**
 * @author Miko Elbrecht
 * @since 2.31.0
 */
data class ANSI256Color(
	val baseColor: UByte,
	val background: Boolean = false
) : ANSIColorSpace {
	override val trailer: String = ansiEscape + (if (background) "48" else "38") + ";5;" + baseColor.toString() + 'm'
}