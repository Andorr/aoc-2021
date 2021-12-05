import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
	val input = parse("input.txt")
	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<Entry>): Long {
	val map = mutableMapOf<Coord, Long>()

	input
		.filter { it.from.x == it.to.x || it.from.y == it.to.y }
		.forEach { entry ->
			entry.points().forEach { map[it] = map.getOrDefault(it, 0) + 1 }
		}

	return map.entries.count { it.value >= 2 }.toLong()
}

fun part02(input: List<Entry>): Long {

	val map = mutableMapOf<Coord, Long>()
	input.forEach { entry ->
		entry.points().forEach { map[it] = map.getOrDefault(it, 0) + 1 }
	}

	return map.entries.count { it.value >= 2 }.toLong()
}

fun parse(filename: String): List<Entry> {
	val reg = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")

	return File(filename)
		.readLines()
		.map { reg.matchEntire(it)!! }
		.map {
			Entry(
				Coord(it.groupValues[1].toLong(), it.groupValues[2].toLong()),
				Coord(it.groupValues[3].toLong(), it.groupValues[4].toLong())
			)
		}
}

data class Coord(val x: Long, val y: Long)
data class Entry(val from: Coord, val to: Coord) {
	fun points(): List<Coord> {
		val ps = mutableListOf<Coord>()
		var x = from.x
		var y = from.y
		val valX = max(min(to.x - x, 1), -1)
		val valY = max(min(to.y - y, 1), -1)

		while(true) {
			ps.add(Coord(x, y))
			if(x == to.x && y == to.y) {
				break
			}

			x += valX
			y += valY
		}
		return ps
	}
}