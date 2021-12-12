import java.io.File

const val START = "start"
const val END = "end"

fun main() {
	val input = mutableMapOf<String, MutableSet<String>>()
	File("input.txt")
		.readLines()
		.map { it.split("-") }
		.forEach {
			input.getOrPut(it[0]) { mutableSetOf() }.add(it[1])
			input.getOrPut(it[1]) { mutableSetOf() }.add(it[0])
		}

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(map: Map<String, Set<String>>): Long {
	val queue = mutableListOf<String>()
	val visited = mutableMapOf<String, Int>()

	queue.add(START)

	return dfs(map, queue, visited) { it, q, _ ->
		it[0].isUpperCase() || it !in q
	}
}

fun part02(map: Map<String, Set<String>>): Long {
	val queue = mutableListOf<String>()
	val visited = mutableMapOf<String, Int>()

	queue.add(START)

	return dfs(map, queue, visited) { it, _, v ->
		(it[0].isUpperCase() || !v.containsValue(2) || v.getOrDefault(it, 0) == 0)
				&& it != START
	}
}

fun dfs(
	map: Map<String, Set<String>>,
	queue: MutableList<String>,
	visited: MutableMap<String, Int>,
	pred: (it: String, queue: List<String>, visited: Map<String, Int>) -> Boolean
): Long {
	if(queue.isEmpty()) {
		return 0L
	}

	val node = queue.last()
	if(node == END) {
		queue.removeLast()
		return 1L
	}

	val neighbors = map[node]!!
	val numPathsWithEnd = neighbors
		.filter { pred(it, queue, visited) }
		.sumOf {
			queue.add(it)
			if(it[0].isLowerCase()) {
				visited[it] = visited.getOrDefault(it, 0) + 1
			}
			val sum = dfs(map, queue, visited, pred)
			if(it[0].isLowerCase()) {
				visited[it] = visited.getOrDefault(it, 0) - 1
			}
			sum
		}

	queue.removeLast()
	return numPathsWithEnd
}