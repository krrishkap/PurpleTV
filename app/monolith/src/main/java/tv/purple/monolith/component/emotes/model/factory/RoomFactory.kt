package tv.purple.monolith.component.emotes.model.factory

import tv.purple.monolith.component.api.repository.BttvRepository
import tv.purple.monolith.component.api.repository.HomiesRepository
import tv.purple.monolith.component.api.repository.StvRepository
import tv.purple.monolith.component.emotes.component.model.room.Room
import tv.purple.monolith.component.emotes.model.Configuration
import tv.purple.monolith.component.emotes.model.EmotePackage
import tv.purple.monolith.component.emotes.model.EmotePackageImpl
import tv.purple.monolith.component.emotes.model.room.IRoom
import tv.purple.monolith.models.wrapper.EmotePackageSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFactory @Inject constructor(
    private val bttv: BttvRepository,
    private val stv: StvRepository,
    private val homies: HomiesRepository
) : IRoomFactory {
    override fun createChannelRoom(
        channelId: Int,
        configuration: Configuration
    ): IRoom {
        val packages = mutableListOf<EmotePackage>()

        if (configuration.bttv) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory {
                        bttv.getChannelBttvEmotes(
                            channelId,
                            configuration.useWebp
                        )
                    },
                    token = EmotePackageSet.BttvChannel
                )
            )
        }
        if (configuration.ffz) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { bttv.getChannelFfzEmotes(channelId) },
                    token = EmotePackageSet.FfzChannel
                )
            )
        }
        if (configuration.stv) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { stv.getStvChannelEmotes(channelId) },
                    token = EmotePackageSet.StvChannel
                )
            )
        }
        if (configuration.homies) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { homies.getChannelEmotes(channelId) },
                    token = EmotePackageSet.HomiesChannel
                )
            )
        }

        return Room(packages)
    }

    override fun createGlobalRoom(configuration: Configuration): IRoom {
        val packages = mutableListOf<EmotePackage>()

        if (configuration.bttv) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { bttv.getGlobalBttvEmotes(configuration.useWebp) },
                    token = EmotePackageSet.BttvGlobal
                )
            )
        }
        if (configuration.ffz) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { bttv.getGlobalFfzEmotes() },
                    token = EmotePackageSet.FfzGlobal
                )
            )
        }
        if (configuration.stvGlobal) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { stv.getStvGlobalEmotes() },
                    token = EmotePackageSet.StvGlobal
                )
            )
        }
        if (configuration.homies) {
            packages.add(
                EmotePackageImpl(
                    source = EmoteFetcherFactory { homies.getGlobalEmotes() },
                    token = EmotePackageSet.HomiesGlobal
                )
            )
        }

        return Room(packages)
    }
}