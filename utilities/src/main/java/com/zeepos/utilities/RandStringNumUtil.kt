package com.zeepos.utilities

import java.security.SecureRandom
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln


/**
 * Created by Arif S. on 9/18/20
 */
object RandStringNumUtil {
    private val RANDOM: SecureRandom = SecureRandom()

    //    private val DEFAULT_ALPHABET =
//        "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    private val DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    private val DEFAULT_SIZE = 4

    fun generate(): String? {
        return generate(RANDOM, DEFAULT_ALPHABET, DEFAULT_SIZE)
    }

    fun generate(random: Random, alphabet: CharArray, size: Int): String? {
        require(!(alphabet.isEmpty() || alphabet.size >= 256)) { "alphabet must contain between 1 and 255 symbols." }
        require(size > 0) { "size must be greater than zero." }

        val mask = (2 shl floor(ln(alphabet.size - 1.toDouble()) / ln(2.0))
            .toInt()) - 1
        val step = ceil(1.6 * mask * size / alphabet.size).toInt()
        val idBuilder = StringBuilder()

        while (true) {
            val bytes = ByteArray(step)
            random.nextBytes(bytes)

            for (i in 0 until step) {
                val alphabetIndex: Int = bytes[i].toInt() and mask

                if (alphabetIndex < alphabet.size) {
                    idBuilder.append(alphabet[alphabetIndex])
                    if (idBuilder.length == size) {
                        return idBuilder.toString()
                    }
                }
            }
        }
    }
}