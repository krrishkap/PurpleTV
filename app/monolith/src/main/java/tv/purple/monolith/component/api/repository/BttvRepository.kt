package tv.purple.monolith.component.api.repository

import io.reactivex.Single
import tv.purple.monolith.component.api.data.source.BttvRemoteDataSource
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.models.data.badges.BadgeSet
import tv.purple.monolith.models.data.emotes.EmoteSet
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BttvRepository @Inject constructor(
    private val bttvDataSource: BttvRemoteDataSource
) {
    fun getGlobalBttvEmotes(useWebp: Boolean): Single<EmoteSet> {
        return bttvDataSource.getGlobalBttvEmotes(useWebp)
            .onErrorResumeNext { throwable ->
                LoggerImpl.error("[BTTV] Cannot fetch global emotes: ${throwable.message}")
                Single.just(EmoteSet.EMPTY)
            }
    }

    fun getChannelBttvEmotes(channelId: Int, useWebp: Boolean): Single<EmoteSet> {
        return bttvDataSource.getChannelBttvEmotes(channelId = channelId, useWebp = useWebp)
            .onErrorResumeNext { throwable ->
                LoggerImpl.warning("[BTTV] [$channelId] Cannot fetch channel emotes: ${throwable.message}")
                Single.just(EmoteSet.EMPTY)
            }
    }

    fun getGlobalFfzEmotes(): Single<EmoteSet> {
        return bttvDataSource.getGlobalFfzEmotes()
            .onErrorResumeNext { throwable ->
                LoggerImpl.error("[FFZ] Cannot fetch global emotes: ${throwable.message}")
                Single.just(EmoteSet.EMPTY)
            }
    }

    fun getChannelFfzEmotes(channelId: Int): Single<EmoteSet> {
        return bttvDataSource.getChannelFfzEmotes(channelId = channelId)
            .onErrorResumeNext { throwable ->
                LoggerImpl.warning("[FFZ] [$channelId] Cannot fetch channel emotes: ${throwable.message}")
                Single.just(EmoteSet.EMPTY)
            }
    }

    fun getBttvBadges(): Single<BadgeSet> {
        return bttvDataSource.getBadges()
            .onErrorResumeNext { throwable ->
                LoggerImpl.error("[BTTV] Cannot fetch badges: ${throwable.message}")
                Single.just(BadgeSet.EMPTY)
            }
    }
}