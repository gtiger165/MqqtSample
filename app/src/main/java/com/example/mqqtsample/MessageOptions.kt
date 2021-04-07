package com.example.mqqtsample

import android.content.Context

class MessageOptions(context: Context?){
    val userName = Keys.client()
    val password = Keys.password()
    val host = context!!.resources.getString(R.string.mqtt_client_host)

    companion object {
        const val SOLACE_CONNECTION_TIMEOUT = 3
        const val SOLACE_CONNECTION_KEEP_ALIVE_INTERVAL = 60
        const val SOLACE_CONNECTION_CLEAN_SESSION = true
        const val SOLACE_CONNECTION_RECONNECT = true
    }
}
