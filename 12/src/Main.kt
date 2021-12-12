import java.io.File

fun main() {
	val input = parse("input.txt")

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part02(map: Map<String, Set<String>>): Long {
	val queue = mutableListOf<String>()
	val visited = mutableMapOf<String, Int>()

	queue.add("start")

	return dfs2(map, queue, visited)
}

fun dfs2(map: Map<String, Set<String>>, queue: MutableList<String>, visited: MutableMap<String, Int>): Long {
	if(queue.isEmpty()) {
		return 0L
	}

	val node = queue.last()
	if(node == "end") {
		// println(queue)
		queue.removeLast()
		return 1L
	}

	val neighbors = map[node]!!
	val numPathsWithEnd = neighbors
		.filter { it[0].isUpperCase() || !visited.containsValue(2) || (visited.containsValue(2) && visited.getOrDefault(it, 0) == 0)}
		.filter { it != "start" }
		.sumOf {
			queue.add(it)
			if(it[0].isLowerCase()) {
				visited[it] = visited.getOrDefault(it, 0) + 1
			}
			val sum = dfs2(map, queue, visited)
			if(it[0].isLowerCase()) {
				visited[it] = visited.getOrDefault(it, 0) - 1
			}
			sum
		}

	queue.removeLast()
	return numPathsWithEnd
}

fun part01(map: Map<String, Set<String>>): Long {
	val queue = mutableListOf<String>()

	queue.add("start")

	return dfs(map, queue)
}

fun dfs(map: Map<String, Set<String>>, queue: MutableList<String>): Long {
	if(queue.isEmpty()) {
		return 0L
	}

	val node = queue.last()
	if(node == "end") {
		queue.removeLast()
		return 1L
	}

	val neighbors = map[node]!!
	val numPathsWithEnd = neighbors
		.filter { it[0].isUpperCase() || !(it in queue) }
		.sumOf {
			queue.add(it)
			dfs(map, queue)
		}

	queue.removeLast()
	return numPathsWithEnd
}


fun parse(fileName: String): Map<String, Set<String>> {
	val map = mutableMapOf<String, MutableSet<String>>()
	File(fileName)
		.readLines()
		.map { it.split("-") }
		.forEach {
			if(!map.containsKey(it[0])) {
				map[it[0]] = mutableSetOf<String>()
			}
			if(!map.containsKey(it[1])) {
				map[it[1]] = mutableSetOf<String>()
			}
			map[it[0]]?.add(it[1])
			map[it[1]]?.add(it[0])
		}
	return map
}