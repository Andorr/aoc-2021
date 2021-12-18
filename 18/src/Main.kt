import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()
		.map { SnailNumber.parse(it).first as SnailNumber.Pair }

	println("Part01: ${part01(input.map { it.copy() as SnailNumber.Pair })}")
	println("Part02: ${part02(input)}")
}

fun part01(input: List<SnailNumber.Pair>): Long {
	return input
		.reduce { acc, snailNumber -> (acc + snailNumber).reduce() }
		.magnitude()
}

fun part02(input: List<SnailNumber.Pair>): Long {
	return input.map { x ->
		input
			.map { y -> listOf(Pair(x.copy(), y.copy()), Pair(y.copy(), x.copy())) }
			.flatten()
	}
	.flatten()
	.maxOf { (x, y) ->
		((x as SnailNumber.Pair) + (y as SnailNumber.Pair)).reduce().magnitude()
	}
}

enum class Direction {
	LEFT, RIGHT;

	fun opposite(): Direction {
		return if(this == LEFT) RIGHT else LEFT
	}
}

sealed class SnailNumber {
	var parent: Pair? = null

	abstract fun copy(): SnailNumber

	data class Value(var value: Int) : SnailNumber() {
		fun increase(v: Value) { value += v.value }

		override fun copy(): SnailNumber = Value(value)

		fun split(parent: Pair): Pair {
			val pair = Pair(Value(value/2), Value((value + 1)/2))
			pair.parent = parent
			return pair
		}

		override fun toString(): String = value.toString()
	}

	data class Pair(var a: SnailNumber, var b: SnailNumber ): SnailNumber() {

		init {
		    a.parent = this
			b.parent = this
		}

		fun magnitude(): Long {
			val valA = 3L * (if(a is Value) (a as Value).value.toLong() else (a as Pair).magnitude())
			val valB = 2L * (if(b is Value) (b as Value).value.toLong() else (b as Pair).magnitude())
			return valA + valB
		}

		override fun copy(): SnailNumber {
			val copy = Pair(a.copy(), b.copy())
			copy.a.parent = copy
			copy.b.parent = copy
			return copy
		}

		private fun child(direction: Direction) = if(direction == Direction.LEFT) a else b

		private fun findNearestValue(direction: Direction): Value? {
			var dir = direction
			var hasSwappedDir = false

			var prevPair: Pair = this
			var curPair: Pair? = this.parent
			while(curPair != null) {
				val child = curPair.child(dir)
				if(child === prevPair) {
					prevPair = curPair
					curPair = prevPair.parent
				}
				else if(child is Value) {
					return child
				}
				else {
					dir = if(!hasSwappedDir) {
						hasSwappedDir = true
						dir.opposite()
					} else dir
					prevPair = curPair
					curPair = child as Pair
				}
			}
			return null
		}

		private fun explode() {
			if(a is Pair) {
				(a as Pair).explode()
			} else if(b is Pair) {
				(b as Pair).explode()
			} else {
				findNearestValue(Direction.LEFT)?.increase(a as Value)
				findNearestValue(Direction.RIGHT)?.increase(b as Value)
				if(parent?.a == this) {
					parent?.a = Value(0)
				} else {
					parent?.b = Value(0)
				}
			}
		}

		private fun tryExplode(depth: Int = 0): Boolean {
			return when {
				depth >= 4 -> {
					explode()
					true
				}
				a is Pair && (a as Pair).tryExplode(depth + 1) -> true
				b is Pair && (b as Pair).tryExplode(depth + 1) -> true
				else -> false
			}
		}

		private fun trySplit(): Boolean {
			return when {
				a is Value && (a as Value).value >= 10 -> {
					a = (a as Value).split(this)
					true
				}
				a is Pair && (a as Pair).trySplit() -> true
				b is Value && (b as Value).value >= 10 -> {
					b = (b as Value).split(this)
					true
				}
				b is Pair && (b as Pair).trySplit() -> true
				else -> false
			}
		}

		fun reduce(): Pair {
			while(tryExplode(0) || trySplit()){}
			return this
		}

		override fun toString(): String = "[$a,$b]"

		operator fun plus(pair: Pair): Pair = Pair(this, pair)
	}


	companion object {
		fun parse(input: String, i: Int = 0): kotlin.Pair<SnailNumber, Int> {
			if(input[i] == ',' || input[i] == ']') {
				return parse(input, i + 1)
			}

			if(input[i] == '[') {
				val first = parse(input, i + 1)
				val second = parse(input, first.second)
				val pair = Pair(first.first, second.first)
				return Pair(pair, second.second)
			}
			// It is a number
			return Pair(Value(input[i].toString().toInt()), i + 1)
		}
	}

}

