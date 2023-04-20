package com.masterproef.masterthesistvapp.model

import kotlin.math.pow

object Calculator {

    fun calculateDistance(txPower: Int, RSSI: Int): Double {
        return 10.0.pow(((-txPower - RSSI) / (10 * 4).toDouble()))
    }
}