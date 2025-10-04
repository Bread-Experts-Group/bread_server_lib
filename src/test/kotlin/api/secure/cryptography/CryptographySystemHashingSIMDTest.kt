package org.bread_experts_group.api.secure.cryptography

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CryptographySystemHashingSIMDTest {
	val system = CryptographySystemProvider.open()

	@Test
	fun hashSHA1_SIMD() {
		val sha1s = system.get(CryptographySystemFeatures.HASHING_SHA1_SIMD)
		sha1s.start(8)
		sha1s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha1s.flush()
		assertEquals(
			"bf8b4530d8d246dd74ac53a13471bba17941dff7",
			computed[0].toHexString()
		)
		assertEquals(
			"c4ea21bb365bbeeaf5f2c654883e56d11e43c44e",
			computed[1].toHexString()
		)
		assertEquals(
			"9842926af7ca0a8cca12604f945414f07b01e13d",
			computed[2].toHexString()
		)
		assertEquals(
			"a42c6cf1de3abfdea9b95f34687cbbe92b9a7383",
			computed[3].toHexString()
		)
		assertEquals(
			"8dc00598417d4eb788a77ac6ccef3cb484905d8b",
			computed[4].toHexString()
		)
		assertEquals(
			"2d0134ed3b9de132c720fe697b532b4c232ff9fe",
			computed[5].toHexString()
		)
		assertEquals(
			"5d1be7e9dda1ee8896be5b7e34a85ee16452a7b4",
			computed[6].toHexString()
		)
		assertEquals(
			"8d883f1577ca8c334b7c6d75ccb71209d71ced13",
			computed[7].toHexString()
		)
		sha1s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha1s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"da39a3ee5e6b4b0d3255bfef95601890afd80709",
				it.toHexString()
			)
		}
		val computedEven = sha1s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"bf8b4530d8d246dd74ac53a13471bba17941dff7",
			computedEven[0].toHexString()
		)
		assertEquals(
			"c4ea21bb365bbeeaf5f2c654883e56d11e43c44e",
			computedEven[1].toHexString()
		)
		assertEquals(
			"9842926af7ca0a8cca12604f945414f07b01e13d",
			computedEven[2].toHexString()
		)
		assertEquals(
			"a42c6cf1de3abfdea9b95f34687cbbe92b9a7383",
			computedEven[3].toHexString()
		)
		sha1s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha1s.flush()
		assertEquals("7037807198c22a7d2b0807371d763779a84fdfcf", computed3s[0].toHexString())
		assertEquals("e809c5d1cea47b45e34701d23f608a9a58034dc9", computed3s[1].toHexString())
		assertEquals("b470cf972a0d84fbaeeedb51a963a902269417e8", computed3s[2].toHexString())
		assertEquals("be99d8769b726224b8042344b400ae2f5df5680e", computed3s[3].toHexString())
		assertEquals("82439619fb7525244413a5e1f5f2f9eaacbebbf5", computed3s[4].toHexString())
		assertEquals("c4c10d5feda1342a1c5a81354e2983f35cc2841a", computed3s[5].toHexString())
		assertEquals("c0c94b317b32f6f7bd0072a82e3b480d45b29585", computed3s[6].toHexString())
		assertEquals("0b84bcd1c2a38932629248f1e0557ef991a4c773", computed3s[7].toHexString())
		sha1s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha1s.flush()
		assertEquals("7037807198c22a7d2b0807371d763779a84fdfcf", computed3Es[0].toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", computed3Es[1].toHexString())
		assertEquals("e809c5d1cea47b45e34701d23f608a9a58034dc9", computed3Es[2].toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", computed3Es[3].toHexString())
		assertEquals("b470cf972a0d84fbaeeedb51a963a902269417e8", computed3Es[4].toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", computed3Es[5].toHexString())
		assertEquals("be99d8769b726224b8042344b400ae2f5df5680e", computed3Es[6].toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", computed3Es[7].toHexString())
	}

	@Test
	fun hashSHA256_SIMD() {
		val sha256s = system.get(CryptographySystemFeatures.HASHING_SHA256_SIMD)
		sha256s.start(8)
		sha256s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha256s.flush()
		assertEquals(
			"4bf5122f344554c53bde2ebb8cd2b7e3d1600ad631c385a5d7cce23c7785459a",
			computed[0].toHexString()
		)
		assertEquals(
			"dbc1b4c900ffe48d575b5da5c638040125f65db0fe3e24494b76ea986457d986",
			computed[1].toHexString()
		)
		assertEquals(
			"084fed08b978af4d7d196a7446a86b58009e636b611db16211b65a9aadff29c5",
			computed[2].toHexString()
		)
		assertEquals(
			"e52d9c508c502347344d8c07ad91cbd6068afc75ff6292f062a09ca381c89e71",
			computed[3].toHexString()
		)
		assertEquals(
			"e77b9a9ae9e30b0dbdb6f510a264ef9de781501d7b6b92ae89eb059c5ab743db",
			computed[4].toHexString()
		)
		assertEquals(
			"67586e98fad27da0b9968bc039a1ef34c939b9b8e523a8bef89d478608c5ecf6",
			computed[5].toHexString()
		)
		assertEquals(
			"ca358758f6d27e6cf45272937977a748fd88391db679ceda7dc7bf1f005ee879",
			computed[6].toHexString()
		)
		assertEquals(
			"beead77994cf573341ec17b58bbf7eb34d2711c993c1d976b128b3188dc1829a",
			computed[7].toHexString()
		)
		sha256s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha256s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				it.toHexString()
			)
		}
		val computedEven = sha256s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"4bf5122f344554c53bde2ebb8cd2b7e3d1600ad631c385a5d7cce23c7785459a",
			computedEven[0].toHexString()
		)
		assertEquals(
			"dbc1b4c900ffe48d575b5da5c638040125f65db0fe3e24494b76ea986457d986",
			computedEven[1].toHexString()
		)
		assertEquals(
			"084fed08b978af4d7d196a7446a86b58009e636b611db16211b65a9aadff29c5",
			computedEven[2].toHexString()
		)
		assertEquals(
			"e52d9c508c502347344d8c07ad91cbd6068afc75ff6292f062a09ca381c89e71",
			computedEven[3].toHexString()
		)
		sha256s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha256s.flush()
		assertEquals("039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81", computed3s[0].toHexString())
		assertEquals("787c798e39a5bc1910355bae6d0cd87a36b2e10fd0202a83e3bb6b005da83472", computed3s[1].toHexString())
		assertEquals("66a6757151f8ee55db127716c7e3dce0be8074b64e20eda542e5c1e46ca9c41e", computed3s[2].toHexString())
		assertEquals("9909ec831e2cf6d0c73fb5480f31945a80987a13faee005704166cb53a26ceca", computed3s[3].toHexString())
		assertEquals("d41470f8fe2547d6c4d4802d484fe7ff5a5bbecd5612eeeb1360df0c4781d95e", computed3s[4].toHexString())
		assertEquals("29076ae7ed6040c5991a47529aa02eb702578c4c6da21f3b975574154b0ee303", computed3s[5].toHexString())
		assertEquals("88d5bbad1571bffc781cf587ee121e8b228da92997a0e16688ecc67a90ef41f4", computed3s[6].toHexString())
		assertEquals("46b65dc5efbeec520a24c7869052752b1e1be231acac65d791272d604eb6f418", computed3s[7].toHexString())
		sha256s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha256s.flush()
		assertEquals("039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81", computed3Es[0].toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", computed3Es[1].toHexString())
		assertEquals("787c798e39a5bc1910355bae6d0cd87a36b2e10fd0202a83e3bb6b005da83472", computed3Es[2].toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", computed3Es[3].toHexString())
		assertEquals("66a6757151f8ee55db127716c7e3dce0be8074b64e20eda542e5c1e46ca9c41e", computed3Es[4].toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", computed3Es[5].toHexString())
		assertEquals("9909ec831e2cf6d0c73fb5480f31945a80987a13faee005704166cb53a26ceca", computed3Es[6].toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", computed3Es[7].toHexString())
	}

	@Test
	fun hashSHA384_SIMD() {
		val sha384s = system.get(CryptographySystemFeatures.HASHING_SHA384_SIMD)
		sha384s.start(8)
		sha384s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha384s.flush()
		assertEquals(
			"8d2ce87d86f55fcfab770a047b090da23270fa206832dfea7e0c946fff451f819add242374be551b0d6318ed6c7d41d8",
			computed[0].toHexString()
		)
		assertEquals(
			"db475240477c5e4497a2c5724ca485f5b1f2c2fc0602b92bae234238ec8d4e873a7148c739593e95a7f4dfe7c6e69f69",
			computed[1].toHexString()
		)
		assertEquals(
			"8a6c69af6fb6247635f837958446fb8f10e39bd5fbc244f7e635176339a3be614f6394247f01dbe1126c178c7bd48cb5",
			computed[2].toHexString()
		)
		assertEquals(
			"e912b65ebb17b1c5d5e13fc7679cbcc56d75d9ec59eac3030186c199bd68b99c7272830c3348edfe8130ea3c02c33fcb",
			computed[3].toHexString()
		)
		assertEquals(
			"598d26bee77a583a7a57bf1cbfc861fb9d8542dde4a868ada4aff5d0fb93aab41de37cad129159412ffcacdcef933272",
			computed[4].toHexString()
		)
		assertEquals(
			"ad0afad02fa57cb93ce03006a67d1f58522f3a59f253edb7ba5abbd747226490df0c6ab7973a244a1de1c52ec3298224",
			computed[5].toHexString()
		)
		assertEquals(
			"f12be95011fa37de81bf76bee4e73a992f431a7c69b60d2495c7383ae0545569842a0c019d89c208ae48f843562b56ff",
			computed[6].toHexString()
		)
		assertEquals(
			"1b31f827313ecd2099362d15ed22c4af62defc0fafa8d944fef85167c1af0cfc76c7083941891b136200d0779019b57a",
			computed[7].toHexString()
		)
		sha384s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha384s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
				it.toHexString()
			)
		}
		val computedEven = sha384s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"8d2ce87d86f55fcfab770a047b090da23270fa206832dfea7e0c946fff451f819add242374be551b0d6318ed6c7d41d8",
			computedEven[0].toHexString()
		)
		assertEquals(
			"db475240477c5e4497a2c5724ca485f5b1f2c2fc0602b92bae234238ec8d4e873a7148c739593e95a7f4dfe7c6e69f69",
			computedEven[1].toHexString()
		)
		assertEquals(
			"8a6c69af6fb6247635f837958446fb8f10e39bd5fbc244f7e635176339a3be614f6394247f01dbe1126c178c7bd48cb5",
			computedEven[2].toHexString()
		)
		assertEquals(
			"e912b65ebb17b1c5d5e13fc7679cbcc56d75d9ec59eac3030186c199bd68b99c7272830c3348edfe8130ea3c02c33fcb",
			computedEven[3].toHexString()
		)
		sha384s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha384s.flush()
		assertEquals(
			"86229dc6d2ffbeac7380744154aa700291c064352a0dbdc77b9ed3f2c8e1dac4dc325867d39ddff1d2629b7a393d47f6",
			computed3s[0].toHexString()
		)
		assertEquals(
			"51e39a81c1f161fa7a364446e6884498607e221e0b1728fc171c088037fbc90f488a80034aaa3e845454c309f1b9475d",
			computed3s[1].toHexString()
		)
		assertEquals(
			"26831d72e30ddb0f00d391a4a2e777df28a5187c369b66c3a0a0d6199644e989e5106325ca47f94b52e450af17e03e37",
			computed3s[2].toHexString()
		)
		assertEquals(
			"f2139d42a0af65a07cf5a6737a068744c573082194083fe47e38a9c540c28a7aea66a4710dd7fdb1ac63572346b57363",
			computed3s[3].toHexString()
		)
		assertEquals(
			"6c92232a06aec5620c66e4c636668be622643f891dc60cae989980f9e5dae2e75b481099e4ad67268fb83a394895fbc5",
			computed3s[4].toHexString()
		)
		assertEquals(
			"7a34383d2ac79c4f53dc7d2a090f0c5031339663721aa9cfe50e251135af704c90b67344f082cbfb2c440b2a2163e72e",
			computed3s[5].toHexString()
		)
		assertEquals(
			"f843676d7a8e78c5a5495d6ba8299541cd35c84207d79f976b362fcad0deb182b99b5b5c306e9be24bda317ad9e36dce",
			computed3s[6].toHexString()
		)
		assertEquals(
			"6c13082c3a12e472525f5abb7c2df0a90f35e2f13afde8564ef0110c38a49dae9bfdf2a3da297b4ec997b8d74a37370f",
			computed3s[7].toHexString()
		)
		sha384s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha384s.flush()
		assertEquals(
			"86229dc6d2ffbeac7380744154aa700291c064352a0dbdc77b9ed3f2c8e1dac4dc325867d39ddff1d2629b7a393d47f6",
			computed3Es[0].toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			computed3Es[1].toHexString()
		)
		assertEquals(
			"51e39a81c1f161fa7a364446e6884498607e221e0b1728fc171c088037fbc90f488a80034aaa3e845454c309f1b9475d",
			computed3Es[2].toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			computed3Es[3].toHexString()
		)
		assertEquals(
			"26831d72e30ddb0f00d391a4a2e777df28a5187c369b66c3a0a0d6199644e989e5106325ca47f94b52e450af17e03e37",
			computed3Es[4].toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			computed3Es[5].toHexString()
		)
		assertEquals(
			"f2139d42a0af65a07cf5a6737a068744c573082194083fe47e38a9c540c28a7aea66a4710dd7fdb1ac63572346b57363",
			computed3Es[6].toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			computed3Es[7].toHexString()
		)
	}

	@Test
	fun hashSHA512_SIMD() {
		val sha512s = system.get(CryptographySystemFeatures.HASHING_SHA512_SIMD)
		sha512s.start(8)
		sha512s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha512s.flush()
		assertEquals(
			"7b54b66836c1fbdd13d2441d9e1434dc62ca677fb68f5fe66a464baadecdbd00" +
					"576f8d6b5ac3bcc80844b7d50b1cc6603444bbe7cfcf8fc0aa1ee3c636d9e339",
			computed[0].toHexString()
		)
		assertEquals(
			"fab848c9b657a853ee37c09cbfdd149d0b3807b191dde9b623ccd95281dd1870" +
					"5b48c89b1503903845bba5753945351fe6b454852760f73529cf01ca8f69dcca",
			computed[1].toHexString()
		)
		assertEquals(
			"e45bf5817ddf94aa2f7a407071f0eedc6beb98f768b4cd33d1176d44d1563a45" +
					"a5d7212290eb7670c6786b13591aedac86478993895e8b24e612014abaa6ba04",
			computed[2].toHexString()
		)
		assertEquals(
			"b5b8c725507b5b13158e020d96fe4cfbf6d774e09161e2b599b8f35ae31f16e3" +
					"95825edef8aa69ad304ef80fed9baa0580d247cd84e57a2ae239aec90d2d5869",
			computed[3].toHexString()
		)
		assertEquals(
			"095f3e448a55fa2c3c7049c01dcf12fb87c44477cf367993132ca74c92a27a1e" +
					"aaa341c2fd5724ea449cec2728547e80edbb7e6029891c5ffd157e283ebb41e5",
			computed[4].toHexString()
		)
		assertEquals(
			"5815087df95b7c04fcf3e89c3cbee8fc10bd15a4b269f2919709167d320384b1" +
					"5c41b6b75e978ab0d4668b6c77aa7f1cae243f69b54fc970bca3726080f49535",
			computed[5].toHexString()
		)
		assertEquals(
			"365d11a1dfe610b60efa996136d37ab8afd2715b8c6bc2850dc5e6005b702bb9" +
					"f59b0f306ecb2c43ee44c429967d45843524eb2f7c16aab9bde142ee268b51c6",
			computed[6].toHexString()
		)
		assertEquals(
			"f65a6bf8f40b01b87757cde53483d057e1442f3bd67d495d2047b7f7c329e057" +
					"2e88c18808426706af3b8df2915ca3d527ad49597f211cf89e475a07c901312b",
			computed[7].toHexString()
		)
		sha512s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha512s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
						"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
				it.toHexString()
			)
		}
		val computedEven = sha512s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"7b54b66836c1fbdd13d2441d9e1434dc62ca677fb68f5fe66a464baadecdbd00" +
					"576f8d6b5ac3bcc80844b7d50b1cc6603444bbe7cfcf8fc0aa1ee3c636d9e339",
			computedEven[0].toHexString()
		)
		assertEquals(
			"fab848c9b657a853ee37c09cbfdd149d0b3807b191dde9b623ccd95281dd1870" +
					"5b48c89b1503903845bba5753945351fe6b454852760f73529cf01ca8f69dcca",
			computedEven[1].toHexString()
		)
		assertEquals(
			"e45bf5817ddf94aa2f7a407071f0eedc6beb98f768b4cd33d1176d44d1563a45" +
					"a5d7212290eb7670c6786b13591aedac86478993895e8b24e612014abaa6ba04",
			computedEven[2].toHexString()
		)
		assertEquals(
			"b5b8c725507b5b13158e020d96fe4cfbf6d774e09161e2b599b8f35ae31f16e3" +
					"95825edef8aa69ad304ef80fed9baa0580d247cd84e57a2ae239aec90d2d5869",
			computedEven[3].toHexString()
		)
		sha512s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha512s.flush()
		assertEquals(
			"27864cc5219a951a7a6e52b8c8dddf6981d098da1658d96258c870b2c88dfbcb" +
					"51841aea172a28bafa6a79731165584677066045c959ed0f9929688d04defc29",
			computed3s[0].toHexString()
		)
		assertEquals(
			"b3ceb982c0be892f21a162ffdf46c343acf1b1da2d5af2622e5787e0e7e719ea" +
					"65d3525251482eff6027377e750f61d530951c25a17dbba9c88b031105e30d43",
			computed3s[1].toHexString()
		)
		assertEquals(
			"bbc28135a52ff273b0ee175977e0ddb5628ab73fb195b299708978384df6b3d4" +
					"4bbe7b042e0c73ca766c350c056ce354cdd6803823d1db559bde99ba033b19db",
			computed3s[2].toHexString()
		)
		assertEquals(
			"6e293c3ec41f7d3b364420710e527fbed74953e41a63d4bbee10102cc181f0d6" +
					"4743c03a5591d869c1204c02205d7998c64249f2f50842cc435b86753afc160f",
			computed3s[3].toHexString()
		)
		assertEquals(
			"799b14a2f3894756dff3cde6ade3de926eca0c047d1b0b8a4fe7776e38f33b85" +
					"22d81a51855ce5554a02a8eba6ee9d448eadb6a136efb2e56bdca5d0d66f6427",
			computed3s[4].toHexString()
		)
		assertEquals(
			"b41e8e6fa64c6271f78f209d03959ebd0118824bd4502e64a0c8b830c1d5f7a1" +
					"c586f543f82374f2fc27a2645009a64c71099d576994cbf8cd86cc296153151a",
			computed3s[5].toHexString()
		)
		assertEquals(
			"9615bdb03df9c3fff8e097195a0d40b6f568f6f39a864987d0c31d457d1d6388" +
					"a39b60530b6d61364c9190774042ff69f458fe312a826355aa1184fc74a24759",
			computed3s[6].toHexString()
		)
		assertEquals(
			"862cb321167f4b35548eec7f643c4f45250819de92eba9d8a90a119bfc1ac295" +
					"fcf1261781b82795d7ac9f1f8eb645c74e2098cf19139c592d6b64927ffb4e38",
			computed3s[7].toHexString()
		)
		sha512s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha512s.flush()
		assertEquals(
			"27864cc5219a951a7a6e52b8c8dddf6981d098da1658d96258c870b2c88dfbcb" +
					"51841aea172a28bafa6a79731165584677066045c959ed0f9929688d04defc29",
			computed3Es[0].toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
					"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
			computed3Es[1].toHexString()
		)
		assertEquals(
			"b3ceb982c0be892f21a162ffdf46c343acf1b1da2d5af2622e5787e0e7e719ea" +
					"65d3525251482eff6027377e750f61d530951c25a17dbba9c88b031105e30d43",
			computed3Es[2].toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
					"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
			computed3Es[3].toHexString()
		)
		assertEquals(
			"bbc28135a52ff273b0ee175977e0ddb5628ab73fb195b299708978384df6b3d4" +
					"4bbe7b042e0c73ca766c350c056ce354cdd6803823d1db559bde99ba033b19db",
			computed3Es[4].toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
					"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
			computed3Es[5].toHexString()
		)
		assertEquals(
			"6e293c3ec41f7d3b364420710e527fbed74953e41a63d4bbee10102cc181f0d6" +
					"4743c03a5591d869c1204c02205d7998c64249f2f50842cc435b86753afc160f",
			computed3Es[6].toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
					"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
			computed3Es[7].toHexString()
		)
	}

	@Test
	fun hashMD5_SIMD() {
		val md5s = system.get(CryptographySystemFeatures.HASHING_MD5_SIMD)
		md5s.start(8)
		md5s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = md5s.flush()
		assertEquals(
			"55a54008ad1ba589aa210d2629c1df41",
			computed[0].toHexString()
		)
		assertEquals(
			"9e688c58a5487b8eaf69c9e1005ad0bf",
			computed[1].toHexString()
		)
		assertEquals(
			"8666683506aacd900bbd5a74ac4edf68",
			computed[2].toHexString()
		)
		assertEquals(
			"ec7f7e7bb43742ce868145f71d37b53c",
			computed[3].toHexString()
		)
		assertEquals(
			"8bb6c17838643f9691cc6a4de6c51709",
			computed[4].toHexString()
		)
		assertEquals(
			"06eca1b437c7904cc3ce6546c8110110",
			computed[5].toHexString()
		)
		assertEquals(
			"89e74e640b8c46257a29de0616794d5d",
			computed[6].toHexString()
		)
		assertEquals(
			"e2ba905bf306f46faca223d3cb20e2cf",
			computed[7].toHexString()
		)
		md5s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		md5s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"d41d8cd98f00b204e9800998ecf8427e",
				it.toHexString()
			)
		}
		val computedEven = md5s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"55a54008ad1ba589aa210d2629c1df41",
			computedEven[0].toHexString()
		)
		assertEquals(
			"9e688c58a5487b8eaf69c9e1005ad0bf",
			computedEven[1].toHexString()
		)
		assertEquals(
			"8666683506aacd900bbd5a74ac4edf68",
			computedEven[2].toHexString()
		)
		assertEquals(
			"ec7f7e7bb43742ce868145f71d37b53c",
			computedEven[3].toHexString()
		)
		md5s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = md5s.flush()
		assertEquals("5289df737df57326fcdd22597afb1fac", computed3s[0].toHexString())
		assertEquals("b4a3ba90641372b4e4eaa841a5a400ec", computed3s[1].toHexString())
		assertEquals("591e40ba188fb59539b7cbff99f58215", computed3s[2].toHexString())
		assertEquals("94263e4d553bcec128704e354b659526", computed3s[3].toHexString())
		assertEquals("5bf0da0276f81218e5e0d7ffe823f656", computed3s[4].toHexString())
		assertEquals("992bec173c9973219011521508135516", computed3s[5].toHexString())
		assertEquals("60b51952b0b3dcfa3d72c19bef8aab03", computed3s[6].toHexString())
		assertEquals("2dddb45a6bb73dcc8bcf239e7b0d5b5b", computed3s[7].toHexString())
		md5s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = md5s.flush()
		assertEquals("5289df737df57326fcdd22597afb1fac", computed3Es[0].toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", computed3Es[1].toHexString())
		assertEquals("b4a3ba90641372b4e4eaa841a5a400ec", computed3Es[2].toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", computed3Es[3].toHexString())
		assertEquals("591e40ba188fb59539b7cbff99f58215", computed3Es[4].toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", computed3Es[5].toHexString())
		assertEquals("94263e4d553bcec128704e354b659526", computed3Es[6].toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", computed3Es[7].toHexString())
	}

	@Test
	fun hashMD4_SIMD() {
		val md4s = system.get(CryptographySystemFeatures.HASHING_MD4_SIMD)
		md4s.start(8)
		md4s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = md4s.flush()
		assertEquals(
			"33f9a9fbd93ecca018b0387a4a6aec5d",
			computed[0].toHexString()
		)
		assertEquals(
			"57259282e40ad5322dc5091e2be438d7",
			computed[1].toHexString()
		)
		assertEquals(
			"7a71b83fcd0caf6fd77de7573b0d24ab",
			computed[2].toHexString()
		)
		assertEquals(
			"ac9cc00b3b00b46427d18a37802e8911",
			computed[3].toHexString()
		)
		assertEquals(
			"f88aa7431c1f7d01ed171c1da53995ba",
			computed[4].toHexString()
		)
		assertEquals(
			"3c641dfee21ffdfed29bdd96dd39c1e3",
			computed[5].toHexString()
		)
		assertEquals(
			"a8deec165ed8ad822e82bc0563574d68",
			computed[6].toHexString()
		)
		assertEquals(
			"d46bebec1ebe56d7ee1dc048d63d49ad",
			computed[7].toHexString()
		)
		md4s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		md4s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"31d6cfe0d16ae931b73c59d7e0c089c0",
				it.toHexString()
			)
		}
		val computedEven = md4s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"33f9a9fbd93ecca018b0387a4a6aec5d",
			computedEven[0].toHexString()
		)
		assertEquals(
			"57259282e40ad5322dc5091e2be438d7",
			computedEven[1].toHexString()
		)
		assertEquals(
			"7a71b83fcd0caf6fd77de7573b0d24ab",
			computedEven[2].toHexString()
		)
		assertEquals(
			"ac9cc00b3b00b46427d18a37802e8911",
			computedEven[3].toHexString()
		)
		md4s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = md4s.flush()
		assertEquals("368913defe31dd44d76dfeaace14bb85", computed3s[0].toHexString())
		assertEquals("6fa6092eb6405c3e095f2c96226b5643", computed3s[1].toHexString())
		assertEquals("541935a6f896d7dd5c1d37ab2a2743bf", computed3s[2].toHexString())
		assertEquals("ac9551efef560721e6c0c2393c091d18", computed3s[3].toHexString())
		assertEquals("52824a28263bd99885a036d82f0ca2a5", computed3s[4].toHexString())
		assertEquals("2e89e85f4f944d9fe560ca6cd380fe5b", computed3s[5].toHexString())
		assertEquals("c289b012e5597271ebd7a9ec8d316376", computed3s[6].toHexString())
		assertEquals("f714a09a44cdfb3031313235bb85ac27", computed3s[7].toHexString())
		md4s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = md4s.flush()
		assertEquals("368913defe31dd44d76dfeaace14bb85", computed3Es[0].toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", computed3Es[1].toHexString())
		assertEquals("6fa6092eb6405c3e095f2c96226b5643", computed3Es[2].toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", computed3Es[3].toHexString())
		assertEquals("541935a6f896d7dd5c1d37ab2a2743bf", computed3Es[4].toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", computed3Es[5].toHexString())
		assertEquals("ac9551efef560721e6c0c2393c091d18", computed3Es[6].toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", computed3Es[7].toHexString())
	}

	@Test
	fun hashMD2_SIMD() {
		val md2s = system.get(CryptographySystemFeatures.HASHING_MD2_SIMD)
		md2s.start(8)
		md2s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = md2s.flush()
		assertEquals(
			"6156665843a7281afec331f09b0ef309",
			computed[0].toHexString()
		)
		assertEquals(
			"49afe10ec59d48cdab9c31d00f0bb512",
			computed[1].toHexString()
		)
		assertEquals(
			"7786e8fa2729c69ded2f7c20bcd052f2",
			computed[2].toHexString()
		)
		assertEquals(
			"573fb4cb27c095bff95d3633a29d0857",
			computed[3].toHexString()
		)
		assertEquals(
			"ce2040aecbdb66f1d60ee62a6e180dcc",
			computed[4].toHexString()
		)
		assertEquals(
			"4dc0d0f1bdaa4b1969ceb793103856b3",
			computed[5].toHexString()
		)
		assertEquals(
			"981172770eb597b342bad018d46eca0a",
			computed[6].toHexString()
		)
		assertEquals(
			"3d09f439ea6c00469ac3198b5712defb",
			computed[7].toHexString()
		)
		md2s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		md2s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"8350e5a3e24c153df2275c9f80692773",
				it.toHexString()
			)
		}
		val computedEven = md2s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"6156665843a7281afec331f09b0ef309",
			computedEven[0].toHexString()
		)
		assertEquals(
			"49afe10ec59d48cdab9c31d00f0bb512",
			computedEven[1].toHexString()
		)
		assertEquals(
			"7786e8fa2729c69ded2f7c20bcd052f2",
			computedEven[2].toHexString()
		)
		assertEquals(
			"573fb4cb27c095bff95d3633a29d0857",
			computedEven[3].toHexString()
		)
		md2s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = md2s.flush()
		assertEquals("30bd026f5b88b4719b563bddb68917be", computed3s[0].toHexString())
		assertEquals("4b9091108150203de0db6a790ddaacc3", computed3s[1].toHexString())
		assertEquals("9f08100df4c690c41f4abbbce0180b4d", computed3s[2].toHexString())
		assertEquals("a1455723c56f2e50fd8b8bd6cf5f1649", computed3s[3].toHexString())
		assertEquals("b0bf08449bb8101324a01333f835b70d", computed3s[4].toHexString())
		assertEquals("6564dc2af466c745ccbeed575cb25fad", computed3s[5].toHexString())
		assertEquals("26eb09090df9d76f0b0bff823cea44bb", computed3s[6].toHexString())
		assertEquals("e1fdd37e06623d05c1333ac03c6022f9", computed3s[7].toHexString())
		md2s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = md2s.flush()
		assertEquals("30bd026f5b88b4719b563bddb68917be", computed3Es[0].toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", computed3Es[1].toHexString())
		assertEquals("4b9091108150203de0db6a790ddaacc3", computed3Es[2].toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", computed3Es[3].toHexString())
		assertEquals("9f08100df4c690c41f4abbbce0180b4d", computed3Es[4].toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", computed3Es[5].toHexString())
		assertEquals("a1455723c56f2e50fd8b8bd6cf5f1649", computed3Es[6].toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", computed3Es[7].toHexString())
	}

	@Test
	fun hashSHA3_256_SIMD() {
		val sha3256s = system.get(CryptographySystemFeatures.HASHING_SHA3_256_SIMD)
		sha3256s.start(8)
		sha3256s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha3256s.flush()
		assertEquals(
			"2767f15c8af2f2c7225d5273fdd683edc714110a987d1054697c348aed4e6cc7",
			computed[0].toHexString()
		)
		assertEquals(
			"0a1e2736777f80a62beb2df72b649878481c0ca10194b832b5136befbae54017",
			computed[1].toHexString()
		)
		assertEquals(
			"e3ed56bd086d8958483a12734fa0ae7f5c8bb160ef9092c67e82ed9b19e4c7b2",
			computed[2].toHexString()
		)
		assertEquals(
			"989216075a288af2c12f115557518d248f93c434965513f5f739df8c9d6e1932",
			computed[3].toHexString()
		)
		assertEquals(
			"3b0c4d506212cd7e7b88bc93b5b1811ab5de6796d2780e9de7378c87fe9a80a6",
			computed[4].toHexString()
		)
		assertEquals(
			"5a3442340ee31fa728f182f7dbaef4825025f40378061428bcc9f859aa4c294a",
			computed[5].toHexString()
		)
		assertEquals(
			"5223f7670b3b9ba04f57d477478ae77a58190d89f21da0b0be774735e23f9c96",
			computed[6].toHexString()
		)
		assertEquals(
			"04058b18052fd86b2a3032bcc55c823c48bf5810a3726f538a1d01ebb42584c5",
			computed[7].toHexString()
		)
		sha3256s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha3256s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a",
				it.toHexString()
			)
		}
		val computedEven = sha3256s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"2767f15c8af2f2c7225d5273fdd683edc714110a987d1054697c348aed4e6cc7",
			computedEven[0].toHexString()
		)
		assertEquals(
			"0a1e2736777f80a62beb2df72b649878481c0ca10194b832b5136befbae54017",
			computedEven[1].toHexString()
		)
		assertEquals(
			"e3ed56bd086d8958483a12734fa0ae7f5c8bb160ef9092c67e82ed9b19e4c7b2",
			computedEven[2].toHexString()
		)
		assertEquals(
			"989216075a288af2c12f115557518d248f93c434965513f5f739df8c9d6e1932",
			computedEven[3].toHexString()
		)
		sha3256s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha3256s.flush()
		assertEquals("fd1780a6fc9ee0dab26ceb4b3941ab03e66ccd970d1db91612c66df4515b0a0a", computed3s[0].toHexString())
		assertEquals("749150c8e285d2f5183405df8b8438f4d12f0b484b554aef612ad7145b8f495d", computed3s[1].toHexString())
		assertEquals("2fd4c360f84dd8bfffe490a86765b4b2601b400df036cfa0f54a642ddbbfe955", computed3s[2].toHexString())
		assertEquals("8273be684fad7b64944679774004ed7f496bf745a1b6e5a9ddbe4901e957dcdb", computed3s[3].toHexString())
		assertEquals("bde00aeb03a5a758d415b125c4d3208718f6122f4d590557369dfe023f68f694", computed3s[4].toHexString())
		assertEquals("b8f1b99cc8482067cdf6b4abf6f5a216a3473aad2cf2914f310f765553a5c96e", computed3s[5].toHexString())
		assertEquals("cd88202bee690a68ac198e7b8919fdcedd2c0738e529854512c626f201814e27", computed3s[6].toHexString())
		assertEquals("083cf64683610e612909a0061e8397b1958eb9c454375d8402cb561b2bbb36de", computed3s[7].toHexString())
		sha3256s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha3256s.flush()
		assertEquals("fd1780a6fc9ee0dab26ceb4b3941ab03e66ccd970d1db91612c66df4515b0a0a", computed3Es[0].toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", computed3Es[1].toHexString())
		assertEquals("749150c8e285d2f5183405df8b8438f4d12f0b484b554aef612ad7145b8f495d", computed3Es[2].toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", computed3Es[3].toHexString())
		assertEquals("2fd4c360f84dd8bfffe490a86765b4b2601b400df036cfa0f54a642ddbbfe955", computed3Es[4].toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", computed3Es[5].toHexString())
		assertEquals("8273be684fad7b64944679774004ed7f496bf745a1b6e5a9ddbe4901e957dcdb", computed3Es[6].toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", computed3Es[7].toHexString())
	}

	@Test
	fun hashSHA3_384_SIMD() {
		val sha384s = system.get(CryptographySystemFeatures.HASHING_SHA3_384_SIMD)
		sha384s.start(8)
		sha384s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha384s.flush()
		assertEquals(
			"317bd009203bc79b064e53f8eccb632e0513872c9ec5c0a69496aef89671af21ed92a3b255480c5a0976ea49258ac08e",
			computed[0].toHexString()
		)
		assertEquals(
			"2917daadd67e9db2f74de0e6a4c64e14e962a34fd9c7172bbdf28e46678a16c43b41b873a698d60d2c8dd9d85844119f",
			computed[1].toHexString()
		)
		assertEquals(
			"80fe0aa4889193dd2b9924e34d2170835a0731de3e299e70748c3906f4f0eba35d531e61e4eb318bf8bde6fe3de9436c",
			computed[2].toHexString()
		)
		assertEquals(
			"52cdcd5ea3e4cd93ea223ec8b9944f417cd976f06c58a64e9af96fa07cabfc32eedd4a501496e51b158479caf42b6afd",
			computed[3].toHexString()
		)
		assertEquals(
			"7d9ebdfe425279bdc78eb9659eddf997961f9e77673863b62016958f8f059a22a9e0faef68e75699f24b4676dcd11784",
			computed[4].toHexString()
		)
		assertEquals(
			"e43a45fff20ba84da1ca0c35b1f4173310d0cc1b63835cd560d7a1a08cce24ef787227ef8033f72a86144828dd030b6d",
			computed[5].toHexString()
		)
		assertEquals(
			"63f5763974f03d59b3305ba886d80e8872d9695ca21d3ecb1612e50ed45c3ea4b69c2a2004f5b19d57e455f6d276e0f8",
			computed[6].toHexString()
		)
		assertEquals(
			"492846c565ff3c2aee9a82482c8b00830e2df61e28f136b5d94528cdca4e6e8db2293787a02707b99da3f502694e19b4",
			computed[7].toHexString()
		)
		sha384s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha384s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
				it.toHexString()
			)
		}
		val computedEven = sha384s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"317bd009203bc79b064e53f8eccb632e0513872c9ec5c0a69496aef89671af21ed92a3b255480c5a0976ea49258ac08e",
			computedEven[0].toHexString()
		)
		assertEquals(
			"2917daadd67e9db2f74de0e6a4c64e14e962a34fd9c7172bbdf28e46678a16c43b41b873a698d60d2c8dd9d85844119f",
			computedEven[1].toHexString()
		)
		assertEquals(
			"80fe0aa4889193dd2b9924e34d2170835a0731de3e299e70748c3906f4f0eba35d531e61e4eb318bf8bde6fe3de9436c",
			computedEven[2].toHexString()
		)
		assertEquals(
			"52cdcd5ea3e4cd93ea223ec8b9944f417cd976f06c58a64e9af96fa07cabfc32eedd4a501496e51b158479caf42b6afd",
			computedEven[3].toHexString()
		)
		sha384s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha384s.flush()
		assertEquals(
			"854ec9031d7e34de34973dd41c3dd156a2cde981fff0153281b87d72b481d98cdfc0a9791d522d719e607656d7655b1a",
			computed3s[0].toHexString()
		)
		assertEquals(
			"5c3cbdd3d317429633a8cc9bfbb45874014804b9f182b6fd3425ce1d32bae539a2698607fda0638c973a272be004abcf",
			computed3s[1].toHexString()
		)
		assertEquals(
			"90b6988ac826ec11d59d2247822ac9966dbe1564d9b90f41ba8dcd89b315ad102cd6774ee427e572998c376c031072d9",
			computed3s[2].toHexString()
		)
		assertEquals(
			"e7f187900677296beb349838847c28f3c897b6e65c0878df31eecb68eeeed03064b36189774a141a448a2ed5549072d6",
			computed3s[3].toHexString()
		)
		assertEquals(
			"f1cf80f9b4b438822f813e288f289f3d44b4823ea9ab47ab5f4b59e95425d892caadce6065f93c27ec67f6bfe8ffc863",
			computed3s[4].toHexString()
		)
		assertEquals(
			"a7557e34f1845d4a0aa731ce957117acdc2590a4ddea5123694c81008c35bc18b0a0bd96bbffd529a7555f6dba610b27",
			computed3s[5].toHexString()
		)
		assertEquals(
			"8e5b16ea35e1e273b119dabeb12b50590f64e4035b700b7b6d13f07f4bf36d7c7cd8d359c8fe1fda8104c812f9a2e6a1",
			computed3s[6].toHexString()
		)
		assertEquals(
			"4a4e26e68fdb1b3286a5e910c391a3467424b4c6a4f13c0cd8e04b395477409a5210f9f728cd077739b4c5aa15f5ba19",
			computed3s[7].toHexString()
		)
		sha384s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha384s.flush()
		assertEquals(
			"854ec9031d7e34de34973dd41c3dd156a2cde981fff0153281b87d72b481d98cdfc0a9791d522d719e607656d7655b1a",
			computed3Es[0].toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			computed3Es[1].toHexString()
		)
		assertEquals(
			"5c3cbdd3d317429633a8cc9bfbb45874014804b9f182b6fd3425ce1d32bae539a2698607fda0638c973a272be004abcf",
			computed3Es[2].toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			computed3Es[3].toHexString()
		)
		assertEquals(
			"90b6988ac826ec11d59d2247822ac9966dbe1564d9b90f41ba8dcd89b315ad102cd6774ee427e572998c376c031072d9",
			computed3Es[4].toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			computed3Es[5].toHexString()
		)
		assertEquals(
			"e7f187900677296beb349838847c28f3c897b6e65c0878df31eecb68eeeed03064b36189774a141a448a2ed5549072d6",
			computed3Es[6].toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			computed3Es[7].toHexString()
		)
	}

	@Test
	fun hashSHA3_512_SIMD() {
		val sha3512s = system.get(CryptographySystemFeatures.HASHING_SHA3_512_SIMD)
		sha3512s.start(8)
		sha3512s.add(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8))
		val computed = sha3512s.flush()
		assertEquals(
			"c46af35fa4594a247543c33e52e17572f94c48d2bdc42e0a2e861b805a28820e" +
					"762a493b9d2660247198bae31ac510903c282ee224f15003cfdfaf402a19cb91",
			computed[0].toHexString()
		)
		assertEquals(
			"423b2cfd2e3645063e80920f9930e3e799bbc5a6fa193c3f16042fef6f708545" +
					"5f811a59ac0241dfb151152c9bd9ff53211ef68e3d4c1a67f0d712b42cecff93",
			computed[1].toHexString()
		)
		assertEquals(
			"a63f9712aadceb0fe16f1881719a802294830fa1f7a0e58ff26a45a814309961" +
					"c5007f43f4e224a82d3f538a6c8af7d87ec71fb6661bfac0dc0cd9367ddb04ce",
			computed[2].toHexString()
		)
		assertEquals(
			"4ac3ed73f75e62f70bee17ae617108e4fe26af1ae9a2945263d9172f5c706dbb" +
					"33079ffa340b005c1541da5dff247859e8c718ad32d9aa27a9fe7b29120c060b",
			computed[3].toHexString()
		)
		assertEquals(
			"d26e1c760118976e6d47cf667a7f901e82ff28f296d1dd582c25a930fa0b709d" +
					"208eff0c4cdfabfa9906417465abfc910f6e15465c37c16afe8ca373ea27aacb",
			computed[4].toHexString()
		)
		assertEquals(
			"d4190e712268f59c841489625c3a99eb165aaf24f176f8b4fb37092abd96d6be" +
					"d7546ac6761babe69c0f1436970f1d1a10ad3937f7b7dc208771f1a1752204ae",
			computed[5].toHexString()
		)
		assertEquals(
			"340d7ff9f80cab5032d68e7437a7aad37695f663934c54922f7f09b22be42c32" +
					"e17e6ab08abf273f9f23121120fa1660e262f8625c97419bacd73310392f8b3f",
			computed[6].toHexString()
		)
		assertEquals(
			"ed8f43bf0966dc77975fed857320e8b4a5e760ae9af419086c0fb04638a7e12e" +
					"5e33fd6c12cc6ed0cc30bcd3d28896497dd173f4d6185b5b362f2aafc3b2beee",
			computed[7].toHexString()
		)
		sha3512s.add(intArrayOf(0, 2, 4, 6), byteArrayOf(1, 2, 3, 4))
		sha3512s.flush(intArrayOf(1, 3, 5, 7)).forEach {
			assertEquals(
				"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a6" +
						"15b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
				it.toHexString()
			)
		}
		val computedEven = sha3512s.flush(intArrayOf(0, 2, 4, 6))
		assertEquals(
			"c46af35fa4594a247543c33e52e17572f94c48d2bdc42e0a2e861b805a28820e" +
					"762a493b9d2660247198bae31ac510903c282ee224f15003cfdfaf402a19cb91",
			computedEven[0].toHexString()
		)
		assertEquals(
			"423b2cfd2e3645063e80920f9930e3e799bbc5a6fa193c3f16042fef6f708545" +
					"5f811a59ac0241dfb151152c9bd9ff53211ef68e3d4c1a67f0d712b42cecff93",
			computedEven[1].toHexString()
		)
		assertEquals(
			"a63f9712aadceb0fe16f1881719a802294830fa1f7a0e58ff26a45a814309961" +
					"c5007f43f4e224a82d3f538a6c8af7d87ec71fb6661bfac0dc0cd9367ddb04ce",
			computedEven[2].toHexString()
		)
		assertEquals(
			"4ac3ed73f75e62f70bee17ae617108e4fe26af1ae9a2945263d9172f5c706dbb" +
					"33079ffa340b005c1541da5dff247859e8c718ad32d9aa27a9fe7b29120c060b",
			computedEven[3].toHexString()
		)
		sha3512s.add(
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12),
				byteArrayOf(13, 14, 15),
				byteArrayOf(16, 17, 18),
				byteArrayOf(19, 20, 21),
				byteArrayOf(22, 23, 24)
			)
		)
		val computed3s = sha3512s.flush()
		assertEquals(
			"0c60ae04fbb17fe36f4e84631a5b8f3cd6d0cd46e80056bdfec97fd305f764da" +
					"adef8ae1adc89b203043d7e2af1fb341df0ce5f66dfe3204ec3a9831532a8e4c",
			computed3s[0].toHexString()
		)
		assertEquals(
			"cbafdfd2f1c09f9e8b37ea3a80501c471d800cc9da33244260da459a9118295f" +
					"42a187379af81e7d8f6654713a5ee8f0286169267ea1987cbd4301f78991527a",
			computed3s[1].toHexString()
		)
		assertEquals(
			"cce3612a987da8a628098b4aca1767c086c94051b8dd4e06be8cef29c732010c" +
					"199a9fe057546c55067fbfa093dfcc6d688a1821d5786b8e81dfa723da291ed4",
			computed3s[2].toHexString()
		)
		assertEquals(
			"76806e07ff0795ef9a1192f8ff76ddc58d86e4b82f2b44b649ee9b7faf25ebbd" +
					"f6a92f7c1aad5a26c4b1da9b078ac53332fe094f6455a1d1493811d025688e05",
			computed3s[3].toHexString()
		)
		assertEquals(
			"8be2b6c2f2f3289408fa57bde43e206f68723f1a8704a20881514a9b8591621b" +
					"5feb1087abd383bbd7b71f4a88771d26258f3133b85aa547d44803559a0e81ef",
			computed3s[4].toHexString()
		)
		assertEquals(
			"e47c1fcc01d141a282079d21fae2d9cd67a6b8a1625412d00d0646aee864fd6e" +
					"a3a07514d7727cd9cf778e8d4b6ecfabf4b1c9337355f095b0df2c897f9fdc2c",
			computed3s[5].toHexString()
		)
		assertEquals(
			"bd9c1d6c2513b09f47f19c9a194ed2ac221d0c457bc9a37bd0c81d55fdb1c678" +
					"f97b6da210052646915f4dc28f6b643c95eff4b88316df08d98a9ca6a693695b",
			computed3s[6].toHexString()
		)
		assertEquals(
			"64c0043bf5615823f453b3a85f4bc1df10e1e5f407c76a81ac2f94c95eeec48a" +
					"43c73bf9cf679cf954ed7c700abbad479f85d8e2462d0bd9e4e175b68985acdf",
			computed3s[7].toHexString()
		)
		sha3512s.add(
			intArrayOf(
				0,
				2,
				4,
				6
			),
			arrayOf(
				byteArrayOf(1, 2, 3),
				byteArrayOf(4, 5, 6),
				byteArrayOf(7, 8, 9),
				byteArrayOf(10, 11, 12)
			)
		)
		val computed3Es = sha3512s.flush()
		assertEquals(
			"0c60ae04fbb17fe36f4e84631a5b8f3cd6d0cd46e80056bdfec97fd305f764da" +
					"adef8ae1adc89b203043d7e2af1fb341df0ce5f66dfe3204ec3a9831532a8e4c",
			computed3Es[0].toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a6" +
					"15b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
			computed3Es[1].toHexString()
		)
		assertEquals(
			"cbafdfd2f1c09f9e8b37ea3a80501c471d800cc9da33244260da459a9118295f" +
					"42a187379af81e7d8f6654713a5ee8f0286169267ea1987cbd4301f78991527a",
			computed3Es[2].toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a6" +
					"15b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
			computed3Es[3].toHexString()
		)
		assertEquals(
			"cce3612a987da8a628098b4aca1767c086c94051b8dd4e06be8cef29c732010c" +
					"199a9fe057546c55067fbfa093dfcc6d688a1821d5786b8e81dfa723da291ed4",
			computed3Es[4].toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a6" +
					"15b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
			computed3Es[5].toHexString()
		)
		assertEquals(
			"76806e07ff0795ef9a1192f8ff76ddc58d86e4b82f2b44b649ee9b7faf25ebbd" +
					"f6a92f7c1aad5a26c4b1da9b078ac53332fe094f6455a1d1493811d025688e05",
			computed3Es[6].toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a6" +
					"15b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26",
			computed3Es[7].toHexString()
		)
	}
}