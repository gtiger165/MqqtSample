package com.example.mqqtsample

import android.content.Context
import android.util.Log
import com.example.mqqtsample.MessageOptions.Companion.SOLACE_CONNECTION_CLEAN_SESSION
import com.example.mqqtsample.MessageOptions.Companion.SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL
import com.example.mqqtsample.MessageOptions.Companion.SOLACE_CONNECTION_RECONNECT
import com.example.mqqtsample.MessageOptions.Companion.SOLACE_CONNECTION_TIMEOUT
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.lang.Exception
import kotlin.jvm.Throws

class MqttClientHelper (context: Context?){

    companion object{
        const val TAG = "MqqtClientHelper"
    }

    var mqttAndroidClient : MqttAndroidClient
    val serverUri =MessageOptions(context).host
    private val clientId : String = MqttClient.generateClientId()

    fun setCallback(callback: MqttCallbackExtended) {
        mqttAndroidClient.setCallback(callback)
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.w(TAG, "connectComplete: $serverURI")
            }

            override fun connectionLost(cause: Throwable?) {}

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w(TAG, "messageArrived: ${message.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        connect(context)
    }

    private fun connect(context: Context?) {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = SOLACE_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = SOLACE_CONNECTION_CLEAN_SESSION
        mqttConnectOptions.userName = MessageOptions(context).userName
        mqttConnectOptions.password = MessageOptions(context).password.toCharArray()
        mqttConnectOptions.connectionTimeout = SOLACE_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "onFailure: failed to connect to $serverUri; $exception")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun subscribe(subscriptionTopic: String, qos: Int = 0) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w(TAG, "onSuccess: Subscribed to topic '$subscriptionTopic'")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "onFailure: Subscribed to topic '$subscriptionTopic' failed!")
                }

            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d(TAG, "publish: Message published to topic `$topic: $msg`")
        } catch (ex: MqttException) {
            Log.d(TAG, "Error Publishing to $topic: " + ex.message)
            ex.printStackTrace()
        }
    }

    fun isConnected(): Boolean{
        return mqttAndroidClient.isConnected
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }
}