package com.masterproef.masterthesistvapp.model

import android.bluetooth.le.ScanResult
import com.masterproef.masterthesistvapp.model.Calculator.calculateDistance
import com.masterproef.masterthesistvapp.model.PresenceDetector.calculateDistance
import com.welie.blessed.BluetoothPeripheral

data class Peripheral(private val peripheral: BluetoothPeripheral, private val scanResult: ScanResult) {

    var deviceName: String = peripheral.name
    var deviceAddress: String = peripheral.address

    var userId: Int? = null

    // Advertisement data
    var lastAdvertisementTimestamp: Long = scanResult.timestampNanos
    var txPower: Int = scanResult.txPower
    var rssi: Int = scanResult.rssi
    var distance: Double = calculateDistance(scanResult.txPower, scanResult.rssi)

    // PP data
    var lastPeakToPeakTimestamp: Long? = null
    var peakToPeakArray = mutableListOf<Float>()
    var arrayLength: Int = 0
}