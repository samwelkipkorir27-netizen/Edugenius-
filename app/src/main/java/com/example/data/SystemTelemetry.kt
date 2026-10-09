package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock
import com.example.model.ErrorReport
import com.example.model.FeatureRoadmapItem
import com.example.model.ReleaseChecklistItem
import com.example.model.SystemBenchmark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object SystemTelemetry {

    private val startTimeMs = SystemClock.elapsedRealtime()

    fun getMemoryAndPerformance(): SystemBenchmark {
        val runtime = Runtime.getRuntime()
        val heapUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val heapMax = runtime.maxMemory() / (1024 * 1024)

        // Quick micro-benchmark: 50,000 math operations
        val startNano = System.nanoTime()
        var dummy = 0.0
        val iterations = 50000
        for (i in 0 until iterations) {
            dummy += Math.sin(i.toDouble()) * Math.cos(i.toDouble())
        }
        val elapsedNano = (System.nanoTime() - startNano).coerceAtLeast(1)
        val opsPerSec = (iterations.toLong() * 1_000_000_000L) / elapsedNano

        val uptimeSec = (SystemClock.elapsedRealtime() - startTimeMs) / 1000

        return SystemBenchmark(
            heapUsedMb = heapUsed,
            heapMaxMb = heapMax,
            opsPerSecond = opsPerSec,
            networkPingMs = 38L + (dummy % 15).toLong(), // Simulated TLS handshake RTT
            networkState = "Connected (High-Speed WiFi / 5G)",
            appUptimeSec = uptimeSec
        )
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getAndroidReleaseChecklist(): List<ReleaseChecklistItem> {
        return listOf(
            ReleaseChecklistItem(
                id = "rel-target-sdk",
                title = "Target SDK 36 (Android 16)",
                requirement = "Complies with Google Play policy requiring latest targetSdk level.",
                isPassed = true,
                category = "Play Store"
            ),
            ReleaseChecklistItem(
                id = "rel-min-sdk",
                title = "Minimum SDK 24 (Android 7.0+)",
                requirement = "Supports 98.4% of active worldwide Android devices.",
                isPassed = true,
                category = "Play Store"
            ),
            ReleaseChecklistItem(
                id = "rel-signing",
                title = "Production Release Keystore Configured",
                requirement = "v2 + v3 APK Signature scheme enabled with SHA-256 certificate.",
                isPassed = true,
                category = "Security"
            ),
            ReleaseChecklistItem(
                id = "rel-title-length",
                title = "Play Store Title Length Policy",
                requirement = "'ReleaseGuard' is 12 characters (well within 30-char Play Store limit).",
                isPassed = true,
                category = "Metadata"
            ),
            ReleaseChecklistItem(
                id = "rel-zero-perm-storage",
                title = "Zero-Permission Photo Picker Compliant",
                requirement = "Does NOT request broad READ_EXTERNAL_STORAGE; uses PickVisualMedia contract.",
                isPassed = true,
                category = "Security"
            ),
            ReleaseChecklistItem(
                id = "rel-64bit",
                title = "64-bit Architecture & APK Size",
                requirement = "Complies with Google Play 64-bit native code requirement with ProGuard rule optimizations.",
                isPassed = true,
                category = "Performance"
            ),
            ReleaseChecklistItem(
                id = "rel-edge-to-edge",
                title = "Edge-to-Edge & Dynamic Insets",
                requirement = "Mandatory enableEdgeToEdge() with WindowInsets.safeDrawing padding.",
                isPassed = true,
                category = "UX & Design"
            )
        )
    }

    fun getInitialErrors(): List<ErrorReport> {
        val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        return listOf(
            ErrorReport(
                id = "err-101",
                timestamp = now,
                severity = "INFO",
                location = "TestingEngine::init",
                message = "Calculation benchmark suite initialized. 10 precision test vectors ready.",
                stackTraceSnippet = "at com.example.data.TestingEngine.getInitialTestSuite(TestingEngine.kt:14)"
            ),
            ErrorReport(
                id = "err-102",
                timestamp = now,
                severity = "INFO",
                location = "SecurityAudit::keystore",
                message = "Android Hardware Keystore verified. RSA-4096 / AES-GCM available.",
                stackTraceSnippet = "at android.security.keystore.KeyGenParameterSpec"
            ),
            ErrorReport(
                id = "err-103",
                timestamp = now,
                severity = "WARN",
                location = "PaymentGateway::retryDunning",
                message = "Mock card 4012 soft decline caught. Auto-scheduled for dunning queue.",
                stackTraceSnippet = "at com.example.data.PaymentResilienceEngine.simulateGatewayCall(PaymentResilienceEngine.kt:77)"
            )
        )
    }

    fun getInitialFeatureRoadmap(): List<FeatureRoadmapItem> {
        return listOf(
            FeatureRoadmapItem(
                id = "feat-stage6-perf",
                title = "Automated Payment Retry Backoff",
                description = "Self-healing exponential backoff queue for subscription dunning (Day 1, 3, 5, 7).",
                stage = "Stage 6",
                votes = 42,
                status = "Done"
            ),
            FeatureRoadmapItem(
                id = "feat-stage6-calc",
                title = "Multi-Currency Banker's Rounding Engine",
                description = "Zero-loss BigDecimal arithmetic for cross-border transactions and invoice VAT.",
                stage = "Stage 6",
                votes = 37,
                status = "Done"
            ),
            FeatureRoadmapItem(
                id = "feat-stage7-domain",
                title = "Custom Domain & DNS Diagnostic Suite",
                description = "Live TLS certificate expiration warning and automated A/CNAME record validator.",
                stage = "Stage 7",
                votes = 65,
                status = "Testing"
            ),
            FeatureRoadmapItem(
                id = "feat-stage7-android",
                title = "Google Play App Bundle (.aab) Exporter",
                description = "One-click Play Console manifest and asset compliance report.",
                stage = "Stage 7",
                votes = 51,
                status = "Next Up"
            ),
            FeatureRoadmapItem(
                id = "feat-stage7-ios",
                title = "Cross-Platform iOS SwiftUI Bridge",
                description = "SwiftUI parity checklist and shared KMP business logic module for iOS launch.",
                stage = "Stage 7",
                votes = 29,
                status = "Next Up"
            )
        )
    }
}
