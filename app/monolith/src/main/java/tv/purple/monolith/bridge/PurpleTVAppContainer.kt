package tv.purple.monolith.bridge

import android.app.Application
import android.content.Context
import dagger.hilt.internal.GeneratedComponent
import tv.purple.monolith.bridge.di.DaggerMotherComponent
import tv.purple.monolith.bridge.di.MotherComponent
import tv.purple.monolith.bridge.di.TwitchActivityComponent
import tv.purple.monolith.bridge.ex.IPurpleTVAppContainerProvider
import tv.purple.monolith.core.LifecycleCore
import tv.purple.monolith.core.LoggerImpl
import tv.purple.monolith.core.PrefManager
import tv.purple.monolith.core.util.PurpleBuildConfigUtil
import tv.purple.monolith.features.chapters.VodChapters
import tv.purple.monolith.features.chat.ChatHookProvider
import tv.purple.monolith.features.logs.ChatLogs
import tv.purple.monolith.features.proxy.Proxy
import tv.purple.monolith.features.settings.PurpleTVSettings
import tv.purple.monolith.features.tracking.NopAnalyticsTracker
import tv.purple.monolith.features.tracking.bridge.BugsnagUtil
import tv.purple.monolith.features.ui.UI
import tv.purple.monolith.features.vodhunter.VodHunter
import tv.purple.monolith.models.IPurpleTVAppContainer
import tv.twitch.android.app.core.ApplicationContext
import tv.twitch.android.core.user.TwitchAccountManager

class PurpleTVAppContainer(
    private val context: Context
) : IPurpleTVAppContainer {
    private lateinit var motherComponent: MotherComponent

    override fun onBeforeHiltBeforeSuperOnCreate() {
        PrefManager.init(context)
        LoggerImpl.init()
        PurpleBuildConfigUtil.init(context)
        BugsnagUtil.init(context as Application)
    }

    override fun onAfterHiltBeforeSuperOnCreate() {
        PrefManager.fixDarkThemeInit()
    }

    override fun onBeforeHiltAfterSuperOnCreate() {
    }

    override fun onAfterHiltAfterSuperOnCreate() {
        motherComponent = provideMotherComponent(context)
        LoggerImpl.debug("Hi, ${motherComponent.provideTwitchAccountManager().displayName}! PurpleTV: ${PurpleBuildConfigUtil.buildConfigVariant.number}")

        motherComponent.provideChatHookProvider().apply {
            init()
            PrefManager.registerFlagListeners(this)
            provideLifecycleController().register(this)
        }
        motherComponent.provideTracker().apply {
            provideLifecycleController().register(this)
        }
        motherComponent.provideVodHunter().apply {
            provideLifecycleController().register(this)
        }
    }

    override fun provideTwitchAccountManager(): TwitchAccountManager {
        return motherComponent.provideTwitchAccountManager()
    }

    override fun provideLifecycleController(): LifecycleCore {
        return motherComponent.provideLifecycle()
    }

    override fun provideChatHookProvider(): ChatHookProvider {
        return motherComponent.provideChatHookProvider()
    }

    override fun provideOrangeSettings(): PurpleTVSettings {
        return motherComponent.provideOrangeSettings()
    }

    override fun provideUI(): UI {
        return motherComponent.provideUI()
    }

    override fun provideTracker(): NopAnalyticsTracker {
        return motherComponent.provideTracker()
    }

    override fun provideChatLogs(): ChatLogs {
        return motherComponent.provideChatLogs()
    }

    override fun provideProxy(): Proxy {
        return motherComponent.provideProxy()
    }

    override fun provideSingletonC(): GeneratedComponent {
        return motherComponent.provideSingletonC()
    }

    override fun provideActivityFactory(): TwitchActivityComponent.Factory {
        return motherComponent.twitchActivityComponentFactory()
    }

    override fun provideChapters(): VodChapters {
        return motherComponent.provideVodChapters()
    }

    override fun provideVODHunter(): VodHunter {
        return motherComponent.provideVodHunter()
    }

    companion object {
        @JvmStatic
        fun create(context: Context): IPurpleTVAppContainer {
            return PurpleTVAppContainer(context)
        }

        @JvmStatic
        fun getInstance(): IPurpleTVAppContainer {
            return (ApplicationContext.getInstance().getContext()
                    as IPurpleTVAppContainerProvider).providePurpleTVAppContainer()
        }

        private fun provideMotherComponent(context: Context): MotherComponent {
            return DaggerMotherComponent.factory().create(context)
        }

        @JvmStatic
        fun getChatHookProvider(): ChatHookProvider {
            return getInstance().provideChatHookProvider()
        }

        @JvmStatic
        fun getPurpleTVSettings(): PurpleTVSettings {
            return getInstance().provideOrangeSettings()
        }

        @JvmStatic
        fun getLifecycle(): LifecycleCore {
            return getInstance().provideLifecycleController()
        }
    }
}