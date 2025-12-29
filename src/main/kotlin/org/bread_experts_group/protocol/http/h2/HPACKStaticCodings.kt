package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.protocol.huffman.HuffmanBranch
import org.bread_experts_group.protocol.huffman.HuffmanCut
import org.bread_experts_group.protocol.huffman.HuffmanEdge

val staticTable: Map<Int, Pair<String, String>> = mapOf(
	1 to (":authority" to ""),
	2 to (":method" to "GET"),
	3 to (":method" to "POST"),
	4 to (":path" to "/"),
	5 to (":path" to "/index.html"),
	6 to (":scheme" to "http"),
	7 to (":scheme" to "https"),
	8 to (":status" to "200"),
	9 to (":status" to "204"),
	10 to (":status" to "206"),
	11 to (":status" to "304"),
	12 to (":status" to "400"),
	13 to (":status" to "404"),
	14 to (":status" to "500"),
	15 to ("accept-charset" to ""),
	16 to ("accept-encoding" to "gzip, deflate"),
	17 to ("accept-language" to ""),
	18 to ("accept-ranges" to ""),
	19 to ("accept" to ""),
	20 to ("access-control-allow-origin" to ""),
	21 to ("age" to ""),
	22 to ("allow" to ""),
	23 to ("authorization" to ""),
	24 to ("cache-control" to ""),
	25 to ("content-disposition" to ""),
	26 to ("content-encoding" to ""),
	27 to ("content-language" to ""),
	28 to ("content-length" to ""),
	29 to ("content-location" to ""),
	30 to ("content-range" to ""),
	31 to ("content-type" to ""),
	32 to ("cookie" to ""),
	33 to ("date" to ""),
	34 to ("etag" to ""),
	35 to ("expect" to ""),
	36 to ("expires" to ""),
	37 to ("from" to ""),
	38 to ("host" to ""),
	39 to ("if-match" to ""),
	40 to ("if-modified-since" to ""),
	41 to ("if-none-match" to ""),
	42 to ("if-range" to ""),
	43 to ("if-unmodified-since" to ""),
	44 to ("last-modified" to ""),
	45 to ("link" to ""),
	46 to ("location" to ""),
	47 to ("max-forwards" to ""),
	48 to ("proxy-authenticate" to ""),
	49 to ("proxy-authorization" to ""),
	50 to ("range" to ""),
	51 to ("referer" to ""),
	52 to ("refresh" to ""),
	53 to ("retry-after" to ""),
	54 to ("server" to ""),
	55 to ("set-cookie" to ""),
	56 to ("strict-transport-security" to ""),
	57 to ("transfer-encoding" to ""),
	58 to ("user-agent" to ""),
	59 to ("vary" to ""),
	60 to ("via" to ""),
	61 to ("www-authenticate" to "")
)

val huffmanCode: HuffmanBranch<Char> = HuffmanBranch(
	HuffmanBranch(
		HuffmanBranch(
			HuffmanBranch(
				HuffmanBranch(
					HuffmanEdge('0'),
					HuffmanEdge('1')
				),
				HuffmanBranch(
					HuffmanEdge('2'),
					HuffmanEdge('a')
				)
			),
			HuffmanBranch(
				HuffmanBranch(
					HuffmanEdge('c'),
					HuffmanEdge('e')
				),
				HuffmanBranch(
					HuffmanEdge('i'),
					HuffmanEdge('o')
				),
			)
		),
		HuffmanBranch(
			HuffmanBranch(
				HuffmanBranch(
					HuffmanEdge('s'),
					HuffmanEdge('t')
				),
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge(' '),
						HuffmanEdge('%')
					),
					HuffmanBranch(
						HuffmanEdge('-'),
						HuffmanEdge('.')
					)
				)
			),
			HuffmanBranch(
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('/'),
						HuffmanEdge('3')
					),
					HuffmanBranch(
						HuffmanEdge('4'),
						HuffmanEdge('5')
					)
				),
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('6'),
						HuffmanEdge('7')
					),
					HuffmanBranch(
						HuffmanEdge('8'),
						HuffmanEdge('9')
					)
				)
			)
		)
	),
	HuffmanBranch(
		HuffmanBranch(
			HuffmanBranch(
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('='),
						HuffmanEdge('A')
					),
					HuffmanBranch(
						HuffmanEdge('_'),
						HuffmanEdge('b')
					)
				),
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('d'),
						HuffmanEdge('f')
					),
					HuffmanBranch(
						HuffmanEdge('g'),
						HuffmanEdge('h')
					)
				)
			),
			HuffmanBranch(
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('l'),
						HuffmanEdge('m')
					),
					HuffmanBranch(
						HuffmanEdge('n'),
						HuffmanEdge('p')
					)
				),
				HuffmanBranch(
					HuffmanBranch(
						HuffmanEdge('r'),
						HuffmanEdge('u')
					),
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge(':'),
							HuffmanEdge('B')
						),
						HuffmanBranch(
							HuffmanEdge('C'),
							HuffmanEdge('D')
						)
					)
				)
			)
		),
		HuffmanBranch(
			HuffmanBranch(
				HuffmanBranch(
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('E'),
							HuffmanEdge('F')
						),
						HuffmanBranch(
							HuffmanEdge('G'),
							HuffmanEdge('H')
						)
					),
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('I'),
							HuffmanEdge('J')
						),
						HuffmanBranch(
							HuffmanEdge('K'),
							HuffmanEdge('L')
						)
					)
				),
				HuffmanBranch(
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('M'),
							HuffmanEdge('N')
						),
						HuffmanBranch(
							HuffmanEdge('O'),
							HuffmanEdge('P')
						)
					),
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('Q'),
							HuffmanEdge('R')
						),
						HuffmanBranch(
							HuffmanEdge('S'),
							HuffmanEdge('T')
						)
					)
				)
			),
			HuffmanBranch(
				HuffmanBranch(
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('U'),
							HuffmanEdge('V')
						),
						HuffmanBranch(
							HuffmanEdge('W'),
							HuffmanEdge('Y')
						)
					),
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('j'),
							HuffmanEdge('k')
						),
						HuffmanBranch(
							HuffmanEdge('q'),
							HuffmanEdge('v')
						)
					)
				),
				// 1111001
				HuffmanBranch(
					HuffmanBranch(
						HuffmanBranch(
							HuffmanEdge('w'),
							HuffmanEdge('x')
						),
						HuffmanCut()
					),
					HuffmanBranch(
						HuffmanCut(),
						HuffmanCut()
					)
				) // TODO: continue
			)
		)
	)
)