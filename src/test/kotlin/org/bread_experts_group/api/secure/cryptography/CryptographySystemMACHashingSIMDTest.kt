package org.bread_experts_group.org.bread_experts_group.api.secure.cryptography

//class CryptographySystemMACHashingSIMDTest {
//	val system = CryptographySystemProvider.open()
//	val key1 = "BreadExpertsGroup".toByteArray()
//	val key2 = "BreadServerLibrary".toByteArray()

//	@Test
//	fun macSHA1_SIMD() {
// TODO: Postponed until Windows adds HMAC / SIMD
//		val sha1macS = system.get(CryptographySystemFeatures.HASHING_SHA1_HMAC_SIMD)
//		assertThrows<IllegalStateException> { sha1macS.add(byteArrayOf(1, 2, 3)) }
//		assertThrows<IllegalStateException> { sha1macS.flush() }
//		sha1macS.start(key1, 3)
//		sha1macS.add(byteArrayOf(1, 2, 3))
//		val computed13 = sha1macS.flush()
//		assertEquals("98dc84848a9a67a4ecdac437fd7794dac6bfcacb", computed13[0].toHexString())
//		assertEquals("98dc84848a9a67a4ecdac437fd7794dac6bfcacb", computed13[1].toHexString())
//		assertEquals("98dc84848a9a67a4ecdac437fd7794dac6bfcacb", computed13[2].toHexString())
//		sha1macS.start(key2, 3)
//		val computed43 = sha1macS.flush()
//		assertEquals("e34dcc8bab461b6cdc32111d2a1a752b5516cd8e", computed43[0].toHexString())
//		assertEquals("e34dcc8bab461b6cdc32111d2a1a752b5516cd8e", computed43[1].toHexString())
//		assertEquals("e34dcc8bab461b6cdc32111d2a1a752b5516cd8e", computed43[2].toHexString())
//	}

//	@Test
//	fun macSHA256() {
//		val sha256mac = system.get(CryptographySystemFeatures.HASHING_SHA256_HMAC)
//		assertThrows<IllegalStateException> { sha256mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha256mac.flush() }
//		sha256mac.setSecret(key1)
//		sha256mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"d5c9b7c22c0a5eaa5d19ec8349b49c9e14a9bbac1a0b30b614172a85e64cbf42",
//			sha256mac.flush().toHexString()
//		)
//		sha256mac.setSecret(key2)
//		sha256mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"f108010ce63ef0a6d50e63e3767f4cb6432da46af54ce992289f8776e48656c6",
//			sha256mac.export().toHexString()
//		)
//		assertEquals(
//			"f108010ce63ef0a6d50e63e3767f4cb6432da46af54ce992289f8776e48656c6",
//			sha256mac.flush().toHexString()
//		)
//	}
//
//	@Test
//	fun macSHA384() {
//		val sha384mac = system.get(CryptographySystemFeatures.HASHING_SHA384_HMAC)
//		assertThrows<IllegalStateException> { sha384mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha384mac.flush() }
//		sha384mac.setSecret(key1)
//		sha384mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"54cbdab685d3e82e97e8ffc71bb4675479db01d408b5bbed8a6193e1743f949183762be37c8cf583c05f00fc374fc1d9",
//			sha384mac.flush().toHexString()
//		)
//		sha384mac.setSecret(key2)
//		sha384mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"ae51e9613001aaf366d61791a856b0aa9366e19095b9f90cf54e8fbde5d9e802d998f6f8ea5201cce542e7d59fd3cc11",
//			sha384mac.export().toHexString()
//		)
//		assertEquals(
//			"ae51e9613001aaf366d61791a856b0aa9366e19095b9f90cf54e8fbde5d9e802d998f6f8ea5201cce542e7d59fd3cc11",
//			sha384mac.flush().toHexString()
//		)
//	}
//
//	@Test
//	fun macSHA512() {
//		val sha512mac = system.get(CryptographySystemFeatures.HASHING_SHA512_HMAC)
//		assertThrows<IllegalStateException> { sha512mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha512mac.flush() }
//		sha512mac.setSecret(key1)
//		sha512mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"6fe56420c580de4881db00feacb2c0e5c6cd9d6bfb7af7cfb941ffaaa1705b16" +
//					"91ee6bee42a875a65fa5abb5a1572ec73170f0542a82cb198c393a9b92f624f0",
//			sha512mac.flush().toHexString()
//		)
//		sha512mac.setSecret(key2)
//		sha512mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"fc39a18e88d5f6fc687860765a843577e24fbe91e49ec32f0db418b66cb18b88" +
//					"b62c552ad5051c1ccc5b349099a23855c95b0c10f9bcecc62914f1196f1d877d",
//			sha512mac.export().toHexString()
//		)
//		assertEquals(
//			"fc39a18e88d5f6fc687860765a843577e24fbe91e49ec32f0db418b66cb18b88" +
//					"b62c552ad5051c1ccc5b349099a23855c95b0c10f9bcecc62914f1196f1d877d",
//			sha512mac.flush().toHexString()
//		)
//	}
//
//	@Test
//	fun macMD2() {
//		val md2mac = system.get(CryptographySystemFeatures.HASHING_MD2_HMAC)
//		assertThrows<IllegalStateException> { md2mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { md2mac.flush() }
//		md2mac.setSecret(key1)
//		md2mac += byteArrayOf(1, 2, 3)
//		assertEquals("8fc919f4c6a7689cb886a49433746d02", md2mac.flush().toHexString())
//		md2mac.setSecret(key2)
//		md2mac += byteArrayOf(4, 5, 6)
//		assertEquals("ef91d9c3ee831682909e85cb443412a3", md2mac.export().toHexString())
//		assertEquals("ef91d9c3ee831682909e85cb443412a3", md2mac.flush().toHexString())
//	}
//
//	@Test
//	fun macMD4() {
//		val md2mac = system.get(CryptographySystemFeatures.HASHING_MD4_HMAC)
//		assertThrows<IllegalStateException> { md2mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { md2mac.flush() }
//		md2mac.setSecret(key1)
//		md2mac += byteArrayOf(1, 2, 3)
//		assertEquals("9eeb3d7f7ac095a46e9b7afd55919af4", md2mac.flush().toHexString())
//		md2mac.setSecret(key2)
//		md2mac += byteArrayOf(4, 5, 6)
//		assertEquals("2f3eabae5cf533beec40a4985f19fc7a", md2mac.export().toHexString())
//		assertEquals("2f3eabae5cf533beec40a4985f19fc7a", md2mac.flush().toHexString())
//	}
//
//	@Test
//	fun macMD5() {
//		val md2mac = system.get(CryptographySystemFeatures.HASHING_MD5_HMAC)
//		assertThrows<IllegalStateException> { md2mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { md2mac.flush() }
//		md2mac.setSecret(key1)
//		md2mac += byteArrayOf(1, 2, 3)
//		assertEquals("50b6be0863afc2ab8147ec802ecf0a29", md2mac.flush().toHexString())
//		md2mac.setSecret(key2)
//		md2mac += byteArrayOf(4, 5, 6)
//		assertEquals("20ce67148ec89abe4c1521a967fd0c5f", md2mac.export().toHexString())
//		assertEquals("20ce67148ec89abe4c1521a967fd0c5f", md2mac.flush().toHexString())
//	}
//
//	@Test
//	fun macSHA3_256() {
//		val sha3256mac = system.get(CryptographySystemFeatures.HASHING_SHA3_256_HMAC)
//		assertThrows<IllegalStateException> { sha3256mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha3256mac.flush() }
//		sha3256mac.setSecret(key1)
//		sha3256mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"feab9a7d9500607cf22e5aa9fc352b8eca8f16fb16422b807d15cdd35de85ed1",
//			sha3256mac.flush().toHexString()
//		)
//		sha3256mac.setSecret(key2)
//		sha3256mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"1ea6a17fe6b149cb1047a994303d0f22433818f8360d5c21da1004f666462dec",
//			sha3256mac.export().toHexString()
//		)
//		assertEquals(
//			"1ea6a17fe6b149cb1047a994303d0f22433818f8360d5c21da1004f666462dec",
//			sha3256mac.flush().toHexString()
//		)
//	}
//
//	@Test
//	fun macSHA3_384() {
//		val sha3384mac = system.get(CryptographySystemFeatures.HASHING_SHA3_384_HMAC)
//		assertThrows<IllegalStateException> { sha3384mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha3384mac.flush() }
//		sha3384mac.setSecret(key1)
//		sha3384mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"f3cea3012d81fa924e3d233101ea754f0a097259a76d076bb09203e371ffa41973abe1cfa9ad4c220cc710f97bd6a7c2",
//			sha3384mac.flush().toHexString()
//		)
//		sha3384mac.setSecret(key2)
//		sha3384mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"0357498143b9cc52608b443d80dbcf4911a779da69c3e7e689e5b24332f2345a4175ade3a6289ded818a7cfe5c29afcf",
//			sha3384mac.export().toHexString()
//		)
//		assertEquals(
//			"0357498143b9cc52608b443d80dbcf4911a779da69c3e7e689e5b24332f2345a4175ade3a6289ded818a7cfe5c29afcf",
//			sha3384mac.flush().toHexString()
//		)
//	}
//
//	@Test
//	fun macSHA3_512() {
//		val sha3512mac = system.get(CryptographySystemFeatures.HASHING_SHA3_512_HMAC)
//		assertThrows<IllegalStateException> { sha3512mac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { sha3512mac.flush() }
//		sha3512mac.setSecret(key1)
//		sha3512mac += byteArrayOf(1, 2, 3)
//		assertEquals(
//			"ee93c55660d12f324a863a5036863d8b18a3b7467e2be0fefc989fb647a8a73a" +
//					"9d4611a4aa91761564bd314dee8717f01eaf0c0eea8431eb99c7d5abc694a604",
//			sha3512mac.flush().toHexString()
//		)
//		sha3512mac.setSecret(key2)
//		sha3512mac += byteArrayOf(4, 5, 6)
//		assertEquals(
//			"459c6d6a39ee33e16366e681960601892b2ad9ee2a2f0151006506bf539ea302" +
//					"05c8e3e8d5d242e16f2db8d10922998f17c938fa0874862f991263667aed2b17",
//			sha3512mac.export().toHexString()
//		)
//		assertEquals(
//			"459c6d6a39ee33e16366e681960601892b2ad9ee2a2f0151006506bf539ea302" +
//					"05c8e3e8d5d242e16f2db8d10922998f17c938fa0874862f991263667aed2b17",
//			sha3512mac.flush().toHexString()
//		)
//	}
//
//	val aesKey1 = "Bread Bred Bread".toByteArray()
//	val aesKey2 = "Bread Bred BreadBread Br".toByteArray()
//	val aesKey3 = "Bread Bred BreadBread Bred Bread".toByteArray()
//
//	@Test
//	fun macAES_CMAC() {
//		val aesCmac = system.get(CryptographySystemFeatures.HASHING_AES_CMAC)
//		assertThrows<IllegalStateException> { aesCmac += byteArrayOf(1, 2, 3) }
//		assertThrows<IllegalStateException> { aesCmac.flush() }
//		assertThrows<IllegalArgumentException> { aesCmac.setSecret(key1) }
//		aesCmac.setSecret(aesKey1)
//		aesCmac += byteArrayOf(1, 2, 3)
//		assertEquals("fe1b64ca61b87281d9f64f4a99345cad", aesCmac.flush().toHexString())
//		aesCmac.setSecret(aesKey2)
//		aesCmac += byteArrayOf(4, 5, 6)
//		assertEquals("5fc094491916b8d5c3a864335ad24063", aesCmac.flush().toHexString())
//		aesCmac.setSecret(aesKey3)
//		aesCmac += byteArrayOf(7, 8, 9)
//		assertEquals("0e99031e1a4596a60b73bb4126441686", aesCmac.export().toHexString())
//		assertEquals("0e99031e1a4596a60b73bb4126441686", aesCmac.flush().toHexString())
//	}
//}