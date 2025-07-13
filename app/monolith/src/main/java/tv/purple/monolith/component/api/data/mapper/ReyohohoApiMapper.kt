package tv.purple.monolith.component.api.data.mapper

import tv.purple.monolith.models.data.badges.Badge
import tv.purple.monolith.models.data.badges.BadgeSet
import tv.purple.monolith.models.data.badges.impl.BadgeImpl
import tv.purple.monolith.models.retrofit.ReyohohoBadgeData
import javax.inject.Inject

class ReyohohoApiMapper @Inject constructor() {
    fun mapBadges(response: List<ReyohohoBadgeData>): BadgeSet {
        val builder = BadgeSet.Builder()
        val urlBadgeCache = mutableMapOf<String, Badge>()

        for (badgeData in response) {
            val badgeId = badgeData.userId?.toIntOrNull() ?: continue
            val badgeUrl = badgeData.badgeUrl ?: continue

            if (badgeUrl.isNotBlank()) {
                val badgeImpl = urlBadgeCache.getOrPut(badgeUrl) {
                    BadgeImpl(
                        code = BADGE_CODE,
                        url = badgeUrl
                    )
                }
                builder.addBadge(badgeImpl, badgeId)
            }
        }
        return builder.build()
    }

    companion object {
        private const val BADGE_CODE = "reyohoho"
    }
}