package coder.format.huffman

import org.bread_experts_group.coder.format.huffman.BitInputStream
import org.bread_experts_group.coder.format.huffman.BitOutputStream
import org.bread_experts_group.coder.format.huffman.HuffmanBranch
import org.bread_experts_group.coder.format.huffman.HuffmanBranch.Companion.export
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class HuffmanBranchTest {
	val logger = ColoredHandler.newLogger("Huffman Branch Tests")
	val testEn = HuffmanBranchTest::class.java.classLoader.getResourceAsStream("coder/format/huffman/test_en.txt")!!
	val testJa = HuffmanBranchTest::class.java.classLoader.getResourceAsStream("coder/format/huffman/test_ja.txt")!!

	@OptIn(ExperimentalStdlibApi::class)
	@Test
	fun compute() {
		fun compute(on: InputStream) {
			val branch = HuffmanBranch.compute(on.readAllBytes().decodeToString().asIterable())
			logger.info(branch.visualize())
			val exported = ByteArrayOutputStream()
			branch.export(exported)
			logger.info(exported.toByteArray().toHexString())
		}
		assertDoesNotThrow { compute(testEn) }
		assertDoesNotThrow { compute(testJa) }
	}

	@Test
	fun write() = assertDoesNotThrow {
		val tree = HuffmanBranch<Char>().also {
			it.edge(true, 'A')
			it.branch(false).also { a0 ->
				a0.edge(false, 'C')
				a0.branch(true).branch(false).edge(false, 'B')
			}
		}
		val outputBits = BitOutputStream(ByteArrayOutputStream())
		tree.write('A', outputBits)
		tree.write('B', outputBits)
		tree.write('C', outputBits)
		outputBits.flush()
		val inputBits = BitInputStream(outputBits.to.toByteArray().inputStream())
		assertEquals('A', tree.next(inputBits))
		assertEquals('B', tree.next(inputBits))
		assertEquals('C', tree.next(inputBits))
	}

	@Test
	fun writeMessage() {
		@Suppress("LongLine", "IncorrectFormatting")
		@OptIn(ExperimentalStdlibApi::class)
		val huffman = HuffmanBranch.import(
			"6f1073116e12691374146115200b894080069c048106d2028206ff0283068a058406d1048506d40486068c058706e106880685448906b3048a06e2018b06ee018c06f1018d068d058e06ca028f06fc019006bc059106a7019206cd029306de019406f7039506cb029606ab029706eb029806f4019906ef019a06eb019b06bf039c06d0019d069d049e06c6019f06ca40a006b807a106f601a206fe01a3068b40a406d904a506e104a6068102a706e801a806e701a90621aa062bab06e901ac06d302ad063dae063eaf069205b0068a40b1065fb206e601b3068305b406f001b5069302b60640b706ea04b8063fb906e84fba06e94fbb0658bc0651bd0626be069440bf0624c006db04c106c805c20623c3065ac4063bc506d005c60659c70625c8064bc9066aca064acb0656cc0671cd0657ce0655cf062fd0064cd1064fd20652d30647d40644d5064ed60622d70627d80646d9069340da067adb0678dc0645dd0649de0650df0648e00637e10609e20643e3063ae40636e50638e60635e70639e80654e9064dea0642eb066bec0634ed0633ee0641ef062df00653f10628f20629f3065df4065bf50630f60676f70632f80631f9062efa0677fb0679fc062cfd0662fe0666ff060a70707167726d7375746475687663776c3c723d651f".hexToByteArray()
				.inputStream()
		)

		val outputBits = BitOutputStream(ByteArrayOutputStream())
		for (char in "message") huffman.write(char, outputBits)
		outputBits.flush()
		val inputBits = BitInputStream(outputBits.to.toByteArray().inputStream())
		var rebuilt = ""
		while (inputBits.available() > 0) rebuilt += huffman.next(inputBits)
		assertEquals("message", rebuilt)
	}
}