package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance private constructor() {
    private val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api: ApiService = retrofit.create(ApiService::class.java)

    companion object {
        private var instance: RetrofitInstance? = null

        fun get(): RetrofitInstance {
            if (instance == null) instance = RetrofitInstance()
            return instance!!
        }
    }
}