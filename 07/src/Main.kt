import java.io.File
import kotlin.math.absoluteValue

fun main() {
	val input = File("input.txt")
		.readLines()[0]
		.split(",")
		.map { it -> it.toLong() }

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<Long>): Long {
	val min = input.minOf { it }
	val max = input.maxOf { it }

	return (min..max).map { i -> input.sumOf { (it - i).absoluteValue } }
	.minOf { it }
}

fun part02(input: List<Long>): Long {
	val min = input.minOf { it }
	val max = input.maxOf { it }

	return (min..max).map { i ->
		input.sumOf { (it - i).absoluteValue*((it - i).absoluteValue + 1L)/2L } // 1 + 2 + ... + n = n*(n + 1) / 2
	}.minOf { it }
}