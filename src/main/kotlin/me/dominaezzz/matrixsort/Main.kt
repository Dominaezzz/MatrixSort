package me.dominaezzz.matrixsort

fun IntRange.width(): Int {
	require(step == 1)
	return last - first + 1
}

fun <T : Any> matrixSort(
	orders: List<T?>,
	fromIndex: Int,
	toIndex: Int,
	ops: LexicalOps<T>
): List<Pair<Int, T>> {
	if (fromIndex == toIndex) return emptyList()
	if (fromIndex !in orders.indices) return emptyList()
	if (toIndex !in orders.indices) return emptyList()
	if (orders.size < 2) return emptyList()

	val ordersWithoutTarget = orders.toMutableList().apply { removeAt(fromIndex) }
	val leftRange = 0 until toIndex
	val rightRange = toIndex until ordersWithoutTarget.size

	val costOfLeftShift = run {
		val rightNeighbor = ordersWithoutTarget.getOrNull(toIndex)
		if (rightNeighbor != null && !ops.hasSpaceBetween(null, rightNeighbor, leftRange.width() + 1)) {
			// Left side is full, can't shift.
			null
		} else {
			// There's space on the left, let's find how much we need.
			leftRange.reversed()
				.asSequence()
				.mapIndexed { spaceMinusOne, index ->
					val order = ordersWithoutTarget[index]
					if (order != null) {
						ops.hasSpaceBetween(order, rightNeighbor, space = spaceMinusOne + 1)
					} else {
						false
					}
				}
				.takeWhile { !it }
				.count()
		}
	}
	println("Cost of left shift: $costOfLeftShift")

	val costOfRightShift = run {
		val definedRange = rightRange.firstOrNull { ordersWithoutTarget[it] == null }
			.let { if (it != null) (rightRange.first until it) else rightRange }

		val leftNeighbour = ordersWithoutTarget.getOrNull(leftRange.last)
		if (leftNeighbour == null && leftRange.last in ordersWithoutTarget.indices) {
			// If there's a null on the left, we can't shift on the right sadly.
			null
		} else if (!ops.hasSpaceBetween(leftNeighbour, null, space = definedRange.width() + 1)) {
			// Right side is full, can't shift.
			null
		} else {
			definedRange.asSequence()
				.mapIndexed { spaceMinusOne, index ->
					val order = ordersWithoutTarget[index]!!
					ops.hasSpaceBetween(leftNeighbour, order, space = spaceMinusOne + 1)
				}
				.takeWhile { !it }
				.count()
		}
	}
	println("Cost of right shift: $costOfRightShift")

	check(costOfLeftShift != null || costOfRightShift != null) {
		"What? What do you mean there's no space on either side"
	}

	// This prefers right if they're equal.
	val isUpdatingLeft = costOfLeftShift != null && (costOfRightShift == null || costOfLeftShift < costOfRightShift)

	val destinationRange = if (isUpdatingLeft) {
		// Update left side
		(toIndex - costOfLeftShift!!) until toIndex
	} else {
		// Update right side
		toIndex until (toIndex + costOfRightShift!!)
	}
	val points = ops.midpoints(
		ordersWithoutTarget.getOrNull(destinationRange.first - 1),
		ordersWithoutTarget.getOrNull(destinationRange.last + 1),
		destinationRange.width() + 1
	)

	return destinationRange.asSequence()
		.map { if (it < fromIndex) it else it + 1 }
		.run {
			if (isUpdatingLeft) {
				sequenceOf(this, sequenceOf(fromIndex))
			} else {
				sequenceOf(sequenceOf(fromIndex), this)
			}.flatten()
		}
		.zip(points.asSequence())
		.toList()
}
