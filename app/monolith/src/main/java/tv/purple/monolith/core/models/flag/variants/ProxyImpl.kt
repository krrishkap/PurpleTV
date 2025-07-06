package tv.purple.monolith.core.models.flag.variants

import tv.purple.monolith.core.models.flag.core.Variant

enum class ProxyImpl(
    val value: String,
    val desc: String,
    val domain: String? = null
) : Variant {
    Disabled("disabled", "Disabled"),
    LUMINOUS_EU("luminous_eu", "Luminous (EU) (RU)", "https://eu.luminous.dev"),
    LUMINOUS_EU2("luminous_eu2", "Luminous (EU2) (UA)", "https://eu2.luminous.dev"),
    LUMINOUS_EU3("luminous_eu3", "Luminous (EU3) (BG)", "https://eu3.luminous.dev"),
    LUMINOUS_AS("luminous_as", "Luminous (AS) (KZ)", "https://as.luminous.dev"),
    PP_EU("pp_eu", "PerfProd (EU)","https://lb-eu.cdn-perfprod.com"),
    PP_EU2("pp_eu2", "PerfProd (EU2)", "https://lb-eu2.cdn-perfprod.com"),
    PP_EU3("pp_eu3", "PerfProd (EU3) (RU)", "https://lb-eu3.cdn-perfprod.com"),
    PP_EU4("pp_eu4", "PerfProd (EU4)", "https://lb-eu4.cdn-perfprod.com"),
    PP_EU5("pp_eu5", "PerfProd (EU5)", "https://lb-eu5.cdn-perfprod.com"),
    PP_NA("pp_na", "PerfProd (NA)", "https://lb-na.cdn-perfprod.com"),
    PP_SA("pp_sa", "PerfProd (SA)", "https://lb-sa.cdn-perfprod.com"),
    PP_AS("pp_as", "PerfProd (AS)", "https://lb-as.cdn-perfprod.com"),
    ReYohohoTwitchProxy("rr", "ReYohoho Twitch Proxy", "https://proxy4.rhhhhhhh.live"),
    CUSTOM("custom", "Custom");

    override fun getDefault(): Variant {
        return Disabled
    }

    override fun toString(): String {
        return value
    }
}