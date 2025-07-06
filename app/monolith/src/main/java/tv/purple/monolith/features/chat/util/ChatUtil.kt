package tv.purple.monolith.features.chat.util

import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.LruCache
import tv.purple.monolith.core.models.flag.variants.Timestamps
import tv.twitch.android.shared.chat.messages.span.ClickableUsernameSpan
import tv.twitch.android.shared.chat.pub.messages.data.ChatMessage
import tv.twitch.android.shared.chat.pub.messages.data.MessageTokenV2
import tv.twitch.android.shared.ui.elements.span.CenteredImageSpan
import tv.twitch.android.shared.ui.elements.span.UrlDrawable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatUtil {
    private const val MIN_DARK_THEME_NICKNAME_HSV_VALUE = .3f
    private const val FIXED_DARK_THEME_NICKNAME_HSV_VALUE = .4f
    private const val MIN_DEFAULT_THEME_NICKNAME_HSV_VALUE = .9f
    private const val FIXED_DEFAULT_THEME_NICKNAME_HSV_VALUE = .8f

    private const val CACHE_SIZE = 500

    private val darkThemeCache: LruCache<Int, Int> = object : LruCache<Int, Int>(CACHE_SIZE) {
        override fun create(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            if (hsv[2] >= MIN_DARK_THEME_NICKNAME_HSV_VALUE) {
                return color
            }
            hsv[2] = FIXED_DARK_THEME_NICKNAME_HSV_VALUE
            return Color.HSVToColor(hsv)
        }
    }

    private val defaultThemeCache: LruCache<Int, Int> = object : LruCache<Int, Int>(CACHE_SIZE) {
        override fun create(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            if (hsv[2] <= MIN_DEFAULT_THEME_NICKNAME_HSV_VALUE) {
                return color
            }
            hsv[2] = FIXED_DEFAULT_THEME_NICKNAME_HSV_VALUE
            return Color.HSVToColor(hsv)
        }
    }

    fun fixUsernameColor(color: Int, isDarkThemeEnabled: Boolean): Int {
        return if (isDarkThemeEnabled) {
            darkThemeCache[color]
        } else {
            defaultThemeCache[color]
        }
    }

    fun createDeletedGrey(message: Spanned?): Spanned? {
        if (message.isNullOrBlank()) {
            return message
        }

        message.getSpans(0, message.length, ForegroundColorSpan::class.java)?.forEach { colorSpan ->
            if (colorSpan.foregroundColor == Color.GRAY) {
                return message
            }
        }

        val messageBuilder = SpannableStringBuilder(message)

        messageBuilder.getSpans(0, messageBuilder.length, ForegroundColorSpan::class.java)
            ?.forEach {
                messageBuilder.removeSpan(it)
            }
        messageBuilder.getSpans(0, messageBuilder.length, CenteredImageSpan::class.java)?.forEach {
            val drawable = it.drawable
            if (drawable is UrlDrawable) {
                drawable.setGrey(true)
            }
        }
        messageBuilder.setSpan(
            ForegroundColorSpan(Color.GRAY),
            0,
            messageBuilder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return SpannedString.valueOf(messageBuilder)
    }

    fun createDeletedStrikethrough(message: Spanned?): Spanned? {
        if (message.isNullOrBlank()) {
            return message
        }

        message.getSpans(0, message.length, StrikethroughSpan::class.java)?.let { spans ->
            if (spans.isNotEmpty()) {
                return message
            }
        }

        return SpannedString.valueOf(SpannableStringBuilder(message).apply {
            setSpan(
                StrikethroughSpan(),
                getMessageStartPos(message = message),
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        })
    }

    private fun getMessageStartPos(message: Spanned): Int {
        var usernameEndPos = 0
        val spans = message.getSpans(0, message.length, ClickableUsernameSpan::class.java)
        if (spans.isEmpty()) {
            return usernameEndPos
        }
        usernameEndPos = message.getSpanEnd(spans[0])
        val pos = usernameEndPos + 2
        if (pos < message.length) {
            if (TextUtils.equals(message.subSequence(usernameEndPos, pos), ": ")) {
                usernameEndPos = pos
            }
        }
        return usernameEndPos
    }

    private fun formatTimestamp(message: Spanned, timestamp: CharSequence): Spanned {
        return SpannableString.valueOf(
            SpannableStringBuilder(timestamp).append(" ").append(message)
        )
    }

    fun createTimestampSpanFromChatMessageSpan(
        message: Spanned,
        date: Date,
        variant: Timestamps
    ): Spanned {
        if (variant == Timestamps.DEFAULT) {
            return message
        }

        return formatTimestamp(
            message = message,
            timestamp = SimpleDateFormat(
                variant.format,
                Locale.ENGLISH
            ).format(date)
        )
    }

    fun isUserMentioned(
        liveChatMessage: ChatMessage.LiveChatMessage,
        username: String
    ): Boolean {
        if (username.isBlank()) {
            return false
        }

        liveChatMessage.replyInfo?.parentUserLogin?.let { parentUserLogin ->
            if (parentUserLogin.equals(username, ignoreCase = true)) {
                return true
            }
        }

        return liveChatMessage.messageTokens.any { token ->
            if (token is MessageTokenV2.MentionToken) {
                token.userName?.let { username.equals(it, ignoreCase = true) } ?: false
            } else {
                false
            }
        }
    }
}