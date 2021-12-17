import kotlin.math.absoluteValue
import kotlin.math.max

data class Vec(var x: Int, var y: Int)
data class Bound(val from: Int, val to: Int)
data class Bounds(val x: Bound, val y: Bound)
data class Result(val success: Boolean, val maxY: Int)

val input = Bounds(Bound(138, 184), Bound(-125, -71))
val test = Bounds(Bound(20, 30), Bound(-10, -5))

fun main() {
	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(bounds: Bounds): Int {
	// Since the max height revolves around reaching v_y = 0, and let if fall down again
	// the x-coord does not matter. So it is all about how large y-velocity we decide to through the ball up
	// in the air that decides the height. At whatever v_y we choose, when the probe falls down again
	// and reaches y = 0, the v_y == -v0_y. Therefore, we want that the next v_y should be min_y.
	// This means that v0_y must be abs(min_y) - 1, since a_y = -1. Falling from max height with v_y = 0 to
	// v_y = abs(min_y) gives a height of H = 1+2+3+4..(min_y-1)+(min_y) = (min_y+1)*min_y/2.
	val minY = bounds.y.from
	return (minY + 1)*minY/2
}

fun part02(bounds: Bounds): Int {
	return (1..bounds.x.to).map { x ->
		(bounds.y.from..bounds.y.from.absoluteValue)
		.map { y -> Pair(x, y) }
	}
		.flatten()
		.count { (x, y) ->
			simulate(Vec(0, 0), Vec(x,y), bounds).success
		}
}

fun simulate(start: Vec, velocity: Vec, bounds: Bounds): Result {

	val position = Vec(start.x, start.y)
	var maxY = position.y

	var boundsState = position.bounds(bounds.x, bounds.y)
	while(boundsState.x != 1 && boundsState.y != 1) {
		position.x += velocity.x
		position.y += velocity.y

		if(position.y > maxY) {
			maxY = position.y
		}

		boundsState = position.bounds(bounds.x, bounds.y)
		if(boundsState.x != -1 && boundsState.y != -1) {
			return Result(boundsState.x == 0 && boundsState.y == 0, maxY)
		}

		velocity.x = max(0, velocity.x.dec())
		velocity.y = velocity.y.dec()
	}

	return Result(boundsState.x == 0 && boundsState.y == 0, maxY)
}

fun Vec.bounds(boundX: Bound, boundY: Bound): Vec {
	val bx = if(this.x < boundX.from) -1 else if(this.x <= boundX.to) 0 else 1
	val by = if(this.y > boundY.to) -1 else if(this.y >= boundY.from) 0 else 1
	return Vec(bx, by)
}