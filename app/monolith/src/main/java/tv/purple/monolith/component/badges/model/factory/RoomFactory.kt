package tv.purple.monolith.component.badges.model.factory

import io.reactivex.Single
import tv.purple.monolith.component.api.repository.BttvRepository
import tv.purple.monolith.component.api.repository.ChatsenRepository
import tv.purple.monolith.component.api.repository.ChatterinoRepository
import tv.purple.monolith.component.api.repository.FfzRepository
import tv.purple.monolith.component.api.repository.FlxrsRepository
import tv.purple.monolith.component.api.repository.HomiesRepository
import tv.purple.monolith.component.api.repository.NopRepository
import tv.purple.monolith.component.api.repository.ReyohohoRepository
import tv.purple.monolith.component.api.repository.StvRepository
import tv.purple.monolith.component.badges.model.BadgePackageImpl
import tv.purple.monolith.component.badges.model.room.Room
import tv.purple.monolith.component.badges.model.room.RoomImpl
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.models.data.badges.BadgeSet
import tv.purple.monolith.models.wrapper.BadgePackageSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFactory @Inject constructor(
    private val bttv: BttvRepository,
    private val ffz: FfzRepository,
    private val stv: StvRepository,
    private val cha: ChatterinoRepository,
    private val nop: NopRepository,
    private val hom: HomiesRepository,
    private val flxrs: FlxrsRepository,
    private val cht: ChatsenRepository,
    private val rhh: ReyohohoRepository
) {
    private data class BadgeConfig(
        val flag: Flag,
        val badgeFetcher: () -> Single<BadgeSet>,
        val token: BadgePackageSet
    )

    private val badgeConfigs = listOf(
        BadgeConfig(Flag.FFZ_BADGES, { ffz.getFfzBadges() }, BadgePackageSet.FFZ),
        BadgeConfig(Flag.FFZ_BADGES, { ffz.getFfzAPBadges() }, BadgePackageSet.FFZ_AP),
        BadgeConfig(Flag.CHA_BADGES, { cha.getChatterinoBadges() }, BadgePackageSet.CHATTERINO),
        BadgeConfig(Flag.HOMIES_BADGES, { hom.getBadges() }, BadgePackageSet.HOMIES),
        BadgeConfig(Flag.PTV_BADGES, { nop.getPtvBadges() }, BadgePackageSet.PTV),
        BadgeConfig(Flag.BTTV_BADGES, { bttv.getBttvBadges() }, BadgePackageSet.BTTV),
        BadgeConfig(Flag.DANKCHAT_BADGES, { flxrs.getBadges() }, BadgePackageSet.DANKCHAT),
        BadgeConfig(Flag.CHATSEN_BADGES, { cht.getBadges() }, BadgePackageSet.CHATSEN),
        BadgeConfig(Flag.REYOHOHO_BADGES, { rhh.getBadges() }, BadgePackageSet.REYOHOHO)
    )

    fun create(): Room {
        return RoomImpl().apply {
            badgeConfigs.filter {
                it.flag.asBoolean()
            }.forEach { config ->
                add(
                    BadgePackageImpl(
                        source = BadgeFetcherFactoryImpl(config.badgeFetcher),
                        token = config.token
                    )
                )
            }
        }
    }
}