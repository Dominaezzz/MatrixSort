package me.dominaezzz.matrixsort

object UIntLexicalOps : LexicalOps<UInt> {
	override fun hasSpaceBetween(lower: UInt?, higher: UInt?, space: Int): Boolean {
		require(space > 0)
		return if (lower != null) {
			if (higher != null) {
				higher > lower + space.toUInt()
			} else {
				lower < UInt.MAX_VALUE - (space - 1).toUInt()
			}
		} else {
			if (higher != null) {
				higher > UInt.MIN_VALUE + (space - 1).toUInt()
			} else {
				// Int always fits in UInt
				true
			}
		}
	}

	override fun midpoint(min: UInt?, max: UInt?): UInt {
		return if (min != null) {
			if (max != null) {
				((min.toULong() + max.toULong()) / 2UL).toUInt()
			} else {
				((min.toULong() + UInt.MAX_VALUE.toULong() + 1U) / 2UL).toUInt()
			}
		} else {
			if (max != null) {
				((max.toULong() + 1U) / 2UL).toUInt()
			} else {
				UInt.MAX_VALUE / 2U
			}
		}
	}

	override fun midpoints(minExclusive: UInt?, maxExclusive: UInt?, count: Int): List<UInt> {
		require(count > 0)

		val newMin = if (minExclusive != null) minExclusive.toULong() + 1U else UInt.MIN_VALUE.toULong()
		val newMax = if (maxExclusive != null) maxExclusive.toULong() + 1U else (UInt.MAX_VALUE.toULong() + 2U)

		val stepSize = ((newMax - newMin) / (count + 1).toUInt()).toUInt()

		return (1..count).map { (newMin + it.toUInt() * stepSize).toUInt() - 1U }
	}
}
