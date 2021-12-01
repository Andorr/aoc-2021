import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()
		.map { it.toLong() }

	println("Part 02: ${part02(input)}")
	println("Part 01: ${part01(input)}")
}

fun part01(input: List<Long>): Long {
	return (1 until input.size)
		.count { i -> input[i] > input[i - 1] }
		.toLong()
}

fun part02(input: List<Long>): Long {
	return (1 until input.size - 3)
		.count { i -> input[i+1] + input[i+2] + input[i+3] > input[i] + input[i+1] + input[i+2]}
		.toLong()
}