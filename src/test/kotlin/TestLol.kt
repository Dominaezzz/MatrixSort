import me.dominaezzz.matrixsort.UIntLexicalOps
import org.junit.Test
import kotlin.test.assertContentEquals

class TestLol {
	@Test
	fun testMidpoints1() {
		val result = UIntLexicalOps.midpoints(1U, 5U, 3)
		assertContentEquals(listOf(2U, 3U, 4U), result)
	}

	@Test
	fun testMidpoints2() {
		val result = UIntLexicalOps.midpoints(1U, 5U, 1)
		println(result)
		assertContentEquals(listOf(3U), result)
	}

	@Test
	fun testMidpoints3() {
		val result = UIntLexicalOps.midpoints(0U, UInt.MAX_VALUE, 1)
		println(result)
		assertContentEquals(listOf(2147483647U), result)
	}
}
