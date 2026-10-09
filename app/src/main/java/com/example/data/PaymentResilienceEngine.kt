package com.example.data

import com.example.model.PaymentAuditLog
import com.example.model.PaymentScenario
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PaymentResilienceEngine {

    fun getStandardScenarios(): List<PaymentScenario> {
        return listOf(
            PaymentScenario(
                id = "scen-insufficient-funds",
                title = "Insufficient Funds (Soft Decline)",
                errorCode = "insufficient_funds",
                httpCode = 402,
                cardDetails = "Visa •••• 4012 (Limit reached)",
                reason = "Customer bank declined due to available credit limit.",
                dunningAction = "Dunning Schedule: Auto-retry in 24h & 72h. Send gentle email reminder with update payment link.",
                retryInterval = "T+24h, T+72h, T+120h"
            ),
            PaymentScenario(
                id = "scen-card-expired",
                title = "Card Expired (Hard Decline)",
                errorCode = "expired_card",
                httpCode = 400,
                cardDetails = "Mastercard •••• 5556 (Exp 09/26)",
                reason = "Card reached expiration date. Retrying will not succeed without cardholder update.",
                dunningAction = "Block auto-retries immediately to avoid merchant dispute fees. Trigger In-App Modal and SMS Alert.",
                retryInterval = "No auto-retry (Action required)"
            ),
            PaymentScenario(
                id = "scen-3ds-challenge",
                title = "3D Secure 2.0 Challenge Required",
                errorCode = "authentication_required",
                httpCode = 403,
                cardDetails = "Amex •••• 1009 (Strong Customer Auth)",
                reason = "EU PSD2 / SCA requires biometric or SMS OTP confirmation by cardholder bank.",
                dunningAction = "Return client_secret to front-end to trigger biometric biometric challenge webview / modal.",
                retryInterval = "Immediate modal challenge"
            ),
            PaymentScenario(
                id = "scen-gateway-timeout",
                title = "Acquirer Gateway Timeout (Network Error)",
                errorCode = "gateway_timeout",
                httpCode = 504,
                cardDetails = "Visa •••• 8821 (Stripe/Adyen Timeout)",
                reason = "Payment processor failed to respond within 8000ms threshold.",
                dunningAction = "Check Idempotency Key before retrying! Never charge twice. Exponential backoff retry at 5s, 15s.",
                retryInterval = "Backoff: 5s, 15s, 45s (Idempotent)"
            ),
            PaymentScenario(
                id = "scen-fraud-stolen",
                title = "Lost/Stolen Card (High-Risk Fraud)",
                errorCode = "stolen_card",
                httpCode = 403,
                cardDetails = "Visa •••• 9999 (Reported Stolen)",
                reason = "Card flagged on international fraud registry.",
                dunningAction = "Instantly suspend subscription, terminate access, log IP and device fingerprint in security audit.",
                retryInterval = "Permanent Block"
            ),
            PaymentScenario(
                id = "scen-rate-limit",
                title = "Processor Rate Limit Exceeded (HTTP 429)",
                errorCode = "rate_limit_exceeded",
                httpCode = 429,
                cardDetails = "Corporate Visa •••• 4242",
                reason = "Too many parallel batch billing requests sent within 1-second burst window.",
                dunningAction = "Trip Circuit Breaker to Half-Open state. Buffer requests into FIFO queue with jitter.",
                retryInterval = "Jittered Backoff: 2s + rand(0-1s)"
            )
        )
    }

    private val seenIdempotencyKeys = mutableSetOf<String>()

    fun simulateGatewayCall(
        scenario: PaymentScenario,
        customAmount: Double,
        idempotencyKey: String
    ): Pair<PaymentScenario, PaymentAuditLog> {
        val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        
        // Test idempotency protection
        val isDuplicate = seenIdempotencyKeys.contains(idempotencyKey)
        val resultDesc: String
        val isSuccess: Boolean

        if (isDuplicate) {
            resultDesc = "REPLAY DETECTED: Idempotency Key '$idempotencyKey' already processed. Return cached receipt (Prevents double billing!)."
            isSuccess = true
        } else {
            seenIdempotencyKeys.add(idempotencyKey)
            when (scenario.errorCode) {
                "insufficient_funds" -> {
                    resultDesc = "HTTP 402 Declined: Insufficient balance for $$customAmount. Dunning queue queued for Day 1."
                    isSuccess = false
                }
                "expired_card" -> {
                    resultDesc = "HTTP 400 Expired: Payment token revoked. Prompting user update banner."
                    isSuccess = false
                }
                "authentication_required" -> {
                    resultDesc = "HTTP 403 3DS Challenge: Verification Intent generated. Redirected to 3DS2 flow."
                    isSuccess = false
                }
                "gateway_timeout" -> {
                    resultDesc = "HTTP 504 Timeout: Circuit Breaker triggered retry #1 with backoff."
                    isSuccess = false
                }
                "stolen_card" -> {
                    resultDesc = "HTTP 403 Security Flag: Account quarantined. Alert dispatched to security team."
                    isSuccess = false
                }
                "rate_limit_exceeded" -> {
                    resultDesc = "HTTP 429 Throttled: Queue paused for 2.4s to drain token bucket."
                    isSuccess = false
                }
                else -> {
                    resultDesc = "Transaction of $$customAmount processed successfully."
                    isSuccess = true
                }
            }
        }

        val updatedScenario = scenario.copy(
            isSimulated = true,
            simulationResult = resultDesc
        )

        val log = PaymentAuditLog(
            id = UUID.randomUUID().toString().take(8),
            timestamp = now,
            event = "[${scenario.errorCode.uppercase()}] HTTP ${scenario.httpCode}",
            details = resultDesc,
            isSuccess = isSuccess,
            idempotencyKey = idempotencyKey
        )

        return Pair(updatedScenario, log)
    }

    fun generateIdempotencyKey(cartId: String, amount: Double): String {
        val raw = "$cartId-$amount-${System.currentTimeMillis() / 60000}" // changes per minute
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return "idem_" + digest.take(6).joinToString("") { "%02x".format(it) }
    }
}
