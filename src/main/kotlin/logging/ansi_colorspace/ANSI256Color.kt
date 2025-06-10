package org.bread_experts_group.logging.ansi_colorspace

/**
 * @author Miko Elbrecht
 * @since 2.31.0
 */
data class ANSI256Color(
	val baseColor: UByte,
	val background: Boolean = false
) : ANSIColorSpace {
	override fun trailer(): String = buildString {
		append(if (background) "48" else "38")
		append(";5;")
		append(baseColor.toString())
		append('m')
	}
}