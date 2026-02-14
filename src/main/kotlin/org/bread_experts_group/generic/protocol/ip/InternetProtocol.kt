package org.bread_experts_group.generic.protocol.ip

import org.bread_experts_group.generic.io.IOLayout
import org.bread_experts_group.generic.io.SelectiveIOLayout
import org.bread_experts_group.generic.protocol.ip.v4.InternetProtocolV4

abstract class InternetProtocol {
	companion object {
		fun selectV4(n: UByte) = n and 0b11110000u == 0b01000000u.toUByte()
		val layout = SelectiveIOLayout<InternetProtocol>(
			mapOf(
				::selectV4 to InternetProtocolV4.layout
			),
			{ w, ip -> TODO("IP") },
			IOLayout.UNSIGNED_BYTE.passedUpwards()
		)
	}
}