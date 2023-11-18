package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.core.AppCore
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = AppPreference.get(AppCore.get().applicationContext).getToken()
        val request = if(token != null) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}