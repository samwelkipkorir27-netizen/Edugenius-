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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.BackupSnapshot
import com.example.ui.components.SectionHeader
import com.example.ui.components.TerminalCodeBlock
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy700
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.ReleaseGuardViewModel

@Composable
fun PrivacyBackupScreen(
    viewModel: ReleaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val privacyState by viewModel.privacyState.collectAsState()
    val snapshots by viewModel.backupSnapshots.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Stage 6: Data Privacy & Disaster Backups",
                subtitle = "PII redaction, GDPR/CCPA rights, cryptographic SHA-256 snapshots & tamper audits",
                badgeText = "Data & Backups"
            )
        }

        // Section 1: PII Scanner & Redaction
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pii_scanner_card"),
                colors = CardDefaults.cardColors(containerColor = SlateNavy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Real-Time PII Detection & Auto-Redaction",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Scans payloads for emails, credit cards, phones, and IP addresses before storage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = privacyState.rawInput,
                        onValueChange = { viewModel.updatePrivacyScanInput(it) },
                        label = { Text("Raw Payload String", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_raw_pii_payload"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = SlateNavy700
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sanitized Output (${privacyState.detectedMatches.size} PII entities masked):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090D16))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = privacyState.maskedOutput,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Section 2: GDPR & Privacy Rights
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gdpr_consent_card"),
                colors = CardDefaults.cardColors(containerColor = SlateNavy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GDPR / CCPA Compliance Controls",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ConsentRow(
                        title = "Telemetry & Analytics Consent",
                        description = "Collect aggregate usage stats",
                        isChecked = privacyState.analyticsConsent,
                        onToggle = { viewModel.toggleConsent("analytics", it) }
                    )
                    ConsentRow(
                        title = "Crash Reporting & Stacktraces",
                        description = "Automated error dumps",
                        isChecked = privacyState.crashConsent,
                        onToggle = { viewModel.toggleConsent("crash", it) }
                    )
                    ConsentRow(
                        title = "Marketing & Educational Promos",
                        description = "Course & feature announcements",
                        isChecked = privacyState.marketingConsent,
                        onToggle = { viewModel.toggleConsent("marketing", it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateDsarExport() },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_export_dsar")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export DSAR", fontSize = 11.sp, color = CyanPrimary)
                        }

                        Button(
                            onClick = { viewModel.requestAccountErasure() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (privacyState.erasureRequested) RoseError.copy(alpha = 0.3f) else Color(0xFF4C0519)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_request_erasure")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = RoseError,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (privacyState.erasureRequested) "Queued Erasure" else "Right to Erase",
                                fontSize = 11.sp,
                                color = RoseError
                            )
                        }
                    }

                    privacyState.exportJsonPreview?.let { json ->
                        Spacer(modifier = Modifier.height(10.dp))
                        TerminalCodeBlock(codeText = json, title = "GDPR Article 15 Data Bundle")
                    }
                }
            }
        }

        // Section 3: Disaster Recovery Snapshots
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "3. Disaster Recovery Snapshots (SHA-256)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { viewModel.createBackupSnapshot() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_create_snapshot")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Snapshot", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(snapshots) { snap ->
            SnapshotCard(snapshot = snap, onVerify = { viewModel.verifySnapshot(snap) })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ConsentRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = description, fontSize = 10.sp, color = SlateTextMuted)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanPrimary,
                checkedTrackColor = CyanPrimary.copy(alpha = 0.3f),
                uncheckedThumbColor = SlateTextMuted,
                uncheckedTrackColor = SlateNavy700
            )
        )
    }
}

@Composable
private fun SnapshotCard(
    snapshot: BackupSnapshot,
    onVerify: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("snapshot_card_${snapshot.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${snapshot.type} (${snapshot.id})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${snapshot.sizeKb} KB • ${snapshot.recordsCount} records",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Timestamp: ${snapshot.timestamp}",
                fontSize = 11.sp,
                color = SlateTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF090D16))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "SHA-256 Checksum:",
                        fontSize = 10.sp,
                        color = SlateTextMuted
                    )
                    Text(
                        text = snapshot.sha256Checksum,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = if (snapshot.isIntegrityVerified) EmeraldSuccess else RoseError,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (snapshot.isIntegrityVerified) "Cryptographic Integrity Verified" else "Checksum Mismatch",
                    color = if (snapshot.isIntegrityVerified) EmeraldSuccess else RoseError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onVerify,
                    colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Re-Verify", fontSize = 10.sp, color = CyanPrimary)
                }
            }
        }
    }
}
