package bread_experts_group

import java.io.ByteArrayOutputStream
import java.io.OutputStream

interface Writable {
	fun write(stream: OutputStream)
	fun asBytes(): ByteArray = ByteArrayOutputStream().use {
		this.write(it)
		it.toByteArray()
	}
}