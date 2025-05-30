package org.bread_experts_group.http.html

import org.bread_experts_group.truncateSI

fun truncateSizeHTML(size: Long): String =
	if (size < 1000) "$size B"
	else "<u title=\"$size B\">${truncateSI(size)}B</u>"