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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.ErrorReport
import com.example.model.FeatureRoadmapItem
import com.example.model.ReleaseChecklistItem
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy700
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.ReleaseGuardViewModel

@Composable
fun PublishMaintainScreen(
    viewModel: ReleaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val domainState by viewModel.domainState.collectAsState()
    val checklist by viewModel.releaseChecklist.collectAsState()
    val errorList by viewModel.errorReports.collectAsState()
    val roadmap by viewModel.roadmapItems.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Stage 7: Publish and Maintain",
                subtitle = "Custom web domain launch, Android Play Store release pre-flight, error monitoring & feature improvements",
                badgeText = "Stage 7 Launch"
            )
        }

        // Section 1: Web App Custom Domain & DNS Launch
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("domain_config_card"),
                colors = CardDefaults.cardColors(containerColor = SlateNavy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Launch Web App on Custom Domain",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Deployment Endpoints:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090D16))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Dev URL: ${domainState.devUrl}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Shared: ${domainState.sharedUrl}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = domainState.customDomain,
                            onValueChange = { viewModel.updateCustomDomain(it) },
                            label = { Text("Custom Web Domain", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_custom_domain"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = SlateNavy700
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.pingCustomDomain() },
                            enabled = !domainState.isChecking,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_ping_domain")
                        ) {
                            if (domainState.isChecking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Ping & Verify", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // DNS & TLS Check Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DnsStatusPill(label = "CNAME", verified = domainState.cnameVerified, modifier = Modifier.weight(1f))
                        DnsStatusPill(label = "A Record", verified = domainState.aRecordVerified, modifier = Modifier.weight(1f))
                        DnsStatusPill(label = "TLS 1.3", verified = domainState.sslActive, modifier = Modifier.weight(1f))
                        DnsStatusPill(label = "HTTPS Redir", verified = domainState.httpToHttpsActive, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${domainState.statusNote} (RTT: ${domainState.latencyMs}ms)",
                        fontSize = 11.sp,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Section 2: Android Play Store Checklist
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("android_release_card"),
                colors = CardDefaults.cardColors(containerColor = SlateNavy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Android & Cross-Platform Release Pre-Flight",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("100% READY", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    checklist.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text(text = item.requirement, fontSize = 10.sp, color = SlateTextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Live Error & Crash Telemetry
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "3. Error & Crash Telemetry Stream",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { viewModel.simulateNewError() },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_simulate_error")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = null,
                        tint = RoseError,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Test Error", color = RoseError, fontSize = 11.sp)
                }
            }
        }

        items(errorList.take(4)) { err ->
            ErrorCard(item = err)
        }

        // Section 4: Feature Roadmap & User Improvement Feedback
        item {
            Text(
                text = "4. Feature Improvement Backlog & Votes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(roadmap) { feat ->
            RoadmapCard(item = feat, onVote = { viewModel.voteFeature(feat.id) })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DnsStatusPill(
    label: String,
    verified: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF090D16))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 10.sp, color = SlateTextMuted)
            Text(
                text = if (verified) "ACTIVE" else "PENDING",
                fontSize = 10.sp,
                color = if (verified) EmeraldSuccess else AmberWarning,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorCard(item: ErrorReport) {
    val (accent, bg) = when (item.severity) {
        "CRITICAL" -> Pair(RoseError, Color(0xFF4C0519))
        "ERROR" -> Pair(RoseError, Color(0xFF3B0715))
        "WARN" -> Pair(AmberWarning, Color(0xFF382305))
        else -> Pair(CyanPrimary, Color(0xFF082635))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("error_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavy800),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(bg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = item.severity, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item.location, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = item.timestamp, fontSize = 10.sp, color = SlateTextMuted)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.message, fontSize = 11.sp, color = SlateTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.stackTraceSnippet,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun RoadmapCard(
    item: FeatureRoadmapItem,
    onVote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("roadmap_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateNavy800),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF090D16))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = item.stage, fontSize = 9.sp, color = CyanPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.description, fontSize = 11.sp, color = SlateTextMuted)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onVote,
                colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("vote_btn_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Vote",
                    tint = CyanPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${item.votes}", fontSize = 11.sp, color = CyanPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
