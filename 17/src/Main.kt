
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
	var bestY = 0
	main@ for(x in (1..bounds.x.to)) {
		var y = 1
		while(y < 1000) {
			var result = simulate(Vec(0, 0), Vec(x, y), bounds)
			if(result.success && result.maxY > bestY) {
				bestY = result.maxY
			}
			y++
		}
	}
	return bestY
}

fun part02(bounds: Bounds): Int {
	val velocities = mutableSetOf<Vec>()

	main@ for(x in (-bounds.x.to..bounds.x.to)) {
		var y = bounds.y.from
		while(y < 1000) {
			val result = simulate(Vec(0, 0), Vec(x, y), bounds)
			if(result.success) {
				velocities.add(Vec(x, y))
			}
			y++
		}
	}
	return velocities.size
}

fun simulate(start: Vec, velocity: Vec, bounds: Bounds): Result {

	val position = Vec(start.x, start.y)
	var maxY = position.y

	var step = 0
	var boundsState = position.bounds(bounds.x, bounds.y)
	while(boundsState.x != 1 && boundsState.y != 1) {

		position.x += velocity.x
		position.y += velocity.y

		if(position.y > maxY) {
			maxY = position.y
		}

		boundsState = position.bounds(bounds.x, bounds.y)
		if(boundsState.x == 0 && boundsState.y == 0) {
			return Result(true, maxY)
		}
		if(boundsState.x >= 1 || boundsState.y >= 1) {
			return Result(false, maxY)
		}

		velocity.x = velocity.x.towardsZero(1)
		velocity.y = velocity.y.dec()

		step++
	}

	return Result(false, maxY)
}

fun Vec.bounds(boundX: Bound, boundY: Bound): Vec {
	val bx = if(this.x < boundX.from) -1 else if(this.x <= boundX.to) 0 else 1
	val by = if(this.y > boundY.to) -1 else if(this.y >= boundY.from) 0 else 1
	return Vec(bx, by)
}

fun Int.towardsZero(step: Int): Int {
	return if(this > 0) this - step else if(this < 0 ) this + step else 0
}

