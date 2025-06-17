package tv.purple.monolith.component.api.repository

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import tv.purple.monolith.component.api.data.source.NopApiDataSource
import tv.purple.monolith.component.api.data.source.NopFileDataSource
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.models.data.Donation
import tv.purple.monolith.models.data.badges.BadgeSet
import tv.purple.monolith.models.retrofit.nop.OrangeUpdateData
import tv.purple.monolith.models.retrofit.nop.PinnyInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NopRepository @Inject constructor(
    private val nopApiDataSource: NopApiDataSource,
    private val nopFileDataSource: NopFileDataSource
) {
    fun getPtvBadges(): Single<BadgeSet> {
        return nopApiDataSource.getBadges()
            .onErrorResumeNext { throwable ->
                LoggerImpl.warning("[nop] Cannot fetch badges: ${throwable.message}. Using a local data source")
                nopFileDataSource.getBadges()
            }
    }

    fun getLocalDonators(): Single<List<Donation>> {
        return nopFileDataSource.getDonators()
    }

    fun getDonators(): Single<List<Donation>> {
        return nopApiDataSource.getDonators()
            .onErrorResumeNext { throwable ->
                LoggerImpl.warning("[nop] Cannot fetch donators: ${throwable.message}. Using a local data source")
                nopFileDataSource.getDonators()
            }
    }

    fun getUpdateData(): Single<OrangeUpdateData> {
        return nopApiDataSource.getOrangeUpdate()
    }

    fun ping(build: Int, deviceId: String): Completable {
        return nopApiDataSource.ping(
            build = build,
            deviceId = deviceId
        )
    }

    fun pingInfo(build: Int): Single<PinnyInfo> {
        return nopApiDataSource.pingInfo(build = build)
    }

    fun getVodHunterPlaylist(vodId: Int): Single<Response<String>> {
        return nopApiDataSource.getVodHunterPlaylist(vodId = vodId)
    }

    fun getVodHunterPlaylist(payload: String): Single<Response<String>> {
        return nopApiDataSource.getVodHunterPlaylist(payload = payload)
    }
}