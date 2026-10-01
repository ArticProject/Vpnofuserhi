package com.example.util

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkLatencyMeterTest {

    @Test
    fun measureLiveLatencyReturnsPositiveValue() {
        val latency = NetworkLatencyMeter.measureLiveLatency(
            vlessUrl = "vless://test@8.8.8.8:443",
            fallbackIp = "8.8.8.8"
        )
        assertTrue("Latency must be positive, was $latency", latency > 0)
    }
}
