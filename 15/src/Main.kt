import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

typealias Pos = Pair<Int, Int>

const val M = 5

fun main() {
	val input = File("input.txt")
		.readLines()
		.map { it.split("").filter { it.length > 0 }.map { it.toInt() } }

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<List<Int>>): Int {
	val end = Pos(input[0].size - 1, input.size - 1)
	return dijkstra(input, Pos(0, 0), end)
}

fun part02(input: List<List<Int>>): Int {
	val end = Pos(input[0].size*M - 1, input.size*M - 1)
	return dijkstra(input, Pos(0, 0), end, input[0].size*M, input.size*M)
}

fun dijkstra(input: List<List<Int>>, start: Pos, end: Pos, maxX: Int = input[0].size, maxY: Int = input.size): Int {
	val visited = mutableSetOf<Pos>()
	val cost = mutableMapOf<Pos, Int>()

	val queue = PriorityQueue<Pair<Pos, Int>>(compareBy { it.second })
	queue.add(Pair(start, 0))
	cost[start] = 0

	while (queue.isNotEmpty()) {
		val (curPos, curCost) = queue.remove()
		visited.add(curPos)

		if(curPos == end) {
			return curCost
		}

		curPos.neighbours(maxX, maxY).minus(visited).forEach { neighbour ->
			val newCost = cost[curPos]!! + neighbour.risk(input)
			if(newCost < cost.getOrDefault(neighbour, Int.MAX_VALUE)) {
				val distance = (end.first - neighbour.first) + (end.second - neighbour.second)
				cost[neighbour] = newCost
				queue.add(Pair(neighbour, newCost + distance))
			}
		}
	}

	return -1
}

fun Pos.neighbours(maxX: Int, maxY: Int): Set<Pos> {
	return (listOf(-1, 0, 1, 0) zip listOf(0, 1, 0, -1))
		.map { (dx, dy) ->
			Pos(this.first + dx, this.second + dy)
		}
		.filter { it.first >= 0 && it.second >= 0 && it.first < maxX && it.second < maxY }
		.toSet()
}

fun Pos.risk(input: List<List<Int>>): Int {
	return (input[this.second%input.size][this.first%input[0].size] + this.second/input.size + this.first/input[0].size).let {
		if(it > 9) it%9 else it
	}
}