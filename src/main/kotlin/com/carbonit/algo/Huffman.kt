package com.carbonit.algo

import com.sun.jmx.remote.internal.ArrayQueue
import java.util.*

class Huffman(private val dictEncode: Map<String, String>) {

    fun encodeIntoByteArray(text: String): ByteArray {
        val encodedText = split(text).asSequence().map { dictEncode[it] }.joinToString("")
        val endingOffset = BYTE_SIZE - (encodedText.length % BYTE_SIZE)
        val textToEncode = encodedText + List(endingOffset) { _ -> '0' }.toCharArray().joinToString("")
        val size = 1 + encodedText.length / BYTE_SIZE + (if (endingOffset > 0) 1 else 0)
        val byteArray = ByteArray(size)

        byteArray[0] = endingOffset.toByte()
        var indexInByeArray = 1
        var indexInText = 0
        while (indexInText < textToEncode.length) {
            val byte = textToEncode.substring(indexInText, indexInText+ BYTE_SIZE).toByte(2)
            byteArray[indexInByeArray] = byte
            indexInText += BYTE_SIZE
            indexInByeArray++
        }
        return byteArray
    }

    fun decode(encodedByteArray: ByteArray): String {
        val dictDecode = dictEncode.entries.asSequence().associateBy({ it.value }) { it.key }
        val offset = encodedByteArray.first().toInt()
        val byteString = encodedByteArray.asSequence().drop(1).map { byteToString(it) }.joinToString("").dropLast(offset)
        return decodeWithLoop(dictDecode, byteString)
        //return decode(dictDecode, byteString, "", "")
    }

    private fun byteToString(byte: Byte): String {
        return ("0000000" + byte.toString(2)).substring(byte.toString(2).length)
    }

    private fun decodeWithLoop(dictDecode: Map<String, String>, byteString: String): String {
        var decryptedIndex = 0
        var possibleIndex = 0
        val decryptedText = StringBuilder("")
        while (decryptedIndex < byteString.length) {
            possibleIndex += 1
            val possibleByte = byteString.substring(decryptedIndex, possibleIndex)
            val word = dictDecode[possibleByte]
            if (word != null) {
                decryptedText.append(word)
                decryptedIndex = possibleIndex
            }
        }
        return decryptedText.toString()
    }

//    private tailrec fun decode(dictDecode: Map<String, String>, byteString: String, possibleByte: String, decryptedText: String): String {
//        if (byteString.isEmpty() && possibleByte.isEmpty()) return decryptedText
//        else if (dictDecode.containsKey(possibleByte))
//            return decode(dictDecode, byteString, "", decryptedText + dictDecode[possibleByte])
//        else return decode(dictDecode, byteString.drop(1), possibleByte + byteString.take(1), decryptedText)
//    }

    companion object {
        val BYTE_SIZE = 7

        fun of(text: String): Huffman {
            val huffmanTree = buildHuffmanTree(text)
            val dictEncode: Map<String, String> = huffmanTree.buildDictEncode()
            return Huffman(dictEncode)
        }

        private fun split(text: String): List<String> {
            return text.split(Regex("(?<=[\\s,;.!?])|(?=[\\s,;.!?])"))
        }

        private fun buildHuffmanTree(text: String): HuffmanTree {
            val wordFrequencies = frequencies(text)
            return if (wordFrequencies.isEmpty()) {
                EmptyHuffmanTree()
            } else {
                buildHuffmanTree(wordFrequencies)
            }
        }

        private fun frequencies(text: String): List<LeafHuffmanTree> {
            return split(text)
                    .asSequence()
                    .groupingBy { it }
                    .eachCount()
                    .toList()
                    .map { LeafHuffmanTree(it.first, it.second) }
        }

        private fun buildHuffmanTree(wordFrequencies: List<HuffmanTree>): HuffmanTree {
            val queue = PriorityQueue<HuffmanTree>(wordFrequencies.size, kotlin.Comparator { o1, o2 -> o1.frequency().compareTo(o2.frequency()) })
            queue.addAll(wordFrequencies)
            return buildHuffmanTree(queue)
        }

        private tailrec fun buildHuffmanTree(queue: PriorityQueue<HuffmanTree>): HuffmanTree {
            if (queue.size < 2) {
                return queue.poll()
            }
            val rightNode = queue.poll()
            val leftNode = queue.poll()
            val parentNode = NodeHuffmanTree(rightNode.frequency() + leftNode.frequency(), leftNode, rightNode)
            queue.add(parentNode)
            return buildHuffmanTree(queue)
        }
    }

}

private interface HasFrequency {
    fun frequency(): Int
}

private sealed class HuffmanTree : HasFrequency {
    fun buildDictEncode(): Map<String, String> {
        //return buildDictEncode("", this)
        return buildDictEncodeTC(LinkedList(Arrays.asList(Pair("", this))), mutableMapOf())
    }

    //for tailrec https://xor0110.wordpress.com/2014/12/31/tail-recursive-tree-traversal-example-in-scala/
    fun buildDictEncode(bitCode: String, huffmanTree: HuffmanTree): Map<String, String> {
        return when (huffmanTree) {
            is NodeHuffmanTree -> buildDictEncode(bitCode + "0", huffmanTree.leftChild) + buildDictEncode(bitCode + "1", huffmanTree.rightChild)
            is LeafHuffmanTree -> mapOf(huffmanTree.word to bitCode)
            else -> emptyMap()
        }
    }

    tailrec fun buildDictEncodeTC(queue: LinkedList<Pair<String, HuffmanTree>>, acc: MutableMap<String, String>) : Map<String, String> {
        if (queue.isEmpty()) return acc

        val elmt = queue.poll()
        val tree = elmt.second
        val bitCode = elmt.first
        when (tree) {
            is NodeHuffmanTree -> {
                queue.add(Pair(bitCode + "0", tree.leftChild))
                queue.add(Pair(bitCode + "1", tree.rightChild))
            }
            is LeafHuffmanTree -> {
                acc.put(tree.word, bitCode)
            }
        }
        return buildDictEncodeTC(queue, acc)
    }
}

private class EmptyHuffmanTree : HuffmanTree() {
    override fun frequency(): Int {
        return 0
    }
}

private data class NodeHuffmanTree(val frequency: Int, val leftChild: HuffmanTree, val rightChild: HuffmanTree) : HuffmanTree() {
    override fun frequency(): Int {
        return frequency
    }
}

private data class LeafHuffmanTree(val word: String, val frequency: Int) : HuffmanTree() {
    override fun frequency(): Int {
        return frequency
    }
}