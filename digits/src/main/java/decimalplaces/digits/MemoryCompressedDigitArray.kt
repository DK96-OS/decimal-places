package decimalplaces.digits

/** Manages an Array of Digits.
 */
class MemoryCompressedDigitArray(
    digitArray: DigitArray,
) {
    /** The Number of Digits.
     */
    val digitCount: Int = digitArray.size

    /** The Size of the Array. At least half of the digit count.
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
        val compressedDigits = digits.getOrNull(computeArrayIndex(index))?.toInt()
            ?: return null
        return if (index.isEven())
            compressedDigits.ushr(4).toByte()
        else
            compressedDigits.shl(28).ushr(28).toByte()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (compressedDigit in digits) {
            val integer = compressedDigit.toInt()
            builder.append(integer.shr(4))
            if (digitCount.isEven()) continue
            builder.append(integer.shl(28).shr(28))
        }
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MemoryCompressedDigitArray) return false
        return this.digitCount == other.digitCount &&
                this.digits.contentEquals(other.digits)
    }

    override fun hashCode()
        : Int = digits.contentHashCode()

}