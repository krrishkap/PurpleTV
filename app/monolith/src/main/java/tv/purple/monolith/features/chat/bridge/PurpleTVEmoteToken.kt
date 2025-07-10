package tv.purple.monolith.features.chat.bridge

import tv.purple.monolith.models.data.emotes.Emote
import tv.purple.monolith.models.wrapper.EmoteCardModelWrapper
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2

class PurpleTVEmoteToken(
    ptvId: String,
    ptvCode: String,
    val isTwitchEmote: Boolean = false,
    val subEmotes: MutableList<EmoteToken> = mutableListOf(),
    val isZW: Boolean = false
) : MessageTokenV2.EmoteToken(
    ptvId, ptvCode
) {
    companion object {
        fun fromEmote(emote: Emote, emoteSize: Emote.Size): PurpleTVEmoteToken {
            return PurpleTVEmoteToken(
                ptvId = EmoteCardModelWrapper(
                    token = emote.getCode(),
                    url = emote.getUrl(Emote.Size.LARGE),
                    emoteUrl = emote.getUrl(emoteSize),
                    set = emote.getPackageSet()
                ).toJsonString(),
                ptvCode = emote.getCode(),
                isZW = emote.isZeroWidth()
            )
        }
    }
}