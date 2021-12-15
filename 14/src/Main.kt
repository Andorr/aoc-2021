import java.io.File
import kotlin.math.max

fun main() {
	val lines = File("input.txt")
		.readLines()

	val input = Formula(lines[0], lines.slice((2 until lines.size))
		.map {
			val parts = it.split(" -> ")
			parts[0] to parts[1]
		}.toMap())

	// println("Part01: ${part01(input, 10)}")
	println("Part02: ${part02(input, 40)}")
}

fun part02(input: Formula, steps: Int = 10): Long {

	var pairCounts = mutableMapOf<String, Long>()
	var charCounts = mutableMapOf<Char, Long>()
	input.template.windowed(2, 1).forEach {
		pairCounts[it] = pairCounts.getOrDefault(it, 0) + 1
	}
	input.template.forEach { c ->
		charCounts[c] = charCounts.getOrDefault(c, 0) + 1L
	}

	println(pairCounts)
	println(charCounts)

	(0 until steps).forEach {
		val pairs = pairCounts.toMutableMap()

		pairCounts.forEach { it
			println(it.key)
			if(input.rules.containsKey(it.key)) {
				val first = it.key[0] + input.rules[it.key]!!
				val second = input.rules[it.key]!! + it.key[1]
				pairs[it.key] = pairs[it.key]!! - it.value

				pairs[first] = pairs.getOrDefault(first, 0) + it.value
				pairs[second] = pairs.getOrDefault(second, 0) + it.value

				val c = input.rules[it.key]!![0]
				charCounts[c] = charCounts.getOrDefault(c, 0) + it.value
			}
		}

		pairCounts = pairs.filter { it.value > 0 }.toMutableMap()
		println(pairCounts)
		println(charCounts)
	}


	return charCounts.maxOf { it.value } - charCounts.minOf { it.value }
}

fun part01(input: Formula, steps: Int = 10): Long {

	var s = input.template.substring(0 until input.template.length)

	(0 until steps).forEach {
		val sb = StringBuilder()

		s.windowed(2, 1, false)
			.forEachIndexed { index, pair ->
				if(input.rules.containsKey(pair)) {
					sb.append(pair[0])
					sb.append(input.rules[pair])
				}
			}
		sb.append(s.last())
		s = sb.toString()
	}


	val m = mutableMapOf<Char, Long>()
	s.forEach {
		m[it] = m.getOrDefault(it, 0) + 1
	}

	return m.maxOf { it.value } - m.minOf { it.value }
}

data class Formula(val template: String, val rules: Map<String, String>)
