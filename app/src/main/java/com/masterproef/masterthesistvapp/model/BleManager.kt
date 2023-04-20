package com.masterproef.masterthesistvapp.model

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.welie.blessed.*
import com.welie.blessed.ConnectionState.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BleManager(context: Context, callback: BleCallback) {

    private val bluetoothCentralManagerCallback: BluetoothCentralManagerCallback =
        object : BluetoothCentralManagerCallback() {

            override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
                Log.i("BluetoothCentralManagerCallback", "Connected to ${peripheral.name}")
                startScanning()
            }

            override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: HciStatus) {
                Log.i("BluetoothCentralManagerCallback", "Connection ${peripheral.name} failed with status $status")
                callback.onPeripheralDisconnected(peripheral)
                startScanning()
            }

            override fun onDisconnectedPeripheral(peripheral: BluetoothPeripheral, status: HciStatus) {
                Log.i("BluetoothCentralManagerCallback", "Disconnected ${peripheral.name} with status $status")
                callback.onPeripheralDisconnected(peripheral)
            }

            override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
                Log.i("BluetoothCentralManagerCallback", "Found peripheral ${peripheral.name}")
                callback.onPeripheralDiscovered(peripheral, scanResult)

                if ((peripheral.state != CONNECTED) and (peripheral.state != CONNECTING) and (peripheral.state != DISCONNECTING)) {
                    bluetoothCentralManager.stopScan()
                    bluetoothCentralManager.connectPeripheral(peripheral, peripheralCallback)
                }
            }
        }

    private val peripheralCallback: BluetoothPeripheralCallback =
        object : BluetoothPeripheralCallback() {

            override fun onServicesDiscovered(peripheral: BluetoothPeripheral) {
                val userIdCharacteristic = peripheral.getCharacteristic(Identifiers.IDENTIFICATION_SERVICE_UUID, Identifiers.USER_ID_CHARACTERISTIC_UUID)
                if(userIdCharacteristic != null){
                    peripheral.readCharacteristic(userIdCharacteristic)
                }

                val ppIntervalCharacteristic = peripheral.getCharacteristic(Identifiers.HRV_SERVICE_UUID, Identifiers.PP_INTERVAL_CHARACTERISTIC_UUID)
                if(ppIntervalCharacteristic != null){
                    peripheral.setNotify(ppIntervalCharacteristic, true)
                }
            }

            override fun onCharacteristicUpdate(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: GattStatus) {
                if (characteristic.uuid == Identifiers.USER_ID_CHARACTERISTIC_UUID) {
                    callback.onUserIdReceived(peripheral, ByteBuffer.wrap(value).int)
                }

                if (characteristic.uuid == Identifiers.PP_INTERVAL_CHARACTERISTIC_UUID) {
                    val buffer = ByteBuffer.wrap(value)
                    buffer.order(ByteOrder.LITTLE_ENDIAN)
                    callback.onPeakToPeakReceived(peripheral, buffer.long, buffer.float)
                }
            }
        }

    private var bluetoothCentralManager: BluetoothCentralManager = BluetoothCentralManager(context, bluetoothCentralManagerCallback, Handler(Looper.getMainLooper()))

    fun startScanning() {
        if (!bluetoothCentralManager.isScanning) {
            bluetoothCentralManager.scanForPeripheralsWithServices(arrayOf(Identifiers.ADVERTISEMENT_UUID))
        }
    }

    fun disableBle() {
        bluetoothCentralManager.stopScan()
        for(peripheral in bluetoothCentralManager.connectedPeripherals){
            val ppIntervalCharacteristic = peripheral.getCharacteristic(Identifiers.HRV_SERVICE_UUID, Identifiers.PP_INTERVAL_CHARACTERISTIC_UUID)
            if(ppIntervalCharacteristic != null){ peripheral.setNotify(ppIntervalCharacteristic, false) }
            bluetoothCentralManager.cancelConnection(peripheral)
        }
    }

}