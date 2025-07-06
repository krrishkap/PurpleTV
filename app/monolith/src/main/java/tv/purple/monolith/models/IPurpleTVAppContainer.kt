package tv.purple.monolith.models

import dagger.hilt.internal.GeneratedComponent
import tv.purple.monolith.bridge.di.TwitchActivityComponent
import tv.purple.monolith.core.LifecycleCore
import tv.purple.monolith.features.chapters.VodChapters
import tv.purple.monolith.features.chat.ChatHookProvider
import tv.purple.monolith.features.logs.ChatLogs
import tv.purple.monolith.features.proxy.Proxy
import tv.purple.monolith.features.settings.PurpleTVSettings
import tv.purple.monolith.features.tracking.NopAnalyticsTracker
import tv.purple.monolith.features.ui.UI
import tv.purple.monolith.features.vodhunter.VodHunter
import tv.twitch.android.core.user.TwitchAccountManager

interface IPurpleTVAppContainer {
    fun onBeforeHiltBeforeSuperOnCreate()
    fun onAfterHiltBeforeSuperOnCreate()
    fun onBeforeHiltAfterSuperOnCreate()
    fun onAfterHiltAfterSuperOnCreate()

    fun provideTwitchAccountManager(): TwitchAccountManager
    fun provideLifecycleController(): LifecycleCore
    fun provideChatHookProvider(): ChatHookProvider
    fun provideOrangeSettings(): PurpleTVSettings
    fun provideUI(): UI
    fun provideTracker(): NopAnalyticsTracker
    fun provideChatLogs(): ChatLogs
    fun provideProxy(): Proxy
    fun provideSingletonC(): GeneratedComponent
    fun provideActivityFactory(): TwitchActivityComponent.Factory
    fun provideChapters(): VodChapters
    fun provideVODHunter(): VodHunter
}