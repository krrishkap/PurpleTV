package tv.purple.monolith.models.retrofit.homies

data class EmotesData(
    val global_emotes: List<Emote>,
    val channel_emotes: Map<String, ChannelEmotes>
)
