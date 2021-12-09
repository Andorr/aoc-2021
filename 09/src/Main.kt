import java.io.File
import kotlin.system.measureTimeMillis

typealias Coord = Pair<Int, Int>

fun main() {
	val input = File("input.txt")
		.readLines()
		.map {s ->
			s.trim().split("")
				.filter { it.isNotEmpty() }
				.map { it.toInt() }
		}

	val (points, result) = part01(input)
	println("Part01: $result")
	println("Part02: ${part02(input, points)}")
}

fun part01(input: List<List<Int>>): Pair<List<Coord>, Int> {

	val points = mutableListOf<Coord>()

	(input.indices.map { x -> input[0].indices.map { y -> Coord(x, y) }})
		.flatten()
		.forEach { (x, y) ->
			(listOf(0, 1, 0, -1) zip listOf(1, 0, -1, 0))
				.filter { (dx, dy) ->
					(x + dx >= 0 && y + dy >= 0 && x + dx < input.size && y + dy < input[0].size)
				}
				.map { (dx, dy) -> input[x + dx][y + dy] }
				.takeIf { neighbours -> neighbours.all { it > input[x][y] } }
				?.also {
					points.add(Pair(x, y))
				}
	}

	return Pair(points, points.sumOf { (x, y) -> input[x][y] + 1 })
}

fun part02(input: List<List<Int>>, points: List<Coord>): Int {
	return points.map { point ->
		bfs(input, point)
	}
		.sortedBy { -it.size }
		.slice(0 until 3)
		.fold(1) { acc, it -> acc * it.size }
}

fun bfs(input: List<List<Int>>, start: Coord): List<Coord> {
	val queue = mutableListOf<Coord>()

	queue.add(start)
	val visited = mutableSetOf<Coord>()

	while(queue.isNotEmpty()) {
		val current = queue.removeAt(0)
		visited.add(current)

		// Find neighbours
		val (x, y) = current;
		(listOf(0, 1, 0, -1) zip listOf(1, 0, -1, 0))
			.filter { (dx, dy) ->
				(x + dx >= 0 && y + dy >= 0 && x + dx < input.size && y + dy < input[0].size)
			}
			.filter { (dx, dy) ->
				input[x + dx][y + dy] > input[x][y] && input[x + dx][y + dy] != 9
			}
			.map { (dx, dy) -> Pair(x + dx, y + dy) }
			.filter { !visited.contains(it) }
			.forEach { queue.add(it) }
	}

	return visited.toList()
}