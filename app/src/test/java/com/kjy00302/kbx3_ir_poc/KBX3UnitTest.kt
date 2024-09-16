package com.kjy00302.kbx3_ir_poc

import org.junit.Test

import org.junit.Assert.*

class KBX3UnitTest {
    @Test
    fun encoderCheck() {
        val source = KBX3.newShowColorPacket(0, 0, 0, 0)
        val target = byteArrayOf(-86, 103, 48, 49, 53, 56, 50, 35, 56, 41, 64, 95, 36, 85)
        KBX3.scramblePacket(source)
        assertArrayEquals(source, target)
    }

    @Test
    fun irSequenceCheck() {
        val target = intArrayOf(78, 338, 78, 754, 78, 754, 78, 754, 78, 1170)
        assertArrayEquals(KBX3.toIRPattern(byteArrayOf(-86)), target)
    }
}