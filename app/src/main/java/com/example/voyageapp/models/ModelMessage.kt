package com.example.voyageapp.models

class ModelMessage {
    var message: String? = null
    var senderId: String? = null
    var timestamp:Long = 0
    var time: String? = null
    var date: String? = null

    constructor()

    constructor(message: String?, senderId: String?, timestamp: Long, time: String, date: String){
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
        this.time = time
        this.date = date
    }
}