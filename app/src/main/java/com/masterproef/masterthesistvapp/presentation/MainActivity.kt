package com.masterproef.masterthesistvapp.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.masterproef.masterthesistvapp.viewmodel.MainViewModel
import com.masterproef.masterthesistvapp.model.Peripheral
import com.masterproef.masterthesistvapp.model.PeripheralManager

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            ), 0
        )

        setContent { PeripheralList(viewModel) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.enableBle(this.applicationContext)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disableBle()
    }
}

@Composable
fun PeripheralList(viewModel: MainViewModel) {

    Column {
        for (peripheral in viewModel.peripherals.values) {
            PeripheralElement(peripheral)
        }
    }

}


@Composable
fun PeripheralElement(peripheral: Peripheral) {
    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = peripheral.deviceName,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TitleRow("Basic Info")
            InfoRow("Device address", peripheral.deviceAddress)
            InfoRow("User id", peripheral.userId.toString())


            TitleRow("Raw Presence Data")
            InfoRow("RSSI", peripheral.rssi.toString())
            InfoRow("Tx power", peripheral.txPower.toString())
            InfoRow("Distance", peripheral.distance.toString() + " meters")

            TitleRow("Raw HRV Data")
            InfoRow("Last timestamp", peripheral.lastPeakToPeakTimestamp.toString())
            //InfoRow("Last PP value", peripheral.peakToPeakArray[0].toString())
        }
    }
}

@Composable
fun TitleRow(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
    )
}

@Composable
fun InfoRow(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
    }
}
