import java.io.File

fun main() {
	val input = File("input.txt")
		.readLines()
		.map { it -> it.split("").filter { s -> s.isNotEmpty() }.map { s -> s.toInt() } }

	println("Part 01: ${part01(input)}")
	println("Part 02: ${part02(input)}")
}

fun part01(input: List<List<Int>>, steps: Int = 100): Int {
	val grid = input.toMutableList().map { it.toMutableList() }

	val sizeX = grid.size
	val sizeY = grid[0].size
	val positions = (0 until sizeX).map { x -> (0 until sizeY).map { y -> Pair(x,y) } }.flatten()

	return (0 until steps).sumOf {
		val visited = mutableSetOf<Pair<Int, Int>>()

		positions.forEach { (x, y) -> grid[x][y] = (grid[x][y] + 1)%10 }

		positions.filter { (x,y) -> grid[x][y] == 0 }.forEach { (x,y) -> incrementEnergy(grid, visited, x, y) }

		positions.count { (x,y) -> grid[x][y] == 0 }
	}
}

fun part02(input: List<List<Int>>): Long  {
	val grid = input.toMutableList().map { it.toMutableList() }

	val sizeX = grid.size
	val sizeY = grid[0].size

	val positions = (0 until sizeX).map { x -> (0 until sizeY).map { y -> Pair(x,y) } }.flatten()

	var steps = 0L
	while(true) {
		steps++

		val visited = mutableSetOf<Pair<Int, Int>>()
		positions.forEach { (x, y) -> grid[x][y] = (grid[x][y] + 1)%10 }
		positions.filter { (x,y) -> grid[x][y] == 0 }.forEach { (x,y) -> incrementEnergy(grid, visited, x, y) }

		if(grid.all { it.all { energy -> energy == 0 } }) {
			break
		}
	}
	return steps
}

fun incrementEnergy(grid: List<MutableList<Int>>, visited: MutableSet<Pair<Int, Int>>, x: Int, y: Int) {
	if(grid[x][y] != 0) {
		return
	}
	if(visited.contains(Pair(x, y))) {
		return
	}
	visited.add(Pair(x, y))

	(-1..1).asSequence().map { dx -> (-1..1).map { dy -> Pair(dx, dy) } }.flatten()
		.filter { (dx, dy) -> !(dx == 0 && dy == 0) } // Ignore itself
		.filter { (dx, dy) -> x + dx >= 0 && y + dy >= 0 && x + dx < grid.size && y + dy < grid[0].size } // Check corner cases
		.filter { (dx, dy) -> grid[x+dx][y+dy] != 0 } // Do not apply to flashing
		.forEach { (dx, dy) ->
			grid[x+dx][y+dy] = (grid[x+dx][y+dy] + 1)%10
			incrementEnergy(grid, visited, x+dx, y+dy)
		}
}