package kh.edu.rupp.ite.furniturestore.model.api.service

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .addHeader(
            "Authorization", "Bearer " + "token"
            )
            .build()
        return chain.proceed(request)
    }
}