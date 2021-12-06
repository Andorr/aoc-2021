import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()[0]
		.split(",")
		.map { it -> it.toLong() }

	println("Part 01: ${calcNumLanternFish(input, 80)}")
	println("Part 02: ${calcNumLanternFish(input, 256)}")
}

fun calcNumLanternFish(input: List<Long>, target: Long): Long {
	var state = LongArray(9){ 0L }
	input.forEach { state[it.toInt()] += 1L }

	(0 until target).forEach { _ ->
		state = state.indices.fold(LongArray(9){ 0L }) { newState, i ->
			when(i) {
				0 -> {
					newState[8] += state[i]
					newState[6] += state[i]
				}
				else -> {
					newState[i-1] += state[i]
				}
			}
			newState
		}
	}
	return state.sum()
}