package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable

enum class WindowsBaudRate(override val id: UInt) : Mappable<WindowsBaudRate, UInt> {
	CBR_110(110u),
	CBR_300(300u),
	CBR_600(600u),
	CBR_1200(1200u),
	CBR_2400(2400u),
	CBR_4800(4800u),
	CBR_9600(9600u),
	CBR_14400(14400u),
	CBR_19200(19200u),
	CBR_38400(38400u),
	CBR_57600(57600u),
	CBR_115200(115200u),
	CBR_128000(128000u),
	CBR_256000(256000u);

	override val tag: String = "Baud Rate ($id bps)"
	override fun toString(): String = stringForm()
}