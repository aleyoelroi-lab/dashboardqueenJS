package com.example.data.integration

import com.example.data.model.WebhookConfig
import com.example.data.model.WebhookEventLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object WordPressWebhookManager {

    private val _webhookConfig = MutableStateFlow(WebhookConfig())
    val webhookConfig: StateFlow<WebhookConfig> = _webhookConfig.asStateFlow()

    private val _logs = MutableStateFlow<List<WebhookEventLog>>(
        listOf(
            WebhookEventLog(
                eventType = "woocommerce_order_created",
                source = "WordPress / WooCommerce Store",
                payloadPreview = "{\"order_id\": 9124, \"total\": \"$349.00\", \"currency\": \"USD\", \"status\": \"completed\"}",
                httpStatus = 200,
                isSuccess = true
            ),
            WebhookEventLog(
                eventType = "user_register_vip",
                source = "WordPress Membership Core",
                payloadPreview = "{\"user_id\": 849, \"tier\": \"enterprise\", \"email\": \"ceo@jeddah-san.com\"}",
                httpStatus = 200,
                isSuccess = true
            ),
            WebhookEventLog(
                eventType = "inventory_low_alert",
                source = "WordPress Stock Telemetry",
                payloadPreview = "{\"sku\": \"JS-NODE-01\", \"remaining_units\": 3, \"threshold\": 10}",
                httpStatus = 202,
                isSuccess = true
            )
        )
    )
    val logs: StateFlow<List<WebhookEventLog>> = _logs.asStateFlow()

    fun updateConfig(url: String, secret: String, triggers: List<String>, isActive: Boolean) {
        _webhookConfig.value = _webhookConfig.value.copy(
            endpointUrl = url,
            secretKey = secret,
            triggers = triggers,
            isActive = isActive
        )
    }

    fun triggerEvent(eventType: String): WebhookEventLog {
        val payload = when (eventType) {
            "woocommerce_order_created" -> "{\"order_id\": ${10000 + (1..999).random()}, \"total\": \"$${(50..800).random()}.00\", \"currency\": \"USD\", \"timestamp\": \"${System.currentTimeMillis()}\"}"
            "user_register_vip" -> "{\"user_id\": ${1000 + (1..999).random()}, \"tier\": \"pro_architect\", \"domain\": \"jeddah.enterprise\"}"
            "system_metric_pulse" -> "{\"node\": \"jeddah-az-1\", \"cpu_utilization\": \"${(40..85).random()}%%\", \"qps\": ${(3000..9000).random()}}"
            else -> "{\"trigger\": \"$eventType\", \"nonce\": \"${UUID.randomUUID().toString().take(8)}\"}"
        }

        val log = WebhookEventLog(
            eventType = eventType,
            source = "WordPress REST API [WP-JSON /js-dashboard/v1]",
            payloadPreview = payload,
            httpStatus = 200,
            isSuccess = true
        )

        _logs.value = listOf(log) + _logs.value.take(25)
        _webhookConfig.value = _webhookConfig.value.copy(
            totalRequestsDelivered = _webhookConfig.value.totalRequestsDelivered + 1,
            lastPingStatus = "200 OK • Delivered just now"
        )

        return log
    }
}
