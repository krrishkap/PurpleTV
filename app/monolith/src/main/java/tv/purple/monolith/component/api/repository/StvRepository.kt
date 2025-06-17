package tv.purple.monolith.component.api.repository

import io.reactivex.Single
import tv.purple.monolith.component.api.data.source.StvRemoteDataSource
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.models.data.badges.BadgeSet
import tv.purple.monolith.models.data.emotes.EmoteSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StvRepository @Inject constructor(
    private val stvDataSource: StvRemoteDataSource
) {
    fun getStvGlobalEmotes(): Single<EmoteSet> {
        return stvDataSource.getGlobalEmotes()
    }

    fun getStvChannelEmotes(channelId: Int): Single<EmoteSet> {
        return stvDataSource.getChannelEmotes(channelId = channelId)
            .onErrorResumeNext { throwable ->
                LoggerImpl.warning("[STV] [$channelId] Cannot fetch channel emotes: ${throwable.message}")
                Single.just(EmoteSet.EMPTY)
            }
    }

    fun getStvBadges(): Single<BadgeSet> {
        return stvDataSource.getBadges()
    }
}