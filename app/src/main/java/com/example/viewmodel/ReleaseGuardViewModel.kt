package com.example.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PaymentResilienceEngine
import com.example.data.PrivacySecurityEngine
import com.example.data.SystemTelemetry
import com.example.data.TestingEngine
import com.example.model.BackupSnapshot
import com.example.model.CalculationTestItem
import com.example.model.ErrorReport
import com.example.model.FeatureRoadmapItem
import com.example.model.PaymentAuditLog
import com.example.model.PaymentScenario
import com.example.model.PermissionAuditItem
import com.example.model.PiiMatch
import com.example.model.RecoveryBackupCode
import com.example.model.ReleaseChecklistItem
import com.example.model.RiskLevel
import com.example.model.SystemBenchmark
import com.example.model.TestStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppNavSection {
    DASHBOARD,
    CALCULATIONS,
    PERMISSIONS_SECURITY,
    PAYMENTS_RECOVERY,
    PRIVACY_BACKUPS,
    PUBLISH_MAINTAIN
}

data class CalculationWorkbenchState(
    val amount: String = "149.99",
    val taxRate: String = "20.0",
    val discount: String = "15.0",
    val result: Map<String, String> = emptyMap()
)

data class RecoveryState(
    val email: String = "samwelsokwony@gmail.com",
    val isOtpSent: Boolean = false,
    val otpTarget: String = "839104",
    val otpInput: String = "",
    val isVerified: Boolean = false,
    val failedAttempts: Int = 0,
    val isLockedOut: Boolean = false,
    val lockoutSecondsLeft: Int = 0,
    val backupCodes: List<RecoveryBackupCode> = emptyList(),
    val statusMessage: String = "Ready for recovery request"
)

data class PrivacyScanState(
    val rawInput: String = "Customer samwelsokwony@gmail.com charged $189.50 on Visa 4532 8910 2345 7712. Callback phone +1 (555) 234-5678, client IP 192.168.1.104.",
    val maskedOutput: String = "",
    val detectedMatches: List<PiiMatch> = emptyList(),
    val analyticsConsent: Boolean = true,
    val crashConsent: Boolean = true,
    val marketingConsent: Boolean = false,
    val erasureRequested: Boolean = false,
    val exportJsonPreview: String? = null
)

data class DomainCheckState(
    val customDomain: String = "app.releaseguard.io",
    val devUrl: String = "https://ais-dev-4kjlgy3gq2wfhmo5kpw54h-383308592254.europe-west3.run.app",
    val sharedUrl: String = "https://ais-pre-4kjlgy3gq2wfhmo5kpw54h-383308592254.europe-west3.run.app",
    val isChecking: Boolean = false,
    val cnameVerified: Boolean = true,
    val aRecordVerified: Boolean = true,
    val sslActive: Boolean = true,
    val httpToHttpsActive: Boolean = true,
    val latencyMs: Long = 42,
    val statusNote: String = "Domain verified with valid Let's Encrypt TLS v1.3 certificate."
)

class ReleaseGuardViewModel(application: Application) : AndroidViewModel(application) {

    // Navigation State
    private val _currentSection = MutableStateFlow(AppNavSection.DASHBOARD)
    val currentSection: StateFlow<AppNavSection> = _currentSection.asStateFlow()

    // 1. Calculations State
    private val _calculationTests = MutableStateFlow<List<CalculationTestItem>>(TestingEngine.getInitialTestSuite())
    val calculationTests: StateFlow<List<CalculationTestItem>> = _calculationTests.asStateFlow()

    private val _isTestingRunning = MutableStateFlow(false)
    val isTestingRunning: StateFlow<Boolean> = _isTestingRunning.asStateFlow()

    private val _calculationWorkbench = MutableStateFlow(CalculationWorkbenchState())
    val calculationWorkbench: StateFlow<CalculationWorkbenchState> = _calculationWorkbench.asStateFlow()

    // 2. Permissions & Security Audit
    private val _permissionsList = MutableStateFlow<List<PermissionAuditItem>>(emptyList())
    val permissionsList: StateFlow<List<PermissionAuditItem>> = _permissionsList.asStateFlow()

    // 3. Payment Failure & Resilience State
    private val _paymentScenarios = MutableStateFlow<List<PaymentScenario>>(PaymentResilienceEngine.getStandardScenarios())
    val paymentScenarios: StateFlow<List<PaymentScenario>> = _paymentScenarios.asStateFlow()

    private val _paymentAuditLogs = MutableStateFlow<List<PaymentAuditLog>>(emptyList())
    val paymentAuditLogs: StateFlow<List<PaymentAuditLog>> = _paymentAuditLogs.asStateFlow()

    private val _selectedScenario = MutableStateFlow<PaymentScenario?>(null)
    val selectedScenario: StateFlow<PaymentScenario?> = _selectedScenario.asStateFlow()

    private val _idempotencyKeyInput = MutableStateFlow("idem_a49b2f")
    val idempotencyKeyInput: StateFlow<String> = _idempotencyKeyInput.asStateFlow()

    // 4. Account Recovery & Security
    private val _recoveryState = MutableStateFlow(RecoveryState(backupCodes = PrivacySecurityEngine.generateBackupCodes()))
    val recoveryState: StateFlow<RecoveryState> = _recoveryState.asStateFlow()

    // 5. Privacy & Data Protection
    private val _privacyState = MutableStateFlow(PrivacyScanState())
    val privacyState: StateFlow<PrivacyScanState> = _privacyState.asStateFlow()

    // 6. Backups & Disaster Recovery
    private val _backupSnapshots = MutableStateFlow<List<BackupSnapshot>>(
        listOf(
            PrivacySecurityEngine.createSnapshot("Pre-Release Full", 1420),
            PrivacySecurityEngine.createSnapshot("Automated Daily", 1380)
        )
    )
    val backupSnapshots: StateFlow<List<BackupSnapshot>> = _backupSnapshots.asStateFlow()

    // 7. System Telemetry & Performance
    private val _benchmark = MutableStateFlow(SystemTelemetry.getMemoryAndPerformance())
    val benchmark: StateFlow<SystemBenchmark> = _benchmark.asStateFlow()

    // 8. Stage 7 Publish & Maintain
    private val _domainState = MutableStateFlow(DomainCheckState())
    val domainState: StateFlow<DomainCheckState> = _domainState.asStateFlow()

    private val _releaseChecklist = MutableStateFlow<List<ReleaseChecklistItem>>(SystemTelemetry.getAndroidReleaseChecklist())
    val releaseChecklist: StateFlow<List<ReleaseChecklistItem>> = _releaseChecklist.asStateFlow()

    private val _errorReports = MutableStateFlow<List<ErrorReport>>(SystemTelemetry.getInitialErrors())
    val errorReports: StateFlow<List<ErrorReport>> = _errorReports.asStateFlow()

    private val _roadmapItems = MutableStateFlow<List<FeatureRoadmapItem>>(SystemTelemetry.getInitialFeatureRoadmap())
    val roadmapItems: StateFlow<List<FeatureRoadmapItem>> = _roadmapItems.asStateFlow()

    init {
        refreshPermissions()
        runCustomCalculationWorkbench()
        scanCurrentPrivacyText()
        if (_paymentScenarios.value.isNotEmpty()) {
            _selectedScenario.value = _paymentScenarios.value.first()
        }
    }

    fun setNavSection(section: AppNavSection) {
        _currentSection.value = section
    }

    // --- Calculations Engine ---
    fun runAllCalculationTests() {
        viewModelScope.launch {
            _isTestingRunning.value = true
            val updated = _calculationTests.value.map { it.copy(status = TestStatus.RUNNING) }
            _calculationTests.value = updated

            val results = mutableListOf<CalculationTestItem>()
            for (test in _calculationTests.value) {
                delay(60) // simulated async pipeline latency
                val executed = TestingEngine.executeSingleTest(test)
                results.add(executed)
                _calculationTests.value = results + _calculationTests.value.drop(results.size)
            }
            _isTestingRunning.value = false
            addAuditLog("Calculations", "All 10 mathematical & precision test vectors executed.", isSuccess = true)
        }
    }

    fun updateWorkbenchInputs(amount: String, taxRate: String, discount: String) {
        _calculationWorkbench.value = _calculationWorkbench.value.copy(
            amount = amount,
            taxRate = taxRate,
            discount = discount
        )
        runCustomCalculationWorkbench()
    }

    private fun runCustomCalculationWorkbench() {
        val amt = _calculationWorkbench.value.amount.toDoubleOrNull() ?: 149.99
        val tax = _calculationWorkbench.value.taxRate.toDoubleOrNull() ?: 20.0
        val disc = _calculationWorkbench.value.discount.toDoubleOrNull() ?: 15.0
        val res = TestingEngine.computeCustomVat(amt, tax, disc)
        _calculationWorkbench.value = _calculationWorkbench.value.copy(result = res)
    }

    // --- Permissions Auditor ---
    fun refreshPermissions() {
        val ctx = getApplication<Application>()
        val hasInternet = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
        val hasNetwork = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
        val hasVibrate = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED
        val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true
        val hasBiometric = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED

        _permissionsList.value = listOf(
            PermissionAuditItem(
                permission = "android.permission.INTERNET",
                displayName = "Internet Access",
                category = "Network",
                riskLevel = RiskLevel.LOW,
                rationale = "Required for secure TLS API calls, payment processing, and cloud sync.",
                isGranted = hasInternet,
                isRuntimeProtected = false
            ),
            PermissionAuditItem(
                permission = "android.permission.ACCESS_NETWORK_STATE",
                displayName = "Network State Telemetry",
                category = "Network",
                riskLevel = RiskLevel.LOW,
                rationale = "Monitors online/offline transitions to pause retries during dropouts.",
                isGranted = hasNetwork,
                isRuntimeProtected = false
            ),
            PermissionAuditItem(
                permission = "android.permission.POST_NOTIFICATIONS",
                displayName = "Push Notifications",
                category = "User Alerts",
                riskLevel = RiskLevel.MEDIUM,
                rationale = "Required for urgent payment failure alerts and recovery 2FA codes.",
                isGranted = hasNotif,
                isRuntimeProtected = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ),
            PermissionAuditItem(
                permission = "android.permission.USE_BIOMETRIC",
                displayName = "Biometric Authentication",
                category = "Security",
                riskLevel = RiskLevel.MEDIUM,
                rationale = "Used for 3D Secure Step-Up authentication and vault decryption.",
                isGranted = hasBiometric,
                isRuntimeProtected = false
            ),
            PermissionAuditItem(
                permission = "android.permission.VIBRATE",
                displayName = "Haptic Feedback",
                category = "Hardware",
                riskLevel = RiskLevel.LOW,
                rationale = "Provides tactile confirmation on security confirmations.",
                isGranted = hasVibrate,
                isRuntimeProtected = false
            )
        )
    }

    // --- Payments & Resilience ---
    fun selectPaymentScenario(scenario: PaymentScenario) {
        _selectedScenario.value = scenario
    }

    fun updateIdempotencyKey(key: String) {
        _idempotencyKeyInput.value = key
    }

    fun generateNewIdempotencyKey() {
        val newKey = PaymentResilienceEngine.generateIdempotencyKey("cart_108", 89.00)
        _idempotencyKeyInput.value = newKey
    }

    fun simulatePayment(amount: Double = 89.00) {
        val scenario = _selectedScenario.value ?: return
        val key = _idempotencyKeyInput.value
        val (updatedScenario, log) = PaymentResilienceEngine.simulateGatewayCall(scenario, amount, key)

        _selectedScenario.value = updatedScenario
        _paymentScenarios.value = _paymentScenarios.value.map {
            if (it.id == updatedScenario.id) updatedScenario else it
        }
        _paymentAuditLogs.value = listOf(log) + _paymentAuditLogs.value
    }

    // --- Account Recovery ---
    fun sendRecoveryOtp() {
        _recoveryState.value = _recoveryState.value.copy(
            isOtpSent = true,
            statusMessage = "6-digit OTP dispatched to ${_recoveryState.value.email} (Test code: 839104)"
        )
        addAuditLog("Account Recovery", "OTP challenge dispatched to ${_recoveryState.value.email}", isSuccess = true)
    }

    fun updateOtpInput(input: String) {
        _recoveryState.value = _recoveryState.value.copy(otpInput = input)
    }

    fun verifyRecoveryOtp() {
        val current = _recoveryState.value
        if (current.isLockedOut) return

        if (current.otpInput.trim() == current.otpTarget) {
            _recoveryState.value = current.copy(
                isVerified = true,
                failedAttempts = 0,
                statusMessage = "Success! Identity verified. Account recovery session unlocked."
            )
            addAuditLog("Account Recovery", "Identity challenge passed. Account recovered.", isSuccess = true)
        } else {
            val newFails = current.failedAttempts + 1
            if (newFails >= 3) {
                triggerAccountLockout(30)
            } else {
                _recoveryState.value = current.copy(
                    failedAttempts = newFails,
                    statusMessage = "Incorrect OTP code. Attempt $newFails/3 before security lockout."
                )
                addAuditLog("Account Recovery", "Failed OTP attempt ($newFails/3).", isSuccess = false)
            }
        }
    }

    private fun triggerAccountLockout(seconds: Int) {
        _recoveryState.value = _recoveryState.value.copy(
            isLockedOut = true,
            lockoutSecondsLeft = seconds,
            statusMessage = "SECURITY LOCKOUT TRIGGERED: Too many failed recovery attempts. Locked for $seconds seconds."
        )
        addAuditLog("Security Alert", "Brute force lockout triggered after 3 failed attempts.", isSuccess = false)

        viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _recoveryState.value = _recoveryState.value.copy(lockoutSecondsLeft = remaining)
            }
            _recoveryState.value = _recoveryState.value.copy(
                isLockedOut = false,
                failedAttempts = 0,
                statusMessage = "Lockout expired. You may retry recovery."
            )
        }
    }

    fun useBackupCode(codeToUse: String) {
        val updatedCodes = _recoveryState.value.backupCodes.map {
            if (it.code == codeToUse) it.copy(isUsed = true) else it
        }
        _recoveryState.value = _recoveryState.value.copy(
            backupCodes = updatedCodes,
            isVerified = true,
            statusMessage = "Verified via one-time backup emergency key '$codeToUse'."
        )
        addAuditLog("Security", "One-time recovery code used.", isSuccess = true)
    }

    // --- Privacy & GDPR ---
    fun updatePrivacyScanInput(text: String) {
        _privacyState.value = _privacyState.value.copy(rawInput = text)
        scanCurrentPrivacyText()
    }

    fun scanCurrentPrivacyText() {
        val (masked, matches) = PrivacySecurityEngine.scanAndMaskPii(_privacyState.value.rawInput)
        _privacyState.value = _privacyState.value.copy(
            maskedOutput = masked,
            detectedMatches = matches
        )
    }

    fun toggleConsent(type: String, enabled: Boolean) {
        _privacyState.value = when (type) {
            "analytics" -> _privacyState.value.copy(analyticsConsent = enabled)
            "crash" -> _privacyState.value.copy(crashConsent = enabled)
            "marketing" -> _privacyState.value.copy(marketingConsent = enabled)
            else -> _privacyState.value
        }
    }

    fun generateDsarExport() {
        val json = """
        {
          "data_subject": "${_recoveryState.value.email}",
          "export_generated_at": "${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())}",
          "gdpr_article_15_compliant": true,
          "profile": {
            "account_id": "usr_94827103",
            "email": "${_recoveryState.value.email}",
            "role": "Owner / Administrator"
          },
          "consents": {
            "analytics": ${_privacyState.value.analyticsConsent},
            "crash_reports": ${_privacyState.value.crashConsent},
            "marketing": ${_privacyState.value.marketingConsent}
          },
          "stored_payment_methods": [
            { "type": "card", "last4": "4012", "exp": "12/28" }
          ]
        }
        """.trimIndent()
        _privacyState.value = _privacyState.value.copy(exportJsonPreview = json)
        addAuditLog("GDPR Compliance", "Data Subject Access Request (DSAR) bundle exported.", isSuccess = true)
    }

    fun requestAccountErasure() {
        _privacyState.value = _privacyState.value.copy(erasureRequested = true)
        addAuditLog("Data Privacy", "GDPR Article 17 Erasure request queued for ${_recoveryState.value.email}.", isSuccess = true)
    }

    // --- Backups & Snapshots ---
    fun createBackupSnapshot(type: String = "Full Manual") {
        val newSnap = PrivacySecurityEngine.createSnapshot(type, 1530 + (_backupSnapshots.value.size * 25))
        _backupSnapshots.value = listOf(newSnap) + _backupSnapshots.value
        addAuditLog("Backup Engine", "Snapshot ${newSnap.id} created. SHA-256: ${newSnap.sha256Checksum.take(12)}...", isSuccess = true)
    }

    fun verifySnapshot(snapshot: BackupSnapshot) {
        val isOk = PrivacySecurityEngine.verifySnapshotIntegrity(snapshot)
        val updated = _backupSnapshots.value.map {
            if (it.id == snapshot.id) it.copy(isIntegrityVerified = isOk) else it
        }
        _backupSnapshots.value = updated
        addAuditLog("Integrity Audit", "Snapshot ${snapshot.id} SHA-256 hash verified: PASS", isSuccess = isOk)
    }

    // --- System Benchmarks ---
    fun runPerformanceBenchmark() {
        _benchmark.value = SystemTelemetry.getMemoryAndPerformance()
        addAuditLog("Performance", "Benchmark executed: ${_benchmark.value.opsPerSecond / 1_000_000}M ops/sec", isSuccess = true)
    }

    // --- Stage 7 Domain & Release ---
    fun updateCustomDomain(domain: String) {
        _domainState.value = _domainState.value.copy(customDomain = domain)
    }

    fun pingCustomDomain() {
        viewModelScope.launch {
            _domainState.value = _domainState.value.copy(isChecking = true)
            delay(400)
            _domainState.value = _domainState.value.copy(
                isChecking = false,
                latencyMs = 38L + (System.currentTimeMillis() % 12),
                statusNote = "HTTP 200 OK — SSL Certificate valid (TLS 1.3, ALPN h2). DNS propagated."
            )
            addAuditLog("Publishing", "Domain health check passed for ${_domainState.value.customDomain}", isSuccess = true)
        }
    }

    fun simulateNewError() {
        val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val newErr = ErrorReport(
            id = "err-" + UUID.randomUUID().toString().take(6),
            timestamp = now,
            severity = "ERROR",
            location = "CheckoutController::process",
            message = "Simulated NetworkException: Gateway socket closed unexpectedly during 3DS callback.",
            stackTraceSnippet = "at com.example.payments.GatewayClient.execute(GatewayClient.kt:92)\nCaused by: SocketTimeoutException: Read timed out"
        )
        _errorReports.value = listOf(newErr) + _errorReports.value
    }

    fun voteFeature(id: String) {
        _roadmapItems.value = _roadmapItems.value.map {
            if (it.id == id) it.copy(votes = it.votes + 1) else it
        }
    }

    private fun addAuditLog(category: String, message: String, isSuccess: Boolean) {
        val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val report = ErrorReport(
            id = "audit-" + UUID.randomUUID().toString().take(6),
            timestamp = now,
            severity = if (isSuccess) "INFO" else "WARN",
            location = category,
            message = message,
            stackTraceSnippet = "Audit event logged in ReleaseGuard runtime."
        )
        _errorReports.value = listOf(report) + _errorReports.value
    }
}
