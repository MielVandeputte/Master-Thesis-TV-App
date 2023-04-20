package com.masterproef.masterthesistvapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.masterproef.masterthesistvapp.model.PeripheralManager

class MainViewModel : ViewModel() {

    val peripherals = PeripheralManager.peripherals

    fun enableBle(context: Context){
        PeripheralManager.enableBle(context)
    }

    fun disableBle(){
        PeripheralManager.disableBle()
    }




}