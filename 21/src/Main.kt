import kotlin.math.max
import kotlin.math.min

data class Game(var p1Pos: Int, var p2Pos: Int, var p1Score: Int, var p2Score: Int)

fun main() {
	val test = 4 to 8
	val input = 8 to 6

	input.also { (p1, p2) ->
		println("Part01: ${part01(p1, p2)}")
		println("Part02: ${part02(p1, p2)}")
	}
}

fun part01(startP1: Int, startP2: Int): Int {
	var dice = 1
	var rolls = 0

	fun roll(): Int {
		val roll = dice
		dice = (dice)%100 + 1 // inc and loop
		rolls++
		return roll
	}

	var (p1, score1) = Pair(startP1-1, 0)
	var (p2, score2) = Pair(startP2-1, 0)

	main@ while(score1 < 1000 && score2 < 1000) {
		p1 = (p1 + roll() + roll() + roll())%10
		score1 += p1 + 1

		if(score1 >= 1000) {
			break
		}

		p2 = (p2 + roll() + roll() + roll())%10
		score2 += p2 + 1
	}

	return rolls * min(score1, score2)
}


fun part02(startP1: Int, startP2: Int): Long {
	val winScore = 21


	var gameQueue = mutableMapOf<Game, Long>(
		Game(startP1-1, startP2-1, 0 ,0) to 1L // Game + number of wins
	)
	var p1Wins = 0L
	var p2Wins = 0L

	// Generate all possible win states
	while(gameQueue.isNotEmpty()) {
		val (game, numWins) = gameQueue.entries.first()
		gameQueue.remove(game)

		for(diceX in (1..3)) {
			for(diceY in (1..3)) {
				for(diceZ in (1..3)) {
					val p1Pos = (game.p1Pos + diceX + diceY + diceZ)%10
					val p1Score = game.p1Score + (p1Pos + 1)
					if(p1Score >= winScore) {
						p1Wins += numWins
						continue
					}

					for(diceXX in (1..3)) {
						for (diceYY in (1..3)) {
							for (diceZZ in (1..3)) {
								val p2Pos = (game.p2Pos + diceXX + diceYY + diceZZ)%10
								val p2Score = game.p2Score + (p2Pos + 1)
								if(p2Score >= winScore) {
									p2Wins += numWins
									continue
								}

								val newGame = Game(p1Pos, p2Pos, p1Score, p2Score)
								gameQueue[newGame] = numWins + gameQueue.getOrDefault(newGame, 0)
							}
						}
					}

				}
			}
		}
	}

	return max(p1Wins, p2Wins)
}