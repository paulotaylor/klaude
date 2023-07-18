package com.klaude

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class KlaudeClient private constructor(builder: Builder){

    companion object {
        const val TAG = "KlaudeClient"
    }

    private var jsonBuilder = Json {
        this.ignoreUnknownKeys = true
        this.encodeDefaults = true
    }

    private var apiKey: String? = null
    private var model: String? = null
    private var maxTokens: Int? = null
    private var timeout = 0
    private var temperature = 0.5f

    init {
        this.maxTokens = builder.maxTokens
        this.model = builder.model
        this.apiKey = builder.apiKey
        this.timeout = builder.timeout
        this.temperature = builder.temperature
    }

    private fun check() {
        if (apiKey.isNullOrBlank()) {
            throw IllegalStateException("apiKey is null or blank")
        }
        if (model.isNullOrBlank()) {
            throw IllegalStateException("model is null or blank")
        }
        if (maxTokens == null) {
            throw IllegalStateException("maxTokens is null or blank")
        }
    }

    class Builder {
        var apiKey: String? = null
            private set
        var model: String? = null
            private set
        var maxTokens : Int? = null
        var timeout = 0
        var temperature = 0.5f

        fun key(key: String) = apply { this.apiKey = key }
        fun model(model: String) = apply { this.model = model }
        fun maxTokensToSample(maxTokensToSample: Int) = apply { this.maxTokens = maxTokensToSample }
        fun timeout(timeout: Int) = apply { this.timeout = timeout }
        fun build() = KlaudeClient(this)
    }


    fun chat(messages: List<KlaudeMessage>, listener: (String?) -> Unit) {
        var prompt = ""
        messages.forEach { message ->
            if (message.text.isNotBlank()) {
                if (message.type == KlaudeMessageType.USER) {
                    prompt += "\n\nHuman:${message.text}"
                }
                if (message.type == KlaudeMessageType.ASSISTANT) {
                    prompt += "\n\nAssistant:${message.text}"
                }
            }
        }
        if (prompt.isEmpty()) {
            listener(null)
            return
        }
        prompt += "\n\nAssistant:"
        execute(prompt, listener)
    }
    fun complete(text: String, listener: (String?) -> Unit) {
        execute("\n\nHuman:$text\n\nAssistant:", listener)
    }

    fun execute(prompt: String, listener: (String?) -> Unit) {
        try {
            check()
            val headers: MutableMap<String, String> = HashMap()
            headers["x-api-key"] = apiKey!!
            val payload = KlaudePayload(prompt, model!!, maxTokens!!, listOf("\n\nHuman:"), temperature)

            val httpAsync = "https://api.anthropic.com/v1/complete".httpPost()
                .jsonBody(Json.encodeToString(payload))
            httpAsync.header(headers)
            httpAsync.timeoutRead(timeout)
            httpAsync.timeout(timeout)
            httpAsync.responseString { request, response, res ->
                try {
                    val json = res.get()
                    val response =
                        jsonBuilder.decodeFromString(KlaudeResponse.serializer(), json)
                    listener(response.completion)
                } catch (e: Exception) {
                    listener(null)
                }
            }.join()
        } catch (e: Exception) {
            listener(null)
        }
    }

}


@Serializable
class KlaudeResponse(val completion: String)
@Serializable
class KlaudePayload(val prompt: String, val model: String, val max_tokens_to_sample: Int, val stop_sequences: List<String> , val temperature :Float = 0.5f)

class KlaudeMessage(val text: String, val type: KlaudeMessageType)

enum class KlaudeMessageType {
    USER, ASSISTANT
}
