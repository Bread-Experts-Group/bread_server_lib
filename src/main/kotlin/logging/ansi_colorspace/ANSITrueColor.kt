package org.bread_experts_group.logging.ansi_colorspace

/**
 * @author Miko Elbrecht
 * @since 2.31.0
 */
data class ANSITrueColor(
	val r: UByte,
	val g: UByte,
	val b: UByte,
	val background: Boolean = false
) : ANSIColorSpace {
	override fun trailer(): String = buildString {
		append(if (background) "48" else "38")
		append(";2;")
		append(r.toString())
		append(';')
		append(g.toString())
		append(';')
		append(b.toString())
		append('m')
	}
}