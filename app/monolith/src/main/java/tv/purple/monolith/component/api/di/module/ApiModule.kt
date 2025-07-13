package tv.purple.monolith.component.api.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import dagger.internal.Provider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import tv.purple.monolith.component.api.data.api.BttvApi
import tv.purple.monolith.component.api.data.api.Chatsen2Api
import tv.purple.monolith.component.api.data.api.ChatsenApi
import tv.purple.monolith.component.api.data.api.ChatterinoApi
import tv.purple.monolith.component.api.data.api.FFZAPApi
import tv.purple.monolith.component.api.data.api.FfzApi
import tv.purple.monolith.component.api.data.api.FlxrsApi
import tv.purple.monolith.component.api.data.api.HomiesApi
import tv.purple.monolith.component.api.data.api.NopApi
import tv.purple.monolith.component.api.data.api.PronounsApi
import tv.purple.monolith.component.api.data.api.ReyohohoApi
import tv.purple.monolith.component.api.data.api.StvApi
import tv.purple.monolith.component.api.data.api.StvOldApi
import tv.purple.monolith.component.api.data.api.TwitchGQLApi
import tv.purple.monolith.component.api.di.scope.BTTV
import tv.purple.monolith.component.api.di.scope.CHATSEN
import tv.purple.monolith.component.api.di.scope.CHATSEN2
import tv.purple.monolith.component.api.di.scope.CHATTERINO
import tv.purple.monolith.component.api.di.scope.FFZ
import tv.purple.monolith.component.api.di.scope.FFZAP
import tv.purple.monolith.component.api.di.scope.FLXRS
import tv.purple.monolith.component.api.di.scope.GQL
import tv.purple.monolith.component.api.di.scope.HOMIES
import tv.purple.monolith.component.api.di.scope.NOP
import tv.purple.monolith.component.api.di.scope.PRN
import tv.purple.monolith.component.api.di.scope.REYOHOHO
import tv.purple.monolith.component.api.di.scope.STV
import tv.purple.monolith.component.api.di.scope.STV_OLD_API
import tv.purple.monolith.core.compat.ClassCompat.getPrivateField
import tv.twitch.android.network.graphql.TwitchApolloClient
import javax.inject.Named
import javax.inject.Singleton

@DisableInstallInCheck
@Module(includes = [ProxyModule::class])
object ApiModule {
    @Singleton
    @Provides
    fun provideScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Named(BTTV)
    fun provideBttvRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.betterttv.net/")

    @Provides
    @Named(STV_OLD_API)
    fun provideStvOldApiRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.7tv.app/")

    @Provides
    @Named(STV)
    fun provideStvRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://7tv.io/")

    @Provides
    @Named(FFZ)
    fun provideFfzRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.frankerfacez.com/")

    @Provides
    @Named(REYOHOHO)
    fun provideReyohohoRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://starege.rhhhhhhh.live/")

    @Provides
    @Named(CHATTERINO)
    fun provideChatterinoRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.chatterino.com/")

    @Provides
    @Named(NOP)
    fun provideNopRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.nopbreak.ru/")

    @Provides
    @Named(FFZAP)
    fun provideFfzapRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.ffzap.com/")

    @Provides
    @Named(PRN)
    fun providePronounsRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://pronouns.alejo.io/")

    @Provides
    @Named(HOMIES)
    fun provideHomiesRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://itzalex.github.io/")

    @Provides
    @Named(FLXRS)
    fun provideFlxrsRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://flxrs.com/")

    @Provides
    @Named(CHATSEN)
    fun provideChatsenRetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://raw.githubusercontent.com/")

    @Provides
    @Named(CHATSEN2)
    fun provideChatsen2RetrofitClient(builder: Retrofit.Builder): Retrofit =
        builder.createRetrofit("https://api.chatsen.app/")

    @Singleton
    @Provides
    @Named(GQL)
    fun provideTwitchGQLApiClient(
        builder: Retrofit.Builder,
        twitchApolloClient: Provider<TwitchApolloClient>
    ): Retrofit {
        return builder
            .baseUrl("https://gql.twitch.tv/gql/")
            .client(twitchApolloClient.get().getPrivateField<OkHttpClient>("injectedClient"))
            .build()
    }

    @Singleton
    @Provides
    fun provideBttvApi(@Named(BTTV) retrofit: Retrofit): BttvApi = retrofit.create()

    @Singleton
    @Provides
    fun provideStvApi(@Named(STV) retrofit: Retrofit): StvApi = retrofit.create()

    @Singleton
    @Provides
    fun provideStvOldApi(@Named(STV_OLD_API) retrofit: Retrofit): StvOldApi = retrofit.create()

    @Singleton
    @Provides
    fun provideFfzApi(@Named(FFZ) retrofit: Retrofit): FfzApi = retrofit.create()

    @Singleton
    @Provides
    fun provideReyohohoApi(@Named(REYOHOHO) retrofit: Retrofit): ReyohohoApi = retrofit.create()

    @Singleton
    @Provides
    fun provideChatterinoApi(@Named(CHATTERINO) retrofit: Retrofit): ChatterinoApi =
        retrofit.create()

    @Singleton
    @Provides
    fun provideNopApi(@Named(NOP) retrofit: Retrofit): NopApi = retrofit.create()

    @Singleton
    @Provides
    fun provideFfzapApi(@Named(FFZAP) retrofit: Retrofit): FFZAPApi = retrofit.create()

    @Singleton
    @Provides
    fun providePronounsApi(@Named(PRN) retrofit: Retrofit): PronounsApi = retrofit.create()

    @Singleton
    @Provides
    fun provideTwitchGQLApi(@Named(GQL) retrofit: Retrofit): TwitchGQLApi = retrofit.create()

    @Singleton
    @Provides
    fun provideHomiesApi(@Named(HOMIES) retrofit: Retrofit): HomiesApi = retrofit.create()

    @Singleton
    @Provides
    fun provideFlxrsApi(@Named(FLXRS) retrofit: Retrofit): FlxrsApi = retrofit.create()

    @Singleton
    @Provides
    fun provideChatsenApi(@Named(CHATSEN) retrofit: Retrofit): ChatsenApi = retrofit.create()

    @Singleton
    @Provides
    fun provideChatsen2Api(@Named(CHATSEN2) retrofit: Retrofit): Chatsen2Api = retrofit.create()

    private inline fun <reified T> Retrofit.create(): T {
        return create(T::class.java)
    }

    private fun Retrofit.Builder.createRetrofit(baseUrl: String): Retrofit {
        return baseUrl(baseUrl).build()
    }
}