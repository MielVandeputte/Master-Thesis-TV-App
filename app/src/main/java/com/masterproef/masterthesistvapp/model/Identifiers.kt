package com.masterproef.masterthesistvapp.model

import java.util.*

object Identifiers {

    val CUD_DESCRIPTOR_UUID: UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")
    val CCC_DESCRIPTOR_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val ADVERTISEMENT_UUID: UUID = UUID.fromString("07b2ac95-f87c-4fb8-a500-5674097e643d")

    val IDENTIFICATION_SERVICE_UUID: UUID = UUID.fromString("07b2ac95-f87c-4fb8-a500-5674097e643d")
    val USER_ID_CHARACTERISTIC_UUID: UUID = UUID.fromString("07b2ac95-f87c-4fb8-a500-5674097e643d")

    val PRESENCE_SERVICE_UUID: UUID = UUID.fromString("6746f62f-8a9d-450a-821e-f645baaee63c")

    val HRV_SERVICE_UUID: UUID = UUID.fromString("f21f5538-0802-4b05-a3f7-b4566f4dbd90")
    val PP_INTERVAL_CHARACTERISTIC_UUID: UUID = UUID.fromString("f21f5538-0802-4b05-a3f7-b4566f4dbd90")
}