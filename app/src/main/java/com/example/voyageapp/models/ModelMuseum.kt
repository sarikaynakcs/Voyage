package com.example.voyageapp.models

class ModelMuseum {

    //variables, must match as in firebase
    var id:String = ""
    var museumName:String = ""
    var overview:String = ""
    var history:String = ""
    var exhibitions:String = ""
    var museumType:String = ""
    var museumCity:String = ""
    var establishment:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    //empty constructor, require by firebase
    constructor()

    //parameterized constructor
    constructor(id: String, museumName: String, museumType: String, overview: String, history: String, exhibitions: String, museumCity: String, establishment: String, timestamp: Long, uid: String) {
        this.id = id
        this.museumName = museumName
        this.museumType = museumType
        this.overview = overview
        this.history = history
        this.exhibitions = exhibitions
        this.museumCity = museumCity
        this.establishment = establishment
        this.timestamp = timestamp
        this.uid = uid
    }

}