package tv.purple.monolith.component.api.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import retrofit2.Retrofit
import tv.purple.monolith.component.api.data.api.proxy.LuminousASApi
import tv.purple.monolith.component.api.data.api.proxy.LuminousEU2Api
import tv.purple.monolith.component.api.data.api.proxy.LuminousEUApi
import tv.purple.monolith.component.api.data.api.proxy.PerfprodApi
import tv.purple.monolith.component.api.di.scope.L_AS
import tv.purple.monolith.component.api.di.scope.L_EU
import tv.purple.monolith.component.api.di.scope.L_EU2
import tv.purple.monolith.component.api.di.scope.PP_AS
import tv.purple.monolith.component.api.di.scope.PP_EU
import tv.purple.monolith.component.api.di.scope.PP_EU2
import tv.purple.monolith.component.api.di.scope.PP_EU3
import tv.purple.monolith.component.api.di.scope.PP_EU4
import tv.purple.monolith.component.api.di.scope.PP_EU5
import tv.purple.monolith.component.api.di.scope.PP_NA
import tv.purple.monolith.component.api.di.scope.PP_SA
import javax.inject.Named
import javax.inject.Singleton

@DisableInstallInCheck
@Module
object ProxyModule {
    @Provides
    @Named(L_EU)
    fun provideLEURetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://eu.luminous.dev/")

    @Provides
    @Named(L_EU2)
    fun provideLEU2RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://eu2.luminous.dev/")

    @Provides
    @Named(L_AS)
    fun provideLASRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://as.luminous.dev/")

    @Provides
    @Named(PP_EU)
    fun providePP_EURetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu.cdn-perfprod.com/")

    @Provides
    @Named(PP_EU2)
    fun providePP_EU2RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu2.cdn-perfprod.com/")

    @Provides
    @Named(PP_EU3)
    fun providePP_EU3RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu3.cdn-perfprod.com/")

    @Provides
    @Named(PP_EU4)
    fun providePP_EU4RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu4.cdn-perfprod.com/")

    @Provides
    @Named(PP_EU5)
    fun providePP_EU5RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-eu5.cdn-perfprod.com/")

    @Provides
    @Named(PP_NA)
    fun providePP_NARetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-na.cdn-perfprod.com/")

    @Provides
    @Named(PP_AS)
    fun providePP_ASRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-as.cdn-perfprod.com/")

    @Provides
    @Named(PP_SA)
    fun providePP_SARetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://lb-sa.cdn-perfprod.com/")

    @Singleton
    @Provides
    fun provideLEUApi(@Named(L_EU) retrofit: Retrofit): LuminousEUApi = retrofit.create()

    @Singleton
    @Provides
    fun provideLEU2Api(@Named(L_EU2) retrofit: Retrofit): LuminousEU2Api = retrofit.create()

    @Singleton
    @Provides
    fun provideLASApi(@Named(L_AS) retrofit: Retrofit): LuminousASApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_EU(@Named(PP_EU) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_EU2(@Named(PP_EU2) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_EU3(@Named(PP_EU3) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_EU4(@Named(PP_EU4) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_EU5(@Named(PP_EU5) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_NA(@Named(PP_NA) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_AS(@Named(PP_AS) retrofit: Retrofit): PerfprodApi = retrofit.create()

    @Singleton
    @Provides
    fun providePP_SA(@Named(PP_SA) retrofit: Retrofit): PerfprodApi = retrofit.create()

    private inline fun <reified T> Retrofit.create(): T {
        return create(T::class.java)
    }

    private fun Retrofit.Builder.createRetrofit(baseUrl: String): Retrofit {
        return baseUrl(baseUrl).build()
    }
}