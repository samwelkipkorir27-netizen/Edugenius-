package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Https
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PermissionAuditItem
import com.example.ui.components.RiskBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy700
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.ReleaseGuardViewModel

@Composable
fun PermissionsSecurityScreen(
    viewModel: ReleaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val permissions by viewModel.permissionsList.collectAsState()

    val grantedCount = permissions.count { it.isGranted }
    val totalCount = permissions.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Stage 6: Permissions & Security",
                subtitle = "Hardware Keystore, biometric access, runtime permission audit & zero trust",
                badgeText = "Security Audit"
            )
        }

        item {
            // Security Posture Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_posture_card"),
                colors = CardDefaults.cardColors(containerColor = SlateNavy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Zero-Trust Security Score: 98/100",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Strict Least Privilege • No unnecessary dangerous scopes",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldSuccess
                            )
                        }

                        Button(
                            onClick = { viewModel.refreshPermissions() },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                            modifier = Modifier.testTag("refresh_permissions_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Key Security Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecurityControlChip(
                            icon = Icons.Default.Fingerprint,
                            title = "Biometrics",
                            status = "Strong (Class 3)",
                            modifier = Modifier.weight(1f)
                        )
                        SecurityControlChip(
                            icon = Icons.Default.Https,
                            title = "Cleartext HTTP",
                            status = "Enforced TLS 1.3",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecurityControlChip(
                            icon = Icons.Default.Security,
                            title = "Keystore Backing",
                            status = "Hardware TEE",
                            modifier = Modifier.weight(1f)
                        )
                        SecurityControlChip(
                            icon = Icons.Default.ScreenLockPortrait,
                            title = "FLAG_SECURE",
                            status = "Screen Cloaking ON",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Declared App Permissions ($grantedCount/$totalCount Active)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // List of Permissions
        items(permissions) { perm ->
            PermissionAuditCard(item = perm)
        }

        item {
            // Least Privilege Compliance Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF090D16))
                    .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "LEAST-PRIVILEGE AUDIT ASSESSMENT",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ Broad Storage access avoided: Android Photo Picker (PickVisualMedia) used.\n✓ Background Location omitted: Zero location telemetry logged.\n✓ Microphone/Audio recording omitted: No unnecessary audio eavesdropping.\n✓ Keystore keys generated with UserAuthenticationRequired = true.",
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SecurityControlChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF090D16))
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = SlateTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                color = EmeraldSuccess,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PermissionAuditCard(item: PermissionAuditItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("perm_card_${item.permission}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.permission,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SlateTextMuted
                    )
                }
                RiskBadge(level = item.riskLevel)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.rationale,
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (item.isGranted) EmeraldSuccess else RoseError)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (item.isGranted) "Granted & Active" else "Not Granted",
                    color = if (item.isGranted) EmeraldSuccess else RoseError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (item.isRuntimeProtected) "Runtime Dialog" else "Install-Time Manifest",
                    color = SlateTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
