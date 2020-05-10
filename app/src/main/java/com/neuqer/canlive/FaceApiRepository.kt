package com.neuqer.canlive

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Time:2020/5/9 21:20
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
object FaceApiRepository {
    var mRetrofit: Retrofit
    init {
        val logging = HttpLoggingInterceptor()
// set your desired log level
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
        mRetrofit = Retrofit.Builder()
            .baseUrl("https://api-cn.faceplusplus.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }


    fun getApiService(): FaceppService {
        return mRetrofit.create(FaceppService::class.java)
    }

}