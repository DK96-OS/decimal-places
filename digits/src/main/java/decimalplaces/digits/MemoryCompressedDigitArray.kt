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
    internal val arraySize: Int = computeArraySizeForDigitCount(digitCount)

    /** An array of digit values.
     */
    internal val digits: ByteArray

    init {
        if (digitArray.isLeadDigitOverflowing()) {
            throw IllegalArgumentException(
                "Remove Lead Digit Overflow Before Compressing DigitArray."
            )
        }
        digits = ByteArray(arraySize) {
            val digitIndex = it * 2
            mergeDigits(
                even = digitArray.digits.getOrElse(digitIndex) { 0 },
                odd = digitArray.digits.getOrNull(digitIndex + 1) ?: 0
            )
        }
    }

    companion object {
        internal fun computeArraySizeForDigitCount(count: Int)
            : Int = if (count < 1) 0 else (count + 1) / 2

        internal fun computeArrayIndex(digitIndex: Int)
            : Int = digitIndex / 2

        internal fun mergeDigits(even: Byte, odd: Byte)
            : Byte = even.toInt().shl(4).plus(odd).toByte()

        internal fun Int.isEven()
            : Boolean = takeLowestOneBit() != 1
    }

    operator fun get(index: Int): Byte? {
        if (index >= digitCount || index < 0)
            return null
        val arrayIndex = computeArrayIndex(index)
        if (arrayIndex < 0)
            return null
        val compressedDigits = digits.getOrNull(arrayIndex)?.toInt()
            ?: return null
        return if (index.isEven())
            compressedDigits.ushr(4).toByte()
        else
            compressedDigits.shl(28).ushr(28).toByte()
    }

    operator fun set(index: Int, digit: Byte) {
        if (digit < 0 || digit > 9) {
            throw IllegalArgumentException("Must set a single Digit non-negative value.")
        }
        if (index < 0 || index >= digitCount)
            throw IndexOutOfBoundsException("Invalid index: $index")
        val arrayIndex = computeArrayIndex(index)
        val compressedValue = digits[arrayIndex].toInt()
        //
        digits[arrayIndex] = if (index.isEven()) mergeDigits(
            even = digit,
            odd = if (index + 1 < digitCount)
                compressedValue.shl(28).ushr(28).toByte()
            else 0
        )
        else mergeDigits(
            even = compressedValue.shr(4).toByte(),
            odd = digit
        )
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (i in 0 until digitCount) {
            builder.append(get(i))
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