import java.io.File
import kotlin.math.max
import kotlin.math.min

data class I(var from: Int, var to: Int)
data class Step(val on: Boolean, val c: Cuboid)
data class Cuboid(var x: I, var y: I, var z: I) {
	fun isValid(): Boolean {
		return x.to >= x.from && y.to >= y.from && z.to >= z.from
	}

	operator fun minus(o: Cuboid): Pair<Cuboid, List<Cuboid>> {

		val cuboids = mutableSetOf<Cuboid>()

		// x
		cuboids.add(Cuboid(I(this.x.from, o.x.from - 1), I(this.y.from, this.y.to), I(this.z.from, this.z.to)))
		cuboids.add(Cuboid(I(o.x.to + 1, this.x.to), I(this.y.from, this.y.to), I(this.z.from, this.z.to)))

		// y
		cuboids.add(Cuboid(I(o.x.from, o.x.to), I(o.y.to + 1, this.y.to), I(o.z.from, o.z.to)))
		cuboids.add(Cuboid(I(o.x.from, o.x.to), I(this.y.from, o.y.from-1), I(o.z.from, o.z.to)))

		// z
		cuboids.add(Cuboid(I(o.x.from, o.x.to), I(this.y.from, this.y.to), I(o.z.to + 1, this.z.to)))
		cuboids.add(Cuboid(I(o.x.from, o.x.to), I(this.y.from, this.y.to), I(this.z.from, o.z.from - 1)))

		val intersect = Cuboid(
			I(max(this.x.from, o.x.from), min(this.x.to, o.x.to)),
			I(max(this.y.from, o.y.from), min(this.y.to, o.y.to)),
			I(max(this.z.from, o.z.from), min(this.z.to, o.z.to))
		)

		cuboids.forEach { c ->
			c.x.from = max(c.x.from, this.x.from)
			c.x.to = min(c.x.to, this.x.to)
			c.y.from = max(c.y.from, this.y.from)
			c.z.to = min(c.z.to, this.z.to)
			c.z.from = max(c.z.from, this.z.from)
			c.z.to = min(c.z.to, this.z.to)
		}

		return (intersect) to cuboids.filter { it.isValid() }
	}
}

fun main() {
	val input = parse("input.txt")

	println("Part01: ${solve(input.filter { (_, c) ->
		c.x.from >= -50 && c.x.to <= 50 &&
		c.y.from >= -50 && c.y.to <= 50 &&
		c.z.from >= -50 && c.z.to <= 50
	})}")
	println("Part02: ${solve(input)}")
}

fun solve(input: List<Step>): Long {
	var cuboids = mutableSetOf(input[0].c)
	input.forEach { step ->
		val update = mutableSetOf<Cuboid>()
		cuboids.forEach { c ->
			val (intersect, splits) = c - step.c
			if(intersect.isValid()) {
				splits.forEach {
					update.add(it)
				}
			} else {
				update.add(c)
			}
		}

		if(step.on) {
			update.add(step.c)
		}
		cuboids = update
	}


	return cuboids.sumOf { it ->
		(it.x.to + 1 - it.x.from).toLong()*(it.y.to + 1 - it.y.from).toLong()*(it.z.to+ 1 - it.z.from).toLong()
	}
}


fun parse(fileName: String): List<Step> {
	return File(fileName)
		.readLines()
		.map { line ->
			line.split(" ")
				.let { it
					val intervals = it[1].split(",")
						.map { numbers ->
							numbers.slice(2 until numbers.length)
								.split("..")
								.let { n ->
									I(n[0].toInt(), n[1].toInt())
								}
						}
					Step(it[0] == "on", Cuboid(intervals[0], intervals[1], intervals[2]))
				}
		}
}