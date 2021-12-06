import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()[0]
		.split(",")
		.map { it -> it.toLong() }

	println("Part 01: ${calcNumLaternFishes(input, 80)}")
	println("Part 02: ${calcNumLaternFishes(input, 256)}")
}

fun calcNumLaternFishes(input: List<Long>, target: Long): Long {
	var numbers = arrayOf<Long>(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)
	input.forEach { numbers[it.toInt()] += 1L }

	var day = 0L
	while(day < target) {
		day++

		val current = arrayOf<Long>(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)
		for(i in numbers.indices) {
			when(i) {
				0 -> {
					current[8] += numbers[i]
					current[6] += numbers[i]
				}
				else -> {
					current[i-1] += numbers[i]
				}
			}
		}
		numbers = current
	}
	return numbers.sum()
}