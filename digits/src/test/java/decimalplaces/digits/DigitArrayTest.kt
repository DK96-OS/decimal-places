package decimalplaces.digits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestDigitArray {

    private lateinit var mInstance: DigitArray

    @BeforeEach
    fun testSetup() {
        val digits1 = byteArrayOf(0, 2, 5)
        val digits2 = byteArrayOf(3, 6, 9)
        mInstance = DigitArray(digits1) + DigitArray(digits2)
    }

    @Test
    fun accessDigits() {
        assertEquals(mInstance[0], 3.toByte())
        assertEquals(mInstance[1], 9.toByte())
        assertEquals(mInstance[2], 4.toByte())
    }

    @Test
    fun toString_InitialCondition_() {
        assertEquals(
            "394", mInstance.toString()
        )
    }

    @Test
    fun equals_InitialCondition_Self_ReturnsTrue() {
        assertEquals(mInstance, mInstance)
    }

    @Test
    fun equals_InitialCondition_SameValues_ReturnsTrue() {
        assertEquals(
            DigitArray(byteArrayOf(3, 9, 4)),
            mInstance
        )
    }

    @Test
    fun equals_InitialCondition_DifferentValue1_ReturnsFalse() {
        assertNotEquals(
            DigitArray(byteArrayOf(2, 9, 4)),
            mInstance
        )
    }

    @Test
    fun equals_InitialCondition_DifferentValue2_ReturnsFalse() {
        assertNotEquals(
            DigitArray(byteArrayOf(3, 7, 4)),
            mInstance
        )
    }

    @Test
    fun addArrays() {
        val expected = byteArrayOf(3, 9, 4)
        for (i in 0 until mInstance.digits.size) {
            assertEquals(mInstance[i], expected[i])
        }
    }

    @Test
    fun testPlus_() {
        val operand1 = DigitArray(byteArrayOf(7, 5))
        val operand2 = DigitArray(byteArrayOf(8, 5))
        val result = operand1 + operand2
        assertEquals(
            2, result.size
        )
        assertEquals(16, result.digits[0])
        assertEquals(0, result.digits[1])
    }

    @Test
    fun testPlus2() {
        val operand1 = DigitArray(byteArrayOf(7, 5))
        val operand2 = DigitArray(byteArrayOf(8, 5))
        val result = operand1 + operand2
        assertEquals(
            2, result.size
        )
        assertEquals(16, result.digits[0])
        assertEquals(0, result.digits[1])
        //
        assertTrue(result.isLeadDigitOverflowing())
    }

    @Test
    fun testPlus_DifferentSizesA1B2() {
        val operand1 = DigitArray(byteArrayOf(7,))
        val operand2 = DigitArray(byteArrayOf(8, 5))
        val result = operand1 + operand2
        assertEquals(
            2, result.size
        )
        assertEquals(15, result.digits[0])
        assertEquals(5, result.digits[1])
    }

    @Test
    fun testPlus_DifferentSizesA2B1() {
        val operand1 = DigitArray(byteArrayOf(6, 4))
        val operand2 = DigitArray(byteArrayOf(5,))
        val result = operand1 + operand2
        assertEquals(
            2, result.size
        )
        assertEquals(11, result.digits[0])
        assertEquals(4, result.digits[1])
    }

    @Test
    fun testPlus_DifferentSizesA2B3() {
        val operand1 = DigitArray(byteArrayOf(6, 4))
        val operand2 = DigitArray(byteArrayOf(5, 7, 8))
        val result = operand1 + operand2
        assertEquals(
            3, result.size
        )
        assertEquals(12, result.digits[0])
        assertEquals(1, result.digits[1])
        assertEquals(8, result.digits[2])
    }

    @Test
    fun testMinus_Simple1_ReturnsValid() {
        val simple1 = DigitArray(byteArrayOf(1))
        val simple2 = DigitArray(byteArrayOf(2))
        val result = simple2 - simple1
        assertEquals(1, result.digits[0])
    }

    @Test
    fun testMinus_InitialCondition_Zero_ReturnsInitialCondition() {
        val zeroInput = DigitArray(byteArrayOf(0))
        assertEquals(mInstance, mInstance - zeroInput)
    }

    @Test
    fun testMinus_InitialCondition_Zero2_ReturnsInitialCondition() {
        val zeroInput = DigitArray(byteArrayOf(0, 0))
        assertEquals(mInstance, mInstance - zeroInput)
    }

    @Test
    fun testMinus_InitialCondition_Zero4_ReturnsLength4WithTrailingZero() {
        val zeroInput = DigitArray(byteArrayOf(0, 0, 0, 0))
        assertEquals(
            DigitArray(byteArrayOf(3, 9, 4, 0)),
            mInstance - zeroInput
        )
    }

    @Test
    fun testMinus_() {
        val subtractOperand = DigitArray(byteArrayOf(1, 2, 3))
        val result = mInstance - subtractOperand
        assertEquals(2, result.digits[0])
        assertEquals(7, result.digits[1])
        assertEquals(1, result.digits[2])
    }

    @Test
    fun testMinus_2() {
        val subtractOperand = DigitArray(byteArrayOf(1, 2, 4))
        val result = mInstance - subtractOperand
        assertEquals(2, result.digits[0])
        assertEquals(7, result.digits[1])
        assertEquals(0, result.digits[2])
    }

    @Test
    fun testMinus_3() {
        val subtractOperand = DigitArray(byteArrayOf(1, 2, 5))
        val result = mInstance - subtractOperand
        assertEquals(2, result.digits[0])
        assertEquals(6, result.digits[1])
        assertEquals(9, result.digits[2])
    }


    @Test
    fun testMinus_BorrowingAtZerothIndex() {
        val operand1 = DigitArray(byteArrayOf(1, 2, 6))
        val operand2 = DigitArray(byteArrayOf(3, 0, 7))
        val result = operand1 - operand2
        assertEquals(
            3, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(8, result.digits[0])
        assertEquals(1, result.digits[1])
        assertEquals(9, result.digits[2])
    }

    @Test
    fun testMinus_BorrowingMultipleColumns() {
        val operand1 = DigitArray(byteArrayOf(1, 0, 6))
        val operand2 = DigitArray(byteArrayOf(5, 0, 7))
        val result = operand1 - operand2
        assertEquals(
            3, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        //assertEquals(-15, result.digits[0])
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(5, result.digits[0])
        assertEquals(9, result.digits[1])
        assertEquals(9, result.digits[2])
    }

    @Test
    fun testMinus_Borrowing_ReturnsValidDifference() {
        mInstance = DigitArray(byteArrayOf(1, 0, 4))
        val subtractOperand = DigitArray(byteArrayOf(0, 2, 5))
        val result = mInstance - subtractOperand
        assertEquals(
            3, result.size
        )
        assertEquals(0, result.digits[0])
        assertEquals(7, result.digits[1])
        assertEquals(9, result.digits[2])
    }

    @Test
    fun testMinus_DifferentSizesA1B2_ReturnsValid() {
        val operand1 = DigitArray(byteArrayOf(4))
        val operand2 = DigitArray(byteArrayOf(2, 5))
        val result = operand1 - operand2
        assertEquals(
            2, result.size
        )
        assertEquals(1, result.digits[0])
        assertEquals(5, result.digits[1])
    }

    @Test
    fun testMinus_DifferentSizesA1B2_IncludingBorrow_ReturnsBorrowStart() {
        val operand1 = DigitArray(byteArrayOf(4))
        val operand2 = DigitArray(byteArrayOf(7, 5))
        val result = operand1 - operand2
        assertEquals(
            2, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(6, result.digits[0])
        assertEquals(5, result.digits[1])
    }

    @Test
    fun testMinus_DifferentSizesA2B1_IncludingBorrow_ReturnsBorrowStart() {
        val operand1 = DigitArray(byteArrayOf(4, 7))
        val operand2 = DigitArray(byteArrayOf(5))
        val result = operand1 - operand2
        assertEquals(
            2, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(9, result.digits[0])
        assertEquals(7, result.digits[1])
    }

    @Test
    fun testMinus_KeyUseCase_() {
        val operand1 = DigitArray(byteArrayOf(0, 1))
        val operand2 = DigitArray(byteArrayOf(0, 2))
        mInstance = operand1 - operand2
        assertEquals(
            2, mInstance.size
        )
        assertTrue(mInstance.isLeadDigitOverflowing())
        assertEquals(
            -1, mInstance.collectOverflowFromLeadDigit()
        )
        assertEquals(9, mInstance.digits[0])
        assertEquals(9, mInstance.digits[1])
    }

    @Test
    fun testMinus_KeyUseCase2_() {
        val operand1 = DigitArray(byteArrayOf(0, 0, 1))
        val operand2 = DigitArray(byteArrayOf(0, 0, 2))
        val result = operand1 - operand2
        assertEquals(
            3, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(9, result.digits[0])
        assertEquals(9, result.digits[1])
        assertEquals(9, result.digits[2])
    }

    @Test
    fun testMinus_KeyUseCase3_() {
        val operand1 = DigitArray(byteArrayOf(0, 0, 0))
        val operand2 = DigitArray(byteArrayOf(0, 0, 9))
        val result = operand1 - operand2
        assertEquals(
            3, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(9, result.digits[0])
        assertEquals(9, result.digits[1])
        assertEquals(1, result.digits[2])
    }

    @Test
    fun testMinus_KeyUseCase3_DifferentSizeArrays() {
        val operand1 = DigitArray(byteArrayOf(0))
        val operand2 = DigitArray(byteArrayOf(0, 0, 9))
        val result = operand1 - operand2
        assertEquals(
            3, result.size
        )
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(
            -1, result.collectOverflowFromLeadDigit()
        )
        assertEquals(9, result.digits[0])
        assertEquals(9, result.digits[1])
        assertEquals(1, result.digits[2])
    }

    @Test
    fun testCollectOverflowFromLeadDigit_InitialCondition_Returns0() {
        val result = mInstance.collectOverflowFromLeadDigit()
        assertFalse(mInstance.isLeadDigitOverflowing())
        assertEquals(0, result)
    }

    @Test
    fun testCollectOverflowFromLeadDigit_PlusWithCarry_Returns1() {
        val result = DigitArray(byteArrayOf(4)) + DigitArray(byteArrayOf(8))
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(1, result.collectOverflowFromLeadDigit())
    }

    @Test
    fun testCollectOverflowFromLeadDigit_MinusWithBorrow_ReturnsNegative1() {
        val result = DigitArray(byteArrayOf(4)) - DigitArray(byteArrayOf(8))
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(-1, result.collectOverflowFromLeadDigit())
    }

    @Test
    fun testCollectOverflowFromLeadDigit_MinusWithBorrows_ReturnsNegative1() {
        val result = DigitArray(byteArrayOf(4, 0, 1, 0)) - DigitArray(byteArrayOf(8, 9, 9, 5))
        assertTrue(result.isLeadDigitOverflowing())
        assertEquals(-1, result.collectOverflowFromLeadDigit())
    }

    @Test
    fun trimTrailingZeros_() {
        mInstance = DigitArray(byteArrayOf(1, 0))
        mInstance = mInstance.trimTrailingZeros()
        assertEquals(
            1, mInstance.digits.size
        )
    }

    @Test
    fun trimTrailingZeros_2() {
        mInstance = DigitArray(byteArrayOf(1, 0, 2, 0))
        mInstance = mInstance.trimTrailingZeros()
        assertEquals(
            3, mInstance.digits.size
        )
        assertEquals(1, mInstance.digits[0])
        assertEquals(0, mInstance.digits[1])
        assertEquals(2, mInstance.digits[2])
    }

    @Test
    fun trimTrailingZeros_AllZero3() {
        mInstance = DigitArray(byteArrayOf(0, 0, 0))
        mInstance = mInstance.trimTrailingZeros()
        assertEquals(
            1, mInstance.digits.size
        )
        assertEquals(0, mInstance.digits[0])
    }

    @Test
    fun trimLeadingZeros_() {
        mInstance = DigitArray(byteArrayOf(1, 0))
        mInstance = mInstance.trimLeadingZeros()
        assertEquals(
            2, mInstance.digits.size
        )
    }

    @Test
    fun trimLeadingZeros_2() {
        mInstance = DigitArray(byteArrayOf(0, 1, 2, 0))
        mInstance = mInstance.trimLeadingZeros()
        assertEquals(
            3, mInstance.digits.size
        )
        assertEquals(1, mInstance.digits[0])
        assertEquals(2, mInstance.digits[1])
        assertEquals(0, mInstance.digits[2])
    }

    @Test
    fun trimLeadingZeros_AllZero3() {
        mInstance = DigitArray(byteArrayOf(0, 0, 0))
        mInstance = mInstance.trimLeadingZeros()
        assertEquals(
            1, mInstance.digits.size
        )
        assertEquals(0, mInstance.digits[0])
    }

    @Test
    fun testFromInteger_SameAs_InitialCondition_IsEqual() {
        assertEquals(
            mInstance, DigitArray.fromInteger(394)
        )
    }

    @Test
    fun testFromInteger_0_ReturnsSingleDigitArray() {
        val result = DigitArray.fromInteger(0)
        assertEquals(1, result.size)
        assertEquals("0", result.toString())
    }


    @Test
    fun testFromInteger_MaxValue_ReturnsMaxValueDigitArray() {
        val result = DigitArray.fromInteger(Integer.MAX_VALUE)
        assertEquals(10, result.size)
        assertEquals(
            Integer.MAX_VALUE.toString(),
            result.toString()
        )
    }

    @Test
    fun testFromString_SameAs_InitialCondition_IsEqual() {
        assertEquals(
            mInstance, DigitArray.fromString("394")
        )
    }

    @Test
    fun testFromString_0_ReturnsSameAsFromInteger0() {
        assertEquals(
            DigitArray.fromInteger(0), DigitArray.fromString("0")
        )
    }

    @Test
    fun testFromString_MaxLongValue_ReturnsMaxLong() {
        val maxLongString = Long.MAX_VALUE.toString()
        val result = DigitArray.fromString(maxLongString)
        assertEquals(
            maxLongString, result.toString()
        )
    }

}