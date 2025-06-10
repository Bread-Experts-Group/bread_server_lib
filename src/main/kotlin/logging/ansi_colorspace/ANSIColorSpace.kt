package org.bread_experts_group.logging.ansi_colorspace

/**
 * Interface for ANSI color spaces (8-16 colors, 256 color indexed palette, true-color, ...)
 * @author Miko Elbrecht
 * @since 2.31.0
 */
interface ANSIColorSpace {
	fun trailer(): String
}