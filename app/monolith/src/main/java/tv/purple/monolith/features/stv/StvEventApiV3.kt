package tv.purple.monolith.features.stv

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import tv.purple.monolith.core.LoggerWithTag
import tv.purple.monolith.core.util.PurpleBuildConfigUtil
import tv.purple.monolith.features.stv.models.v7.events.EventApiDispatch
import tv.purple.monolith.features.stv.models.v7.events.EventApiEvent
import tv.purple.monolith.features.stv.models.v7.events.EventApiSubscribe
import tv.purple.monolith.features.stv.models.v7.events.EventApiUnsubscribe
import tv.purple.monolith.features.stv.models.v7.events.HelloEvent
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StvEventApiV3 @Inject constructor(
    private val client: OkHttpClient,
) : CoroutineScope {
    private val subscribers = mutableListOf<StvDispatchListener>()

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private var socket: WebSocket? = null
    private var heartbeatJob: Job? = null

    private var isConnected: Boolean = false

    private val logger = LoggerWithTag("7TV")
    private val subscribedChannels = LinkedHashSet<Int>()

    private var reconnectAttempts = 0
    private var reconnectJob: Job? = null

    fun addListener(l: StvDispatchListener) {
        if (!subscribers.contains(l)) {
            subscribers.add(l)
        }
    }

    fun removeListener(l: StvDispatchListener) {
        if (subscribers.contains(l)) {
            subscribers.remove(l)
        }
    }

    fun connect() {
        if (isConnected) {
            return
        }
        reconnectJob?.cancel()
        socket = client.newWebSocket(
            Request.Builder()
                .url("wss://events.7tv.io/v3")
                .header("User-Agent", PurpleBuildConfigUtil.userAgent)
                .build(),
            EventSocketListener()
        )
    }

    fun disconnect(clearChannels: Boolean = true) {
        job.cancel()
        reconnectJob?.cancel()
        socket?.close(1000, null)
        if (clearChannels) {
            subscribedChannels.clear()
        }
    }

    fun checkSubscriptionToChannel(channelId: Int) {
        if (subscribedChannels.contains(channelId)) {
            return
        }

        if (subscribedChannels.size >= MAX_SUBSCRIPTIONS) {
            val oldestChannel = subscribedChannels.first()
            unsubscribeFromChannel(oldestChannel)
        }

        subscribeToChannelInternal(channelId)
        subscribedChannels.add(channelId)
    }

    private fun resubscribeToChannels() {
        subscribedChannels.forEach {
            subscribeToChannelInternal(it)
        }
    }

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) {
            return
        }
        isConnected = false

        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            logger.error("Max reconnect attempts reached.")
            return
        }

        reconnectAttempts++
        val delay = (BASE_RECONNECT_DELAY * reconnectAttempts).toLong()
        logger.debug("Reconnecting in ${delay}ms...")

        reconnectJob = launch {
            delay(delay)
            connect()
        }
    }

    private fun subscribeToChannelInternal(channelId: Int) {
        subscribeInternal("emote_set.*", createChannelCondition(channelId))
        subscribeInternal("cosmetic.*", createChannelCondition(channelId))
        subscribeInternal("entitlement.*", createChannelCondition(channelId))
    }

    fun unsubscribeFromChannel(channelId: Int) {
        unsubscribeInternal("emote_set.*", createChannelCondition(channelId))
        unsubscribeInternal("cosmetic.*", createChannelCondition(channelId))
        unsubscribeInternal("entitlement.*", createChannelCondition(channelId))
        subscribedChannels.remove(channelId)
    }

    private fun subscribeInternal(type: String, condition: String) {
        val conditionJson = JSONObject(condition)
        val message = EventApiSubscribe(type = type, condition = conditionJson)
        socket?.send(message.asJson().toString())
    }

    private fun unsubscribeInternal(type: String, condition: String) {
        val conditionJson = JSONObject(condition)
        val message = EventApiUnsubscribe(type = type, condition = conditionJson)
        socket?.send(message.asJson().toString())
    }

    fun reconnect() {
        disconnect(clearChannels = false)
        connect()
    }

    private inner class EventSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            logger.debug("onOpen")
            isConnected = true
            reconnectAttempts = 0
            resubscribeToChannels()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            logger.debug("onMessage: $text")
            val json = JSONObject(text)
            val event = EventApiEvent(json)

            when (event.op) {
                EventApiEvent.OpCode.HELLO -> {
                    val hello = HelloEvent(event.data)
                    heartbeatJob = launch {
                        while (isActive) {
                            delay(hello.heartbeatInterval.toLong())
                            logger.debug("Send heartbeat message: $HEARTBEAT_MESSAGE")
                            socket?.send(HEARTBEAT_MESSAGE)
                        }
                    }
                }
                EventApiEvent.OpCode.DISPATCH -> {
                    val dispatch = EventApiDispatch(event.data)
                    subscribers.forEach { it.onDispatch(dispatch) }
                }
                EventApiEvent.OpCode.RECONNECT -> reconnect()

                else -> {
                    logger.debug("Event: $event")
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            logger.error("${t.message}")
            scheduleReconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            logger.warning("onClosed: $code, $reason")
            if (code != 1000) {
                scheduleReconnect()
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            logger.warning("onClosing")
            isConnected = false
        }
    }

    companion object {
        private const val MAX_SUBSCRIPTIONS = 5
        private const val BASE_RECONNECT_DELAY = 2000 // 2 seconds
        private const val MAX_RECONNECT_ATTEMPTS = 10

        private val HEARTBEAT_MESSAGE = JSONObject().apply {
            put("op", EventApiEvent.OpCode.HEARTBEAT.value)
        }.toString()

        private fun createChannelCondition(channelId: Int): String {
            return JSONObject().apply {
                put("ctx", "channel")
                put("id", channelId.toString())
                put("platform", "TWITCH")
            }.toString()
        }
    }
}

interface StvDispatchListener {
    fun onDispatch(event: EventApiDispatch)
}
