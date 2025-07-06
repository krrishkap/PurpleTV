package tv.purple.monolith.features.proxy.bridge

import java.util.regex.Pattern

class ReYohohoTwitchProxy : BaseInterceptor(
    pattern = Pattern.compile("^https://usher\\.ttvnw\\.net/.*"),
    debugInjection = true
)