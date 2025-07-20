package tv.purple.monolith.features.stv.models.v7.events

import org.json.JSONObject

data class EventApiEvent(
    val op: OpCode,
    val data: JSONObject,
) {
    constructor(json: JSONObject) : this(
        op = OpCode.fromInt(json.getInt("op")),
        data = json.getJSONObject("d")
    )

    enum class OpCode(val value: Int) {
        DISPATCH(0),
        HELLO(1),
        HEARTBEAT(2),
        RECONNECT(4),
        ACK(5),
        ERROR(6),
        END_OF_STREAM(7),
        IDENTIFY(33),
        RESUME(34),
        SUBSCRIBE(35),
        UNSUBSCRIBE(36),
        SIGNAL(37),
        UNKNOWN(-1);

        companion object {
            fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}

data class EventApiSubscribe(
    val type: String,
    val condition: JSONObject
) {
    fun asJson(): JSONObject {
        return JSONObject().apply {
            put("op", EventApiEvent.OpCode.SUBSCRIBE.value)
            put("d", JSONObject().apply {
                put("type", type)
                put("condition", condition)
            })
        }
    }
}

data class EventApiUnsubscribe(
    val type: String,
    val condition: JSONObject
) {
    fun asJson(): JSONObject {
        return JSONObject().apply {
            put("op", EventApiEvent.OpCode.UNSUBSCRIBE.value)
            put("d", JSONObject().apply {
                put("type", type)
                put("condition", condition)
            })
        }
    }
}

data class HelloEvent(
    val heartbeatInterval: Int,
    val sessionId: String,
) {
    constructor(json: JSONObject) : this(
        heartbeatInterval = json.getInt("heartbeat_interval"),
        sessionId = json.getString("session_id")
    )
}

data class EventApiDispatch(
    val type: String,
    val body: JSONObject
) {
    constructor(json: JSONObject) : this(
        type = json.getString("type"),
        body = json.getJSONObject("body")
    )
} 