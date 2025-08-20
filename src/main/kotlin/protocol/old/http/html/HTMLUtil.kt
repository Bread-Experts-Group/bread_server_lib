package org.bread_experts_group.protocol.old.http.html

import org.bread_experts_group.formatMetric

fun truncateSizeHTML(size: Long): String =
	if (size < 1000) "$size B"
	else "<u title=\"$size B\">${size.toDouble().formatMetric()}B</u>"