package org.bread_experts_group.api.secure.cryptography

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CryptographySystemTest {
	val system = CryptographySystemProvider.open()

	@Test
	fun hashSHA1() {
		val sha1 = system.get(CryptographySystemFeatures.HASHING_SHA1, false)
		sha1 += 5
		sha1 += 5
		sha1 += 5
		assertEquals("fadcda8a4bdd7644c31bcd039f3347a9a10611fa", sha1.flush().toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", sha1.flush().toHexString())
		sha1 += byteArrayOf(6, 7, 8)
		assertEquals("ed7f6eb492bc5b620c1ada04016db120086ddb69", sha1.flush().toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", sha1.flush().toHexString())
	}

	@Test
	fun hashSHA256() {
		val sha256 = system.get(CryptographySystemFeatures.HASHING_SHA256, false)
		sha256 += 5
		sha256 += 5
		sha256 += 5
		assertEquals("348fbb44967377ced7e055fcbee6c17092a342966c8f1f867f5fbf3269bbb845", sha256.flush().toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256.flush().toHexString())
		sha256 += byteArrayOf(6, 7, 8)
		assertEquals("4387f68386622af940deb007ce713c167e3b981b0bdc47576c6ea2e78b962344", sha256.flush().toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256.flush().toHexString())
	}

	@Test
	fun hashSHA384() {
		val sha384 = system.get(CryptographySystemFeatures.HASHING_SHA384, false)
		sha384 += 5
		sha384 += 5
		sha384 += 5
		assertEquals(
			"bd4f4dbcbbd41c250b25a1bf51461602055e41cf56f90f49b0c5e98009218d131004a704f7f6bd93b15a71b0063b0720",
			sha384.flush().toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			sha384.flush().toHexString()
		)
		sha384 += byteArrayOf(6, 7, 8)
		assertEquals(
			"bb6db1c670e728a04e9bdb0f7b797d6a17b408676d3266194ab8b374249391d8cdb3b3a655476a2eb03284ef6b0f35df",
			sha384.flush().toHexString()
		)
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			sha384.flush().toHexString()
		)
	}

	@Test
	fun hashSHA512() {
		val sha512 = system.get(CryptographySystemFeatures.HASHING_SHA512, false)
		sha512 += 5
		sha512 += 5
		sha512 += 5
		assertEquals(
			"cf9cca9f89a87a9c358c6bccb7734c73e6c512a2c4d4786b5c198bb01487725a31506b8a40e804496ea1" +
					"d86709926610e23698028462e79c03ab11581507961b",
			sha512.flush().toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff83" +
					"18d2877eec2f63b931bd47417a81a538327af927da3e",
			sha512.flush().toHexString()
		)
		sha512 += byteArrayOf(6, 7, 8)
		assertEquals(
			"2c03bfe3c55b0a5f94ce9f42b524729373179fc5b0f7786c34aeb3ba603252f3984f3d8136237c466afa" +
					"2e0bf38e2d292cab5d17a7d771f196fcbccbb42adb14",
			sha512.flush().toHexString()
		)
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff83" +
					"18d2877eec2f63b931bd47417a81a538327af927da3e",
			sha512.flush().toHexString()
		)
	}

	@Test
	fun hashMD5() {
		val md5 = system.get(CryptographySystemFeatures.HASHING_MD5, false)
		md5 += 5
		md5 += 5
		md5 += 5
		assertEquals("7b8eaf5c5bc7b2808304629c289ff6d9", md5.flush().toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5.flush().toHexString())
		md5 += byteArrayOf(6, 7, 8)
		assertEquals("848b404bf853b66f9d200898e4705349", md5.flush().toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5.flush().toHexString())
	}

	@Test
	fun hashMD4() {
		val md4 = system.get(CryptographySystemFeatures.HASHING_MD4, false)
		md4 += 5
		md4 += 5
		md4 += 5
		assertEquals("80a62db3ef543993700ab117ef95008f", md4.flush().toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", md4.flush().toHexString())
		md4 += byteArrayOf(6, 7, 8)
		assertEquals("a7b9864c9e83fc81bb6b734809100506", md4.flush().toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", md4.flush().toHexString())
	}

	@Test
	fun hashMD2() {
		val md2 = system.get(CryptographySystemFeatures.HASHING_MD2, false)
		md2 += 5
		md2 += 5
		md2 += 5
		assertEquals("9e6a7b881df2a6d74f9ac7c3859a2d44", md2.flush().toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", md2.flush().toHexString())
		md2 += byteArrayOf(6, 7, 8)
		assertEquals("59a9377241c6d432dd2808c505b7a5ca", md2.flush().toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", md2.flush().toHexString())
	}

	@Test
	fun hashSHA3_256() {
		val sha3256 = system.get(CryptographySystemFeatures.HASHING_SHA3_256, false)
		sha3256 += 5
		sha3256 += 5
		sha3256 += 5
		assertEquals("a0c2e56a14eab909dc50f91f03acc71bc9411dcbd05293f650f3fa1703c93e72", sha3256.flush().toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", sha3256.flush().toHexString())
		sha3256 += byteArrayOf(6, 7, 8)
		assertEquals("7d47e4577d139e80ac53b14c1d06b480188363538d50e6d6f6de5e92a05e929d", sha3256.flush().toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", sha3256.flush().toHexString())
	}

	@Test
	fun hashSHA3_384() {
		val sha3384 = system.get(CryptographySystemFeatures.HASHING_SHA3_384, false)
		sha3384 += 5
		sha3384 += 5
		sha3384 += 5
		assertEquals(
			"4a1e3317c2882cc2270c13b2896d76419039ee287a5b4ca6560c0c2aea6f3859541db4ba147401521271252c4203ba7e",
			sha3384.flush().toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			sha3384.flush().toHexString()
		)
		sha3384 += byteArrayOf(6, 7, 8)
		assertEquals(
			"99d328a1ae4b9f06f5ee80c9a459e23d1d017eb84cedb80b825fbf1476ef174e6395220a272af54f9bce73f2d6a21187",
			sha3384.flush().toHexString()
		)
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			sha3384.flush().toHexString()
		)
	}

	@Test
	fun hashSHA3_512() {
		val sha3512 = system.get(CryptographySystemFeatures.HASHING_SHA3_512, false)
		sha3512 += 5
		sha3512 += 5
		sha3512 += 5
		assertEquals(
			"71296730bc139d44e254b035063e511b104d66a3ccf457d2e00d2b1a76f717cac079e48f36d61654fd73" +
					"571fbb083a56b7c884411d6f2d4900bf731a5818bea6",
			sha3512.flush().toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3" +
					"e9402c3ac558f500199d95b6d3e301758586281dcd26",
			sha3512.flush().toHexString()
		)
		sha3512 += byteArrayOf(6, 7, 8)
		assertEquals(
			"1353198beaa7be84d5e5e6dd9e700979241eb7db9d7a16362cb5a00c129b3ad34e46d9a67f86a0315286" +
					"61097faeb4f4a5c3665d45b20e3d246a867ed1c80cb0",
			sha3512.flush().toHexString()
		)
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3" +
					"e9402c3ac558f500199d95b6d3e301758586281dcd26",
			sha3512.flush().toHexString()
		)
	}
}