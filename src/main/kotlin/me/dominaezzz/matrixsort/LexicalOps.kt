package me.dominaezzz.matrixsort

interface LexicalOps<T : Any> {
	fun hasSpaceBetween(lower: T?, higher: T?, space: Int = 1): Boolean
	fun midpoint(min: T?, max: T?): T
	fun midpoints(minExclusive: T?, maxExclusive: T?, count: Int): List<T>
}
