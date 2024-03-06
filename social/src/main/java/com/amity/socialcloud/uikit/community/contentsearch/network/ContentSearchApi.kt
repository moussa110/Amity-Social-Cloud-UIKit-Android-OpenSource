package com.amity.socialcloud.uikit.community.contentsearch.network


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ContentSearchApi {
    companion object {
       private fun getRetrofit(): Retrofit {
            val httpOk = OkHttpClient.Builder()

            httpOk.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build()

                chain.proceed(request)
            }

            httpOk.connectTimeout(600, TimeUnit.MINUTES)
            httpOk.connectTimeout(600, TimeUnit.MINUTES)

            return Retrofit.Builder()
                    .baseUrl("https://beta.amity.services/search/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpOk.build())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
        }

        fun getContentSearchWebServices(): ContentSearchWebServices {
            return getRetrofit().create(ContentSearchWebServices::class.java)
        }
    }
}