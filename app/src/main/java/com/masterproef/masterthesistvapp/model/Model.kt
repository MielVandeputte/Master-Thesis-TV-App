package com.masterproef.masterthesistvapp.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.welie.blessed.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Model {

    @SuppressLint("StaticFieldLeak")
    private var central: BluetoothCentralManager? = null
    private var appContext: Context? = null

    private var connectedPeripherals: MutableMap<String, Peripheral> = mutableMapOf()

    fun startScanning(context: Context) {

        if(appContext == null || central == null) {
            appContext = context.applicationContext

            this.central = BluetoothCentralManager(
                appContext!!, bluetoothCentralManagerCallback, Handler(Looper.getMainLooper())
            )
        }

        if(central!!.isScanning) {
            central!!.scanForPeripheralsWithServices(arrayOf(Identifiers.ADVERTISEMENT_UUID))
        }
    }

    fun stopScanning(){
        connectedPeripherals = mutableMapOf()
        central?.close()
    }

    private val bluetoothCentralManagerCallback: BluetoothCentralManagerCallback =
        object : BluetoothCentralManagerCallback() {

            override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
                super.onConnectedPeripheral(peripheral)
                Log.i("BluetoothCentralManagerCallback", "Connected to ${peripheral.name}")

                connectedPeripherals[peripheral.address]?.connected = true

                // Block will not be executed if central doesn't exist
                if (central?.isScanning == false) {
                    central!!.scanForPeripheralsWithServices(arrayOf(Identifiers.ADVERTISEMENT_UUID))
                }
            }

            override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: HciStatus) {
                super.onConnectionFailed(peripheral, status)
                Log.i("BluetoothCentralManagerCallback","Connection ${peripheral.name} failed with status $status")

                connectedPeripherals.remove(peripheral.address)

                if(central?.isScanning == false){
                    central!!.scanForPeripheralsWithServices(arrayOf(Identifiers.ADVERTISEMENT_UUID))
                }
            }

            override fun onDisconnectedPeripheral(peripheral: BluetoothPeripheral, status: HciStatus) {
                super.onDisconnectedPeripheral(peripheral, status)
                Log.i("BluetoothCentralManagerCallback","Disconnected ${peripheral.name} with status $status")

                connectedPeripherals.remove(peripheral.address)
            }

            override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
                super.onDiscoveredPeripheral(peripheral, scanResult)
                Log.i("BluetoothCentralManagerCallback","Found peripheral ${peripheral.name}")

                if( peripheral.address in connectedPeripherals.keys) {

                    connectedPeripherals[peripheral.address]?.txPower = scanResult.txPower
                    connectedPeripherals[peripheral.address]?.rssi  = scanResult.rssi

                }else{

                    central?.stopScan()
                    connectedPeripherals[peripheral.address] = Peripheral(peripheral, scanResult)
                    central?.connectPeripheral(peripheral, peripheralCallback)

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
                    connectedPeripherals[peripheral.address]?.userId = ByteBuffer.wrap(value).int
                }

                if (characteristic.uuid == Identifiers.PP_INTERVAL_CHARACTERISTIC_UUID) {

                    val buffer = ByteBuffer.wrap(value)
                    buffer.order(ByteOrder.LITTLE_ENDIAN)

                    connectedPeripherals[peripheral.address]?.timestamp = buffer.long
                    connectedPeripherals[peripheral.address]?.pp = buffer.float

                }
            }
        }
}