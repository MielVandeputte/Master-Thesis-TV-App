package com.masterproef.masterthesistvapp.model

import android.bluetooth.le.ScanResult
import com.welie.blessed.BluetoothPeripheral

interface BleCallback {

    fun onPeripheralDiscovered(bluetoothPeripheral: BluetoothPeripheral, scanResult: ScanResult)

    fun onPeripheralDisconnected(bluetoothPeripheral: BluetoothPeripheral)

    fun onUserIdReceived(bluetoothPeripheral: BluetoothPeripheral, int: Int)

    fun onPeakToPeakReceived(bluetoothPeripheral: BluetoothPeripheral, long: Long, float: Float)

}