package tv.purple.monolith.component.api.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import retrofit2.Retrofit
import tv.purple.monolith.component.api.data.api.proxy.PublicProxyApi
import javax.inject.Singleton

@DisableInstallInCheck
@Module
object ProxyModule {
    @Provides
    fun providePPRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu.cdn-perfprod.com/")

    @Singleton
    @Provides
    fun providePP(retrofit: Retrofit): PublicProxyApi = retrofit.create()

    private inline fun <reified T> Retrofit.create(): T {
        return create(T::class.java)
    }

    private fun Retrofit.Builder.createRetrofit(baseUrl: String): Retrofit {
        return baseUrl(baseUrl).build()
    }
}