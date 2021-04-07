package com.example.mqqtsample

class Keys {
    companion object {
        private external fun getClientMqtt(): String
        private external fun getPassMqtt() : String
        fun client(): String{
            return getClientMqtt()
        }

        fun password(): String {
            return getPassMqtt()
        }

        init {
            System.loadLibrary("Keys")
        }
    }
}