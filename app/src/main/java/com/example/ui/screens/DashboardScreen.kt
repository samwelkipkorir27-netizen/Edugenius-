package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TestStatus
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy700
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.AppNavSection
import com.example.viewmodel.ReleaseGuardViewModel

@Composable
fun DashboardScreen(
    viewModel: ReleaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val calcTests by viewModel.calculationTests.collectAsState()
    val benchmark by viewModel.benchmark.collectAsState()
    val domainState by viewModel.domainState.collectAsState()
    val backupList by viewModel.backupSnapshots.collectAsState()
    val errorList by viewModel.errorReports.collectAsState()

    val passedCalcs = calcTests.count { it.status == TestStatus.PASSED }
    val totalCalcs = calcTests.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Hero Banner with Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_launch_guard_1791552667829),
                    contentDescription = "Mission Control Launch Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD0B1120))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STAGE 6 & 7 ACTIVE ASSURANCE",
                            color = CyanPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ReleaseGuard Mission Control",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Testing calculations, security, recovery & domain readiness",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        item {
            // High-Level KPI Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Stage 6 Tests",
                    value = "$passedCalcs / $totalCalcs",
                    subtitle = if (passedCalcs == totalCalcs) "100% Passed" else "Pending run",
                    icon = Icons.Default.CheckCircle,
                    iconTint = if (passedCalcs == totalCalcs) EmeraldSuccess else CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Memory & Heap",
                    value = "${benchmark.heapUsedMb} MB",
                    subtitle = "of ${benchmark.heapMaxMb} MB max",
                    icon = Icons.Default.Memory,
                    iconTint = IndigoAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Web Domain",
                    value = "HTTPS Active",
                    subtitle = domainState.customDomain,
                    icon = Icons.Default.Public,
                    iconTint = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Vault Backups",
                    value = "${backupList.size} Snapshots",
                    subtitle = "SHA-256 Validated",
                    icon = Icons.Default.CloudDone,
                    iconTint = CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SectionHeader(
                title = "Stage 6: Testing & Security Suites",
                subtitle = "Core reliability, mathematical precision, resilient payments & disaster recovery"
            )
        }

        item {
            // Stage 6 Quick Access Hub
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickNavCard(
                    title = "Calculations & Precision Engine",
                    description = "Verify VAT/Tax, tiered discounts, currency floating-point drift, and proration.",
                    icon = Icons.Default.Calculate,
                    badge = "$totalCalcs Test Cases",
                    testTag = "nav_calculations_card",
                    onClick = { viewModel.setNavSection(AppNavSection.CALCULATIONS) }
                )
                QuickNavCard(
                    title = "Permissions & Security Posture",
                    description = "Audit hardware biometric access, notification alerts, TLS and least privilege.",
                    icon = Icons.Default.Shield,
                    badge = "Audit Active",
                    testTag = "nav_permissions_card",
                    onClick = { viewModel.setNavSection(AppNavSection.PERMISSIONS_SECURITY) }
                )
                QuickNavCard(
                    title = "Payment Failures & Account Recovery",
                    description = "Simulate 3DS, soft/hard declines, idempotency duplicates, OTP & lockout.",
                    icon = Icons.Default.CreditCardOff,
                    badge = "Dunning Engine",
                    testTag = "nav_payments_card",
                    onClick = { viewModel.setNavSection(AppNavSection.PAYMENTS_RECOVERY) }
                )
                QuickNavCard(
                    title = "Data Privacy & Backups (SHA-256)",
                    description = "PII detector & auto-redactor, GDPR DSAR exporter, cryptographic backup snapshots.",
                    icon = Icons.Default.Policy,
                    badge = "GDPR / CCPA",
                    testTag = "nav_privacy_card",
                    onClick = { viewModel.setNavSection(AppNavSection.PRIVACY_BACKUPS) }
                )
            }
        }

        item {
            SectionHeader(
                title = "Stage 7: Publish & Maintain Console",
                subtitle = "Custom web domain DNS check, Android Play Store pre-flight, and error telemetry"
            )
        }

        item {
            QuickNavCard(
                title = "Launch Console & Live Error Monitor",
                description = "Ping web domains (${domainState.devUrl.take(24)}...), Play Store readiness, bug stream.",
                icon = Icons.Default.RocketLaunch,
                badge = "Ready for Launch",
                testTag = "nav_publish_card",
                onClick = { viewModel.setNavSection(AppNavSection.PUBLISH_MAINTAIN) }
            )
        }

        item {
            // Live System Benchmark Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateNavy800)
                    .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active Runtime Telemetry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { viewModel.runPerformanceBenchmark() },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateNavy700),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("benchmark_refresh_button")
                        ) {
                            Text("Re-Bench", fontSize = 11.sp, color = CyanPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Calculation Throughput: ${benchmark.opsPerSecond / 1_000_000}M ops/sec\n• TLS RTT Latency: ${benchmark.networkPingMs} ms\n• Network State: ${benchmark.networkState}\n• Session Uptime: ${benchmark.appUptimeSec}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextMuted,
                        lineHeight = 20.sp
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
private fun QuickNavCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateNavy800)
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyanPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateNavy700)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextMuted
                )
            }
        }
    }
}
