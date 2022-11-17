package com.example.voyageapp.Common

import com.example.voyageapp.Remote.IGoogleAPIService
import com.example.voyageapp.Remote.RetrofitClient

object Common {

    private const val GOOGLE_API_URL="https://maps.googleapis.com/"
    val googleAPIService:IGoogleAPIService
        get()=RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}