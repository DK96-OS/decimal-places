# Decimal Places
A package containing a set of Kotlin Classes that represent numbers as arrays of digits.
- Static Methods are provided for creating DigitArray from Int or String.
- Instance Methods help manage digit overflow and resizing the Array.
- Operator Methods provide digit access, and addition/subtraction between DigitArray instances.

## `DigitArray` Class
The Main Class used for representing numbers, and performing basic operations.

Properties:
- digits: ByteArray
- size / digitCount: Int

Operator Methods:
- get(index): Byte?
- set(index, Byte)
- plus(DigitArray): DigitArray
- minus(DigitArray): DigitArray

Override Methods:
- toString(): string
- equals(DigitArray): Bool
- hashCode(): Int

Instance Methods:
- isLeadDigitOverflowing(): Boolean
- collectOverflowFromLeadDigit(): Byte
- findBorrowableIndex(Int): Int
- trimTrailingZeros(): DigitArray
- trimLeadingZeros(): DigitArray

(Static) Companion Methods:
- fromInteger(Int): DigitArray
- fromString(String): DigitArray

## `MemoryCompressedDigitArray` Class
A complimentary class structure designed to store numerical digits in a smaller array.
- Designed to be immutable, but it is possible to modify a digit (at a performance cost).
- Easily construct a new `DigitArray` structure from an instance of `MemoryCompressedDigitArray`.
- Reduces the size of the array by half.
