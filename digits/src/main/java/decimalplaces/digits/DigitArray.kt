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

    operator fun get(index: Int): Byte {
        require(index >= 0 && index < digits.size)
        return digits[index]
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
        if (result[0] > 9) {
            val carryOverflow = (result[0] / 10)
            result[0] = (result[0] % 10).toByte()
            return DigitArray(
                ByteArray(newSize + 1) {
                    when (it) {
                        0 -> carryOverflow.toByte()
                        else -> result[it - 1]
                    }
                }
            )
        }
        return DigitArray(result)
    }

    operator fun minus(other: DigitArray): DigitArray {
        val newSize = maxOf(digits.size, other.digits.size)
        val result = ByteArray(newSize) {
            if (it < digits.size) digits[it] else 0
        }
        // Perform subtraction digit by digit from right to left
        for (i in newSize - 1 downTo 1) {
            val currentDigitValue = result[i]
            val otherDigitValue = if (i < other.digits.size)
                other.digits[i] else 0
            // If the current digit value is less than the other digit value, borrow from next position
            if (currentDigitValue < otherDigitValue) {
                var borrowPosition = i - 1
                while (result[borrowPosition] < 1)
                    if (--borrowPosition < 0)
                        throw ArithmeticException("Cannot subtract greater value")
                // Borrow from digit and subtract it from the current position
                result[borrowPosition] = result[borrowPosition].dec()
                while (i - borrowPosition > 0) {
                    result[++borrowPosition] = 9
                }
                result[i] = (10 + currentDigitValue - otherDigitValue).toByte()
            } else {
                result[i] = (currentDigitValue - otherDigitValue).toByte()
            }
        }
        val firstValue = result[0]
        val diff = firstValue - other.digits[0]
        if (firstValue < 0 || diff < 0) {
            result[0] = (10 + diff).toByte()
            val newArray = ByteArray(newSize + 1) {
                if (it == 0) -1 else result[it - 1]
            }
            return DigitArray(newArray)
        } else {
            result[0] = diff.toByte()
            return DigitArray(result)
        }
    }

    /** Remove the Trailing Zeros at the end of the Array.
     */
    fun trimTrailingZeros()
        : DigitArray {
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
    fun trimLeadingZeros()
        : DigitArray {
        val initialZerothIndex = size - 1
        for (trimIndex in digits.indices) {
            if (digits[trimIndex] != 0.toByte()) {
                return if (trimIndex < initialZerothIndex) {
                    DigitArray(digits.copyOfRange(trimIndex, size))
                } else
                    this
            }
        }
        return DigitArray(byteArrayOf(0))
    }

}