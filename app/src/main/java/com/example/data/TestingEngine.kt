package com.example.data

import com.example.model.CalculationTestItem
import com.example.model.TestStatus
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.system.measureNanoTime

object TestingEngine {

    fun getInitialTestSuite(): List<CalculationTestItem> {
        return listOf(
            CalculationTestItem(
                id = "calc-vat-standard",
                title = "Standard 20% VAT with Banker's Rounding",
                category = "Tax & VAT",
                description = "Tests gross amount calculation from net price $149.99 with exact half-even rounding.",
                formula = "Gross = Net + (Net × 0.20), rounded HALF_EVEN",
                inputSummary = "Net: $149.99, Rate: 20.0%",
                expectedOutput = "Gross: $179.99 (Tax: $30.00)"
            ),
            CalculationTestItem(
                id = "calc-tax-compound",
                title = "Multi-tier Progressive Tax Bracket",
                category = "Tax & VAT",
                description = "Tier 1: 0-$10k @ 0%, Tier 2: $10k-$50k @ 12%, Tier 3: >$50k @ 22% on $65,000 income.",
                formula = "Tax = 0 + (40k × 0.12) + (15k × 0.22)",
                inputSummary = "Income: $65,000.00",
                expectedOutput = "Total Tax: $8,100.00 (Effective Rate: 12.46%)"
            ),
            CalculationTestItem(
                id = "calc-discount-tier",
                title = "Tiered Volume Discount Calculation",
                category = "Discounts",
                description = "10% off for 10-49 units, 20% off for 50+ units. Order of 55 units at $40/unit.",
                formula = "Total = (55 × $40) × (1 - 0.20)",
                inputSummary = "Qty: 55, Base Price: $40.00, Tier: 20%",
                expectedOutput = "Net Total: $1,760.00 (Savings: $440.00)"
            ),
            CalculationTestItem(
                id = "calc-coupon-stack",
                title = "Coupon Stacking & Max Cap Protection",
                category = "Discounts",
                description = "Applying 25% promo with a $50 maximum discount cap on a $350 cart.",
                formula = "Discount = min(Cart × 0.25, $50.00)",
                inputSummary = "Cart: $350.00, Promo: 25%, Cap: $50.00",
                expectedOutput = "Discount Applied: $50.00 (Final: $300.00)"
            ),
            CalculationTestItem(
                id = "calc-float-vs-bigdec",
                title = "Float Drift vs BigDecimal Precision",
                category = "Currency Precision",
                description = "Adding 0.1 + 0.2 repeatedly. Demonstrates IEEE-754 precision loss vs exact currency math.",
                formula = "Sum of ten 0.1 increments in BigDecimal",
                inputSummary = "Ten $0.10 micro-transactions",
                expectedOutput = "BigDecimal: $1.00 (Float would drift to 0.99999994)"
            ),
            CalculationTestItem(
                id = "calc-fx-exchange",
                title = "Cross-Currency FX Conversion with Spread",
                category = "Currency Precision",
                description = "Convert 1,250 EUR to USD at rate 1.0845 with 1.25% intermediary exchange spread.",
                formula = "USD = EUR × (Rate × (1 - Spread))",
                inputSummary = "EUR 1,250.00, Rate: 1.0845, Spread: 1.25%",
                expectedOutput = "USD: $1,338.68 (Spread Fee: $16.95)"
            ),
            CalculationTestItem(
                id = "calc-sub-proration",
                title = "Mid-Cycle Subscription Proration",
                category = "Proration",
                description = "Upgrade from Starter ($15/mo) to Pro ($45/mo) on Day 18 of a 30-day billing cycle.",
                formula = "Net Due = (Pro - Starter) × (Remaining Days / Total Days)",
                inputSummary = "Starter: $15, Pro: $45, Days remaining: 12/30",
                expectedOutput = "Prorated Charge: $12.00 (Unused credit applied)"
            ),
            CalculationTestItem(
                id = "calc-boundary-zero",
                title = "Zero Division & Empty Cart Guard",
                category = "Boundary",
                description = "Verifies system does not throw ArithmeticException when calculating average item cost on 0 items.",
                formula = "SafeDiv(CartTotal, ItemCount, fallback = 0)",
                inputSummary = "Total: $0.00, Items: 0",
                expectedOutput = "Safe Average: $0.00 (Zero Division Prevented)"
            ),
            CalculationTestItem(
                id = "calc-boundary-overflow",
                title = "Integer & Currency Overflow Protection",
                category = "Boundary",
                description = "Ensures transactions exceeding 2^31 cents saturate safely without wrapping to negative numbers.",
                formula = "Check math overflow on 999,999,999.99 units",
                inputSummary = "Extreme balance transaction: $999,999,999.99",
                expectedOutput = "Valid Currency Range: Verified within BigDecimal limits"
            ),
            CalculationTestItem(
                id = "calc-negative-shield",
                title = "Negative Value & Tampering Rejection",
                category = "Boundary",
                description = "Reject malicious negative prices (-$500.00 refund exploit attempt in cart total).",
                formula = "Validate(price >= 0.00)",
                inputSummary = "Price: -$500.00, Quantity: 2",
                expectedOutput = "Rejected: Validation Exception (Price must be non-negative)"
            )
        )
    }

    fun executeSingleTest(item: CalculationTestItem): CalculationTestItem {
        val updated = item.copy()
        var actual = ""
        var isSuccess = false
        var err: String? = null

        val elapsedNanos = measureNanoTime {
            try {
                when (item.id) {
                    "calc-vat-standard" -> {
                        val net = BigDecimal("149.99")
                        val vatRate = BigDecimal("0.20")
                        val tax = (net * vatRate).setScale(2, RoundingMode.HALF_EVEN)
                        val gross = net + tax
                        actual = "Gross: $$gross (Tax: $$tax)"
                        isSuccess = (gross.compareTo(BigDecimal("179.99")) == 0 && tax.compareTo(BigDecimal("30.00")) == 0)
                    }
                    "calc-tax-compound" -> {
                        val income = BigDecimal("65000.00")
                        val tier1Max = BigDecimal("10000.00")
                        val tier2Max = BigDecimal("50000.00")

                        val tier2Tax = (tier2Max - tier1Max) * BigDecimal("0.12") // 40k * 0.12 = 4800
                        val tier3Tax = (income - tier2Max) * BigDecimal("0.22") // 15k * 0.22 = 3300
                        val totalTax = tier2Tax + tier3Tax
                        val effectiveRate = (totalTax.divide(income, 4, RoundingMode.HALF_UP) * BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP)
                        actual = "Total Tax: $$totalTax (Effective Rate: $effectiveRate%)"
                        isSuccess = (totalTax.compareTo(BigDecimal("8100.00")) == 0)
                    }
                    "calc-discount-tier" -> {
                        val qty = 55
                        val basePrice = BigDecimal("40.00")
                        val discountRate = if (qty >= 50) BigDecimal("0.20") else BigDecimal("0.10")
                        val gross = basePrice * BigDecimal(qty) // 2200
                        val savings = gross * discountRate // 440
                        val net = gross - savings // 1760
                        actual = "Net Total: $$net (Savings: $$savings)"
                        isSuccess = (net.compareTo(BigDecimal("1760.00")) == 0)
                    }
                    "calc-coupon-stack" -> {
                        val cart = BigDecimal("350.00")
                        val rate = BigDecimal("0.25")
                        val cap = BigDecimal("50.00")
                        val rawDiscount = cart * rate // 87.50
                        val appliedDiscount = if (rawDiscount > cap) cap else rawDiscount
                        val finalTotal = cart - appliedDiscount
                        actual = "Discount Applied: $$appliedDiscount (Final: $$finalTotal)"
                        isSuccess = (appliedDiscount.compareTo(BigDecimal("50.00")) == 0 && finalTotal.compareTo(BigDecimal("300.00")) == 0)
                    }
                    "calc-float-vs-bigdec" -> {
                        var bigDecSum = BigDecimal.ZERO
                        for (i in 1..10) {
                            bigDecSum = bigDecSum.add(BigDecimal("0.10"))
                        }
                        actual = "BigDecimal: $$bigDecSum (Float would drift to 0.99999994)"
                        isSuccess = (bigDecSum.compareTo(BigDecimal("1.00")) == 0)
                    }
                    "calc-fx-exchange" -> {
                        val eur = BigDecimal("1250.00")
                        val rate = BigDecimal("1.0845")
                        val spread = BigDecimal("0.0125")
                        val effectiveRate = rate * (BigDecimal.ONE - spread)
                        val usd = (eur * effectiveRate).setScale(2, RoundingMode.HALF_EVEN)
                        val spreadFee = ((eur * rate) * spread).setScale(2, RoundingMode.HALF_EVEN)
                        actual = "USD: $$usd (Spread Fee: $$spreadFee)"
                        isSuccess = (usd.compareTo(BigDecimal("1338.68")) == 0)
                    }
                    "calc-sub-proration" -> {
                        val starter = BigDecimal("15.00")
                        val pro = BigDecimal("45.00")
                        val diff = pro - starter // 30.00
                        val daysRemaining = BigDecimal("12")
                        val totalDays = BigDecimal("30")
                        val prorated = (diff * daysRemaining).divide(totalDays, 2, RoundingMode.HALF_EVEN)
                        actual = "Prorated Charge: $$prorated (Unused credit applied)"
                        isSuccess = (prorated.compareTo(BigDecimal("12.00")) == 0)
                    }
                    "calc-boundary-zero" -> {
                        val cartTotal = BigDecimal("0.00")
                        val count = 0
                        val avg = if (count > 0) cartTotal.divide(BigDecimal(count), 2, RoundingMode.HALF_EVEN) else BigDecimal.ZERO
                        actual = "Safe Average: $$avg (Zero Division Prevented)"
                        isSuccess = (avg.compareTo(BigDecimal.ZERO) == 0)
                    }
                    "calc-boundary-overflow" -> {
                        val extremeAmount = BigDecimal("999999999.99")
                        val tax = (extremeAmount * BigDecimal("0.05")).setScale(2, RoundingMode.HALF_EVEN)
                        val total = extremeAmount + tax
                        actual = "Valid Currency Range: Verified within BigDecimal limits"
                        isSuccess = total > extremeAmount
                    }
                    "calc-negative-shield" -> {
                        val price = BigDecimal("-500.00")
                        if (price < BigDecimal.ZERO) {
                            actual = "Rejected: Validation Exception (Price must be non-negative)"
                            isSuccess = true
                        } else {
                            actual = "Failed to reject negative price"
                            isSuccess = false
                        }
                    }
                    else -> {
                        actual = "Unknown test definition"
                        isSuccess = false
                    }
                }
            } catch (e: Exception) {
                err = e.localizedMessage
                actual = "Exception: ${e.message}"
                isSuccess = false
            }
        }

        return updated.copy(
            actualOutput = actual,
            status = if (isSuccess) TestStatus.PASSED else TestStatus.FAILED,
            latencyMs = elapsedNanos / 1_000_000,
            errorMessage = err
        )
    }

    // Live custom calculation workbench
    fun computeCustomVat(amount: Double, taxRatePercent: Double, discountPercent: Double): Map<String, String> {
        return try {
            val base = BigDecimal(amount.toString()).setScale(2, RoundingMode.HALF_EVEN)
            val discountRate = BigDecimal((discountPercent / 100.0).toString())
            val taxRate = BigDecimal((taxRatePercent / 100.0).toString())

            val discountAmount = (base * discountRate).setScale(2, RoundingMode.HALF_EVEN)
            val discountedBase = base - discountAmount
            val taxAmount = (discountedBase * taxRate).setScale(2, RoundingMode.HALF_EVEN)
            val finalTotal = discountedBase + taxAmount

            // Also compute Float equivalent to demonstrate drift
            val floatBase = amount.toFloat()
            val floatDisc = floatBase * (discountPercent.toFloat() / 100f)
            val floatTax = (floatBase - floatDisc) * (taxRatePercent.toFloat() / 100f)
            val floatFinal = (floatBase - floatDisc) + floatTax

            mapOf(
                "base" to "$$base",
                "discount" to "-$$discountAmount",
                "tax" to "+$$taxAmount",
                "finalTotal" to "$$finalTotal",
                "floatDifference" to String.format("%.6f", floatFinal.toDouble() - finalTotal.toDouble()),
                "status" to "SUCCESS"
            )
        } catch (e: Exception) {
            mapOf("status" to "ERROR", "error" to (e.message ?: "Invalid numeric values"))
        }
    }
}
