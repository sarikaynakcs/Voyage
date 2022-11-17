package com.example.voyageapp.models

import android.provider.ContactsContract.CommonDataKinds.Email

class ModelUser {
    var name: String = ""
    var username: String = ""
    var profileImage: String = ""
    var email: String = ""
    var userType: String = ""
    var timestamp:Long = 0
    var uid: String = ""
    var barcodeId: String = ""
    var friends: Map<String, Boolean> = emptyMap()

    constructor(){}

    constructor(name: String, email: String, uid: String, username: String, profileImage: String, userType: String, timestamp: Long, barcodeId: String, friends: Map<String, Boolean>){
        this.name = name
        this.email = email
        this.uid = uid
        this.username = username
        this.profileImage = profileImage
        this.userType = userType
        this.timestamp = timestamp
        this.barcodeId = barcodeId
        this.friends = friends
    }
}