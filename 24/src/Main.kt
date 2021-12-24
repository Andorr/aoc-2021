
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

fun main() {

	var A = listOf(1, 1, 1, 1, 26, 1, 26, 26, 1, 26, 1, 26, 26, 26)
	var B = listOf(10, 15, 14, 15, -8, 10, -16, -4, 11, -3, 12, -7, -15, -7)
	var C = listOf(2, 16, 9, 0, 1, 12, 6, 6, 3, 5, 9, 3, 2, 3)
	var W = listOf(9, 9, 9, 9, 1, 9, 2, 4, 9, 9, 9, 8, 8, 3)

	// Construed by A:
	var pairs = mapOf(
		3 to 4,
		5 to 6,
		2 to 7,
		8 to 9,
		10 to 11,
		1 to 12,
		0 to 13
	)

	// Part 01: Largest number - begin high
	var number = IntArray(14) { 0 }
	for (r in pairs) {
		var w1 = 9
		var w2 = 9
		while(w1 + C[r.key] + B[r.value] != w2) {
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
		while(w1 + C[r.key] + B[r.value] != w2) {
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