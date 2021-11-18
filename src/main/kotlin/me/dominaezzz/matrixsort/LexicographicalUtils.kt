package me.dominaezzz.matrixsort

import kotlin.math.min

object LexicographicalUtils {
	private fun CharRange.width(): Int {
		require(step == 1)
		return last - first + 1
	}

	private fun pow(base: Int, exponent: Int): Long {
		if (exponent == 0) return 1
		if (exponent == 1) return base.toLong()
		// if (base == 0) return 0
		// if (base == 1) return 1

		return if (exponent % 2 == 0) {
			// 2^6  -> 4^3
			pow(base * base, exponent shr 1)
		} else {
			// 2^7  -> 2 * (2 * 2)^3
			base * pow(base * base, exponent shr 1)
		}
	}

	private fun geometricSum(base: Int, n: Int): Long {
		// This function is a fast way of doing this.
		// (0 until n).sumOf { pow(alphabetWidth, it) }

		val top = 1 - pow(base, n)
		val bottom = 1 - base
		return top / bottom
	}

	/**
	 * Returns the number of iterations it would take to increment "" to [value].
	 */
	private fun iterationsFromFirstOld(value: String, alphabet: CharRange, limit: Int): Long {
		require(value.length <= limit)
		val alphabetWidth = alphabet.width()

		var totalTicks: Long = value.length.toLong()
		for (i in value.indices) {
			val gapToFirst = value[i] - alphabet.first
			totalTicks += gapToFirst * geometricSum(base = alphabetWidth, n = limit - i)
		}
		return totalTicks
	}


	/**
	 * Returns the number of iterations it would take to increment [value] to the last possible string.
	 */
	fun iterationsToLast(value: String, alphabet: CharRange, limit: Int): Long {
		require(value.length <= limit)
		val alphabetWidth = alphabet.width()

		var totalTicks: Long = 0
		for (i in value.indices) {
			val gapToLast = alphabet.last - value[i]
			totalTicks += gapToLast * geometricSum(base = alphabetWidth, n = limit - i)
		}
		totalTicks += geometricSum(base = alphabetWidth, n = limit - value.length + 1) - 1
		return totalTicks
	}

	/**
	 * Returns the number of iterations it would take to increment [lower] to [higher].
	 */
	fun iterationsBetween(lower: String, higher: String, alphabet: CharRange, limit: Int): Long {
		require(lower.length <= limit)
		require(higher.length <= limit)
		require(lower <= higher)

		if (lower == higher) {
			return 0
		}
		val alphabetWidth = alphabet.width()

		var totalTicks: Long = 0

		val sharedLength = min(lower.length, higher.length)
		for (i in 0 until sharedLength) {
			val gap = higher[i] - lower[i]
			totalTicks += gap * geometricSum(base = alphabetWidth, n = limit - i)
		}

		when {
			higher.length > lower.length -> {
				totalTicks += iterationsFromFirst(higher.drop(sharedLength), alphabet, limit - sharedLength)
			}
			higher.length < lower.length -> {
				totalTicks -= iterationsFromFirst(lower.drop(sharedLength), alphabet, limit - sharedLength)
			}
		}

		return totalTicks
	}


	/**
	 * Returns the number of iterations it would take to increment "" to [value].
	 */
	fun iterationsFromFirst(value: String, alphabet: CharRange, limit: Int): Long {
		require(value.length <= limit)
		val alphabetWidth = alphabet.width()

		var totalIterations: Long = 0
		var accumulatedGaps = 0

		for (i in value.indices) {
			accumulatedGaps += value[i] - alphabet.first
			totalIterations *= alphabetWidth
			totalIterations += accumulatedGaps
		}

		for (i in value.length until limit) {
			totalIterations *= alphabetWidth
			totalIterations += accumulatedGaps
		}

		totalIterations += value.length
		return totalIterations
	}

	fun iterateFromFirst(iterations: Long, alphabet: CharRange, limit: Int): String? {
		require(iterations >= 0)
		require(limit > 0)

		val alphabetWidth = alphabet.width()

		val builder = StringBuilder(limit)

		var leftOverIterations = iterations - 1
		for (i in 0 until limit) {
			if (leftOverIterations < 0) break

			val scale = geometricSum(base = alphabetWidth, n = limit - i)
			val gapToFirst = (leftOverIterations / scale).toInt()
			if (gapToFirst >= alphabetWidth) {
				return null
			}
			leftOverIterations -= (gapToFirst * scale).toInt()
			leftOverIterations -= 1
			builder.append(alphabet.first + gapToFirst)
		}

		return builder.toString()
	}

	private fun iterateFromSimple(value: String, iterations: Long, alphabet: CharRange, limit: Int): String? {
		require(value.length <= limit)
		require(iterations >= 0)

		if (iterations == 0L) return value

		val valueIters = iterationsFromFirst(value, alphabet, limit)
		val newIters = valueIters + iterations
		return iterateFromFirst(newIters, alphabet, limit)
	}

	fun iterateFrom(value: String, iterations: Long, alphabet: CharRange, limit: Int): String? {
		require(value.length <= limit)
		require(iterations >= 0)

		if (iterations == 0L) return value

		val alphabetWidth = alphabet.width()

		var requiredIterations = iterations
		for (index in value.indices.reversed()) {
			// Number of iterations it would take to increment this index.
			val nonWrapIters = geometricSum(alphabetWidth, limit - index)

			// If this index is unaffected then simply append.
			if (requiredIterations < nonWrapIters) {
				val prefix = value.substring(startIndex = 0, endIndex = index + 1)
				val postfix = iterateFromFirst(requiredIterations, alphabet, limit - prefix.length)
				return prefix + postfix
			}

			// Delete the last character and convert it to iterations we need to make.
			val c = value[index]
			requiredIterations += (c - alphabet.first) * nonWrapIters + 1

			// TODO: Shrink the limit
		}

		return null
	}

	private fun iterateFrom(value: CharSequence, iterations: Long, alphabet: CharRange, limit: Int): String? {
		require(value.length <= limit)
		require(iterations >= 0)

		if (iterations == 0L) return value.toString()

		val alphabetWidth = alphabet.width()

		// Number of iterations it would take to increment this index.
		val iterationToIncr = geometricSum(alphabetWidth, limit - value.lastIndex)

		// If this index is unaffected then simply append.
		if (iterations < iterationToIncr) {
			val postfix = iterateFromFirst(iterations, alphabet, limit - value.length)
			return buildString { append(value); append(postfix) }
		}

		if (value.isEmpty()) {
			return null
		}

		// Delete the last character and convert it to iterations we need to make.
		val c = value.last()
		val requiredIterations = iterations + (c - alphabet.first) * iterationToIncr + 1

		// TODO: Try to shrink the limit

		return iterateFrom(value.dropLast(1), requiredIterations, alphabet, limit)
	}

	// Doesn't work
	fun _not_working_iterateFrom(value: String, iterations: Int, alphabet: CharRange, limit: Int): String? {
		require(value.length <= limit)
		require(iterations >= 0)

		if (iterations == 0) {
			return value
		}

		val alphabetWidth = alphabet.width()

		val builder = StringBuilder(limit)

		var carryFlag = false
		for (i in value.indices.reversed()) {
			// Number of iterations it would take the current index to wrap around.
			val wrapIterations = geometricSum(base = alphabetWidth, n = limit - i + 1)
			// Number of iterations it would take the current index to increment.
			val nonWrapIterations = geometricSum(base = alphabetWidth, n = limit - i)
			val actualIterations = (iterations % wrapIterations).toInt()
			val carry = if (carryFlag) {
				carryFlag = false
				1
			} else {
				0
			}
			val localIterations = (actualIterations / nonWrapIterations).toInt() + carry
			val newChar = value[i] + localIterations
			if (newChar > alphabet.last) {
				carryFlag = true
			} else {
				builder.append(newChar)
			}
		}

		if (carryFlag) {
			return null
		}

		builder.reverse()

		if (value.length < limit) {
			val wrapIterations = geometricSum(base = alphabetWidth, n = limit - value.length + 1)
			val actualIterations = iterations % wrapIterations
			val result = iterateFromFirst(actualIterations, alphabet, limit - value.length)
			if (result != null) {
				builder.append(result)
			} else {
				return null
			}
		}

		return builder.toString()
	}
}
