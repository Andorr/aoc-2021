import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

fun main() {
	val input = File("input.txt")
		.readLines()[0]
		.split(",")
		.map { it -> it.toLong() }

	println("Part 01: ${calcNumLanternFish(input, 80)}")
	println("Part 02: ${calcNumLanternFish(input, 256)}")
}

fun calcNumLanternFish(input: List<Long>, target: Long): Long {
	val state = LongArray(9){ 0L }.toMutableList()
	input.forEach { state[it.toInt()] += 1L }

	(0 until target).forEach { _ ->
		Collections.rotate(state, -1)
		state[6] += state[8]
	}
	return state.sum()
}