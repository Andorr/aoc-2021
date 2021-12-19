import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.system.measureTimeMillis

val ROTATIONS = rotations()

fun main() {
	val input = parse("input.txt")
	solve(input).also { (part1, part2) ->
		println("Part01: $part1")
		println("Part02: $part2")
	}
}

fun solve(input: List<List<Coord>>): Pair<Int, Int> {
	val results = mutableMapOf<Pair<Int, Int>, Result>()
	for(scanner in input.indices) {
		for(target in input.indices) {
			if(scanner == target) {
				continue
			}

			val result = doBeaconsOverlap(input[scanner], input[target]) ?: continue

			val mappings = result.first
			val rotation = result.second
			val entry = mappings.entries.first()
			val position = entry.key - entry.value

			results[Pair(scanner, target)] = Result(rotation, position, mappings)
		}
	}

	fun dfs(s: ArrayDeque<Int>, target: Int, visited: MutableSet<Int>): List<Int>? {
		val scanner = s.last()
		if(visited.contains(scanner)) {
			s.removeLast()
			return null
		}
		visited.add(scanner)
		if(scanner == target) {
			return s.toList()
		}

		results.entries.filter { it -> it.key.first == scanner }.forEach {
			s.add(it.key.second)
			val list = dfs(s, target, visited)
			if(list != null) {
				return list
			}
		}

		s.removeLast()
		return null
	}

	val beacons = input[0].toMutableSet()
	val positions = mutableMapOf<Int, Coord>()
	for (scanner in input.indices.drop(1)) {

		val s = ArrayDeque<Int>()
		val visited = mutableSetOf<Int>()
		s.add(0)
		val (coords, position) = dfs(s, scanner, visited)!!
			.reversed()
			.windowed(2)
			.fold(Pair(input[scanner], Coord(0, 0, 0))) { acc, i ->
				val result = results[Pair(i[1], i[0])]!!
				val beacons = acc.first.map { result.position + it.rotate(result.rotation) }

				Pair(beacons, acc.second.rotate(result.rotation) + result.position)
			}
		beacons.addAll(coords)
		positions[scanner] = position
	}

	val maxPosDistance = positions.values.maxOf { a ->
		positions.values.maxOf { b ->
			val dx = (a.x - b.x).absoluteValue
			val dy = (a.y - b.y).absoluteValue
			val dz = (a.z - b.z).absoluteValue
			dx + dy + dz
		}
	}

	return Pair(beacons.size, maxPosDistance)
}

fun doBeaconsOverlap(a: List<Coord>, b: List<Coord>): Pair<Map<Coord, Coord>, Coord>? {

	// Find rotation
	val distancesA = a.distances()
	var distancesB = mapOf<Distance, List<Pair<Coord, Coord>>>()
	val rotation = ROTATIONS.find { rotation ->
		distancesB = b.map { it.rotate(rotation) }.distances()
		distancesA.keys.toSet().intersect(distancesB.keys.toSet()).size >= 12
	} ?: return null


	val mappings = mutableMapOf<Coord, Coord>()
	distancesA.keys.toSet().intersect(distancesB.keys.toSet())
		.map { Pair(distancesA[it]!!, distancesB[it]!!) }
		.forEach { (a,b) ->
			val (fromA, toA) = a[0]
			val (fromB, toB) = b[0]
			if(mappings.containsKey(fromA)) {
				return@forEach
			}
			if(fromA.x - toA.x == fromB.x - toB.x) {
				mappings[fromA] = fromB
				mappings[toA] = toB
			} else {
				mappings[fromA] = toB
				mappings[toA] = fromB
			}
		}

	return Pair(mappings, rotation)
}

fun List<Coord>.distances(): Map<Distance, List<Pair<Coord, Coord>>> {
	val result = mutableListOf<Triple<Distance, Coord, Coord>>()
	val visited = mutableSetOf<Pair<Coord, Coord>>()

	for(a in this) {
		for(b in this) {
			if(a == b || visited.contains(Pair(a, b)) || visited.contains(Pair(b, a))) {
				continue
			}
			val distance = Distance((a.x - b.x), (a.y - b.y), (a.z - b.z))
			result.add(Triple(distance, a, b))
			visited.add(Pair(a, b))
			visited.add(Pair(b, a))
		}
	}
	return result.groupBy { it.first }
		.mapValues { it.value.map { it.second to it.third } }
}

fun rotations(): Set<Coord> {
	val rotations = mutableSetOf<Coord>()
	for(x in listOf(0, 90, 180, 270)) {
		for(z in listOf(0, 90, 180, 270)) {
			rotations.add(Coord(x, 0, z))
		}
	}
	for(y in listOf(0, 90, 270)) {
		for(z in listOf(0, 90, 180, 270)) {
			rotations.add(Coord(0, y, z))
		}
	}
	return rotations
}

data class Coord(val x: Int, val y: Int, val z: Int) {
	fun rotate(rot: Coord): Coord {
		val rotX = Math.toRadians(rot.x.toDouble())
		val rotY = Math.toRadians(rot.y.toDouble())
		val rotZ = Math.toRadians(rot.z.toDouble())
		var newCoord = Coord(x, y, z)

		var newX = newCoord.x
		var newY = (cos(rotX)*newCoord.y.toDouble() - sin(rotX)*newCoord.z.toDouble()).roundToInt()
		var newZ = (sin(rotX)*newCoord.y.toDouble() + cos(rotX)*newCoord.z.toDouble()).roundToInt()
		newCoord = Coord(newX, newY, newZ)

		newX = (cos(rotY)*newCoord.x.toDouble() + sin(rotY)*newCoord.z.toDouble()).roundToInt()
		newY = newCoord.y
		newZ = (-sin(rotY)*newCoord.x.toDouble() + cos(rotY)*newCoord.z.toDouble()).roundToInt()
		newCoord = Coord(newX, newY, newZ)

		newX = (cos(rotZ)*newCoord.x.toDouble() - sin(rotZ)*newCoord.y.toDouble()).roundToInt()
		newY = (sin(rotZ)*newCoord.x.toDouble() + cos(rotZ)*newCoord.y.toDouble()).roundToInt()
		newZ = newCoord.z
		newCoord = Coord(newX, newY, newZ)

		return newCoord
	}

	operator fun plus(o: Coord): Coord = Coord(x + o.x, y + o.y, z + o.z)
	operator fun minus(o: Coord): Coord = Coord(x - o.x, y - o.y, z - o.z)
	operator fun unaryMinus(): Coord = Coord(-x, -y, -z)
}
data class Distance(val x: Int, val y: Int, val z: Int)
data class Result(val rotation: Coord, val position: Coord, val map: Map<Coord, Coord>)


fun parse(fileName: String): List<List<Coord>> {
	return File(fileName)
		.readText()
		.split("\n\n")
		.map {
			it.split("\n")
				.drop(1)
				.filter { it.isNotEmpty() }
				.map {
					it.split(",")
						.filter { it.isNotEmpty() }
						.let { Coord(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
				}
		}
}