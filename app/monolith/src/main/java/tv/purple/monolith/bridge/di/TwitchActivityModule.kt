package tv.purple.monolith.bridge.di

import dagger.Module
import dagger.Provides
import dagger.hilt.internal.GeneratedComponent
import dagger.hilt.internal.GeneratedComponentManager
import dagger.hilt.migration.DisableInstallInCheck
import dagger.internal.Provider
import tv.purple.monolith.core.compat.ClassCompat.callPrivateMethod
import tv.purple.monolith.core.compat.ClassCompat.getPrivateField
import tv.twitch.android.app.core.Experience
import tv.twitch.android.core.activities.TwitchHiltActivity
import tv.twitch.android.core.user.TwitchAccountManager
import tv.twitch.android.preferences.PreferenceType
import tv.twitch.android.provider.experiments.helpers.ChatRepliesExperiment
import tv.twitch.android.provider.experiments.helpers.MessageEffectsExperiment
import tv.twitch.android.routing.routers.WebViewDialogRouter
import tv.twitch.android.shared.badges.ChatBadgeProvider
import tv.twitch.android.shared.chat.commerce.notices.CommerceNoticeViewStateFactory
import tv.twitch.android.shared.chat.messages.data.ChatMessageV2Parser
import tv.twitch.android.shared.chat.messages.notices.UserNoticeViewStateFactory
import tv.twitch.android.shared.chat.messages.span.ChatMessageSpanFactoryV2
import tv.twitch.android.shared.chat.messages.ui.ChatMessageFactoryV2
import tv.twitch.android.shared.chat.settings.preferences.ChatSettingsPreferencesFile
import tv.twitch.android.shared.chat.settings.readablecolors.ReadableColorsCache
import tv.twitch.android.shared.community.debug.CommunityDebugSharedPreferences
import tv.twitch.android.shared.emotes.utils.AnimatedEmotesPresenterUtils
import tv.twitch.android.shared.experiments.helpers.ClipEditorPortalExperimentImpl
import tv.twitch.android.shared.preferences.chatfilters.ChatFiltersPreferenceFile
import tv.twitch.android.shared.ui.elements.span.ISpanHelper
import tv.twitch.android.shared.ui.elements.span.annotation.AnnotationSpanHelper
import tv.twitch.android.shared.webview.WebViewDialogFragmentUtil
import tv.twitch.android.util.ColorUtil
import tv.twitch.android.util.NumberFormatUtil
import javax.inject.Named

@Suppress("MoveLambdaOutsideParentheses")
@Module
@DisableInstallInCheck
class TwitchActivityModule {
    @Suppress("UNCHECKED_CAST")
    @Provides
    @Named("activity")
    fun provideActivityGC(activity: TwitchHiltActivity): GeneratedComponent {
        return (activity as GeneratedComponentManager<GeneratedComponent>).generatedComponent()
    }

    @Provides
    fun provideAnimatedEmotesPresenterUtils(gc: GeneratedComponent): AnimatedEmotesPresenterUtils {
        val ref = gc.getPrivateField<Provider<ChatSettingsPreferencesFile>>(
            fieldName = "chatSettingsPreferencesFileProvider"
        ).get()

        return AnimatedEmotesPresenterUtils(ref)
    }

    @Provides
    fun provideChatBadgeProvider(gc: GeneratedComponent): ChatBadgeProvider {
        return gc.getPrivateField<Provider<ChatBadgeProvider>>(
            fieldName = "chatBadgeProvider"
        ).get()
    }

    @Provides
    fun provideChatFiltersPreferenceFile(gc: GeneratedComponent): ChatFiltersPreferenceFile {
        return gc.getPrivateField<Provider<ChatFiltersPreferenceFile>>("chatFiltersPreferenceFileProvider")
            .get()
    }

    @Provides
    fun provideClipEditorPortalExperimentImpl(gc: GeneratedComponent): ClipEditorPortalExperimentImpl {
        return callPrivateMethod(
            obj = gc, methodName = "clipEditorPortalExperimentImpl"
        )
    }

    @Provides
    fun provideNumberFormatUtil(gc: GeneratedComponent): NumberFormatUtil {
        return gc.getPrivateField<Provider<NumberFormatUtil>>(
            fieldName = "provideNumberFormatUtilProvider"
        ).get()
    }

    @Provides
    fun provideAnnotationSpanHelper(gc: GeneratedComponent): AnnotationSpanHelper {
        return callPrivateMethod(
            obj = gc,
            methodName = "annotationSpanHelper"
        )
    }

    @Provides
    fun provideISpanHelper(gc: GeneratedComponent): ISpanHelper {
        return callPrivateMethod(
            obj = gc,
            methodName = "spanHelper"
        )
    }

    @Provides
    fun provideColorUtil(gc: GeneratedComponent): ColorUtil {
        return gc.getPrivateField<Provider<ColorUtil>>(
            fieldName = "colorUtilProvider"
        ).get()
    }

    @Provides
    fun provideReadableColorsCache(gc: GeneratedComponent): ReadableColorsCache {
        return gc.getPrivateField<Provider<ReadableColorsCache>>(
            fieldName = "readableColorsCacheProvider"
        ).get()
    }

    @Provides
    fun provideWebViewDialogFragmentUtil(gc: GeneratedComponent): WebViewDialogRouter {
        return gc.getPrivateField<Provider<WebViewDialogFragmentUtil>>(
            fieldName = "webViewDialogFragmentUtilProvider"
        ).get()
    }

    @Provides
    fun provideMessageEffectsExperiment(): MessageEffectsExperiment {
        return MessageEffectsExperiment { false }
    }

    @Provides
    fun provideChatRepliesExperiment(gc: GeneratedComponent): ChatRepliesExperiment {
        return callPrivateMethod(
            obj = gc,
            methodName = "chatRepliesExperimentImpl"
        )
    }

    @Provides
    fun provideExperienceProvider(gc: GeneratedComponent): Experience {
        return gc.getPrivateField<Provider<Experience>>(
            fieldName = "provideExperienceProvider"
        ).get()
    }

    @Provides
    fun provideChatMessageV2Parser(
        cbp: ChatBadgeProvider,
        cfpf: ChatFiltersPreferenceFile,
        cspf: ChatSettingsPreferencesFile,
        cepei: ClipEditorPortalExperimentImpl,
        cre: ChatRepliesExperiment
    ): ChatMessageV2Parser {
        return ChatMessageV2Parser(
            cbp,
            cfpf,
            cspf,
            cepei,
            { false },
            cre
        )
    }

    @Provides
    fun provideChatMessageFactoryV2(
        var1: TwitchAccountManager,
        var2: AnnotationSpanHelper,
        var3: ChatMessageSpanFactoryV2,
        var4: ColorUtil,
        var5: CommerceNoticeViewStateFactory,
        var6: UserNoticeViewStateFactory,
        var7: MessageEffectsExperiment,
        var8: ChatRepliesExperiment,
        var9: Experience
    ): ChatMessageFactoryV2 {
        return ChatMessageFactoryV2(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9
        )
    }

    @Provides
    fun provideCommunityDebugSharedPreferences(var1: PreferenceType.Debug): CommunityDebugSharedPreferences =
        CommunityDebugSharedPreferences(var1)
}