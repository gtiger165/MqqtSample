package com.example.mqqtsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*
import kotlin.concurrent.schedule
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {
    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewMsgPayload.movementMethod = ScrollingMovementMethod()

        setMqttCallBack()

        textViewNumMsgs.text = "0"

        btnPub.setOnClickListener { view ->
            var snackBarMsg = "Cannot publish to empty topic"
            val topic = editTextPubTopic.text.toString().trim()
            if (topic.isNotEmpty()) {
                snackBarMsg = try {
                    mqttClient.publish(topic, editTextMsgPayload.text.toString())
                    "Published to topic '$topic'"
                } catch (ex: MqttException) {
                    "Error publishing to topic: $topic"
                }
            }
            Snackbar.make(view, snackBarMsg, 300)
                .setAction("Action", null).show()
        }

        btnSub.setOnClickListener { view ->
            var snackbarMsg: String = "Cannot subscribe to empty topic!"
            val topic = editTextSubTopic.text.toString().trim()

            if (topic.isNotEmpty()) {
                snackbarMsg = try {
                    mqttClient.subscribe(topic)
                    "Subscribed to topic '$topic'"
                } catch (ex: MqttException) {
                    "Error subscribing to topic: $topic"
                }
            }

            Snackbar.make(view, snackbarMsg, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

        Timer("CheckMqttConnection", false).schedule(300) {
            if (!mqttClient.isConnected()) {
                Snackbar.make(textViewNumMsgs, "Failed to connect to: '${mqttClient.serverUri}' within 3 seconds", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
            }
        }
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                val snackBarMsg = "Connected to host lost:\n'${mqttClient.serverUri}'."
                Log.w("Debug", "connectComplete: ",)
                Snackbar.make(findViewById(android.R.id.content), snackBarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w(
                    "Debug",
                    "messageArrived: message received from host '${mqttClient.serverUri}': $message"
                )
                textViewNumMsgs.text = ("${textViewNumMsgs.text.toString().toInt() + 1}")
                val str: String =
                    "------------" + Calendar.getInstance().time + "-------------\n$message\n${textViewMsgPayload.text}"
                textViewMsgPayload.text = str
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.w(
                    "Debug",
                    "deliveryComplete: Message published to host '${mqttClient.serverUri}'"
                )
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                val snackBarMsg = "Connected to host:\n'${mqttClient.serverUri}'."
                Log.w("Debug", "connectComplete: ",)
                Snackbar.make(findViewById(android.R.id.content), snackBarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        })
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }
}