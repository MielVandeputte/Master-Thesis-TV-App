package com.masterproef.masterthesistvapp.model

import android.bluetooth.le.ScanResult
import com.welie.blessed.BluetoothPeripheral

class Peripheral(peripheral: BluetoothPeripheral ,scanResult: ScanResult) {

    var connected: Boolean = false

    var deviceName: String
    val deviceAddress: String
    var userId: Int? = null

    var txPower: Int
    var rssi: Int

    var timestamp: Long? = null
    var pp: Float? = null

    init {
        this.deviceName = peripheral.name
        this.deviceAddress = peripheral.address

        this.txPower = scanResult.txPower
        this.rssi = scanResult.rssi
    }

}