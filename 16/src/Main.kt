import java.io.File

typealias Result = Pair<Packet, Int>

fun main() {
	val input = File("input.txt").readText()
		.trim()
		.chunked(2)
		.joinToString("") { it.toUByte(16).toString(2).padStart(8, '0') }

	println("Part01: ${part01(input)}")
	println("Part02: ${part02(input)}")
}

fun part01(input: String): Long = parsePacket(input, 0).first.sumOfVersions
fun part02(input: String): Long = parsePacket(input, 0).first.eval()

fun parsePacket(input: String, start: Int): Result {
	val packet = Packet(0, PacketType.LIT, 0, mutableListOf())
	packet.version = (input.slice(start until start + 3)).toLong(2)
	packet.type = PacketType.fromInt(input.slice(start + 3 until start + 6).toInt(2))
	var cursor = start + 6

	when(packet.type) {
		PacketType.LIT -> { // Number literal
			var number = ""
			do {
				number += input.slice(cursor + 1 until cursor + 1 + 4)
				cursor += 5
			} while(input[cursor-5] == '1')
			packet.value = number.toLong(2)
		}
		else -> { // Operation
			val lengthId = if(input[cursor++] == '0') 0 else 1
			val lengthNumBits = if(lengthId == 0) 15 else 11
			val length = input.slice(cursor until cursor + lengthNumBits).toLong(2)
			cursor += lengthNumBits
			if(lengthId == 0) { // length = total length of sub-packets
				val stopAt = cursor + length
				while(cursor < stopAt) {
					val result = parsePacket(input, cursor)
					packet.children += result.first
					cursor = result.second
				}
			}
			else { // length = number of sub-packets
				repeat(length.toInt()) {
					val result = parsePacket(input, cursor)
					packet.children += result.first
					cursor = result.second
				}
			}
		}
	}

	return Result(packet, cursor)
}

data class Packet(var version: Long, var type: PacketType, var value: Long, var children: MutableList<Packet>) {
	val sumOfVersions: Long get() = version + children.sumOf { it.sumOfVersions }

	fun eval(): Long {
		return when(type) {
			PacketType.SUM -> children.sumOf { it.eval() }
			PacketType.PROD -> children.fold(1L) { acc, packet -> acc * packet.eval() }
			PacketType.MIN -> children.minOf { it.eval() }
			PacketType.MAX -> children.maxOf { it.eval() }
			PacketType.LIT -> value
			PacketType.GT -> if(children[0].eval() > children[1].eval()) 1 else 0
			PacketType.LT -> if(children[0].eval() < children[1].eval()) 1 else 0
			PacketType.EQ -> if(children[0].eval() == children[1].eval()) 1 else 0
		}
	}
}

enum class PacketType {
	SUM,
	PROD,
	MIN,
	MAX,
	LIT,
	GT,
	LT,
	EQ;

	companion object {
		fun fromInt(value: Int) = values()[value]
	}
}