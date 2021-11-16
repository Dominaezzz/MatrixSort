import io.kotest.core.spec.style.DescribeSpec
import me.dominaezzz.matrixsort.UIntLexicalOps
import me.dominaezzz.matrixsort.matrixSort
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TestSort : DescribeSpec({

	fun testCase(list: List<UInt?>, fromIndex: Int, toIndex: Int, expectedNumOfUpdates: Int) {
		assertContentEquals(
			list.sortedWith(compareBy(nullsLast()) { it }),
			list,
			"ILLEGAL TEST CASE!!!"
		)

		val updates = matrixSort(list, fromIndex, toIndex, UIntLexicalOps)

		assertEquals(expectedNumOfUpdates, updates.size, "$updates")

		val updateMap = updates.toMap()
		val result = list.mapIndexed { index, order -> updateMap.getOrDefault(index, order) }
			.withIndex()
			.sortedWith(compareBy(nullsLast()) { it.value })
			.map { it.index }
		val expected = list.indices.toMutableList()
		expected.add(toIndex, expected.removeAt(fromIndex))
		assertContentEquals(expected, result)
	}

	// testCase(
	// 	list,
	// 	fromIndex = -1,
	// 	toIndex = -1,
	// 	expectedNumOfUpdates = 0
	// )

	context("all orders are undefined") {
		val list = listOf<UInt?>(null, null, null, null, null, null)

		it("moving to the start") {
			testCase(
				list,
				fromIndex = 2,
				toIndex = 0,
				expectedNumOfUpdates = 1
			)
		}

		it("moving to the end") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 5,
				expectedNumOfUpdates = 6
			)
		}

		it("moving left") {
			testCase(
				list,
				fromIndex = 4,
				toIndex = 1,
				expectedNumOfUpdates = 2
			)
		}

		it("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 2,
				expectedNumOfUpdates = 3
			)
		}

		it("moving from start to end") {
			testCase(
				list,
				fromIndex = 0,
				toIndex = 5,
				expectedNumOfUpdates = 6
			)
		}

		it("moving from end to start") {
			testCase(
				list,
				fromIndex = 5,
				toIndex = 0,
				expectedNumOfUpdates = 1
			)
		}
	}

	context("some orders are defined") {
		val list = listOf(20u, 50u, 65u, null, null, null, null)

		describe("moving right") {
			it("within the undefined region") {
				testCase(
					list,
					fromIndex = 3,
					toIndex = 5,
					expectedNumOfUpdates = 3
				)
			}

			it("within the defined region") {
				testCase(
					list,
					fromIndex = 0,
					toIndex = 1,
					expectedNumOfUpdates = 1
				)
			}

			it("into the undefined region") {
				testCase(
					list,
					fromIndex = 1,
					toIndex = 6,
					expectedNumOfUpdates = 5
				)
			}
		}

		describe("moving left") {
			it("within the undefined region") {
				testCase(
					list,
					fromIndex = 5,
					toIndex = 3,
					expectedNumOfUpdates = 1
				)
			}

			it("within the defined region") {
				testCase(
					list,
					fromIndex = 1,
					toIndex = 0,
					expectedNumOfUpdates = 1
				)
			}

			xit("into the undefined region") {
				// Impossible
			}
		}
	}

	context("all orders are defined") {
		val list = listOf<UInt?>(11U, 13U, 15U, 17U, 19U)

		it("moving left") {
			testCase(
				list,
				fromIndex = 2,
				toIndex = 1,
				expectedNumOfUpdates = 1
			)
		}

		it("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 2,
				expectedNumOfUpdates = 1
			)
		}
	}

	context("no gaps between orders") {
		val list = listOf(11U, 12U, 13U, 14U, 15U, 16U, 17U, 18U, 19U)

		describe("moving left") {
			testCase(
				list,
				fromIndex = 7,
				toIndex = 2,
				expectedNumOfUpdates = 3
			)
		}

		describe("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 5,
				expectedNumOfUpdates = 4
			)
		}
	}

	context("no space on the left") {
		val list = listOf(0U, 1U, 2U, 3U, 4U, 5U, 6U, 7U)

		it("moving left") {
			testCase(
				list,
				fromIndex = 5,
				toIndex = 1,
				expectedNumOfUpdates = 5
			)
		}

		it("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 3,
				expectedNumOfUpdates = 3
			)
		}
	}

	context("no space on the right") {
		val list = (7U downTo 0U).map { UInt.MAX_VALUE - it }

		it("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 6,
				expectedNumOfUpdates = 6
			)
		}

		it("moving left") {
			testCase(
				list,
				fromIndex = 4,
				toIndex = 3,
				expectedNumOfUpdates = 2
			)
		}
	}

	context("first order is max") {
		val list = listOf(UInt.MAX_VALUE, null, null, null, null)

		it("moving left") {
			testCase(
				list,
				fromIndex = 3,
				toIndex = 1,
				expectedNumOfUpdates = 2
			)
		}

		it("moving right") {
			testCase(
				list,
				fromIndex = 1,
				toIndex = 3,
				expectedNumOfUpdates = 4
			)
		}
	}

	context("first order is min") {
		val list = listOf(UInt.MIN_VALUE, null, null, null, null)

		it("moving item in front start") {
			testCase(
				list,
				fromIndex = 3,
				toIndex = 0,
				expectedNumOfUpdates = 2
			)
		}

		it("moving start into undefined") {
			testCase(
				list,
				fromIndex = 0,
				toIndex = 3,
				expectedNumOfUpdates = 4
			)
		}
	}
})
