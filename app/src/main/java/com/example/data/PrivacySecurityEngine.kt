package com.example.data

import com.example.model.BackupSnapshot
import com.example.model.PiiMatch
import com.example.model.RecoveryBackupCode
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PrivacySecurityEngine {

    // Regex matchers for PII
    private val emailRegex = Regex("""[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+""")
    private val cardRegex = Regex("""\b(?:\d{4}[ -]?){3}\d{4}\b""")
    private val phoneRegex = Regex("""\b(?:\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}\b""")
    private val ipRegex = Regex("""\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b""")

    fun scanAndMaskPii(input: String): Pair<String, List<PiiMatch>> {
        val matches = mutableListOf<PiiMatch>()
        var masked = input

        // 1. Credit Cards
        masked = cardRegex.replace(masked) { matchResult ->
            val raw = matchResult.value
            val digits = raw.filter { it.isDigit() }
            val last4 = digits.takeLast(4)
            val maskedCard = "••••-••••-••••-$last4"
            matches.add(PiiMatch("Credit Card", raw, maskedCard))
            maskedCard
        }

        // 2. Emails
        masked = emailRegex.replace(masked) { matchResult ->
            val raw = matchResult.value
            val parts = raw.split("@")
            val name = parts.first()
            val domain = parts.getOrElse(1) { "domain.com" }
            val maskedName = if (name.length > 2) name.take(2) + "•••" else "•••"
            val maskedEmail = "$maskedName@$domain"
            matches.add(PiiMatch("Email", raw, maskedEmail))
            maskedEmail
        }

        // 3. Phones
        masked = phoneRegex.replace(masked) { matchResult ->
            val raw = matchResult.value
            val digits = raw.filter { it.isDigit() }
            val last4 = digits.takeLast(4)
            val maskedPhone = "(•••) •••-$last4"
            matches.add(PiiMatch("Phone Number", raw, maskedPhone))
            maskedPhone
        }

        // 4. IP Addresses
        masked = ipRegex.replace(masked) { matchResult ->
            val raw = matchResult.value
            val parts = raw.split(".")
            val maskedIp = "${parts.first()}.•••.•••.${parts.last()}"
            matches.add(PiiMatch("IP Address", raw, maskedIp))
            maskedIp
        }

        return Pair(masked, matches)
    }

    fun generateBackupCodes(): List<RecoveryBackupCode> {
        return (1..8).map {
            val part1 = UUID.randomUUID().toString().take(4).uppercase(Locale.US)
            val part2 = UUID.randomUUID().toString().take(4).uppercase(Locale.US)
            RecoveryBackupCode(code = "$part1-$part2", isUsed = false)
        }
    }

    fun sha256(content: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(content.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun createSnapshot(type: String, recordCount: Int): BackupSnapshot {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val id = "snap-" + UUID.randomUUID().toString().take(8)
        val samplePayload = "SNAPSHOT_DATA:$id:$timestamp:RECORDS:$recordCount:USER:samwelsokwony@gmail.com"
        val checksum = sha256(samplePayload)
        val sizeKb = (recordCount * 0.42) + 12.5

        return BackupSnapshot(
            id = id,
            timestamp = timestamp,
            recordsCount = recordCount,
            sizeKb = String.format(Locale.US, "%.1f", sizeKb).toDouble(),
            sha256Checksum = checksum,
            isIntegrityVerified = true,
            type = type
        )
    }

    fun verifySnapshotIntegrity(snapshot: BackupSnapshot): Boolean {
        // Recalculate checksum to test tamper detection
        val samplePayload = "SNAPSHOT_DATA:${snapshot.id}:${snapshot.timestamp}:RECORDS:${snapshot.recordsCount}:USER:samwelsokwony@gmail.com"
        val recalculated = sha256(samplePayload)
        return recalculated.equals(snapshot.sha256Checksum, ignoreCase = true)
    }
}
