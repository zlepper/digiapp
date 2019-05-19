package dk.zlepper.digiapp.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mmDamRetrofit = Retrofit.Builder()
    .baseUrl("https://mm-dam.dev.digizuite.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val mmDevRetrofit = Retrofit.Builder()
    .baseUrl("https://mm5.dev.digizuite.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()