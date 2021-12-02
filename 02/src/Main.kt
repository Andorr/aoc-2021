import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<String>): Long {
	var h = 0L; var depth = 0L;

	input.forEach { it ->
		val s = it.split(" ")
		when (s[0]) {
			"forward" -> h += s[1].toLong()
			"down" -> depth += s[1].toLong()
			"up" -> depth -= s[1].toLong()
		}
	}
	return h * depth
}

fun part02(input: List<String>): Long {
	var h = 0L; var depth = 0L; var aim = 0L

	input.forEach { it ->
		val s = it.split(" ")
		when (s[0]) {
			"forward" -> {
				h += s[1].toLong()
				depth += aim*s[1].toLong()
			}
			"down" -> aim += s[1].toLong()
			"up" -> aim -= s[1].toLong()
		}
	}
	return h * depth
}
