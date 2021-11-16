import me.dominaezzz.matrixsort.LexicographicalUtils
import org.junit.Test
import kotlin.test.assertEquals

class LexicographicalUtilsTests {
	private fun String.increment(alphabet: CharRange, limit: Int): String? {
		if (length < limit) {
			return plus(alphabet.first)
		}
		val index = indexOfLast { it != alphabet.last }
		if (index == -1) {
			return null
		}
		return substring(0, index) + (get(index) + 1)
	}

	private fun allCombinations(alphabet: CharRange, limit: Int): Sequence<String> {
		return generateSequence("") { it.increment(alphabet, limit) }
	}

	@Test
	fun testIncrement() {
	}

	@Test
	fun testIterationsFromStart() {
		val alphabet = 'A'..'C'
		val limit = 4

		for ((index, combination) in allCombinations(alphabet, limit).withIndex()) {
			val iterations = LexicographicalUtils.iterationsFromFirst(combination, alphabet, limit)
			// println("${combination.padEnd(limit)} $iterations")
			assertEquals(index.toLong(), iterations)
		}
	}

	@Test
	fun testIterationsToEnd() {
		val alphabet = 'A'..'C'
		val limit = 4

		for ((index, combination) in allCombinations(alphabet, limit).toList().asReversed().withIndex()) {
			val iterations = LexicographicalUtils.iterationsToLast(combination, alphabet, limit)
			assertEquals(index.toLong(), iterations)
		}
	}

	@Test
	fun testIterateFromFirst() {
		val alphabet = 'A'..'C'
		val limit = 4

		LexicographicalUtils.iterateFromFirst(10, alphabet, limit)
		LexicographicalUtils.iterateFromFirst(11, alphabet, limit)

		for (i in 0 until 300) {
			val nextValue = LexicographicalUtils.iterateFromFirst(i, alphabet, limit)
			println("$i = $nextValue")
		}
	}

	@Test
	fun testIterateFrom() {
		val alphabet = 'A'..'C'
		val limit = 4

		LexicographicalUtils.iterateFrom("AAAA", 1, alphabet, limit)

		val start = "AAAA"
		for (i in 0 until 30) {
			val nextValue = LexicographicalUtils.iterateFrom(start, i, alphabet, limit)
			println("$start + $i = $nextValue")
		}
	}
}
