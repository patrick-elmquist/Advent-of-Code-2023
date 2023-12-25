package common.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

@DisplayName("Math")
class MathTest {
    @Test
    fun `verify isBitSet`() {
        assertTrue(0b101.isBitSet(0))
        assertFalse(0b101.isBitSet(1))
        assertTrue(0b101.isBitSet(2))
    }

    @Test
    fun `verify getBit`() {
        assertEquals(1, 0b101.getBit(0))
        assertEquals(0, 0b101.getBit(1))
        assertEquals(1, 0b101.getBit(2))
    }
}

