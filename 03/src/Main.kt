import java.io.File
import kotlin.math.pow

fun main() {
	val input = File("input.txt")
		.readLines()

	println("Part 01: ${part01(input)}")
	println("Part 02: ${part02(input)}")
}

fun part01(input: List<String>): Long {

	val size = input[0].length
	var gamma = 0L
	var epsilon = 0L

	for(i in 0 until size) {
		var num_1 = input.map { it[i] }.count { it == '1' }
		if(num_1 > input.size/2) {
			gamma +=  2.0.pow(size - 1 - i).toLong()
		} else {
			epsilon +=  2.0.pow((size - 1 - i)).toLong()
		}
	}

	return gamma*epsilon
}

fun part02(input: List<String>): Long {

	val size = input[0].length

	fun extractBinary(pred: (a: Int, b: Int) -> Boolean): Long {
		var numbers = input.toMutableList()
		var decimal = 0L
		for(i in 0 until size) {
			val num1 = numbers.map { it[i] }.count { it == '1' }
			val bit = if (pred(num1, numbers.size - num1)) '1' else '0'
			numbers = numbers.filter { it[i] == bit }.toMutableList()

			if(numbers.size == 1) {
				numbers[0].forEachIndexed { j, it ->
						decimal += 2.0.pow(size - j - 1).toLong() * it.toString().toLong()
				}
				break
			}
		}
		return decimal
	}

	val oxygen = extractBinary {a, b -> a >= b}
	val c02 = extractBinary {a, b -> a < b}

	return oxygen*c02
}