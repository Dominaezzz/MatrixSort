import me.dominaezzz.matrixsort.SpaceStringOps
import org.junit.Test
import kotlin.math.min
import kotlin.test.Ignore
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestStringOps {
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
		return generateSequence(
			seed = "",
			nextFunction = { it.increment(alphabet, limit) }
		)
	}

	@Ignore
	@Test
	fun testStringSpace() {
		val ops = SpaceStringOps('A'..'C', limit = 4)
		for (combination in allCombinations(ops.alphabet, ops.limit)) {
			val space0 = ops.iterationsFromFirst(combination)
			val space1 = ops.iterationsToLast(combination)
			println("${combination.padEnd(ops.limit)} $space0 $space1")
		}
	}

	@Test
	fun testStringHasSpaceBetween() {
		val ops = SpaceStringOps('A'..'C', limit = 2)
		val combinations = allCombinations(ops.alphabet, ops.limit).toList()
		for (leftIndex in combinations.indices) {
			val combination0 = combinations[leftIndex]
			for (combination1 in combinations.drop(leftIndex)) {
				val space = ops.iterationsBetween(combination0, combination1)
				println("${combination0.padEnd(ops.limit)} ${combination1.padEnd(ops.limit)}, iterations = $space")
			}
		}
	}

	@Test
	fun testStringHasSpace() {
		val ops = SpaceStringOps('A'..'B', limit = 3)

		assertFalse(ops.hasSpaceBetween("", ""))
		assertFalse(ops.hasSpaceBetween("", "A"))
		assertTrue(ops.hasSpaceBetween("A", "B"))
		assertTrue(ops.hasSpaceBetween("AA", "AB"))
		assertFalse(ops.hasSpaceBetween("AAA", "AAA"))
		assertFalse(ops.hasSpaceBetween("ABA", "ABA"))
		assertTrue(ops.hasSpaceBetween("A", "ABA"))
		assertTrue(ops.hasSpaceBetween("AAA", "AB"))
	}
}
