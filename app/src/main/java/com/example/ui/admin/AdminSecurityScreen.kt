package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.admin.AdminSecurityManager
import com.example.ui.theme.DarkGrayCard
import com.example.ui.theme.ElectricBabyBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MetricEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminSecurityScreen(
    viewModel: DashboardViewModel,
    onNavigate: (AppScreen) -> Unit
) {
    val currentRole by AdminSecurityManager.currentRole.collectAsState()
    val is2FaActive by AdminSecurityManager.is2FaActive.collectAsState()
    val isIpWhitelistingEnabled by AdminSecurityManager.isIpWhitelistingEnabled.collectAsState()
    val isHmacVerificationRequired by AdminSecurityManager.isHmacVerificationRequired.collectAsState()
    val adminApiKey by AdminSecurityManager.adminApiKey.collectAsState()
    val auditLogs by AdminSecurityManager.auditLogs.collectAsState()

    val roles = listOf("Super Admin (Jeddah San)", "Data Architect", "Security Auditor", "Viewer")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "Enterprise Security & RBAC Console",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "High-security controls, cryptographic key rotation, and immutable audit trails by Jeddah San",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Role Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Role-Based Access Control (RBAC)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.take(2).forEach { role ->
                            val isSel = currentRole == role
                            FilterChip(
                                selected = isSel,
                                onClick = { AdminSecurityManager.setRole(role) },
                                label = { Text(role, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.drop(2).forEach { role ->
                            val isSel = currentRole == role
                            FilterChip(
                                selected = isSel,
                                onClick = { AdminSecurityManager.setRole(role) },
                                label = { Text(role, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Security Policies Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Security Enforcement Modules", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    // 2FA Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enforce Hardware 2FA & FIDO2", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Requires WebAuthn biometric key for dashboard exports", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = is2FaActive,
                            onCheckedChange = { AdminSecurityManager.toggle2Fa() },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricBabyBlue)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    // IP Whitelisting Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("IP Range Whitelisting (CIDR)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Restricts administrative panels to approved corporate subnets", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isIpWhitelistingEnabled,
                            onCheckedChange = { AdminSecurityManager.toggleIpWhitelist() },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricBabyBlue)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    // HMAC Verification Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("HMAC-SHA256 Webhook Verification", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Rejects untrusted WordPress triggers without cryptographic signature", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isHmacVerificationRequired,
                            onCheckedChange = { AdminSecurityManager.toggleHmacVerification() },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricBabyBlue)
                        )
                    }
                }
            }
        }

        // Secret Key Rotation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        Text("Administrative Master API Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = adminApiKey.take(16) + "********************",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ElectricBabyBlue,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { AdminSecurityManager.rotateApiKey() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = Color(0xFF0B0F17)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("rotate_api_key_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rotate API Key & Invalidate Sessions", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Immutable Audit Trail
        item {
            Text(
                text = "Immutable Security Audit Trail (${auditLogs.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(auditLogs) { record ->
            val dateFmt = remember(record.timestamp) {
                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(record.timestamp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record.action,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = ElectricBabyBlue
                        )

                        Surface(
                            color = MetricEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = record.status,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MetricEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Actor: ${record.actor} (${record.role}) • IP: ${record.ipAddress} • $dateFmt",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Resource: ${record.resource}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
