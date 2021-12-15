import java.io.File
import java.util.*

typealias Pos = Pair<Int, Int>

fun main() {
	val input = File("input.txt")
		.readLines()
		.map { it.split("").filter { it.length > 0 }.map { it.toInt() } }

	println("Part01: ${part01(input)}")
	println("Part01: ${part02(input)}")
}

fun part01(input: List<List<Int>>): Int {

	val paths = djikstra(input, Pos(0, 0), Pos(input[0].size - 1, input.size - 1))
	val shortest = paths.shortestPath(Pos(0, 0), Pos(input[0].size - 1, input.size - 1))

	val risks = shortest.subList(1, shortest.size).map { input[it.second][it.first] }
	return risks.sum()
}

fun part02(input: List<List<Int>>): Int {

	val grid = input.map { it.toMutableList() }.toMutableList()
	val gridY = grid.size

	for(dy in (0 until gridY)) {
		val g1 = grid[dy].map { (it + 1).let { if(it >= 10) it - 9 else it } }
		val g2 = grid[dy].map { (it + 2).let { if(it >= 10) it - 9 else it } }
		val g3 = grid[dy].map { (it + 3).let { if(it >= 10) it - 9 else it } }
		val g4 = grid[dy].map { (it + 4).let { if(it >= 10) it - 9 else it } }

		grid[dy].addAll(g1)
		grid[dy].addAll(g2)
		grid[dy].addAll(g3)
		grid[dy].addAll(g4)
	}

	for(i in (1 until 5)) {
		for(dy in (0 until gridY)) {
			val newY = grid[dy + (i-1)*gridY].map {
				val newVal = (it + 1)%10
				if(newVal == 0) 1 else newVal
			}
			grid.add(newY.toMutableList())
		}
	}

	val paths = djikstra(grid, Pos(0, 0), Pos(grid[0].size - 1, grid.size - 1))
	val shortest = paths.shortestPath(Pos(0, 0), Pos(grid[0].size - 1, grid.size - 1))

	val risks = shortest.subList(1, shortest.size).map { grid[it.second][it.first] }
	return risks.sum()
}

fun djikstra(input: List<List<Int>>, start: Pos, end: Pos): Map<Pos, Pos?> {
	val maxX = input[0].size
	val maxY = input.size
	val positions = input.mapIndexed() { y, l -> l.mapIndexed() { x, ll -> Pos(x, y) } }.flatten().toSet()

	val visited = mutableSetOf<Pos>()
	val cost = positions.map { it to Int.MAX_VALUE }.toMap().toMutableMap()
	val prev: MutableMap<Pos, Pos?> = positions.associateWith { null }.toMutableMap()

	val queue = PriorityQueue<Pair<Pos, Int>>(compareBy { it.second })
	queue.add(Pair(start, 0))
	cost[start] = 0

	while (queue.isNotEmpty()) {

		val next = queue.remove()
		val curPos = next.first
		visited.add(curPos)

		curPos.neighbours(maxX, maxY).minus(visited).forEach { neighbour ->
			val newCost = cost[curPos]!! + input[neighbour.second][neighbour.first]

			if(newCost < cost[neighbour]!!) {
				cost[neighbour] = newCost
				prev[neighbour] = curPos

				queue.add(Pair(neighbour, newCost))
			}
		}
	}

	return prev.toMap()
}

fun Pos.neighbours(maxX: Int, maxY: Int): Set<Pos> {
	return (listOf(-1, 0, 1, 0) zip listOf(0, 1, 0, -1))
		.map { (dx, dy) ->
			Pos(this.first + dx, this.second + dy)
		}
		.filter { it.first >= 0 && it.second >= 0 && it.first < maxX && it.second < maxY }
		.toSet()
}

fun Map<Pos, Pos?>.shortestPath(start: Pos, end: Pos): List<Pos> {
	fun pathTo(start: Pos, end: Pos): List<Pos> {
		if(this[end] == null) return listOf(end)
		return listOf(pathTo(start, this[end]!!), listOf(end)).flatten()
	}
	return pathTo(start, end)
}