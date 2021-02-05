import org.junit.Assert.assertEquals
import org.junit.Test

class Test {

    @Test
    fun `2 plus 2 expect 4`() {
        val first = 2
        val second = 2
        val expected = 6

        val result = first + second

        assertEquals(expected, result)
    }
}