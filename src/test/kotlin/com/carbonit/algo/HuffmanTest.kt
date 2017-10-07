package com.carbonit.algo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.net.URL

class HuffmanTest {

    @Test
    fun `huffman encoding on a large text`() {
        val text = HuffmanTest::class.java.getResource("/example/text.txt").readText()
        val huffman = Huffman.of(text)
        val encodeIntoByteArray = huffman.encodeIntoByteArray(text)
        val file = File("encoded-text.txt")
                file.createNewFile()
        file.writeBytes(encodeIntoByteArray)
        assertThat(huffman.decode(encodeIntoByteArray)).isEqualTo(text)
        println()
    }

    @Test
    fun `huffman compression should be lossless`() {
        val text = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat. Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue. Ut in risus volutpat libero pharetra tempor. Cras vestibulum bibendum augue. Praesent egestas leo in pede. Praesent blandit odio eu enim. Pellentesque sed dui ut augue blandit sodales. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aliquam nibh. Mauris ac mauris sed pede pellentesque fermentum. Maecenas adipiscing ante non diam sodales hendrerit.\n" +
                "Ut velit mauris, egestas sed, gravida nec, ornare ut, mi. Aenean ut orci vel massa suscipit pulvinar. Nulla sollicitudin. Fusce varius, ligula non tempus aliquam, nunc turpis ullamcorper nibh, in tempus sapien eros vitae ligula. Pellentesque rhoncus nunc et augue. Integer id felis. Curabitur aliquet pellentesque diam. Integer quis metus vitae elit lobortis egestas. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Morbi vel erat non mauris convallis vehicula. Nulla et sapien. Integer tortor tellus, aliquam faucibus, convallis id, congue eu, quam. Mauris ullamcorper felis vitae erat. Proin feugiat, augue non elementum posuere, metus purus iaculis lectus, et tristique ligula justo vitae magna.\n" +
                "Aliquam convallis sollicitudin purus. Praesent aliquam, enim at fermentum mollis, ligula massa adipiscing nisl, ac euismod nibh nisl eu lectus. Fusce vulputate sem at sapien. Vivamus leo. Aliquam euismod libero eu enim. Nulla nec felis sed leo placerat imperdiet. Aenean suscipit nulla in justo. Suspendisse cursus rutrum augue. Nulla tincidunt tincidunt mi. Curabitur iaculis, lorem vel rhoncus faucibus, felis magna fermentum augue, et ultricies lacus lorem varius purus. Curabitur eu amet."
        val huffman = Huffman.of(text)
        val encodedByteArray = huffman.encodeIntoByteArray(text)
        assertThat(huffman.decode(encodedByteArray)).isEqualTo(text)
    }

    @Test
    fun `should encode text in byte array with ending offset`() {
        val text = " toto . titi"
        val huffman = Huffman.of(text)
        val encodeIntoByteArray = huffman.encodeIntoByteArray(text)
        assertThat(encodeIntoByteArray).isEqualTo(listOf(
                3.toByte(),
                "1001101".toByte(2),
                "1000000".toByte(2)
        ).toByteArray())
        huffman.decode(encodeIntoByteArray)
        assertThat(text).isEqualTo(text)
    }

}
