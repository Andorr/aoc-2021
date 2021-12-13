import java.io.File

typealias P = Pair<Int, Int>

fun main() {
	val input = parse("input.txt")

	println("Part01: ${part01(input)}")
	println("Part02:")
	part02(input).print()
}

fun part01(input: Instructions): Long {
	val paper = input.paper.toMutableSet()

	if(input.folds[0].second == 0) {
		foldY(paper, input.folds[0].first)
	} else {
		foldX(paper, input.folds[0].second)
	}

	return paper.size.toLong()
}

fun part02(input: Instructions): MutableSet<P> {
	val paper = input.paper.toMutableSet()

	input.folds.forEach {
		if(it.second == 0) {
			foldY(paper, it.first)
		} else {
			foldX(paper, it.second)
		}
	}

	return paper
}

fun foldX(paper: MutableSet<P>, y: Int) {
	paper.filter { it.second > y }
		.forEach { pos ->
			paper.remove(pos)
			paper.add(Pair(pos.first, y - (pos.second-y)))
		}
}

fun foldY(paper: MutableSet<P>, x: Int) {
	paper.filter { it.first > x }
		.forEach { pos ->
			paper.remove(pos)
			paper.add(Pair(x - (pos.first-x), pos.second))
		}
}

fun MutableSet<P>.print() {
	val maxX = maxOf { it.first }
	val maxY = maxOf { it.second }

	for(y in (0..maxY)) {
		for(x in (0..maxX)) {
			if(contains(P(x, y))) {
				print("#")
			} else {
				print(".")
			}
		}
		println()
	}
}

fun parse(fileName: String): Instructions {
	val paper = mutableSetOf<P>()
	val folds = mutableListOf<P>()
	var isFolds = false

	File(fileName)
		.readLines()
		.forEach {
			if(it.isEmpty()) {
				isFolds = true
				return@forEach
			}
			if(isFolds) {
				val (coord, value) = it.slice(("fold along ".length until it.length)).split("=")
				folds.add(if(coord == "x") P(value.toInt(), 0) else P(0, value.toInt()))
				return@forEach
			}
			val (x, y) = it.split(",")
			paper.add(P(x.toInt(), y.toInt()))
		}

	return Instructions(paper, folds)
}

class Instructions(val paper: Set<P>, val folds: List<P>)
