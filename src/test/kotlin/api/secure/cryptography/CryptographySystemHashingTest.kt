package org.bread_experts_group.api.secure.cryptography

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CryptographySystemHashingTest {
	val system = CryptographySystemProvider.open()

	@Test
	fun hashSHA1() {
		val sha1 = system.get(CryptographySystemFeatures.HASHING_SHA1)
		sha1 += 5
		sha1 += 5
		sha1 += 5
		assertEquals("fadcda8a4bdd7644c31bcd039f3347a9a10611fa", sha1.export().toHexString())
		assertEquals("fadcda8a4bdd7644c31bcd039f3347a9a10611fa", sha1.flush().toHexString())
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", sha1.flush().toHexString())
		sha1 += byteArrayOf(6, 7, 8)
		assertEquals("ed7f6eb492bc5b620c1ada04016db120086ddb69", sha1.export().toHexString())
		sha1.reset()
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", sha1.flush().toHexString())
	}

	@Test
	fun hashSHA256() {
		val sha256 = system.get(CryptographySystemFeatures.HASHING_SHA256)
		sha256 += 5
		sha256 += 5
		sha256 += 5
		assertEquals("348fbb44967377ced7e055fcbee6c17092a342966c8f1f867f5fbf3269bbb845", sha256.flush().toHexString())
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256.flush().toHexString())
		sha256 += byteArrayOf(6, 7, 8)
		assertEquals("4387f68386622af940deb007ce713c167e3b981b0bdc47576c6ea2e78b962344", sha256.export().toHexString())
		sha256.reset()
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sha256.flush().toHexString())
	}

	@Test
	fun hashSHA384() {
		val sha384 = system.get(CryptographySystemFeatures.HASHING_SHA384)
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
			sha384.export().toHexString()
		)
		sha384.reset()
		assertEquals(
			"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
			sha384.flush().toHexString()
		)
	}

	@Test
	fun hashSHA512() {
		val sha512 = system.get(CryptographySystemFeatures.HASHING_SHA512)
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
			sha512.export().toHexString()
		)
		sha512.reset()
		assertEquals(
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff83" +
					"18d2877eec2f63b931bd47417a81a538327af927da3e",
			sha512.flush().toHexString()
		)
	}

	@Test
	fun hashMD5() {
		val md5 = system.get(CryptographySystemFeatures.HASHING_MD5)
		md5 += 5
		md5 += 5
		md5 += 5
		assertEquals("7b8eaf5c5bc7b2808304629c289ff6d9", md5.flush().toHexString())
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5.flush().toHexString())
		md5 += byteArrayOf(6, 7, 8)
		assertEquals("848b404bf853b66f9d200898e4705349", md5.export().toHexString())
		md5.reset()
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5.flush().toHexString())
	}

	@Test
	fun hashMD4() {
		val md4 = system.get(CryptographySystemFeatures.HASHING_MD4)
		md4 += 5
		md4 += 5
		md4 += 5
		assertEquals("80a62db3ef543993700ab117ef95008f", md4.flush().toHexString())
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", md4.flush().toHexString())
		md4 += byteArrayOf(6, 7, 8)
		assertEquals("a7b9864c9e83fc81bb6b734809100506", md4.export().toHexString())
		md4.reset()
		assertEquals("31d6cfe0d16ae931b73c59d7e0c089c0", md4.flush().toHexString())
	}

	@Test
	fun hashMD2() {
		val md2 = system.get(CryptographySystemFeatures.HASHING_MD2)
		md2 += 5
		md2 += 5
		md2 += 5
		assertEquals("9e6a7b881df2a6d74f9ac7c3859a2d44", md2.flush().toHexString())
		assertEquals("8350e5a3e24c153df2275c9f80692773", md2.flush().toHexString())
		md2 += byteArrayOf(6, 7, 8)
		assertEquals("59a9377241c6d432dd2808c505b7a5ca", md2.export().toHexString())
		md2.reset()
		assertEquals("8350e5a3e24c153df2275c9f80692773", md2.flush().toHexString())
	}

	@Test
	fun hashSHA3_256() {
		val sha3256 = system.get(CryptographySystemFeatures.HASHING_SHA3_256)
		sha3256 += 5
		sha3256 += 5
		sha3256 += 5
		assertEquals("a0c2e56a14eab909dc50f91f03acc71bc9411dcbd05293f650f3fa1703c93e72", sha3256.flush().toHexString())
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", sha3256.flush().toHexString())
		sha3256 += byteArrayOf(6, 7, 8)
		assertEquals("7d47e4577d139e80ac53b14c1d06b480188363538d50e6d6f6de5e92a05e929d", sha3256.export().toHexString())
		sha3256.reset()
		assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", sha3256.flush().toHexString())
	}

	@Test
	fun hashSHA3_384() {
		val sha3384 = system.get(CryptographySystemFeatures.HASHING_SHA3_384)
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
			sha3384.export().toHexString()
		)
		sha3384.reset()
		assertEquals(
			"0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004",
			sha3384.flush().toHexString()
		)
	}

	@Test
	fun hashSHA3_512() {
		val sha3512 = system.get(CryptographySystemFeatures.HASHING_SHA3_512)
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
			sha3512.export().toHexString()
		)
		sha3512.reset()
		assertEquals(
			"a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3" +
					"e9402c3ac558f500199d95b6d3e301758586281dcd26",
			sha3512.flush().toHexString()
		)
	}

	@Test
	fun hashSHAKE128() {
		val shake128 = system.get(CryptographySystemFeatures.HASHING_SHAKE128)
		shake128 += 5
		shake128 += 5
		shake128 += 5
		assertEquals("25cf107413d49574", shake128.exportX(8).toHexString())
		assertEquals("25cf107413d49574342b4d8381cde910", shake128.exportX(16).toHexString())
		assertEquals(
			"25cf107413d49574342b4d8381cde910aacb3a9b32bb3784a6467937e5ebec24",
			shake128.flushX(32).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d",
			shake128.exportX(8).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853e",
			shake128.exportX(16).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
			shake128.flushX(32).toHexString()
		)
		shake128 += byteArrayOf(6, 7, 8)
		assertEquals("8695946431946214", shake128.exportX(8).toHexString())
		assertEquals("8695946431946214f5f2ad167950bb33", shake128.exportX(16).toHexString())
		assertEquals(
			"8695946431946214f5f2ad167950bb3328f90acc92fd1c614f35b96a03769f40",
			shake128.flushX(32).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853e",
			shake128.flushX(16).toHexString()
		)
		shake128 += byteArrayOf(9, 10, 11)
		assertEquals(
			"f59818bf4183078af1a4aab9902af9159d06a07d162a56aabe49e3f22f88d0f4",
			shake128.flush().toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
			shake128.export().toHexString()
		)
		shake128 += byteArrayOf(12, 13, 14)
		assertEquals("e43fb1aab7c42336", shake128.exportIncremental(8).toHexString())
		assertEquals("33eeb2dba1733dfa", shake128.exportIncremental(8).toHexString())
		assertEquals("0c3a76b4fc992d89", shake128.exportIncremental(8).toHexString())
		assertEquals("bb922d49213b0c9c", shake128.exportIncremental(8).toHexString())
		shake128.reset()
		assertEquals("7f9c2ba4e88f827d", shake128.flushX(8).toHexString())
	}

	@Test
	fun hashSHAKE256() {
		val shake256 = system.get(CryptographySystemFeatures.HASHING_SHAKE256)
		shake256 += 5
		shake256 += 5
		shake256 += 5
		assertEquals("c849692afeb8b673", shake256.exportX(8).toHexString())
		assertEquals("c849692afeb8b673cad939705415efb0", shake256.exportX(16).toHexString())
		assertEquals(
			"c849692afeb8b673cad939705415efb0a8a1d1ac919d11a6e1d0919fb5effd19",
			shake256.flushX(32).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13",
			shake256.exportX(8).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb24",
			shake256.exportX(16).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762f",
			shake256.flushX(32).toHexString()
		)
		shake256 += byteArrayOf(6, 7, 8)
		assertEquals("4c495ff7811ce4ca", shake256.exportX(8).toHexString())
		assertEquals("4c495ff7811ce4ca41bc6d30b423a244", shake256.exportX(16).toHexString())
		assertEquals(
			"4c495ff7811ce4ca41bc6d30b423a244d89c3ce70379328bf1d7f9800f5d5e0c",
			shake256.flushX(32).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb24",
			shake256.flushX(16).toHexString()
		)
		shake256 += byteArrayOf(9, 10, 11)
		assertEquals(
			"6516e85d35d3361d5d8fef7fbce7ed4ecb4f70b5266e24a909589b27f05b11bf" +
					"c500beb8f29698bb8d219b237cfe8543bec8a84382f18e01b3baa83bd8a2a9cd",
			shake256.flush().toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762f" +
					"d75dc4ddd8c0f200cb05019d67b592f6fc821c49479ab48640292eacb3b7c4be",
			shake256.export().toHexString()
		)
		shake256 += byteArrayOf(12, 13, 14)
		assertEquals("6006239624ea0915", shake256.exportIncremental(8).toHexString())
		assertEquals("f8cea5e94da9d444", shake256.exportIncremental(8).toHexString())
		assertEquals("7487081a1617c983", shake256.exportIncremental(8).toHexString())
		assertEquals("6192e38b7c438fc6", shake256.exportIncremental(8).toHexString())
		assertEquals("b1f28f83bb134c41", shake256.exportIncremental(8).toHexString())
		assertEquals("cc21cd5daba1c9af", shake256.exportIncremental(8).toHexString())
		assertEquals("395af6e27409fbe3", shake256.exportIncremental(8).toHexString())
		assertEquals("2388422f0d54e0c0", shake256.exportIncremental(8).toHexString())
		shake256.reset()
		assertEquals("46b9dd2b0ba88d13", shake256.flushX(8).toHexString())
	}

	@Test
	fun hashCSHAKE128() {
		val cshake128 = system.get(CryptographySystemFeatures.HASHING_CSHAKE128)
		cshake128 += 5
		cshake128 += 5
		cshake128 += 5
		assertEquals("25cf107413d49574", cshake128.exportX(8).toHexString())
		assertEquals("25cf107413d49574342b4d8381cde910", cshake128.exportX(16).toHexString())
		assertEquals(
			"25cf107413d49574342b4d8381cde910aacb3a9b32bb3784a6467937e5ebec24",
			cshake128.flushX(32).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d",
			cshake128.exportX(8).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853e",
			cshake128.exportX(16).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
			cshake128.flushX(32).toHexString()
		)
		cshake128 += byteArrayOf(6, 7, 8)
		assertEquals("8695946431946214", cshake128.exportX(8).toHexString())
		assertEquals("8695946431946214f5f2ad167950bb33", cshake128.exportX(16).toHexString())
		assertEquals(
			"8695946431946214f5f2ad167950bb3328f90acc92fd1c614f35b96a03769f40",
			cshake128.flushX(32).toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853e",
			cshake128.flushX(16).toHexString()
		)
		cshake128 += byteArrayOf(9, 10, 11)
		assertEquals(
			"f59818bf4183078af1a4aab9902af9159d06a07d162a56aabe49e3f22f88d0f4",
			cshake128.flush().toHexString()
		)
		assertEquals(
			"7f9c2ba4e88f827d616045507605853ed73b8093f6efbc88eb1a6eacfa66ef26",
			cshake128.export().toHexString()
		)
		cshake128 += byteArrayOf(12, 13, 14)
		assertEquals("e43fb1aab7c42336", cshake128.exportIncremental(8).toHexString())
		assertEquals("33eeb2dba1733dfa", cshake128.exportIncremental(8).toHexString())
		assertEquals("0c3a76b4fc992d89", cshake128.exportIncremental(8).toHexString())
		assertEquals("bb922d49213b0c9c", cshake128.exportIncremental(8).toHexString())
		cshake128.reset()
		assertEquals("7f9c2ba4e88f827d", cshake128.flushX(8).toHexString())
		cshake128.setFunctionName("FUNCTION.TEST".toByteArray())
		cshake128 += "Test String N".toByteArray()
		assertEquals(
			"a4bb97a0cfe17e5d1ffd2d9263874ca3890651c978740e863a100a321423edc8",
			cshake128.flush().toHexString()
		)
		assertEquals(
			"c71f61f2ede7fa443616ac20b9123fbe362ab8af7e647d2641c5d824c01a9225",
			cshake128.flush().toHexString()
		)
		cshake128.setCustomizationString("CUSTOM.TEST".toByteArray())
		cshake128 += "Test String C".toByteArray()
		assertEquals(
			"df7ed364020b156877383a265b14e78ff1534c11310f5789825e8a80ce4b824b",
			cshake128.flush().toHexString()
		)
		assertEquals(
			"e2fa38d724ac07322b9c44a638c1f5e2aab8a507cc226f01195fc062a3ad7511",
			cshake128.flush().toHexString()
		)
	}

	@Test
	fun hashCSHAKE256() {
		val cshake256 = system.get(CryptographySystemFeatures.HASHING_CSHAKE256)
		cshake256 += 5
		cshake256 += 5
		cshake256 += 5
		assertEquals("c849692afeb8b673", cshake256.exportX(8).toHexString())
		assertEquals("c849692afeb8b673cad939705415efb0", cshake256.exportX(16).toHexString())
		assertEquals(
			"c849692afeb8b673cad939705415efb0a8a1d1ac919d11a6e1d0919fb5effd19",
			cshake256.flushX(32).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13",
			cshake256.exportX(8).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb24",
			cshake256.exportX(16).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762f",
			cshake256.flushX(32).toHexString()
		)
		cshake256 += byteArrayOf(6, 7, 8)
		assertEquals("4c495ff7811ce4ca", cshake256.exportX(8).toHexString())
		assertEquals("4c495ff7811ce4ca41bc6d30b423a244", cshake256.exportX(16).toHexString())
		assertEquals(
			"4c495ff7811ce4ca41bc6d30b423a244d89c3ce70379328bf1d7f9800f5d5e0c",
			cshake256.flushX(32).toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb24",
			cshake256.flushX(16).toHexString()
		)
		cshake256 += byteArrayOf(9, 10, 11)
		assertEquals(
			"6516e85d35d3361d5d8fef7fbce7ed4ecb4f70b5266e24a909589b27f05b11bf" +
					"c500beb8f29698bb8d219b237cfe8543bec8a84382f18e01b3baa83bd8a2a9cd",
			cshake256.flush().toHexString()
		)
		assertEquals(
			"46b9dd2b0ba88d13233b3feb743eeb243fcd52ea62b81b82b50c27646ed5762f" +
					"d75dc4ddd8c0f200cb05019d67b592f6fc821c49479ab48640292eacb3b7c4be",
			cshake256.export().toHexString()
		)
		cshake256 += byteArrayOf(12, 13, 14)
		assertEquals("6006239624ea0915", cshake256.exportIncremental(8).toHexString())
		assertEquals("f8cea5e94da9d444", cshake256.exportIncremental(8).toHexString())
		assertEquals("7487081a1617c983", cshake256.exportIncremental(8).toHexString())
		assertEquals("6192e38b7c438fc6", cshake256.exportIncremental(8).toHexString())
		assertEquals("b1f28f83bb134c41", cshake256.exportIncremental(8).toHexString())
		assertEquals("cc21cd5daba1c9af", cshake256.exportIncremental(8).toHexString())
		assertEquals("395af6e27409fbe3", cshake256.exportIncremental(8).toHexString())
		assertEquals("2388422f0d54e0c0", cshake256.exportIncremental(8).toHexString())
		cshake256.reset()
		assertEquals("46b9dd2b0ba88d13", cshake256.flushX(8).toHexString())
		cshake256.setFunctionName("FUNCTION.TEST".toByteArray())
		cshake256 += "Test String N".toByteArray()
		assertEquals(
			"f6471f44d742e0ca6f11343fd57ac5fcad4c293a8fab09958e2cf6a23bcf6a11" +
					"bab0b2ee31d6caa0ce69a66a6329f51e9d1002763be66060a82c1b8d24d68bf3",
			cshake256.flush().toHexString()
		)
		assertEquals(
			"8ead0ccec11158eddf27dd04207e46327cfac1413fec43bafcdacff80e0a543c" +
					"76e67e8a7d1a6cbf86f8515f0339e0686594054b4c681a9c61ebdd0f1db5e6ef",
			cshake256.flush().toHexString()
		)
		cshake256.setCustomizationString("CUSTOM.TEST".toByteArray())
		cshake256 += "Test String C".toByteArray()
		assertEquals(
			"078581ce38aa7bfd47714b121da2a3b92404b7898b4def2ca237cc9a6226ca1d" +
					"8440ac1c7bdf0bf2e92330b10c26e05b581e1a98f2c034c06b08f7394e665d08",
			cshake256.flush().toHexString()
		)
		assertEquals(
			"69f8662c9ac52d5b614fe0300c0c17234cf70ae49b5d9daebc9b7c5bb26508fd" +
					"cdc903dc49e5b3b3e98eff980dfce111aba6f5ea916be967e9212d0903069e07",
			cshake256.flush().toHexString()
		)
	}
}