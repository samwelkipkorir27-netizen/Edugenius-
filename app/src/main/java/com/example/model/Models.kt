package com.example.model

enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class TestStatus {
    IDLE, RUNNING, PASSED, FAILED
}

data class CalculationTestItem(
    val id: String,
    val title: String,
    val category: String, // "Tax & VAT", "Discounts", "Currency Precision", "Proration", "Boundary"
    val description: String,
    val formula: String,
    val inputSummary: String,
    val expectedOutput: String,
    var actualOutput: String = "",
    var status: TestStatus = TestStatus.IDLE,
    var latencyMs: Long = 0,
    var errorMessage: String? = null
)

data class PermissionAuditItem(
    val permission: String,
    val displayName: String,
    val category: String,
    val riskLevel: RiskLevel,
    val rationale: String,
    val isGranted: Boolean,
    val isRuntimeProtected: Boolean
)

data class PaymentScenario(
    val id: String,
    val title: String,
    val errorCode: String,
    val httpCode: Int,
    val cardDetails: String,
    val reason: String,
    val dunningAction: String,
    val retryInterval: String,
    var isSimulated: Boolean = false,
    var simulationResult: String = ""
)

data class PaymentAuditLog(
    val id: String,
    val timestamp: String,
    val event: String,
    val details: String,
    val isSuccess: Boolean,
    val idempotencyKey: String
)

data class RecoveryBackupCode(
    val code: String,
    val isUsed: Boolean = false
)

data class PiiMatch(
    val type: String, // "Email", "Credit Card", "Phone", "IP Address"
    val rawValue: String,
    val maskedValue: String
)

data class BackupSnapshot(
    val id: String,
    val timestamp: String,
    val recordsCount: Int,
    val sizeKb: Double,
    val sha256Checksum: String,
    val isIntegrityVerified: Boolean,
    val type: String // "Full", "Incremental", "Pre-Release"
)

data class SystemBenchmark(
    val heapUsedMb: Long,
    val heapMaxMb: Long,
    val opsPerSecond: Long,
    val networkPingMs: Long,
    val networkState: String,
    val appUptimeSec: Long
)

data class ReleaseChecklistItem(
    val id: String,
    val title: String,
    val requirement: String,
    val isPassed: Boolean,
    val category: String // "Security", "Play Store", "Performance", "Metadata"
)

data class ErrorReport(
    val id: String,
    val timestamp: String,
    val severity: String, // "INFO", "WARN", "ERROR", "CRITICAL"
    val message: String,
    val location: String,
    val stackTraceSnippet: String
)

data class FeatureRoadmapItem(
    val id: String,
    val title: String,
    val description: String,
    val stage: String, // "Stage 6" or "Stage 7"
    var votes: Int,
    val status: String // "Done", "Testing", "Next Up"
)
