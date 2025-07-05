package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.coder.format.BitInputStream
import org.bread_experts_group.coder.format.huffman.HuffmanBranch
import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.writeString
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class HTTP2HeaderFrame(
	identifier: Int,
	val flags: List<HTTP2HeaderFrameFlag>,
	val priority: Priority?,
	val block: Map<String, String>,
	val dynamic: List<Pair<String, String>> = listOf()
) : HTTP2Frame(HTTP2FrameType.HEADERS, identifier) {
	override fun toString(): String = super.toString() + " [${flags.joinToString(" ")}]" +
			(if (priority != null) "\n$priority" else "") +
			block.entries.joinToString("") { (name, value) -> "\n$name: $value" }

	override fun collectFlags(): Int = flags.fold(0) { flags, flag -> flags or flag.position }

	val encoded: ByteArray = ByteArrayOutputStream().use {
		block.forEach { (name, value) ->
			it.write(0b00010000)
			encodeHPACKInteger(name.length, 7, it)
			it.writeString(name)
			encodeHPACKInteger(value.length, 7, it)
			it.writeString(value)
		}
		it.toByteArray()
	}

	override fun computeSize(): Long = encoded.size.toLong()
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write(encoded)
	}

	data class Priority(
		val exclusive: Boolean,
		val dependency: Int,
		val weight: Int
	) {
		override fun toString(): String = "(HTTP/2, HEADERS Priority) [${if (exclusive) "EX" else ""}] " +
				"DEP: ${hex(dependency)}, WHT: $weight"
	}

	companion object {
		@Suppress("LocalVariableName", "DuplicatedCode")
		val huffmanRoot: HuffmanBranch<Char> = HuffmanBranch<Char>().also { root ->
			root.branch(false).also { _0 ->
				_0.branch(false).also { _00 ->
					_00.branch(false).also { _000 ->
						_000.branch(false).also { _0000 ->
							_0000.edge(false, '0')
							_0000.edge(true, '1')
						}
						_000.branch(true).also { _0001 ->
							_0001.edge(false, '2')
							_0001.edge(true, 'a')
						}
					}
					_00.branch(true).also { _001 ->
						_001.branch(false).also { _0010 ->
							_0010.edge(false, 'c')
							_0010.edge(true, 'e')
						}
						_001.branch(true).also { _0011 ->
							_0011.edge(false, 'i')
							_0011.edge(true, 'o')
						}
					}
				}
				_0.branch(true).also { _01 ->
					_01.branch(false).also { _010 ->
						_010.branch(false).also { _0100 ->
							_0100.edge(false, 's')
							_0100.edge(true, 't')
						}
						_010.branch(true).also { _0101 ->
							_0101.branch(false).also { _01010 ->
								_01010.edge(false, ' ')
								_01010.edge(true, '%')
							}
							_0101.branch(true).also { _01011 ->
								_01011.edge(false, '-')
								_01011.edge(true, '.')
							}
						}
					}
					_01.branch(true).also { _011 ->
						_011.branch(false).also { _0110 ->
							_0110.branch(false).also { _01100 ->
								_01100.edge(false, '/')
								_01100.edge(true, '3')
							}
							_0110.branch(true).also { _01101 ->
								_01101.edge(false, '4')
								_01101.edge(true, '5')
							}
						}
						_011.branch(true).also { _0111 ->
							_0111.branch(false).also { _01110 ->
								_01110.edge(false, '6')
								_01110.edge(true, '7')
							}
							_0111.branch(true).also { _01111 ->
								_01111.edge(false, '8')
								_01111.edge(true, '9')
							}
						}
					}
				}
			}
			root.branch(true).also { _1 ->
				_1.branch(false).also { _10 ->
					_10.branch(false).also { _100 ->
						_100.branch(false).also { _1000 ->
							_1000.branch(false).also { _10000 ->
								_10000.edge(false, '=')
								_10000.edge(true, 'A')
							}
							_1000.branch(true).also { _10001 ->
								_10001.edge(false, '_')
								_10001.edge(true, 'b')
							}
						}
						_100.branch(true).also { _1001 ->
							_1001.branch(false).also { _10010 ->
								_10010.edge(false, 'd')
								_10010.edge(true, 'f')
							}
							_1001.branch(true).also { _10011 ->
								_10011.edge(false, 'g')
								_10011.edge(true, 'h')
							}
						}
					}
					_10.branch(true).also { _101 ->
						_101.branch(false).also { _1010 ->
							_1010.branch(false).also { _10100 ->
								_10100.edge(false, 'l')
								_10100.edge(true, 'm')
							}
							_1010.branch(true).also { _10101 ->
								_10101.edge(false, 'n')
								_10101.edge(true, 'p')
							}
						}
						_101.branch(true).also { _1011 ->
							_1011.branch(false).also { _10110 ->
								_10110.edge(false, 'r')
								_10110.edge(true, 'u')
							}
							_1011.branch(true).also { _10111 ->
								_10111.branch(false).also { _101110 ->
									_101110.edge(false, ':')
									_101110.edge(true, 'B')
								}
								_10111.branch(true).also { _101111 ->
									_101111.edge(false, 'C')
									_101111.edge(true, 'D')
								}
							}
						}
					}
				}
				_1.branch(true).also { _11 ->
					_11.branch(false).also { _110 ->
						_110.branch(false).also { _1100 ->
							_1100.branch(false).also { _11000 ->
								_11000.branch(false).also { _110000 ->
									_110000.edge(false, 'E')
									_110000.edge(true, 'F')
								}
								_11000.branch(true).also { _110001 ->
									_110001.edge(false, 'G')
									_110001.edge(true, 'H')
								}
							}
							_1100.branch(true).also { _11001 ->
								_11001.branch(false).also { _110010 ->
									_110010.edge(false, 'I')
									_110010.edge(true, 'J')
								}
								_11001.branch(true).also { _110011 ->
									_110011.edge(false, 'K')
									_110011.edge(true, 'L')
								}
							}
						}
						_110.branch(true).also { _1101 ->
							_1101.branch(false).also { _11010 ->
								_11010.branch(false).also { _110100 ->
									_110100.edge(false, 'M')
									_110100.edge(true, 'N')
								}
								_11010.branch(true).also { _110101 ->
									_110101.edge(false, 'O')
									_110101.edge(true, 'P')
								}
							}
							_1101.branch(true).also { _11011 ->
								_11011.branch(false).also { _110110 ->
									_110110.edge(false, 'Q')
									_110110.edge(true, 'R')
								}
								_11011.branch(true).also { _110111 ->
									_110111.edge(false, 'S')
									_110111.edge(true, 'T')
								}
							}
						}
					}
					_11.branch(true).also { _111 ->
						_111.branch(false).also { _1110 ->
							_1110.branch(false).also { _11100 ->
								_11100.branch(false).also { _111000 ->
									_111000.edge(false, 'U')
									_111000.edge(true, 'V')
								}
								_11100.branch(true).also { _111001 ->
									_111001.edge(false, 'W')
									_111001.edge(true, 'Y')
								}
							}
							_1110.branch(true).also { _11101 ->
								_11101.branch(false).also { _111010 ->
									_111010.edge(false, 'j')
									_111010.edge(true, 'k')
								}
								_11101.branch(true).also { _111011 ->
									_111011.edge(false, 'q')
									_111011.edge(true, 'v')
								}
							}
						}
						_111.branch(true).also { _1111 ->
							_1111.branch(false).also { _11110 ->
								_11110.branch(false).also { _111100 ->
									_111100.edge(false, 'w')
									_111100.edge(true, 'x')
								}
								_11110.branch(true).also { _111101 ->
									_111101.edge(false, 'y')
									_111101.edge(true, 'z')
								}
							}
							_1111.branch(true).also { _11111 ->
								_11111.branch(false).also { _111110 ->
									_111110.branch(false).also { _1111100 ->
										_1111100.edge(false, '&')
										_1111100.edge(true, '*')
									}
									_111110.branch(true).also { _1111101 ->
										_1111101.edge(false, ',')
										_1111101.edge(true, ';')
									}
								}
								_11111.branch(true).also { _111111 ->
									_111111.branch(false).also { _1111110 ->
										_1111110.edge(false, 'X')
										_1111110.edge(true, 'Z')
									}
									_111111.branch(true).also { _1111111 ->
										_1111111.branch(false).also { _11111110 ->
											_11111110.branch(false).also { _111111100 ->
												_111111100.edge(false, '!')
												_111111100.edge(true, '"')
											}
											_11111110.branch(true).also { _111111101 ->
												_111111101.edge(false, '(')
												_111111101.edge(true, ')')
											}
										}
										_1111111.branch(true).also { _11111111 ->
											_11111111.branch(false).also { _111111110 ->
												_111111110.edge(false, '?')
												_111111110.branch(true).also { _1111111101 ->
													_1111111101.edge(false, '\'')
													_1111111101.edge(true, '+')
												}
											}
											_11111111.branch(true).also { _111111111 ->
												_111111111.branch(false).also { _1111111110 ->
													_1111111110.edge(false, '|')
													_1111111110.branch(true).also { _11111111101 ->
														_11111111101.edge(false, '#')
														_11111111101.edge(true, '>')
													}
												}
												_111111111.branch(true).also { _1111111111 ->
													_1111111111.branch(false).also { _11111111110 ->
														_11111111110.branch(false).also { _111111111100 ->
															_111111111100.edge(false, '\u0000')
															_111111111100.edge(true, '$')
														}
														_11111111110.branch(true).also { _111111111101 ->
															_111111111101.edge(false, '@')
															_111111111101.edge(true, '[')
														}
													}
													_1111111111.branch(true).also { _11111111111 ->
														_11111111111.branch(false).also { _111111111110 ->
															_111111111110.edge(false, ']')
															_111111111110.edge(true, '~')
														}
														_11111111111.branch(true).also { _111111111111 ->
															_111111111111.branch(false).also { _1111111111110 ->
																_1111111111110.edge(false, '^')
																_1111111111110.edge(true, '}')
															}
															_111111111111.branch(true).also { _1111111111111 ->
																ColoredHandler.newLoggerResourced("http2header")
																	.severe("TODO _1111111111111 branch")
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		val staticHeaders: Map<Int, Pair<String, String?>> = mapOf<Int, Pair<String, String?>>(
			1 to (":authority" to null),
			2 to (":method" to "GET"),
			4 to (":path" to "/"),
			7 to (":scheme" to "https"),
			16 to ("accept-encoding" to null),
			17 to ("accept-language" to null),
			19 to ("accept" to null),
			28 to ("content-length" to null),
			29 to ("content-location" to null),
			30 to ("content-range" to null),
			31 to ("content-type" to null),
			32 to ("cookie" to null),
			33 to ("date" to null),
			34 to ("etag" to null),
			35 to ("expect" to null),
			36 to ("expires" to null),
			37 to ("from" to null),
			38 to ("host" to null),
			51 to ("referrer" to null),
			58 to ("user-agent" to null)
		)

		fun parseHPACKInteger(x: Int, n: Int, stream: InputStream): Int {
			val mask = (0 until n).fold(0) { mask, i -> mask or (1 shl i) }
			var masked = x and mask
			if (masked == mask) {
				var position = 0
				do {
					val next = stream.read()
					masked += (next and 0b01111111) shl position
					position += 7
				} while (next and 0b10000000 != 0)
			}
			return masked
		}

		fun encodeHPACKInteger(x: Int, n: Int, out: OutputStream) {
			val maxPrefixValue = (1 shl n) - 1
			if (x < maxPrefixValue) out.write(x)
			else {
				out.write(maxPrefixValue)
				var rem = x - maxPrefixValue
				while (rem >= 128) {
					out.write((rem % 128) + 128)
					rem /= 128
				}
				out.write(rem)
			}
		}

		fun parseString(descriptor: Int, stringData: ByteArray): String {
			if (descriptor and 0b10000000 != 0) {
				var string = ""
				val bits = BitInputStream(stringData.inputStream())
				while (bits.available() > 0) string += huffmanRoot.next(bits)
				return string
			} else {
				return stringData.decodeToString()
			}
		}

		fun read(
			stream: InputStream, length: Int, flagsRaw: Int, identifier: Int,
			setDynamic: List<Pair<String, String>>
		): HTTP2HeaderFrame {
			val flags = buildList { HTTP2HeaderFrameFlag.entries.forEach { if (it.position and flagsRaw > 0) add(it) } }
			if (identifier == 0)
				throw HTTP2ProtocolError("Header frame identifier must be non-zero, got ${hex(identifier)}")
			var remainder = length
			val padding = if (flags.contains(HTTP2HeaderFrameFlag.PADDED)) {
				val padding = stream.read()
				remainder -= padding + 1
				padding
			} else 0
			val priority = if (flags.contains(HTTP2HeaderFrameFlag.PRIORITY)) {
				val depE = stream.read32ul()
				remainder -= 5
				Priority(
					depE > Int.MAX_VALUE,
					(depE and 0x7FFFFFFF).toInt(),
					stream.read()
				)
			} else null
			val block = mutableMapOf<String, String>()
			val data = stream.readNBytes(remainder).inputStream()
			var dynamic = setDynamic.toMutableList()
			var dynamicIndex = dynamic.size
			while (data.available() > 0) {
				val nextByte = data.read()
				when {
					nextByte shr 7 == 1 -> {
						val index = parseHPACKInteger(nextByte, 7, data)
						val (header, value) = if (index > 61) dynamic[index - 62] else staticHeaders.getValue(index)
						block[header] = value!!
					}

					nextByte shr 6 == 0b01 -> {
						val index = parseHPACKInteger(nextByte, 6, data)
						val blockName = if (index == 0) {
							val stringDescriptor = data.read()
							val stringData = data.readNBytes(parseHPACKInteger(stringDescriptor, 7, data))
							parseString(stringDescriptor, stringData)
						} else if (index > 61) {
							dynamic[index - 62].first
						} else staticHeaders.getValue(index).first
						val stringDescriptor = data.read()
						val stringData = data.readNBytes(parseHPACKInteger(stringDescriptor, 7, data))
						val blockValue = parseString(stringDescriptor, stringData)
						dynamic.add(dynamicIndex++, blockName to blockValue)
						block[blockName] = blockValue
					}

					nextByte shr 4 == 0 -> {
						val index = parseHPACKInteger(nextByte, 4, data)
						val stringDescriptor = data.read()
						val stringData = data.readNBytes(parseHPACKInteger(stringDescriptor, 7, data))
						val (header, _) = staticHeaders.getValue(index)
						block[header] = parseString(stringDescriptor, stringData)
					}

					nextByte shr 5 == 1 -> {
						val newSize = parseHPACKInteger(nextByte, 5, data)
						dynamic =
							if (dynamic.size > newSize) dynamic.slice(0 until newSize).toMutableList()
							else dynamic
					}

					else -> TODO("? ${nextByte.toString(2).padStart(8, '0')}")
				}
			}
			stream.skip(padding.toLong())
			return HTTP2HeaderFrame(identifier, flags, priority, block, dynamic)
		}
	}
}