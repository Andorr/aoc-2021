import java.io.File
import java.util.ArrayDeque

const val OPEN = "([{<"
val CLOSING = mapOf(
	'(' to ')',
	'[' to ']',
	'{' to '}',
	'<' to '>'
)

fun main() {
	val input = File("input.txt")
		.readLines()

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<String>): Long {
	return input.sumOf { line ->
		val stack = ArrayDeque<Char>()
		line.forEach { c ->
			if(c in OPEN) {
				stack.push(c)
			}
			else {
				if(stack.pop().match(c)) {
					return@forEach
				}
				// Error
				return@sumOf c.score()
			}
		}
		0L
	}
}

fun part02(input: List<String>): Long {
	val scores =  input.map { line ->
		val stack = ArrayDeque<Char>()
		line.forEach { c ->
			if(c in OPEN) {
				stack.push(c)
			}
			else {
				if(stack.pop().match(c)) {
					return@forEach
				}
				// Error - ignore
				return@map 0L
			}
		}

		stack.fold(0L) { acc, c -> acc * 5 + CLOSING[c]!!.scoreAutoComplete()}
	}.filter { it > 0L }.sorted()

	return scores[scores.size/2]
}


fun Char.match(o: Char): Boolean {
	return when(this) {
		'(' -> o == ')'
		'[' -> o == ']'
		'{' -> o == '}'
		'<' -> o == '>'
		else -> false
	}
}

fun Char.score(): Long {
	return when(this) {
		')' -> 3L
		']' -> 57L
		'}' -> 1197L
		'>' -> 25137L
		else -> 0L
	}
}

fun Char.scoreAutoComplete(): Long {
	return when(this) {
		')' -> 1L
		']' -> 2L
		'}' -> 3L
		'>' -> 4L
		else -> 0L
	}
}