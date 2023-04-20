package com.masterproef.masterthesistvapp.model

import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.masterproef.masterthesistvapp.model.PresenceDetector.calculateDistance
import com.welie.blessed.BluetoothPeripheral

object PeripheralManager: BleCallback {

    val peripherals = mutableStateMapOf<String, Peripheral>()
    var bleManager: BleManager? = null

    override fun onPeripheralDiscovered(bluetoothPeripheral: BluetoothPeripheral, scanResult: ScanResult) {
        var peripheral = peripherals[bluetoothPeripheral.address]

        if (peripheral != null) {
            peripheral.rssi = scanResult.rssi
            peripheral.txPower = scanResult.txPower
            peripheral.lastAdvertisementTimestamp = scanResult.timestampNanos
            peripheral.distance = calculateDistance(scanResult.txPower, scanResult.rssi)
            peripherals.remove(bluetoothPeripheral.address)

        } else {
            peripherals.forEach { (key, value) ->
                run {
                    if (value.deviceName == bluetoothPeripheral.name) {
                        peripheral = peripherals[key]
                        peripheral!!.rssi = scanResult.rssi
                        peripheral!!.txPower = scanResult.txPower
                        peripheral!!.lastAdvertisementTimestamp = scanResult.timestampNanos
                        peripheral!!.distance =
                            calculateDistance(scanResult.txPower, scanResult.rssi)
                        peripherals.remove(bluetoothPeripheral.address)
                    }
                }
            }
        }

        if(peripheral == null) {
            peripheral = Peripheral(bluetoothPeripheral, scanResult)
        }

        peripherals[bluetoothPeripheral.address] = peripheral!!
    }

    override fun onPeripheralDisconnected(bluetoothPeripheral: BluetoothPeripheral) {
        peripherals.remove(bluetoothPeripheral.address)
    }

    override fun onUserIdReceived(bluetoothPeripheral: BluetoothPeripheral, userId: Int) {
        val peripheral = peripherals[bluetoothPeripheral.address]

        if(peripheral != null) {
            peripheral.userId = userId

            peripherals.remove(bluetoothPeripheral.address)
            peripherals[bluetoothPeripheral.address] = peripheral
        }
    }

    override fun onPeakToPeakReceived(bluetoothPeripheral: BluetoothPeripheral, timestamp: Long, pp: Float) {
        val peripheral = peripherals[bluetoothPeripheral.address]

        if(peripheral != null) {
            if((peripheral.lastPeakToPeakTimestamp != null) && (timestamp - peripheral.lastPeakToPeakTimestamp!! < 1000)) {
                peripheral.peakToPeakArray = mutableListOf(pp)
            }else{
                peripheral.peakToPeakArray.add(pp)
            }

            peripheral.lastPeakToPeakTimestamp = timestamp
            peripheral.arrayLength = peripheral.peakToPeakArray.size

            peripherals.remove(bluetoothPeripheral.address)
            peripherals[bluetoothPeripheral.address] = peripheral
        }
    }

    fun enableBle(context: Context){
        if(bleManager == null){
            bleManager = BleManager(context, this)
        }
        bleManager!!.startScanning()
    }

    fun disableBle(){
        bleManager?.disableBle()
    }

}