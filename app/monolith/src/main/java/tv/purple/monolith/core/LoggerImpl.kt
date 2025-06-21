package tv.purple.monolith.core

import timber.log.Timber
import tv.purple.monolith.core.models.flag.Flag
import tv.purple.monolith.core.models.flag.FlagListener
import tv.purple.monolith.core.util.DebugUtils
import tv.purple.monolith.models.util.ILogger
import tv.twitch.android.core.mvp.presenter.PresenterAction
import tv.twitch.android.core.mvp.presenter.PresenterState
import tv.twitch.android.core.mvp.presenter.StateUpdateEvent
import java.util.concurrent.atomic.AtomicBoolean

object LoggerImpl : ILogger, FlagListener {
    private const val LOGGER_TAG = "PurpleTV-M"

    private var devMode = false
    private var loggingStateMachine = false
    private var disabled = false

    private var isInitialized = AtomicBoolean(false)

    /**
     * Initialize LoggerImpl. Must be called once and only after the PrefManager
     */
    fun init() {
        if (isInitialized.getAndSet(true)) {
            warning("The second attempt to initialize the LoggerImpl. Could there be a memory leak?")
            return
        }

        devMode = Flag.DEV_MODE.asBoolean()
        loggingStateMachine = Flag.DEBUG_STATE_MACHINE.asBoolean()
        PrefManager.registerFlagListeners(this)
    }

    override fun getTag() = LOGGER_TAG

    override fun warning(messageText: String?) {
        if (disabled) {
            return
        }
        Timber.tag(getTag())
        Timber.w(messageText)
    }

    override fun info(messageText: String?) {
        if (disabled) {
            return
        }
        Timber.tag(getTag())
        Timber.i(messageText)
    }

    override fun debug(messageText: String?) {
        if (disabled) {
            return
        }
        Timber.tag(getTag())
        Timber.d(messageText)
    }

    override fun error(messageText: String?) {
        if (disabled) {
            return
        }
        Timber.tag(getTag())
        Timber.e(messageText)
    }

    override fun debug(invoke: () -> String) {
        if (!devMode || disabled) {
            return
        }
        Timber.tag(getTag())
        Timber.d(invoke.invoke())
    }

    /**
     * Provide information of the current state of the Twitch StateMachine
     */
    @JvmStatic
    fun debugStateUpdate(
        p0: String?,
        p1: StateUpdateEvent?,
        p2: PresenterState?,
        p3: PresenterState?,
        p4: MutableList<PresenterAction?>?
    ) {
        if (!devMode || disabled) {
            return
        }

        if (!loggingStateMachine) {
            return
        }

        val clazz =
            p0 ?: Throwable().stackTrace.getOrNull(16)?.fileName?.removeSuffix(".kt") ?: "Unknown"

        debug("||->$clazz:new transition")
        debug("||->$clazz:   event: $p1")
        debug("||->$clazz:   previous state: $p2")
        debug("||->$clazz:   new State: $p3")
        p4?.let {
            val iterator: Iterator<*> = it.iterator()
            while (iterator.hasNext()) {
                debug("||->$clazz:   action: " + iterator.next())
            }
        }
    }

    @JvmStatic
    fun debugTrackEvent(str: String, map: MutableMap<String, Any>) {
        if (disabled) {
            return
        }

        debug { "trackEvent: [$str] --> $map" }
    }

    override fun onFlagValueChanged(flag: Flag) {
        when (flag) {
            Flag.DEV_MODE -> devMode = flag.asBoolean()
            Flag.DEBUG_STATE_MACHINE -> loggingStateMachine = flag.asBoolean()
            else -> {}
        }
    }

    @JvmStatic
    fun debugObject(obj: Any?) {
        if (!devMode) {
            return
        }

        obj ?: kotlin.run {
            debug("debugObject(null)")
            return
        }
        DebugUtils.debugObject(obj)
    }
}