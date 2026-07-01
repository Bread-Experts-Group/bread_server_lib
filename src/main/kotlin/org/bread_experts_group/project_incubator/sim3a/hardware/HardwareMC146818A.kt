package org.bread_experts_group.project_incubator.sim3a.hardware

import java.lang.foreign.Arena

class HardwareMC146818A(
	arena: Arena,
	timeBase: TimeBase
) {
	enum class TimeBase {
		`4.194304 MHz`,
		`1.048576 MHz`,
		`32.768 kHz`
	}

	// TODO: 4.194304 update cycle is 248 microseconds
	// TODO: 1.048576 update cycle is 248 microseconds
	// TODO: 32.768 update cycle is 1984 microseconds

	// TODO: time before update cycles for all frequencies is 244 microseconds

	// TODO: Registers C and D, bit 7 of Register A, and the high order bit of the seconds bit is read only.
	// TODO: everything else is writable

	// TODO: 0x00: Seconds (& BCD)       [0-59]                   ... READ WRITE [0b1_0000000 IS READ ONLY]
	// TODO: 0x01: Seconds Alarm (& BCD) [0-59]
	// TODO: 0x02: Minutes (& BCD)       [0-59]
	// TODO: 0x03: Minutes Alarm (& BCD) [0-59]
	// TODO: 0x04: Hours (& BCD)         [1-12] OR [0-23]
	// TODO: 0x05: Hours Alarm (& BCD)   [1-12] OR [0-23]
	// TODO: 0x06: Day of Week (& BCD)   [1-7]
	// TODO: 0x07: Date of Month (& BCD) [1-31]
	// TODO: 0x08: Month (& BCD)         [1-12]
	// TODO: 0x09: Year (& BCD)          [0-99]
	// TODO: 0x0A: Register A [UIP DV2 DV1 DV0 RS3 RS2 RS1 RS0]    ... READ WRITE [UIP IS READ ONLY]
	// TODO: 0x0B: Register B [SET PIE AIE UIE SQWE DM 24/12 DSE]  ... READ WRITE
	// TODO: the SET bit will disable updating of the time and calendar bytes
	// TODO: the PIE bit will enable "periodic interrupts" specified by RS3-RS0, but the PF flag will still be set
	// TODO: the AIE bit will enable "alarm interrupts" that occur when the second/minute/hour bytes equal their alarm
	// TODO:  ... counterpart. IF the alarm bytes are set to 0b11_XXXXXX, then it indicates don't care (true).
	// TODO:  ... additionally, AF is set during an alarm.
	// TODO: the DM flag sets between binary and BCD (0 is BCD).
	// TODO: the 24/12 flag, if 1, is 24 hours. otherwise, 12 hours
	// TODO: the DSE flag, daylight savings: on the last sunday in april, the time goes from 1:59:59 AM to 3:00 AM.
	// TODO:  ... and on the last sunday in October, when the time is 1:59:59 AM it goes to 1:00 AM.
	// TODO: the UIE enables update end interrupts (UF).
	// TODO: 0x0C: Register C [IRQF PF AF UF 0 0 0 0]              ... READ ONLY
	// TODO:  ... the IRQF flag is set to true if PF, or AF, or UF
	// TODO: 0x0D: Register D [VRT 0 0 0 0 0 0 0]                  ... READ ONLY
	// TODO: Always set the VRT bit.

	// TODO: + 50 bytes of user RAM
}