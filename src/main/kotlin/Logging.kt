package bread_experts_group

fun hexLen(s: String, n: Int) = "0x${s.uppercase().padStart(n, '0')}"
fun hex(value: Long): String = hexLen(value.toString(16), 16)
fun hex(value: Int): String = hexLen(value.toString(16), 8)
fun hex(value: Short): String = hexLen(value.toString(16), 4)
fun hex(value: Byte): String = hexLen(value.toString(16), 2)