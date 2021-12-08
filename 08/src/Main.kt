import java.io.File

fun main() {
	val input = parse("input.txt")

	println("Part 01: ${part01(input)}")
	println("Part 02: ${part02(input)}")
}

fun part01(input: List<Entry>): Long {
	val m = mutableMapOf<Long, Long>()
	input
		.forEach { it ->
			it.output
			.forEach { n -> m[n.length.toLong()] = m.getOrDefault(n.length.toLong(), 0) + 1L }
		}

	return m.toList().sortedBy { it.second }.slice(0 until 4).map { it -> it.second }.sum()
}

fun part02(input: List<Entry>): Long {

	return input.sumOf { entry ->
		val m = mutableMapOf<Long, String>()

		m[1] = entry.input.filter { it.length == 2 }.first()
		m[4] = entry.input.filter { it.length == 4 }.first()
		m[7] = entry.input.filter { it.length == 3 }.first()
		m[8] = entry.input.filter { it.length == 7 }.first()

		m[2] = entry.input.filter { it.length == 5 }.first { matches(it, m.getValue(4)) == 2L }
		m[3] = entry.input.filter { it.length == 5 }.first { matches(it, m.getValue(1)) == 2L }
		m[5] = entry.input.filter { it.length == 5 }.first { it != m.getValue(2) && it != m.getValue(3) }

		m[6] = entry.input.filter { it.length == 6 }.first { matches(it, m.getValue(1)) == 1L }
		m[9] = entry.input.filter { it.length == 6 }.first { matches(it, m.getValue(4)) == 4L }
		m[0] = entry.input.filter { it.length == 6 }.first { it != m.getValue(6) && it != m.getValue(9) }

		entry.output.joinToString("") { o ->
			m.filter { it.value.length == o.length && matches(it.value, o).toInt() == o.length }.keys.first().toString()
		}.toLong()
	}
}

fun matches(a: String, b: String): Long {
	return a.count { c -> b.contains(c) }.toLong()
}

fun parse(filename: String): List<Entry> {
	return File(filename)
		.readLines()
		.map { it.split(" | ") }
		.map {
			Entry(
				it[0].split(" "),
				it[1].split(" "),
			)
		}
}

data class Entry(var input: List<String>, var output: List<String>)