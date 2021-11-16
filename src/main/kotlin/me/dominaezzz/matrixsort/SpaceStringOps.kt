package me.dominaezzz.matrixsort

import kotlin.math.*

class SpaceStringOps(
	val alphabet: CharRange = ' '..'~',
	val limit: Int = 50
) : LexicalOps<String> {
	private val alphabetWidth = alphabet.width()
	private fun CharRange.width(): Int {
		require(step == 1)
		return last - first + 1
	}

	/**
	 * Returns the number of iterations it would take to increment "" to [value].
	 */
	fun iterationsFromFirst(value: String, limit: Int = this.limit): Long {
		require(value.length <= limit)
		return LexicographicalUtils.iterationsFromFirst(value, alphabet, limit)
	}

	/**
	 * Returns the number of iterations it would take to increment [value] to the last possible string.
	 */
	fun iterationsToLast(value: String, limit: Int = this.limit): Long {
		return LexicographicalUtils.iterationsToLast(value, alphabet, limit)
	}

	/**
	 * Returns the number of iterations it would take to increment [lower] to [higher].
	 */
	fun iterationsBetween(lower: String, higher: String, limit: Int = this.limit): Long {
		require(lower.length <= limit)
		require(higher.length <= limit)
		require(lower <= higher)

		return LexicographicalUtils.iterationsBetween(lower, higher, alphabet, limit)
	}


	override fun hasSpaceBetween(lower: String?, higher: String?, space: Int): Boolean {
		require(space > 0)

		val roughRequiredLimit = log(
			x = 1 + (alphabetWidth * space).toDouble(),
			base = alphabetWidth.toDouble() + 1
		)
		val requiredLimit = ceil(roughRequiredLimit).roundToInt() + 1 // + 1 because why not

		val commonLength = if (lower != null && higher != null) {
			lower.zip(higher).takeWhile { (l, h) -> l == h }.size
		} else {
			0
		}

		val newLower = lower?.drop(commonLength)?.take(requiredLimit)
		val newHigher = higher?.drop(commonLength)?.take(requiredLimit)

		if (newLower == null) {
			return if (newHigher == null) {
				space < iterationsFromFirst(alphabet.last.toString().repeat(requiredLimit), limit = requiredLimit)
			} else {
				space < iterationsFromFirst(newHigher, limit = requiredLimit)
			}
		}
		if (newHigher == null) {
			return space < iterationsToLast(newLower, limit = requiredLimit)
		}

		return space < iterationsBetween(newLower, newHigher, limit = requiredLimit)
	}

	override fun midpoint(min: String?, max: String?): String {
		TODO("Not yet implemented")
	}

	override fun midpoints(minExclusive: String?, maxExclusive: String?, count: Int): List<String> {
		TODO("Not yet implemented")
	}
}
