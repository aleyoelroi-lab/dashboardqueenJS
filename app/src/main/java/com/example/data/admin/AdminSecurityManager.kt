package com.example.data.admin

import com.example.data.model.AdminSecurityRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object AdminSecurityManager {

    private val _currentRole = MutableStateFlow("Super Admin (Jeddah San)")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _is2FaActive = MutableStateFlow(true)
    val is2FaActive: StateFlow<Boolean> = _is2FaActive.asStateFlow()

    private val _isIpWhitelistingEnabled = MutableStateFlow(true)
    val isIpWhitelistingEnabled: StateFlow<Boolean> = _isIpWhitelistingEnabled.asStateFlow()

    private val _isHmacVerificationRequired = MutableStateFlow(true)
    val isHmacVerificationRequired: StateFlow<Boolean> = _isHmacVerificationRequired.asStateFlow()

    private val _adminApiKey = MutableStateFlow("js_sec_live_" + UUID.randomUUID().toString().take(24))
    val adminApiKey: StateFlow<String> = _adminApiKey.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminSecurityRecord>>(
        listOf(
            AdminSecurityRecord(
                actor = "jeddah_san_admin",
                role = "Super Admin",
                action = "INITIALIZE_DASHBOARD_ENGINE",
                resource = "GeminiPro_ModelRouter",
                ipAddress = "197.38.12.84",
                status = "AUTHORIZED"
            ),
            AdminSecurityRecord(
                actor = "sec_ops_audit",
                role = "Security Auditor",
                action = "ROTATE_WEBHOOK_SIGNATURE_KEY",
                resource = "WordPress_HMAC_Validator",
                ipAddress = "10.14.0.22",
                status = "AUTHORIZED"
            ),
            AdminSecurityRecord(
                actor = "api_gateway_waf",
                role = "System Guard",
                action = "RATE_LIMIT_EVALUATION",
                resource = "POST /api/v1/integrations/sql",
                ipAddress = "185.199.108.153",
                status = "COMPLIANT"
            ),
            AdminSecurityRecord(
                actor = "data_architect_1",
                role = "Data Architect",
                action = "SCHEMA_INGEST_VALIDATION",
                resource = "SaaS_Metrics_Q3.csv",
                ipAddress = "192.168.1.105",
                status = "AUTHORIZED"
            )
        )
    )
    val auditLogs: StateFlow<List<AdminSecurityRecord>> = _auditLogs.asStateFlow()

    fun logAction(action: String, resource: String, status: String = "AUTHORIZED") {
        val entry = AdminSecurityRecord(
            actor = when (_currentRole.value) {
                "Super Admin (Jeddah San)" -> "jeddah_san_admin"
                "Data Architect" -> "data_architect_lead"
                else -> "security_operator"
            },
            role = _currentRole.value,
            action = action,
            resource = resource,
            ipAddress = "197.38.12.84",
            status = status
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value.take(40)
    }

    fun setRole(newRole: String) {
        _currentRole.value = newRole
        logAction("ROLE_SWITCH", "Target Role: $newRole")
    }

    fun toggle2Fa() {
        _is2FaActive.value = !_is2FaActive.value
        logAction("TOGGLE_2FA", "New State: ${_is2FaActive.value}")
    }

    fun toggleIpWhitelist() {
        _isIpWhitelistingEnabled.value = !_isIpWhitelistingEnabled.value
        logAction("TOGGLE_IP_WHITELIST", "New State: ${_isIpWhitelistingEnabled.value}")
    }

    fun toggleHmacVerification() {
        _isHmacVerificationRequired.value = !_isHmacVerificationRequired.value
        logAction("TOGGLE_HMAC_ENFORCEMENT", "New State: ${_isHmacVerificationRequired.value}")
    }

    fun rotateApiKey(): String {
        val newKey = "js_sec_live_" + UUID.randomUUID().toString().take(24)
        _adminApiKey.value = newKey
        logAction("API_KEY_ROTATION", "Key ID: ...${newKey.takeLast(6)}")
        return newKey
    }
}
