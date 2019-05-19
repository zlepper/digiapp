package dk.zlepper.digiapp.services.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)

val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

val mmDamRetrofit = Retrofit.Builder()
    .baseUrl("https://mm-dam.dev.digizuite.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(httpClient)
    .build()
val mmDevRetrofit = Retrofit.Builder()
    .baseUrl("https://mm5.dev.digizuite.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(httpClient)
    .build()