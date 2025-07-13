package tv.purple.monolith.component.api.repository

import io.reactivex.Single
import tv.purple.monolith.component.api.data.source.ReyohohoRemoteDataSource
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.models.data.badges.BadgeSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReyohohoRepository @Inject constructor(
    private val dataSource: ReyohohoRemoteDataSource,
) {
    fun getBadges(): Single<BadgeSet> {
        return dataSource.getBadges().doOnSuccess {
            LoggerImpl.debug("Badges: ${it}}")
        }
    }
}