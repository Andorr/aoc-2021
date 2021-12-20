import java.io.File
import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun main() {
	val input = parse("input.txt")

	println("Part01: ${enhanceImage(input.second, input.first, 2).size}")
	println("Part02: ${  enhanceImage(input.second, input.first, 50).size}")
}

fun enhanceImage(input: List<String>, enhancement: String, steps: Int = 1): Set<Coord> {

	val image = input.mapIndexed { y, s ->
		s.mapIndexed { x, c -> Pair(c, Coord(x, y)) }
			.filter { it.first == '#' }
			.map { it.second }
	}.flatten().toSet()

	val hasBorderEdgeCase = enhancement.first() == '#' && enhancement.last() == '.'

	return (0 until steps).fold(image) { img, step ->
		val minX = img.minOf { it.x }
		val maxX = img.maxOf { it.x }
		val minY = img.minOf { it.y }
		val maxY = img.maxOf { it.y }

		val positions = (minY-1..maxY+1).map {
				y -> (minX-1..maxX+1).map { x -> Coord(x, y) }
		}.flatten()

		positions.fold(mutableSetOf<Coord>()) { newImage, coord ->
			val x = coord.x
			val y = coord.y
			val decimal = (-1..1).map { dy -> (-1..1)
				.map { dx -> Coord(x + dx, y + dy) } }
				.flatten()
				.map {
					if(hasBorderEdgeCase && step%2 == 1) {
						if(!img.contains(it)) 1 else 0
					} else {
						if(img.contains(it)) 1 else 0
					}
				}
				.let {
					it.mapIndexed { i, bit -> bit*(2.0.pow(it.size - i - 1).toInt()) }
						.sum()
				}

			if(enhancement[decimal] == '#') {
				newImage.add(Coord(x, y))
			}
			newImage
		}.let { newImage ->
			if(hasBorderEdgeCase && step%2 == 0) {
				positions.filter { !newImage.contains(it) }.toSet()
			} else {
				newImage
			}
		}
	}
}

fun parse(fileName: String): Pair<String, List<String>> {
	val lines = File(fileName)
		.readLines()

	return Pair(lines[0], lines.slice(2 until lines.size))
}

fun Set<Coord>.print() {
	val minX = this.minOf { it.x }
	val maxX = this.maxOf { it.x }
	val minY = this.minOf { it.y }
	val maxY = this.maxOf { it.y }

	(minY-1..maxY+1).map { y ->  (minX - 1..maxX + 1)
		.map { x -> if(contains(Coord(x, y))) '#' else '.' }.joinToString("") }
		.forEach { println(it) }
}

data class Coord(val x: Int, val y: Int)
