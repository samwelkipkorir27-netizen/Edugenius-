package com.example

import com.example.data.PaymentResilienceEngine
import com.example.data.PrivacySecurityEngine
import com.example.data.TestingEngine
import com.example.model.TestStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStage6_CalculationSuitePassesAllVectors() {
        val tests = TestingEngine.getInitialTestSuite()
        assertEquals(10, tests.size)

        for (test in tests) {
            val result = TestingEngine.executeSingleTest(test)
            assertEquals("Test failed for vector: ${test.title} - ${result.errorMessage}", TestStatus.PASSED, result.status)
        }
    }

    @Test
    fun testStage6_FloatVsBigDecimalPrecision() {
        val calc = TestingEngine.computeCustomVat(149.99, 20.0, 15.0)
        assertEquals("SUCCESS", calc["status"])
        assertEquals("$149.99", calc["base"])
        assertEquals("-$22.50", calc["discount"])
        assertEquals("+$25.50", calc["tax"])
        assertEquals("$152.99", calc["finalTotal"])
    }

    @Test
    fun testStage6_PaymentGatewayIdempotencyReplay() {
        val scenario = PaymentResilienceEngine.getStandardScenarios().first()
        val key = "idem_test_key_999"

        // First call
        val (_, log1) = PaymentResilienceEngine.simulateGatewayCall(scenario, 99.0, key)
        assertFalse(log1.details.startsWith("REPLAY"))

        // Duplicate replay call with same key should be caught
        val (_, log2) = PaymentResilienceEngine.simulateGatewayCall(scenario, 99.0, key)
        assertTrue(log2.details.startsWith("REPLAY DETECTED"))
    }

    @Test
    fun testStage6_PiiDetectionAndMasking() {
        val raw = "Contact admin samwelsokwony@gmail.com with card 4111 2222 3333 4444 and IP 10.0.0.1"
        val (masked, matches) = PrivacySecurityEngine.scanAndMaskPii(raw)

        assertEquals(3, matches.size)
        assertTrue(masked.contains("••••-••••-••••-4444"))
        assertTrue(masked.contains("sa•••@gmail.com"))
        assertTrue(masked.contains("10.•••.•••.1"))
    }

    @Test
    fun testStage6_BackupSnapshotSha256Verification() {
        val snapshot = PrivacySecurityEngine.createSnapshot("UnitTest Snapshot", 500)
        val verified = PrivacySecurityEngine.verifySnapshotIntegrity(snapshot)
        assertTrue("Snapshot SHA-256 hash must verify successfully", verified)

        val tamperedSnapshot = snapshot.copy(recordsCount = 501)
        val tamperedVerified = PrivacySecurityEngine.verifySnapshotIntegrity(tamperedSnapshot)
        assertFalse("Tampered snapshot must fail integrity verification", tamperedVerified)
    }
}
