package tv.purple.monolith.component.api.data.source

import io.reactivex.Scheduler
import io.reactivex.Single
import tv.purple.monolith.component.api.data.api.ReyohohoApi
import tv.purple.monolith.component.api.data.mapper.ReyohohoApiMapper
import tv.purple.monolith.models.data.badges.BadgeSet
import javax.inject.Inject

class ReyohohoRemoteDataSource @Inject constructor(
    private val api: ReyohohoApi,
    private val mapper: ReyohohoApiMapper,
    private val scheduler: Scheduler
) {
    fun getBadges(): Single<BadgeSet> {
        return api.badges()
            .subscribeOn(scheduler)
            .map(mapper::mapBadges)
    }
}