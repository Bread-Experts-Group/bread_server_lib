package org.bread_experts_group.api.compile.mzdos

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel

@Suppress("PropertyName")
@OptIn(ExperimentalUnsignedTypes::class)
class MZDOSFile private constructor(private val structure: FileStructure) {
	companion object {
		fun of(builder: FileStructure.() -> Unit): MZDOSFile {
			val mzDos = FileStructure()
			builder(mzDos)
			return MZDOSFile(mzDos)
		}
	}

	class FileStructure internal constructor() {
		var e_magic: UShort = 0x5A4Du
		var e_cblp: UShort = 0u
		var e_cp: UShort = 0u
		var e_crlc: UShort = 0u
		var e_cparhdr: UShort = 0u
		var e_minalloc: UShort = 0u
		var e_maxalloc: UShort = 0u
		var e_ss: UShort = 0u
		var e_sp: UShort = 0u
		var e_csum: UShort = 0u
		var e_ip: UShort = 0u
		var e_cs: UShort = 0u
		var e_lfarlc: UShort = 0u
		var e_ovno: UShort = 0u
		var e_res: UShortArray = UShortArray(4)
		var e_oemid: UShort = 0u
		var e_oeminfo: UShort = 0u
		var e_res2: UShortArray = UShortArray(10)
	}

	fun build(into: SeekableByteChannel) {
		val buffer = ByteBuffer.allocate(60)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		buffer.putShort(structure.e_magic.toShort())
		buffer.putShort(structure.e_cblp.toShort())
		buffer.putShort(structure.e_cp.toShort())
		buffer.putShort(structure.e_crlc.toShort())
		buffer.putShort(structure.e_cparhdr.toShort())
		buffer.putShort(structure.e_minalloc.toShort())
		buffer.putShort(structure.e_maxalloc.toShort())
		buffer.putShort(structure.e_ss.toShort())
		buffer.putShort(structure.e_sp.toShort())
		buffer.putShort(structure.e_csum.toShort())
		buffer.putShort(structure.e_ip.toShort())
		buffer.putShort(structure.e_cs.toShort())
		buffer.putShort(structure.e_lfarlc.toShort())
		buffer.putShort(structure.e_ovno.toShort())
		structure.e_res.forEach { buffer.putShort(it.toShort()) }
		buffer.putShort(structure.e_oemid.toShort())
		buffer.putShort(structure.e_oeminfo.toShort())
		structure.e_res2.forEach { buffer.putShort(it.toShort()) }
		into.write(buffer.clear())
	}
}