package com.example.gnss_sync

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import org.json.JSONObject

class MainActivity: FlutterActivity() {
    private val channel = "bluetooth.channel"
    private val deviceFoundList = mutableListOf<Any>()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {

        GeneratedPluginRegistrant.registerWith(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                "getBlue" -> bluetoothWrapper(result)
                "discoverBlue" -> discoverDevices(result)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun bluetoothWrapper(result: MethodChannel.Result) {
        val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val defaultAdapter = bluetoothManager.adapter
        if (defaultAdapter == null) {
            result.error("Bluetooth adapter doesn't exist on this device", null, null)
        } else {
            result.success("bluetooth adapter exists on device")
        }
        defaultAdapter?.let {
            val requestEnableBt = 1
            if (!defaultAdapter.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                result.success("im here")
                startActivityForResult(enableIntent, requestEnableBt)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("MissingPermission")
    private fun discoverDevices(result: MethodChannel.Result) {
        val list: MutableList<String> = arrayListOf()
        val s = JSONObject()
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val myAdapter = bluetoothManager.adapter
        myAdapter.startDiscovery()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val receiver = object : BroadcastReceiver() {
            @SuppressLint("NewApi")
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {bt ->
                            s.put("name", bt.name)
                            s.put("address", bt.address)
                            s.put("bondState", bt.bondState.toString())
                            s.put("type", bt.type.toString())
                            bt.uuids?.let {
                                if(it.isNotEmpty()) {
                                    s.put("uuid", it[0].uuid.toString())
                                }
                            }
                            deviceFoundList.add(bt)
                            println("device found")
                            list.add(s.toString())
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        result.success(list.toList())
                    }
                    "" -> println("broadcast receiver intent.action has no attribute")
                    null -> println("broadcast receiver intent.action was null")
                }
            }
        }
        registerReceiver(receiver, filter)
    }

}
