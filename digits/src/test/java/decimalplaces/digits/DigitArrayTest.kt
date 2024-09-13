package decimalplaces.digits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
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
            3, result.size
        )
        assertEquals(1, result.digits[0])
        assertEquals(6, result.digits[1])
        assertEquals(0, result.digits[2])
    }

    @Test
    fun testPlus2() {
        val operand1 = DigitArray(byteArrayOf(7, 5))
        val operand2 = DigitArray(byteArrayOf(8, 5))
        val result = operand1 + operand2
        assertEquals(
            3, result.size
        )
        assertEquals(1, result.digits[0])
        assertEquals(6, result.digits[1])
        assertEquals(0, result.digits[2])
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
            4, result.size
        )
        assertEquals(-1, result.digits[0])
        assertEquals(8, result.digits[1])
        assertEquals(1, result.digits[2])
        assertEquals(9, result.digits[3])
    }

    @Test
    fun testMinus_BorrowingMultipleColumns() {
        val operand1 = DigitArray(byteArrayOf(1, 0, 6))
        val operand2 = DigitArray(byteArrayOf(5, 0, 7))
        val result = operand1 - operand2
        assertEquals(
            4, result.size
        )
        assertEquals(-1, result.digits[0])
        assertEquals(5, result.digits[1])
        assertEquals(9, result.digits[2])
        assertEquals(9, result.digits[3])
    }

    @Test
    fun testMinus_Borrowing_ReturnsValidDifference() {
        mInstance = DigitArray(byteArrayOf(1, 0, 4))
        val subtractOperand = DigitArray(byteArrayOf(0, 2, 5))
        val result = mInstance - subtractOperand
        assertEquals(0, result.digits[0])
        assertEquals(7, result.digits[1])
        assertEquals(9, result.digits[2])
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

}