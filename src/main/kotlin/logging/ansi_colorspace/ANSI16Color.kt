package org.bread_experts_group.logging.ansi_colorspace

/**
 * @author Miko Elbrecht
 * @since 2.31.0
 */
data class ANSI16Color(
	val baseColor: ANSI16,
	val background: Boolean = false
) : ANSIColorSpace {
	override fun trailer(): String = (30 + baseColor.code + (if (background) 10 else 0)).toString() + 'm'
}