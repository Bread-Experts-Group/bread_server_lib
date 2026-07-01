package org.bread_experts_group.project_incubator.sim3a.hardware

import org.bread_experts_group.project_incubator.sim3a.aio.ArrayIO
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

@Suppress("DANGEROUS_CHARACTERS", "PropertyName", "FunctionName")
class HardwareW65C02S(
	private val addrIn: ArrayIO.ReadExact<UShort>,
	private val addrOut: ArrayIO.WriteExact<UShort>,
) {
	companion object {
		const val HW_BRK_IRQB_VECTOR_START: UShort = 0xFFFEu /* ... 0xFFFF */
		const val RESB_VECTOR_START: UShort = 0xFFFCu /* ... 0xFFFD */
		const val NMIB_VECTOR_START: UShort = 0xFFFAu /* ... 0xFFFB */
	}

	private var nmiLatch = false
	var nmi = false
		set(value) {
			if (value == field) return
			if (!value) nmiLatch = false
			field = value
		}

	var `Accumulator A`: Byte = 0
	var `Index Register Y`: Byte = 0
	var `Index Register X`: Byte = 0
	var `Program Counter PC`: UShort = 0u
	var `Stack Pointer S`: UShort = 0u
		get() = (field and 0xFFu) or 0b1_00000000u

	var `Processor Status Register "P"`: UByte = 0u
		get() = field or 0b00100000u

	var A: Byte
		get() = `Accumulator A`
		set(value) {
			`Accumulator A` = value
		}

	var Y: Byte
		get() = `Index Register Y`
		set(value) {
			`Index Register Y` = value
		}

	var X: Byte
		get() = `Index Register X`
		set(value) {
			`Index Register X` = value
		}

	var PCH: Byte
		get() = (`Program Counter PC`.toInt() ushr 8).toByte()
		set(value) {
			`Program Counter PC` = ((`Program Counter PC`.toInt() and 0x00FF) or (value.toInt() shl 8)).toUShort()
		}

	var PCL: Byte
		get() = `Program Counter PC`.toByte()
		set(value) {
			`Program Counter PC` = ((`Program Counter PC`.toInt() and 0xFF00) or (value.toInt())).toUShort()
		}

	var S: Byte
		get() = `Stack Pointer S`.toByte()
		set(value) {
			`Stack Pointer S` = value.toUShort()
		}

	var N: Boolean
		get() = `Processor Status Register "P"` and 0b10000000u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b10000000u.toUByte().inv()
			if (value) n = n or 0b10000000u
			`Processor Status Register "P"` = n
		}

	var V: Boolean
		get() = `Processor Status Register "P"` and 0b01000000u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b01000000u.toUByte().inv()
			if (value) n = n or 0b01000000u
			`Processor Status Register "P"` = n
		}

	var B: Boolean
		get() = `Processor Status Register "P"` and 0b00010000u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b00010000u.toUByte().inv()
			if (value) n = n or 0b00010000u
			`Processor Status Register "P"` = n
		}

	var D: Boolean
		get() = `Processor Status Register "P"` and 0b00001000u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b00001000u.toUByte().inv()
			if (value) n = n or 0b00001000u
			`Processor Status Register "P"` = n
		}

	var I: Boolean
		get() = `Processor Status Register "P"` and 0b00000100u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b00000100u.toUByte().inv()
			if (value) n = n or 0b00000100u
			`Processor Status Register "P"` = n
		}

	var Z: Boolean
		get() = `Processor Status Register "P"` and 0b00000010u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b00000010u.toUByte().inv()
			if (value) n = n or 0b00000010u
			`Processor Status Register "P"` = n
		}

	var C: Boolean
		get() = `Processor Status Register "P"` and 0b00000001u != 0u.toUByte()
		set(value) {
			var n = `Processor Status Register "P"` and 0b00000001u.toUByte().inv()
			if (value) n = n or 0b00000001u
			`Processor Status Register "P"` = n
		}

	fun reset() {
		`Processor Status Register "P"` = 0b00110100u
		`Program Counter PC` = addrIn.readUShort(RESB_VECTOR_START)
	}

	private fun postIncrementPC(by: UShort): UShort = `Program Counter PC`.also {
		`Program Counter PC` = (it + by).toUShort()
	}

	private fun `Absolute a`(): UShort = addrIn.readUShort(postIncrementPC(2u))
	private fun `Absolute Indexed with X a,x`(): UShort = (`Absolute a`() + X.toUShort()).toUShort()
	private fun `Absolute Indexed with Y a,y`(): UShort = (`Absolute a`() + Y.toUShort()).toUShort()
	private fun `Immediate Addressing #`(): Byte = addrIn.readByte(postIncrementPC(1u))
	private fun `Program Counter Relative r`(): UShort {
		return (this.`Program Counter PC`.toInt() + 1 + `Immediate Addressing #`()).toUShort()
	}

	private fun `Zero Page zp`(): UShort = addrIn.readUByte(postIncrementPC(1u)).toUShort()
	private fun `Zero Page Indirect Indexed with Y (zp), y`(): UShort {
		val zp = addrIn.readUByte(postIncrementPC(1u))
		val lo = addrIn.readUByte(zp.toUShort())
		val hi = addrIn.readUByte(((zp + 1u).toUByte()).toUShort())

		return ((lo.toUInt() or (hi.toUInt() shl 8)) + Y.toUByte().toUInt()).toUShort()
	}

	private fun `Zero Page Indexed with X zp,x`(): UShort {
		val added = addrIn.readUByte(postIncrementPC(1u)) + this.X.toUByte()
		return added.toUShort() and 0xFFu
	}

	val buffer = BufferedOutputStream(FileOutputStream("LOG.LOG"))
	fun step() {
		if (nmi && !nmiLatch) {
			buffer.write("NMI ===\n".toByteArray())
			nmiLatch = true
			`Stack Pointer S` = (`Stack Pointer S` - 2u).toUShort()
			addrOut.writeUShort(`Stack Pointer S`, `Program Counter PC`)
			`Stack Pointer S` = (`Stack Pointer S` - 1u).toUShort()
			addrOut.writeUByte(`Stack Pointer S`, `Processor Status Register "P"`)
			`Program Counter PC` = addrIn.readUShort(NMIB_VECTOR_START)
			return
		}
		var op: String
		when (val opcode = addrIn.readUByte(postIncrementPC(1u))) {
			0x40.toUByte() -> {
				op = "RTI"
				this.`Processor Status Register "P"` = addrIn.readUByte(`Stack Pointer S`)
				`Stack Pointer S` = (`Stack Pointer S` + 1u).toUShort()
				this.`Program Counter PC` = addrIn.readUShort(`Stack Pointer S`)
				`Stack Pointer S` = (`Stack Pointer S` + 2u).toUShort()
			}

			0x05u.toUByte() -> {
				val operand = `Zero Page zp`()
				val ob = addrIn.readByte(operand)
				op = "A (${this.A.toUByte().toHexString()}) | [${operand.toUByte().toHexString()}] -> "
				this.A = this.A or ob
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x09u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "A (${this.A.toUByte().toHexString()}) | ${operand.toUByte().toHexString()} -> "
				this.A = this.A or operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x29u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "A (${this.A.toUByte().toHexString()}) & ${operand.toUByte().toHexString()} -> "
				this.A = this.A and operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x25u.toUByte() -> {
				val operand = addrIn.readByte(`Zero Page zp`())
				op = "A (${this.A.toUByte().toHexString()}) & ${operand.toUByte().toHexString()} -> "
				this.A = this.A and operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			/* TODO 0x2Au.toUByte() -> {
				this.C = this.A and 0b1_000_0000.toByte() != 0.toByte()
				this.A = ((this.A.toInt() shl 1) or (if (this.C) 1 else 0)).toByte()
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op = "ROL"
			} */

			0x2Au.toUByte() -> {
				val oldCarry = this.C
				this.C = this.A and 0x80.toByte() != 0.toByte()
				this.A = ((this.A.toInt() shl 1) or (if (oldCarry) 1 else 0)).toByte()
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op = "ROL"
			}

			0x24u.toUByte() -> {
				val operand = `Zero Page zp`()
				val value = addrIn.readByte(operand)

				this.Z = (this.A and value) == 0.toByte()
				this.N = value < 0
				this.V = value and 0x40 != 0.toByte()

				op = "A (${this.A.toUByte().toHexString()}) BIT [${operand.toHexString()}] (${
					value.toUByte().toHexString()
				}) " +
						"[N=${this.N} V=${this.V} Z=${this.Z}]"
			}

			0x06u.toUByte() -> {
				val operand = `Zero Page zp`()
				val b = addrIn.readByte(operand)
				this.C = b and 0b1_000_0000.toByte() != 0.toByte()
				op = "[${operand.toHexString()}] << 1 -> "
				val result = (b.toInt() and 0xFF shl 1).toByte()
				addrOut.writeByte(operand, result)
				this.N = result < 0
				this.Z = result == 0.toByte()
				op += "(${result.toUByte().toHexString()}) [C=${this.C} N=${this.N} Z=${this.Z}]"
			}

			0x0Au.toUByte() -> {
				this.C = this.A and 0b1_000_0000.toByte() != 0.toByte()
				op = "A (${this.A.toUByte().toHexString()}) << 1 -> "
				val result = (this.A.toInt() and 0xFF shl 1).toByte()
				this.A = result
				this.N = result < 0
				this.Z = result == 0.toByte()
				op += "A (${result.toUByte().toHexString()}) [C=${this.C} N=${this.N} Z=${this.Z}]"
			}

			0x4Au.toUByte() -> {
				this.C = this.A and 0b1 == 1.toByte()
				op = "A (${this.A.toUByte().toHexString()}) >>> 1 -> "
				this.A = (this.A.toInt() and 0xFF ushr 1).toByte()
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [C=${this.C} N=${this.N} Z=${this.Z}]"
			}

			0x46u.toUByte() -> {
				val operand = `Zero Page zp`()
				val b = addrIn.readByte(operand)
				this.C = b and 0b1 == 1.toByte()
				op = "[${operand.toHexString()}] (${b.toUByte().toHexString()}) >>> 1 -> "
				val result = (b.toInt() and 0xFF ushr 1).toByte()
				addrOut.writeByte(operand, result)
				this.N = result < 0
				this.Z = result == 0.toByte()
				op += "(${result.toUByte().toHexString()}) [C=${this.C} N=${this.N} Z=${this.Z}]"
			}

			0x8Du.toUByte() -> {
				val operand = `Absolute a`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand.toHexString()}]"
			}

			0x99u.toUByte() -> {
				val operand = `Absolute Indexed with Y a,y`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand.toHexString()}]"
			}

			0x9Du.toUByte() -> {
				val operand = `Absolute Indexed with X a,x`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand.toHexString()}]"
			}

			0x95u.toUByte() -> {
				val operand = `Zero Page Indexed with X zp,x`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand.toHexString()}]"
			}

			0x84u.toUByte() -> {
				val operand = `Zero Page zp`()
				addrOut.writeByte(operand, this.Y)
				op = "Y (${this.Y.toUByte().toHexString()}) -> [${operand.toHexString()}])"
			}

			0x8Cu.toUByte() -> {
				val operand = `Absolute a`()
				addrOut.writeByte(operand, this.Y)
				op = "Y (${this.Y.toUByte().toHexString()}) -> [${operand.toHexString()}])"
			}

			0x88u.toUByte() -> {
				op = "Y (${this.Y.toUByte().toHexString()})-- -> "
				this.Y--
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "Y (${this.Y.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xC8u.toUByte() -> {
				op = "Y (${this.Y.toUByte().toHexString()})++ -> "
				this.Y++
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "Y (${this.Y.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xE8u.toUByte() -> {
				op = "X (${this.X.toUByte().toHexString()})++ -> "
				this.X++
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "X (${this.X.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xCAu.toUByte() -> {
				op = "X (${this.X.toUByte().toHexString()})-- -> "
				this.X--
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "X (${this.X.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x91u.toUByte() -> {
				val operand = `Zero Page Indirect Indexed with Y (zp), y`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand}]"
			}

			0x85u.toUByte() -> {
				val operand = `Zero Page zp`()
				addrOut.writeByte(operand, this.A)
				op = "A (${this.A.toUByte().toHexString()}) -> [${operand.toHexString()}]"
			}

			0x9Au.toUByte() -> {
				op = "X (${this.X.toUByte().toHexString()}) -> S (${this.S.toUShort().toHexString()})"
				this.S = this.X
			}

			0x8Au.toUByte() -> {
				op = "X (${this.X.toUByte().toHexString()}) -> A (${this.A.toUByte().toHexString()}) "
				this.A = this.X
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xAAu.toUByte() -> {
				op = "A (${this.A.toUByte().toHexString()}) -> X (${this.X.toUByte().toHexString()}) "
				this.X = this.A
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xA8u.toUByte() -> {
				op = "A (${this.A.toUByte().toHexString()}) -> Y (${this.Y.toUByte().toHexString()}) "
				this.Y = this.A
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0x98u.toUByte() -> {
				op = "Y (${this.Y.toUByte().toHexString()}) -> A (${this.A.toUByte().toHexString()}) "
				this.A = this.Y
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xA0.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "${operand.toUByte().toHexString()} -> Y (${this.Y.toUByte().toHexString()}) "
				this.Y = operand
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xB4.toUByte() -> {
				val operand = `Zero Page Indexed with X zp,x`()
				op = "[${operand.toHexString()}] -> Y (${this.Y.toUByte().toHexString()}) "
				this.Y = addrIn.readByte(operand)
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xA4.toUByte() -> {
				val operand = `Zero Page zp`()
				op = "[${operand.toHexString()}] -> Y (${this.Y.toUByte().toHexString()}) "
				this.Y = addrIn.readByte(operand)
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xAC.toUByte() -> {
				val operand = `Absolute a`()
				op = "[${operand.toHexString()}] -> Y (${this.Y.toUByte().toHexString()}) "
				this.Y = addrIn.readByte(operand)
				this.N = this.Y < 0
				this.Z = this.Y == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xA2u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "${operand.toUByte().toHexString()} -> X (${this.X.toUByte().toHexString()}) "
				this.X = operand
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xAEu.toUByte() -> {
				val operand = `Absolute a`()
				op = "[${operand.toUByte().toHexString()}] -> X (${this.X.toUByte().toHexString()}) "
				this.X = addrIn.readByte(operand)
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0x86u.toUByte() -> {
				val operand = `Zero Page zp`()
				op = "X (${this.X.toUByte().toHexString()}) -> [${operand.toUByte().toHexString()}]"
				addrOut.writeByte(operand, this.X)
			}

			0x8Eu.toUByte() -> {
				val operand = `Absolute a`()
				op = "X (${this.X.toUByte().toHexString()}) -> [${operand.toUByte().toHexString()}]"
				addrOut.writeByte(operand, this.X)
			}

			0xA6u.toUByte() -> {
				val operand = `Zero Page zp`()
				op = "[${operand.toHexString()}] -> X (${this.X.toUByte().toHexString()}) "
				this.X = addrIn.readByte(operand)
				this.N = this.X < 0
				this.Z = this.X == 0.toByte()
				op += "-> X (${this.X.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xA9u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "${operand.toUByte().toHexString()} -> A (${this.A.toUByte().toHexString()}) "
				this.A = operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "[N=${this.N} Z=${this.Z}]"
			}

			0xC9u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				val result = this.A - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.A >= operand
				op = "CMP"
			}

			0xDDu.toUByte() -> {
				val o = `Absolute Indexed with X a,x`()
				val operand = addrIn.readByte(o)
				val result = this.A - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.A >= operand
				op = "CMP"
			}

			0xD9u.toUByte() -> {
				val o = `Absolute Indexed with Y a,y`()
				val operand = addrIn.readByte(o)
				val result = this.A - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.A >= operand
				op = "CMP"
			}

			0xC5u.toUByte() -> {
				val o = `Zero Page zp`()
				val operand = addrIn.readByte(o)
				val result = this.A - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.A >= operand
				op = "CMP"
			}

			0xCDu.toUByte() -> {
				val o = `Absolute a`()
				val operand = addrIn.readByte(o)
				val result = this.A - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.A >= operand
				op = "CMP"
			}

			0xC0u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				val result = this.Y - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.Y >= operand
				op = "CPY"
			}

			0xE0u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				val result = this.X - operand
				this.N = result < 0
				this.Z = result == 0
				this.C = this.X >= operand
				op = "CPX"
			}

			0x65u.toUByte() -> {
				val operand = `Zero Page zp`()
				val o2 = addrIn.readUByte(operand)
				val result = this.A.toUByte() + o2 + (if (this.C) 1u else 0u)
				op = "A (${this.A.toUByte().toHexString()}) + [${operand.toHexString()}] ($o2) + " +
						"C (${if (this.C) 1 else 0}) "
				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u
				val a = this.A.toInt()
				val m = o2.toInt()
				val r = result.toInt() and 0xFF

				this.V = ((a xor r) and (m xor r) and 0x80) != 0
				this.C = result > 0xFFu
				this.A = resultB.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0x7Du.toUByte() -> {
				val operand = `Absolute Indexed with X a,x`()
				val o2 = addrIn.readUByte(operand)
				val result = this.A.toUByte() + o2 + (if (this.C) 1u else 0u)
				op = "A (${this.A.toUByte().toHexString()}) + [${operand.toHexString()}] ($o2) + " +
						"C (${if (this.C) 1 else 0}) "
				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u
				val a = this.A.toInt()
				val m = o2.toInt()
				val r = result.toInt() and 0xFF

				this.V = ((a xor r) and (m xor r) and 0x80) != 0
				this.C = result > 0xFFu
				this.A = resultB.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0xE5u.toUByte() -> { // TODO: generalize
				val operand = `Zero Page zp`()
				val o2 = addrIn.readUByte(operand)
				val value = o2 xor 0xFFu

				val result = this.A.toUByte() + value + (if (this.C) 1u else 0u)

				op = "A (${this.A.toUByte().toHexString()}) - [${operand.toHexString()}] ($o2) - " +
						"(1-C=${if (this.C) 0 else 1}) "

				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u

				val a = this.A.toInt()
				val m = o2.toInt()
				val r = resultB and 0xFF
				this.V = ((a xor r) and (a xor m) and 0x80) != 0
				this.C = result > 0xFFu

				this.A = resultB.toByte()

				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0xE9u.toUByte() -> {  // TODO: generalize
				val o2 = `Immediate Addressing #`().toUByte()
				val value = o2 xor 0xFFu

				val result = this.A.toUByte() + value + (if (this.C) 1u else 0u)

				op = "A (${this.A.toUByte().toHexString()}) - ${o2.toHexString()} - " +
						"(1-C=${if (this.C) 0 else 1}) "

				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u

				val a = this.A.toInt()
				val m = o2.toInt()
				val r = resultB and 0xFF
				this.V = ((a xor r) and (a xor m) and 0x80) != 0
				this.C = result > 0xFFu

				this.A = resultB.toByte()

				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0x69u.toUByte() -> {
				val operand = `Immediate Addressing #`().toUByte()
				val result = this.A.toUByte() + operand + (if (this.C) 1u else 0u)
				op = "A (${this.A.toUByte().toHexString()}) + ${operand.toHexString()} + " +
						"C (${if (this.C) 1 else 0}) "
				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u
				val a = this.A.toInt()
				val m = operand.toInt()
				val r = result.toInt() and 0xFF

				this.V = ((a xor r) and (m xor r) and 0x80) != 0
				this.C = result > 0xFFu
				this.A = resultB.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0x6Du.toUByte() -> {
				val operandA = `Absolute a`()
				val operand = addrIn.readUByte(operandA)
				val result = this.A.toUByte() + operand + (if (this.C) 1u else 0u)
				op = "A (${this.A.toUByte().toHexString()}) + ${operand.toHexString()} + " +
						"C (${if (this.C) 1 else 0}) "
				val resultB = result.toInt()
				this.N = resultB.toByte() < 0
				this.Z = (result and 0xFFu) == 0u
				val a = this.A.toInt()
				val m = operand.toInt()
				val r = result.toInt() and 0xFF

				this.V = ((a xor r) and (m xor r) and 0x80) != 0
				this.C = result > 0xFFu
				this.A = resultB.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z} C=${this.C} V=${this.V}]"
			}

			0xA5u.toUByte() -> {
				val operand = `Zero Page zp`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xB1u.toUByte() -> {
				val operand = `Zero Page Indirect Indexed with Y (zp), y`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xBDu.toUByte() -> {
				val operand = `Absolute Indexed with X a,x`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xADu.toUByte() -> {
				val operand = `Absolute a`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xB5u.toUByte() -> {
				val operand = `Zero Page Indexed with X zp,x`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xB9u.toUByte() -> {
				val operand = `Absolute Indexed with Y a,y`()
				op = "[${operand.toHexString()}] -> A (${this.A.toUByte().toHexString()}) "
				this.A = addrIn.readByte(operand)
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "-> A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xD8u.toUByte() -> {
				this.D = false
				op = "CLD"
			}

			0x78u.toUByte() -> {
				this.I = true
				op = "SEI"
			}

			0x38u.toUByte() -> {
				this.C = true
				op = "SEC"
			}

			0x18u.toUByte() -> {
				this.C = false
				op = "CLC"
			}

			0xC6u.toUByte() -> {
				val operand = `Zero Page zp`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})-- -> "
				val result = current - 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xD6u.toUByte() -> {
				val operand = `Zero Page Indexed with X zp,x`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})-- -> "
				val result = current - 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xCEu.toUByte() -> {
				val operand = `Absolute a`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})-- -> "
				val result = current - 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xEEu.toUByte() -> {
				val operand = `Absolute a`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})++ -> "
				val result = current + 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xFEu.toUByte() -> {
				val operand = `Absolute Indexed with X a,x`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})++ -> "
				val result = current + 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xE6u.toUByte() -> {
				val operand = `Zero Page zp`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})++ -> "
				val result = current + 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0xF6u.toUByte() -> {
				val operand = `Zero Page Indexed with X zp,x`()
				val current = addrIn.readByte(operand)
				op = "[${operand.toHexString()}] (${current.toUByte().toHexString()})++ -> "
				val result = current + 1
				this.N = result < 0
				this.Z = result == 0
				addrOut.writeByte(operand, result.toByte())
				op += "(${result.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x20u.toUByte() -> {
				val operand = `Absolute a`()
				`Stack Pointer S` = (`Stack Pointer S` - 2u).toUShort()
				addrOut.writeUShort(`Stack Pointer S`, `Program Counter PC`)
				this.`Program Counter PC` = operand
				op = "JSR ${operand.toHexString()}"
			}

			0x45u.toUByte() -> {
				val operand = addrIn.readByte(`Zero Page zp`())
				op = "A (${this.A.toUByte().toHexString()}) xor (${operand.toUByte().toHexString()}) -> "
				this.A = this.A xor operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x49u.toUByte() -> {
				val operand = `Immediate Addressing #`()
				op = "A (${this.A.toUByte().toHexString()}) xor ${operand.toUByte().toHexString()} -> "
				this.A = this.A xor operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x4Du.toUByte() -> {
				val o = `Absolute a`()
				val operand = addrIn.readByte(o)
				op = "A (${this.A.toUByte().toHexString()}) xor ${operand.toUByte().toHexString()} -> "
				this.A = this.A xor operand
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op += "A (${this.A.toUByte().toHexString()}) [N=${this.N} Z=${this.Z}]"
			}

			0x66u.toUByte() -> {
				val operand = `Zero Page zp`()
				val datum = addrIn.readByte(operand).toInt() and 0xFF
				val oldCarry = this.C
				this.C = datum and 1 != 0
				val result = ((datum ushr 1) or (if (oldCarry) 0x80 else 0)).toByte()
				addrOut.writeByte(operand, result)
				this.N = result < 0
				this.Z = result == 0.toByte()
				op = "ROR"
			}

			0x6Au.toUByte() -> {
				val datum = this.A.toInt() and 0xFF
				val oldCarry = this.C
				this.C = datum and 1 != 0
				val result = ((datum ushr 1) or (if (oldCarry) 0x80 else 0)).toByte()
				this.A = result
				this.N = result < 0
				this.Z = result == 0.toByte()
				op = "ROR"
			}

			0x60u.toUByte() -> {
				this.`Program Counter PC` = addrIn.readUShort(`Stack Pointer S`)
				`Stack Pointer S` = (`Stack Pointer S` + 2u).toUShort()
				op = "RTS"
			}

			0x4Cu.toUByte() -> {
				val operand = `Absolute a`()
				this.`Program Counter PC` = operand
				op = "JMP ${operand.toHexString()}"
			}

			0x48u.toUByte() -> {
				`Stack Pointer S` = (`Stack Pointer S` - 1u).toUShort()
				addrOut.writeByte(`Stack Pointer S`, this.A)
				op = "PHA"
			}

			0x68u.toUByte() -> {
				this.A = addrIn.readByte(`Stack Pointer S`)
				`Stack Pointer S` = (`Stack Pointer S` + 1u).toUShort()
				this.N = this.A < 0
				this.Z = this.A == 0.toByte()
				op = "PLA"
			}

			0x08u.toUByte() -> {
				`Stack Pointer S` = (`Stack Pointer S` - 1u).toUShort()
				addrOut.writeUByte(`Stack Pointer S`, this.`Processor Status Register "P"`)
				op = "PHP"
			}

			0x28u.toUByte() -> {
				this.`Processor Status Register "P"` = addrIn.readUByte(`Stack Pointer S`)
				`Stack Pointer S` = (`Stack Pointer S` + 1u).toUShort()
				op = "PLP"
			}

			0x10u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (!this.N) this.`Program Counter PC` = operand
				op = "BPL ${operand.toHexString()}"
			}

			0xD0u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (!this.Z) this.`Program Counter PC` = operand
				op = "BNE ${operand.toHexString()}"
			}

			0xF0u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (this.Z) this.`Program Counter PC` = operand
				op = "BEQ ${operand.toHexString()}"
			}

			0x90u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (!this.C) this.`Program Counter PC` = operand
				op = "BCC ${operand.toHexString()}"
			}

			0x6Cu.toUByte() -> {
				val operand = `Absolute a`()
				this.`Program Counter PC` = addrIn.readUShort(operand)
				op = "JMP ${operand.toHexString()}"
			}

			0x30u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (this.N) this.`Program Counter PC` = operand
				op = "BMI ${operand.toHexString()}"
			}

			0xB0u.toUByte() -> {
				val operand = `Program Counter Relative r`()
				if (this.C) this.`Program Counter PC` = operand
				op = "BCS ${operand.toHexString()}"
			}

			else -> TODO("Op ${opcode.toHexString()}")
		}
//		buffer.write("$op\n".toByteArray())
//		buffer.flush()
	}
}