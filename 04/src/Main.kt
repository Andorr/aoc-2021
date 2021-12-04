import java.io.File
import java.util.*

fun main() {
	val input = parse("input.txt")
	println(input.order)
	println(input.boards[0])
	// println("Part 01: ${part01(input)}")
	println("Part 02: ${part02(input)}")
}

fun part01(game: Game): Long {

	game.order.forEach { it ->

		game.boards.forEach{ board -> board.setNumber(it) }

		var winner = game.boards.find { board -> board.hasBingo() }
		if(winner != null) {
			return winner.b.filter { cell -> !cell.marked }.sumOf { it.num }*it
		}

	}

	return -1
}

fun part02(game: Game): Long {

	var prevWinner: Board? = null
	var lastNumber: Long = 0
	game.order.forEach { num ->

		game.boards.forEach{ board -> board.setNumber(num) }

		var winners = game.boards.filter { board -> board.hasBingo() }
			.forEach { winner ->
				game.boards.remove(winner)
				println(num)
				prevWinner = winner
				lastNumber = num
			}
	}

	if(prevWinner != null) {
		return prevWinner!!.b.filter { it -> !it.marked }.sumOf { it.num }*lastNumber
	}

	return -1
}

fun parse(filename: String): Game {
	val lines = File(filename)
		.readLines()

	var order = lines[0].split(",").map { it -> it.toLong() }

	var scanner = Scanner(lines.slice(1 until lines.size).joinToString(" "))

	var boards = mutableListOf<Board>()
	var board = mutableListOf<Cell>()
	while (scanner.hasNext()) {
		board.add(Cell(scanner.nextLong(), false))

		if(board.size >= 5 * 5) {
			boards.add(Board(board))
			println(board.size)
			board = mutableListOf<Cell>()
		}
	}

	return Game(order, boards)
}

class Game(val order: List<Long>, var boards: MutableList<Board>)

data class Board(var b: MutableList<Cell>) {

	fun setNumber(number: Long): Int {
		val index = b.indexOfFirst { it -> it.num == number }
		if(index != -1) {
			b[index].marked = true
		}
		return index
	}

	fun hasBingo(): Boolean {
		// Check rows
		for(i in 0 until 5) {
			val isRowMarked = b.slice(5*i until 5*i + 5).all { it -> it.marked }
			if(isRowMarked) {
				return true
			}
		}

		// Check columns
		main@ for(i in 0 until 5) {
			var marks = mutableListOf<Boolean>()
			for(j in 0 until 5) {
				marks.add(b[j*5 + i].marked)
			}
			val isColMarked = marks.all { it -> it }
			if (isColMarked) {
				return true
			}
		}

		return false
	}
}

data class Cell(val num: Long, var marked: Boolean)