import java.io.File

// The input consist of 14 segments of these code chunks.
//
// 		x = ((z%26L + B[i]) != w).toLong()
// 		z /= A[i]
// 		z *= 25L*x + 1L
// 		z += (w + C[i])*x
//
// We notice that A only consist of 1s and 26s, where A[i] == 1 means that B[i] is a positive number.
// A[i] == 26 means that B[i] is a negative number. Since "x" is the variable that decides if "z" should
// increase in value or not, "z" increases when (z%26 + B[i]) is 1, or not equal to the input "w". However,
// when B[i] is positive, (z%26 + B[i]) != "w" is always true, making "z" increase. This means that "z" must
// decrease the same number of times it increases, by an equal amount of the last decrease. Furthermore, this
// means that there exist pairs that increase and decrease by an equal amount, where A[i] == 1 for the increase
// and A[k] == 26 for the decrease. All of these pairs must then satisfy the following condition for the decrease
// to happen.
//
//		Condition: w_i + c_i + b_k = w_k
//
// By construction a map of pairs from A, and then looking for values of w_i and w_k that satisfy the condition,
// we are able to construct our number.

data class MonadProgram(val a: IntArray, val b: IntArray, val c: IntArray)

fun main() {

	val program = parse("input.txt")

	// Calculate the pairs
	val pairs = mutableMapOf<Int, Int>()
	val stack = mutableListOf<Int>()
	program.a.forEachIndexed { i, it ->
		if(it == 1) {
			stack.add(i)
		} else {
			pairs[stack.removeLast()] = i
		}
	}

	// Part 01: Largest number - begin high
	var number = IntArray(14) { 0 }
	for (r in pairs) {
		var w1 = 9
		var w2 = 9
		while(w1 + program.c[r.key] + program.b[r.value] != w2) {
			if(w2 == 1) {
				w1--
				w2 = 9
			} else {
				w2--
			}
		}
		if(w1 != 0) {
			number[r.key] = w1
			number[r.value] = w2
		}
	}
	println("Part01: ${number.joinToString("")}")

	// Part 02: Smallest number - begin low
	for (r in pairs) {
		var w1 = 1
		var w2 = 1
		while(w1 + program.c[r.key] + program.b[r.value] != w2) {
			if(w2 == 9) {
				w1++
				w2 = 1
			} else {
				w2++
			}
		}
		if(w1 != 10) {
			number[r.key] = w1
			number[r.value] = w2
		}
	}
	println("Part02: ${number.joinToString("")}")
}

fun parse(fileName: String): MonadProgram {
	return File(fileName)
		.readLines()
		.chunked(18)
		.let { lines ->
			MonadProgram(
				lines.map{ chunk -> chunk[4].split(" ")[2].toInt()}.toIntArray(),
				lines.map{ chunk -> chunk[5].split(" ")[2].toInt()}.toIntArray(),
				lines.map{ chunk -> chunk[15].split(" ")[2].toInt()}.toIntArray()
			)
		}
}