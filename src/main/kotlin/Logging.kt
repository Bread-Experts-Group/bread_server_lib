package bread_experts_group

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun hex(value: Long): String = "0x${value.toString(16).uppercase().padStart(16, '0')}"
fun hex(value: Int): String = "0x${value.toString(16).uppercase().padStart(8, '0')}"
fun hex(value: Short): String = "0x${value.toString(16).uppercase().padStart(4, '0')}"
fun hex(value: Byte): String = "0x${value.toString(16).uppercase().padStart(2, '0')}"

const val ESC = "\u001b["
const val RST = ESC + "0m"
fun logLn(color: Int? = 255, text: Any?, vararg args: Any?) {
	if (text is String && text.length == 3) return
	val time = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()).take(24).padEnd(24)
	val prepend = "${ESC}38;5;${color}m[${Thread.currentThread().name} @ ${time}] "
	val spaces = " ".padEnd(prepend.length)
	val (text, last) = text.toString().replace("\t", "  ").let {
		it.take(it.length - 1).replace("\n", "\n$spaces") to it.takeLast(1)
	}
	System.out.printf(prepend + text + last, *args)
	System.out.printf(RST + '\n')
}

fun debug(text: Any?, vararg args: Any?) = logLn(251, "DBG: $text", args)
fun info(text: Any?, vararg args: Any?) = logLn(33, "INF: $text", args)
fun warn(text: Any?, vararg args: Any?) = logLn(220, "WRN: $text", args)
fun error(text: Any?, vararg args: Any?) = logLn(197, "ERR: $text", args)