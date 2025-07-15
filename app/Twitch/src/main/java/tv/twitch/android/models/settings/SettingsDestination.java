package tv.twitch.android.models.settings;

public enum SettingsDestination {
    Account(true),
    AddEmail(true),
    ChallengeGates(true),
    ChangePassword(true),
    ChannelNotifications(true),
    ClearAmazonLogin(true),
    CommunityGuidelines(false),
    Connections(true),
    ContentFiltering(true),
    CreateRoom(true),
    Credits(false),
    DeleteAccount(true),
    DisableAccount(true),
    Drops(true),
    EditBio(true),
    EditProfile(true),
    EditRoom(true),
    EditSocialLinks(true),
    EmailNotifications(true),
    EmailSettingsUnverified(true),
    EmailSettingsVerified(true),
    EntityInformation(false),
    Help(false),
    InAppNotifications(true),
    Legal(false),
    Licenses(false),
    ModerationSettings(true),
    Notifications(true),
    PasswordConfirmation(true),
    PersonalizedAds(false),
    PhoneNumberSettings(true),
    Preferences(false),
    PrivacyNotice(false),
    PushNotifications(true),
    RecommendationsFeedbackPerCategory(true),
    RecommendationsFeedbackPerChannel(true),
    RecommendationsFeedbackPerVideos(true),
    Rooms(true),
    SecurityPrivacy(true),
    Settings(false),
    StreamManager(true),
    SubmitBugReport(false),
    Subscriptions(true),
    System(false),
    TwoFactorAuthenticationDisable(true),
    TwoFactorAuthenticationEnable(true),
    ViewerChatFilters(true),
    // TODO: __INJECT_FIELDS
    PurpleThirdParty(false),
    PurpleThirdPartyEmotes(false),
    PurpleThirdPartyBadges(false),
    PurpleChat(false),
    PurplePlayer(false),
    PurpleGestures(false),
    PurpleView(false),
    PurplePatches(false),
    PurpleDev(false),
    PurpleSupport(false),
    PurpleWiki(false),
    PurpleOTA(false),
    PurpleInfo(false),
    PurpleHighlighter(false),
    PurpleUpdater(false),
    PurpleBlacklist(false),
    PurpleHighlightColor(false),
    PurpleSettings(false),
    PurpleCerberus(false),
    PurpleAdBlock(false),
    PurpleCustomProxy(false);

    private final boolean requiresLogin;

    SettingsDestination(boolean requiresLogin) {
        this.requiresLogin = requiresLogin;
    }

    public final boolean getRequiresLogin() {
        return this.requiresLogin;
    }
}