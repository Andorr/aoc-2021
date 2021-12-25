import java.io.File
import kotlin.system.measureTimeMillis

enum class State {
	EMPTY,
	EAST,
	SOUTH
}

fun main() {
	val input = parse("input.txt")
	println("Part01: ${part01(input)}")
}

fun part01(input: Array<Array<State>>): Int {
	val maxX = input[0].size
	val maxY = input.size

	var step = 0
	var didMove = true
	while(didMove) {
		step++
		didMove = false

		// Move all east looking sea cucumbers
		for(y in input.indices) {
			var x = 0
			var firstMoved = false
			while(x < input[y].size) {
				val nextX = (x + 1)%maxX
				if(input[y][x] == State.EAST && input[y][nextX] == State.EMPTY && !(nextX == 0 && firstMoved)) {
					input[y][x] = State.EMPTY
					input[y][nextX] = State.EAST
					didMove = true
					firstMoved = firstMoved || x == 0
					x++
				}
				x++
			}
		}

		// Move all south looking sea cucumbers
		for(x in input[0].indices) {
			var y = 0
			var firstMoved = false
			while(y < input.size) {
				val nextY = (y + 1)%maxY
				if(input[y][x] == State.SOUTH && input[nextY][x] == State.EMPTY && !(nextY == 0 && firstMoved)) {
					input[y][x] = State.EMPTY
					input[nextY][x] = State.SOUTH
					didMove = true
					firstMoved = firstMoved || y == 0
					y++
				}
				y++
			}
		}
	}

	return step
}

fun parse(fileName: String): Array<Array<State>> {
	return File(fileName)
		.readLines()
		.map { line -> line.map {
				when(it) {
					'.' -> State.EMPTY
					'>' -> State.EAST
					'v' -> State.SOUTH
					else -> throw IllegalArgumentException("invalid input")
				}
			}.toTypedArray()
		}.toTypedArray()
}