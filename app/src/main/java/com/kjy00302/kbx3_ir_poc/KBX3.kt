package com.kjy00302.kbx3_ir_poc

import kotlin.experimental.xor

object KBX3 {
    private val MAGIC =
        byteArrayOf(38, 48, 49, 53, 36, 50, 35, 56, 41, 64, 95, 33, 40, 68, 94, 46, 34)
    private const val BIT_TIME = 417  // (1second / 2400baud)
    private const val BURST_TIME = BIT_TIME * 3 / 16  // From IrDA spec

    fun newShowColorPacket(r: Byte, g: Byte, b: Byte, w: Byte): ByteArray {
        return newPacket(65, byteArrayOf(r, g, b, w))
    }

    fun newSetMemoryPacket(n: Int, r: Byte, g: Byte, b: Byte, w: Byte): ByteArray {
        return newPacket((128 + n).toByte(), byteArrayOf(r, g, b, w))
    }

    fun newSetMemoryEnd1Packet(cnt: Byte): ByteArray {
        return newPacket(-98, byteArrayOf(cnt))
    }

    fun newSetMemoryEnd2Packet(cnt: Byte): ByteArray {
        return newPacket(-97, byteArrayOf(cnt))
    }

    fun newPacket(cmd: Byte, data: ByteArray): ByteArray {
        val arr = ByteArray(14)
        arr[0] = -86
        arr[1] = cmd
        arr[13] = 85
        data.copyInto(arr, 2)
        return arr
    }

    fun scramblePacket(array: ByteArray) {
        val l: Byte = 0 // 0 ~ 255, fixed to 0 due to negative modulo issue
        for (i in 1..11) {
            array[i] = array[i] xor l xor MAGIC[i - 1 + l % 5]
        }
        array[12] = MAGIC[array[1].toInt() and 0x0f] xor l
        val s = array[1] % 10 + 2
        array[s] = array[12].also { array[12] = array[s] }
    }

    fun toIRPattern(bytes: ByteArray): IntArray {
        val seq = mutableListOf<Int>()
        for (byte in bytes.map { it.toInt() }) {
            seq.add(BURST_TIME)
            seq.add(BIT_TIME - BURST_TIME)
            for (i in 0..7) {
                if ((byte shr i) and 1 == 0) {
                    seq.add(BURST_TIME)
                    seq.add(BIT_TIME - BURST_TIME)
                } else {
                    // Android api does not support zero on pattern
                    seq[seq.size - 1] += BIT_TIME
                }
            }
            seq[seq.size - 1] += BIT_TIME
        }
        return seq.toIntArray()
    }
}
