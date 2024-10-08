package decimalplaces.digits

/** Manages an Array of Digits.
 */
class DigitArray(
    /** An array of digit values.
     */
    val digits: ByteArray
) {
    /** The Size of the Array.
     */
    val size: Int = digits.size

    /** The number of Digits.
     */
    val digitCount: Int = size

    operator fun get(index: Int): Byte? {
        return digits.getOrNull(index)
    }

    operator fun set(index: Int, value: Byte) {
        if (index < 0 || index >= digitCount)
            throw IndexOutOfBoundsException()
        if (value < 0 || value > 9)
            throw IllegalArgumentException(
                "The Value must be a single digit non-negative number."
            )
        digits[index] = value
    }

    override fun toString()
        : String = digits.joinToString("")

    override fun equals(other: Any?): Boolean {
        if (other !is DigitArray) return false
        return this.digits.contentEquals(other.digits)
    }

    override fun hashCode()
        : Int = digits.contentHashCode()

    operator fun plus(other: DigitArray): DigitArray {
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
        return DigitArray(result)
    }

    fun isLeadDigitOverflowing(
        results: ByteArray = digits,
    ) : Boolean {
        return results.isNotEmpty() && (results[0] > 9 || results[0] < 0)
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

    operator fun minus(other: DigitArray): DigitArray {
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
        return DigitArray(result)
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
    fun trimTrailingZeros(): DigitArray {
        val initialSize = digits.size - 1
        for (trimIndex in initialSize downTo 1) {
            if (digits[trimIndex] != 0.toByte()) {
                return if (trimIndex < initialSize) {
                    DigitArray(digits.copyOf(trimIndex + 1))
                } else
                    this
            }
        }
        return DigitArray(byteArrayOf(0))
    }

    /** Remove the Leading Zeros at the start of the Array.
     */
    fun trimLeadingZeros(): DigitArray {
        for (trimIndex in digits.indices) {
            if (digits[trimIndex] != 0.toByte()) {
                return if (trimIndex > 0) {
                    DigitArray(digits.copyOfRange(trimIndex, size))
                } else
                    this
            }
        }
        return DigitArray(byteArrayOf(0))
    }

    companion object {
        /** Construct a Digit Array from an Integer.
         * Note that integers cannot produce DigitArrays with leading zeros.
         * @param integer The Integer to be translated into a DigitArray.
         * @return The DigitArray containing the base 10 digits of the Integer.
         */
        fun fromInteger(integer: Int): DigitArray {
            val numberString = integer.toString()
            return DigitArray(ByteArray(numberString.length) {
                // 48 is the code for 0, and 49 is the code for 1.
                numberString[it].code.minus(48).toByte()
            })
        }

        /** Construct a Digit Array from a String.
         * Filters all characters not within the digit range.
         * @param value The String to be translated into a DigitArray.
         * @return The DigitArray containing the base 10 digits of the Integer.
         */
        fun fromString(value: String): DigitArray {
            val numberString = value.chars()
                .map { (it - 48) }
                .filter { it > -1 && it < 10 }
                .toArray()
            return DigitArray(ByteArray(numberString.size) {
                numberString[it].toByte()
            })
        }
    }

}