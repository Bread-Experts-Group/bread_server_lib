package org.bread_experts_group.generic.protocol.http.h2

import org.bread_experts_group.generic.protocol.huffman.HuffmanBranch
import org.bread_experts_group.generic.protocol.huffman.HuffmanCut
import org.bread_experts_group.generic.protocol.huffman.HuffmanEdge

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

val huffmanCode: org.bread_experts_group.generic.protocol.huffman.HuffmanBranch<Char> =
	_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
		_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
			_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('0'),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('1')
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('2'),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('a')
					)
				),
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('c'),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('e')
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('i'),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('o')
					),
				)
			),
			_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('s'),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('t')
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge(' '),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('%')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('-'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('.')
						)
					)
				),
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('/'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('3')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('4'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('5')
						)
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('6'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('7')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('8'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('9')
						)
					)
				)
			)
		),
		_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
			_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('='),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('A')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('_'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('b')
						)
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('d'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('f')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('g'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('h')
						)
					)
				),
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('l'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('m')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('n'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('p')
						)
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('r'),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('u')
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge(':'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('B')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('C'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('D')
							)
						)
					)
				)
			),
			_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('E'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('F')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('G'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('H')
							)
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('I'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('J')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('K'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('L')
							)
						)
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('M'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('N')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('O'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('P')
							)
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('Q'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('R')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('S'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('T')
							)
						)
					)
				),
				_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('U'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('V')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('W'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('Y')
							)
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('j'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('k')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('q'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('v')
							)
						)
					),
					_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('w'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('x')
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('y'),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge('z')
							)
						),
						_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanCut(),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
									_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge(','),
									_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanEdge(';')
								)
							),
							_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanCut(),
								_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
									_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanCut(),
									_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanBranch(
										_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanCut(),
										_root_ide_package_.org.bread_experts_group.generic.protocol.huffman.HuffmanCut()
									)
								)
							)
						)
					) // TODO: continue
				)
			)
		)
	)