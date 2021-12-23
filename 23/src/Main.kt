import java.io.File
import java.util.*
import kotlin.math.absoluteValue

// NOTE:
// All right, this one was a big mess. The solution is slow, took some time, is a bit of a mess, and is not
// optimized at all. I am just happy that I was able to come up with a solution.
//
// Simply put, this solution utilizes Dijkstra + distance heuristic to search the possible amphipod-states until it ends up in
// a state where all the amphipods are in their correct rooms. Calculating the possible moves are the mess here. Since
// a state is a hashmap, it may be the performance killer here.

data class Coord(var x: Int, var y: Int)
data class Move(val coord: Coord, val energy: Long, var final: Boolean = false)

const val HALLWAY_Y = 1
val ROOM_BY_A = mapOf(
	'A' to 3,
	'B' to 5,
	'C' to 7,
	'D' to 9
)
val ENERGY = mapOf(
	'A' to 1,
	'B' to 10,
	'C' to 100,
	'D' to 1000,
)


typealias State = Pair<Map<Coord, Char>, Long>
var ROOM_HEIGHT = 2

fun main() {
	var input = parse("input.txt")
	input.print()
	println("Part01: ${part01(input)}")

	input = parse("input2.txt")
	input.print()
	println("Part02: ${part02(input)}")
}

fun part01(input: Map<Coord, Char>): Long {
	val map = input.toMutableMap()

	ROOM_HEIGHT = 2
	val (energy, _, _) = solve(map)

	return energy
}

fun part02(input: Map<Coord, Char>): Long {
	val map = input.toMutableMap()

	ROOM_HEIGHT = 4
	val (energy, _, _) = solve(map)
	return energy
}

fun solve(input: Map<Coord, Char>): Triple<Long, Map<Coord, Char>, Map<String, Pair<Map<Coord, Char>, Long>>> {
	var map = input.toMutableMap()

	val queue = PriorityQueue<State>(compareBy { it.second }) /*ArrayDeque<State>()*/
	val cost = mutableMapOf<String, Long>()
	val visited = mutableSetOf<String>()
	val parent = mutableMapOf<String, Pair<Map<Coord, Char>, Long>>()
	queue.add(State(map, distance(map).toLong()))
	cost.put(map.asString(), 0L)
	while(queue.isNotEmpty()) {
		val (current, _) = queue.remove()
		// Converts map to string due to better hash performance compared to using a normal map
		val currentAsString = current.asString()
		if(visited.contains(currentAsString)) {
			continue
		}
		visited.add(currentAsString)
		val curCost = cost[currentAsString]!!

		var amphipods = current.filter { it.value in listOf('A', 'B', 'C', 'D') }
		if(amphipods.isEmpty()) {
			// Found solution
			return Triple(cost[currentAsString]!!, current, parent)
		}

		val inHallway = amphipods.filter { it.key.y == HALLWAY_Y && it.key.x in ROOM_BY_A.values }
		if(inHallway.isNotEmpty()) {
			amphipods = inHallway
		}

		amphipods.forEach {

			moves(current, it).forEach { move ->
				val newMap = current.toMutableMap()
				newMap[it.key] = '.'
				if(move.final) {
					newMap[move.coord] = it.value.lowercaseChar()
				} else {
					newMap[move.coord] = it.value
				}
				val newCost = curCost + move.energy

				val newMapAsString = newMap.asString()
				if(cost.getOrDefault(newMapAsString, Long.MAX_VALUE) > newCost) {
					queue.add(State(newMap, newCost + distance(newMap)))
					cost[newMapAsString] = newCost
					parent[newMapAsString] = current to move.energy
				}

			}
		}
	}
	return Triple(-1L, input, parent)
}

fun moves(map: Map<Coord, Char>, entry: Map.Entry<Coord, Char>): List<Move> {
	val moves = mutableListOf<Move>()
	val stepCost = ENERGY[entry.value]!!.toLong()

	val move = canMoveFromHallwayToRoom(map, entry)
	if(move != null) {
		moves.add(move)
	}
	else if(entry.key.y != HALLWAY_Y) {

		val room = getRoom(map, entry.key.x)
		val below = room.slice(entry.key.y - 1 until ROOM_HEIGHT)
		if(entry.key.x != ROOM_BY_A[entry.value] || below.any { it.isLetter() && it.lowercaseChar() != entry.value.lowercaseChar() }) {
			val above = room.slice(0 until entry.key.y - (HALLWAY_Y + 1))
			val hallwayPos = Coord(entry.key.x, HALLWAY_Y)
			if((above.isEmpty() || above.all { it == '.' }) && map[hallwayPos] == '.') {
				moves.add(Move(hallwayPos, stepCost*(entry.key.y - HALLWAY_Y)))
			}
		}
	} else {
		if(entry.key.x in ROOM_BY_A.values) {
			// The amphipod is not locked, calculate its possible positions in the hallway (and make it locked)
			var isPosValid = true
			var x = entry.key.x - 1
			while(isPosValid) {
				isPosValid = map[Coord(x, HALLWAY_Y)] == '.'
				if(isPosValid && x !in ROOM_BY_A.values) {
					moves.add(Move(Coord(x, HALLWAY_Y), stepCost*(x-entry.key.x).absoluteValue))
				}
				x--
			}
			isPosValid = true
			x = entry.key.x + 1
			while(isPosValid) {
				isPosValid = map[Coord(x, HALLWAY_Y)] == '.'
				if(isPosValid && x !in ROOM_BY_A.values) {
					moves.add(Move(Coord(x, HALLWAY_Y), stepCost*(x-entry.key.x).absoluteValue))
				}
				x++
			}
		}
	}

	return moves.filter { it -> map[it.coord] == '.' }
}

fun canMoveFromHallwayToRoom(map: Map<Coord, Char>, entry: Map.Entry<Coord, Char>): Move? {
	val target = Coord(ROOM_BY_A[entry.value]!!, ROOM_HEIGHT + 1)
	val stepCost = ENERGY[entry.value]!!.toLong()
	var totalCost = 0L

	// If relevant, can we move up from our current room?
	if(entry.key.y != HALLWAY_Y) {
		val room = getRoom(map, entry.key.x)
		val above = room.slice(0 until entry.key.y - (HALLWAY_Y + 1))
		if(above.isNotEmpty() && !above.all { it == '.' }) {
			return null
		}
		if(map[Coord(entry.key.x, HALLWAY_Y)] != '.') {
			return null
		}
		totalCost += (above.size + 1) * stepCost
	}

	// Can we move in the hallway?
	val xs = (if(entry.key.x > target.x) (target.x until entry.key.x) else (entry.key.x + 1..target.x)).toList()
	if(xs.isNotEmpty() && !xs.all { x -> map[Coord(x, HALLWAY_Y)] == '.' }) {
		return null
	}
	totalCost += stepCost*xs.size

	// Can we move down to our room?
	val room = getRoom(map, target.x)
	if(!room.all { it == '.' || it.lowercaseChar() == entry.value.lowercaseChar() }) {
		return null
	}
	val dy = room.mapIndexed{ i, c -> Pair(i + 1, c)}.findLast { it.second == '.' }!!.first
	totalCost += stepCost*dy
	return Move(Coord(target.x, HALLWAY_Y + dy), totalCost, true)
}

fun distance(map: Map<Coord, Char>): Int {
	return map.entries.filter { it.value in listOf('A', 'B', 'C', 'D') }
		.sumOf { (it.key.x - ROOM_BY_A[it.value]!!).absoluteValue * ENERGY[it.value]!! }
}

fun getRoom(map: Map<Coord, Char>, index: Int): List<Char> {
	return (2..ROOM_HEIGHT+1).map { y -> Coord(index, y) }.map { map[it]!! }
}

fun Map<Coord, Char>.asString(): String {
	return this.values.joinToString("")
}

fun Map<Coord, Char>.print() {
	val minX = this.entries.minOf { it.key.x }
	val maxX = this.entries.maxOf { it.key.x }
	val minY = this.entries.minOf { it.key.y }
	val maxY = this.entries.maxOf { it.key.y }

	for(y in (minY..maxY)) {
		for(x in (minX..maxX)) {
			print(this.getOrDefault(Coord(x, y), " "))
		}
		println()
	}
}

fun parse(fileName: String): Map<Coord, Char> {
	val map = mutableMapOf<Coord, Char>()
	File(fileName)
		.readLines()
		.forEachIndexed { y, s ->
			s.forEachIndexed { x, c ->
				map[Coord(x, y)] = c
			}
		}
	return map
}