package decimalplaces.digits

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MemoryCompressedDigitArrayTest {


    private lateinit var regularDigitArray: DigitArray
    private lateinit var mInstance: MemoryCompressedDigitArray

    @BeforeEach
    fun testSetup() {
        regularDigitArray = DigitArray(byteArrayOf(4, 6, 2))
        mInstance = MemoryCompressedDigitArray(regularDigitArray)
    }

    private fun setupAlternative() {
        regularDigitArray = DigitArray(byteArrayOf(1, 2, 3, 4))
        mInstance = MemoryCompressedDigitArray(regularDigitArray)
    }

    @Test
    fun testArraySize_InitialCondition_Returns2() {
        assertEquals(
            2, mInstance.arraySize
        )
        // Regular Digit Array
        assertEquals(
            3, regularDigitArray.digits.size
        )
    }

    @Test
    fun testArraySize_Alternative_Returns2() {
        setupAlternative()
        assertEquals(
            2, mInstance.arraySize
        )
        assertEquals(
            4, regularDigitArray.digits.size
        )
    }

    @Test
    fun testDigitCount_InitialCondition_Returns3() {
        assertEquals(
            3, mInstance.digitCount
        )
        // Regular Digit Array Size
        assertEquals(
            3, regularDigitArray.digits.size
        )
    }

    @Test
    fun testDigitCount_Alternative_Returns4() {
        setupAlternative()
        assertEquals(
            4, mInstance.digitCount
        )
        // Regular Digit Array Size
        assertEquals(
            4, regularDigitArray.digits.size
        )
    }

    @Test
    fun testGet_InitialCondition_ReturnsDigits() {
        assertEquals(
            4, mInstance[0]
        )
        assertEquals(
            6, mInstance[1]
        )
        assertEquals(
            2, mInstance[2]
        )
    }

    @Test
    fun testGet_InitialCondition_OutOfBounds_ReturnsNull() {
        assertNull(mInstance[3])
    }

    @Test
    fun testGet_InitialCondition_NegativeIndex_ReturnsEndOfArrayIndex() {
        assertNull(mInstance[-1])
        assertNull(mInstance[-2])
    }

    @Test
    fun testGet_Alternative_NegativeIndex_ReturnsArrayIndex() {
        regularDigitArray = DigitArray(byteArrayOf(1, 2, 3, 4))
        mInstance = MemoryCompressedDigitArray(regularDigitArray)
        assertNull(mInstance[-1])
        assertNull(mInstance[-2])
        assertNull(mInstance[-3])
    }

    @Test
    fun testSet_Index0_Value0_UpdatesDigits() {
        mInstance[0] = 0
        assertEquals("062", mInstance.toString())
    }

    @Test
    fun testSet_Index1_Value0_UpdatesDigits() {
        mInstance[1] = 0
        assertEquals("402", mInstance.toString())
    }

    @Test
    fun testSet_NegativeIndex_ThrowsIndexOutOfBoundsException() {
        assertThrows<IndexOutOfBoundsException> {
            mInstance[-1] = 5
        }
    }

    @Test
    fun testSet_IndexOutOfBounds_ThrowsIndexOutOfBoundsException() {
        assertThrows<IndexOutOfBoundsException> {
            mInstance[10] = 5
        }
    }

    @Test
    fun testSet_Index0_NegativeValue_ThrowsIllegalArgumentsException() {
        assertThrows<IllegalArgumentException> {
            mInstance[0] = -1
        }
    }

    @Test
    fun testSet_Index0_LargeValue_ThrowsIllegalArgumentsException() {
        assertThrows<IllegalArgumentException> {
            mInstance[0] = 20
        }
    }

    @Test
    fun testToString_InitialCondition_SameAsRegularDigitArray() {
        assertEquals(
            regularDigitArray.toString(),
            mInstance.toString()
        )
    }

    @Test
    fun testToString_Alternative_SameAsRegularDigitArray() {
        regularDigitArray = DigitArray(byteArrayOf(1, 2, 3, 4))
        mInstance = MemoryCompressedDigitArray(regularDigitArray)
        assertEquals(
            regularDigitArray.toString(),
            mInstance.toString()
        )
    }

    @Test
    fun testConstructor_LeadDigitOverflow_ThrowsIllegalArgumentException() {
        val overflowingDigitArray = regularDigitArray + DigitArray(byteArrayOf(9, 9, 9))
        assertThrows<IllegalArgumentException> {
            MemoryCompressedDigitArray(overflowingDigitArray)
        }
    }

    @Test
    fun testEquals_InitialCondition_SameDigitArray_ReturnsFalse() {
        assertNotEquals(mInstance, regularDigitArray)
    }

    @Test
    fun testEquals_InitialCondition_SimilarMemoryCompressedDigitArray_ReturnsTrue() {
        assertEquals(
            mInstance, MemoryCompressedDigitArray(regularDigitArray)
        )
    }

    @Test
    fun testEquals_InitialCondition_DifferentDigitCount_ReturnsTrue() {
        regularDigitArray = DigitArray(byteArrayOf(5, 4, 3, 2, 1))
        assertNotEquals(
            mInstance, MemoryCompressedDigitArray(regularDigitArray)
        )
    }

    @Test
    fun testEquals_InitialCondition_DifferentDigitValue_ReturnsFalse() {
        regularDigitArray.digits[1] = 0
        assertNotEquals(
            mInstance, MemoryCompressedDigitArray(regularDigitArray)
        )
    }

    @Test
    fun testHashCode_InitialCondition_ReturnsStable() {
        assertEquals(
            3163, mInstance.hashCode()
        )
    }

}