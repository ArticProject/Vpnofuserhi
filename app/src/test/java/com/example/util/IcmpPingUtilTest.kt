package com.example.util

import org.junit.Assert.assertTrue
import org.junit.Test

class IcmpPingUtilTest {

    @Test
    fun executeIcmpPingReturnsPositiveLatency() {
        val latency = IcmpPingUtil.executeIcmpPing("8.8.8.8", timeoutSec = 2)
        // Latency should be a valid positive number
        assertTrue("Expected latency > 0, but was $latency", latency > 0)
    }
}
