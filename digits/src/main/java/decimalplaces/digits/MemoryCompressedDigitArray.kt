package decimalplaces.digits

/** Manages an Array of Digits.
 */
class MemoryCompressedDigitArray(
    digitArray: DigitArray,
) {

    /** A flag indicating whether there is an overflow from the lead digit.
     */
    val hasLeadDigitOverflow: Boolean = false

    /** The Number of Digits.
     */
    val digitCount: Int = digitArray.size

    /** The Size of the Array.
     */
    val arraySize: Int = computeArraySizeForDigitCount(digitCount)

    /** An array of digit values.
     */
    val digits: ByteArray

    init {
        if (digitArray.isLeadDigitOverflowing()) {
            throw IllegalArgumentException(
                "Remove Lead Digit Overflow Before Compressing DigitArray."
            )
            //todo: Transfer Lead Digit Overflow Into Compressed DigitArray
        }
        digits = ByteArray(arraySize) {
            val digitIndex = it * 2
            val even = digitArray.digits.getOrNull(digitIndex)
                ?: return@ByteArray 0
            even.toInt().shl(4)
                .plus(digitArray.digits.getOrNull(digitIndex + 1) ?: 0)
                .toByte()
        }
    }

    companion object {
        internal fun computeArraySizeForDigitCount(count: Int): Int {
            if (count < 1) return 0
            return (count + 1) / 2
        }

        internal fun computeArrayIndex(digitIndex: Int): Int {
            val arrayIndex = digitIndex / 2
            return arrayIndex
        }

        internal fun Int.isEven(): Boolean {
            return takeLowestOneBit() != 1
        }
    }

    operator fun get(index: Int): Byte? {
        require(index in 0..< digitCount)
        val arrayIndex = computeArrayIndex(index)
        val compressedByte = digits.getOrNull(arrayIndex) ?: return null
        return if (index.isEven())
            compressedByte.toInt().ushr(4).toByte()
        else
            compressedByte.toInt().shl(28).ushr(28).toByte()
    }

    override fun toString()
        : String = digits.joinToString("")

    override fun equals(other: Any?): Boolean {
        if (other !is MemoryCompressedDigitArray) return false
        return this.hasLeadDigitOverflow == other.hasLeadDigitOverflow &&
                this.digits.contentEquals(other.digits)
    }

    override fun hashCode()
        : Int = digits.contentHashCode()

    operator fun plus(other: MemoryCompressedDigitArray): MemoryCompressedDigitArray {
        val newSize = maxOf(digits.size, other.digits.size)
        val result = if (digits.size > other.digits.size)
            ByteArray(newSize) {
                if (it < other.digits.size) (digits[it] + other.digits[it]).toByte()
                else digits[it]
            }
        else if (digits.size == other.digits.size)
            ByteArray(newSize) {
                (digits[it] + other.digits[it]).toByte()
            }
        else
            ByteArray(newSize) {
                if (it < digits.size) (digits[it] + other.digits[it]).toByte()
                else other.digits[it]
            }
        for (i in newSize - 1 downTo 1) {
            val digitValue = result[i]
            if (digitValue > 9) {
                result[i - 1] = (result[i - 1] + digitValue / 10).toByte()
                result[i] = (digitValue % 10).toByte()
            }
        }
        return MemoryCompressedDigitArray(DigitArray(result))
    }

    fun isLeadDigitOverflowing(
        results: ByteArray = digits,
    ) : Boolean {
        if (hasLeadDigitOverflow) return true
        return results[0] > 9 || results[0] < 0
    }

    /** Obtain the Overflow Value from the Lead Digit.
     */
    fun collectOverflowFromLeadDigit(
        results: ByteArray = digits,
    ) : Byte {
        val leadDigit = results[0]
        if (leadDigit < 0) { // Lead Digit is Negative
            results[0] = (10 - ((leadDigit * -1) % 10)).toByte()
            return ((leadDigit) / 10).toByte()
        }
        results[0] = (leadDigit % 10).toByte()
        return (leadDigit / 10).toByte()
    }

    operator fun minus(other: MemoryCompressedDigitArray): MemoryCompressedDigitArray {
        val result = ByteArray(maxOf(digits.size, other.digits.size)) {
            if (it < digits.size) digits[it] else 0
        }
        for (i in result.size - 1 downTo 0) {
            val otherDigitValue = if (i < other.digits.size) other.digits[i] else 0
            if (otherDigitValue < 0 || otherDigitValue > 9)
                throw IllegalArgumentException("Other DigitArray contains non-normalized digits.")
            if (otherDigitValue == 0.toByte())
                continue
            val diff = result[i] - otherDigitValue
            result[i] = (if (diff < 0) {
                val borrowIndex = findBorrowableIndex(i - 1, result)
                if (-1 < borrowIndex) {
                    result[borrowIndex] = result[borrowIndex].dec()
                    for (j in borrowIndex + 1 until i)
                        result[j] = 9
                    10 + diff
                } else if (i == 0) diff - 10 else {
                    // Negative Lead Digit Overflow
                    result[0] = (-11).toByte()
                    for (j in 1 until i)
                        result[j] = 9
                    10 + diff
                }
            } else diff).toByte()
        }
        return MemoryCompressedDigitArray(DigitArray(result))
    }

    /** Find an index that can be borrowed from.
     *  Returns the largest index below the start index where the value is greater than zero.
     *  @param startIndex The first index in the search. This index will be checked first.
     *  @param digitArray The Array of Digits.
     *  @return The index of the first digit that can be borrowed from, given the start index.
     */
    fun findBorrowableIndex(
        startIndex: Int,
        digitArray: ByteArray = digits,
    ) : Int {
        for (i in startIndex downTo  0) {
            if (digitArray[i] > 0) return i // Can be borrowed from
        }
        return -1
    }

    /** Remove the Trailing Zeros at the end of the Array.
     */
    fun trimTrailingZeros()
        : MemoryCompressedDigitArray {
        val initialSize = digits.size - 1
        for (trimIndex in initialSize downTo 1) {
            if (digits[trimIndex] != 0.toByte()) {
                return if (trimIndex < initialSize) {
                    MemoryCompressedDigitArray(DigitArray(digits.copyOf(trimIndex + 1)))
                } else
                    this
            }
        }
        return MemoryCompressedDigitArray(DigitArray(byteArrayOf(0)))
    }

    /** Remove the Leading Zeros at the start of the Array.
     */
    fun trimLeadingZeros()
        : MemoryCompressedDigitArray {
        val initialZerothIndex = arraySize - 1
        for (trimIndex in digits.indices) {
            if (digits[trimIndex] != 0.toByte()) {
                return if (trimIndex < initialZerothIndex) {
                    MemoryCompressedDigitArray(DigitArray(digits.copyOfRange(trimIndex, arraySize)))
                } else
                    this
            }
        }
        return MemoryCompressedDigitArray(DigitArray(byteArrayOf(0)))
    }

}