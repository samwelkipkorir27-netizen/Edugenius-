package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationTestItem
import com.example.model.TestStatus
import com.example.ui.components.SectionHeader
import com.example.ui.components.TestStatusBadge
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseError
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy700
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.ReleaseGuardViewModel

@Composable
fun CalculationsScreen(
    viewModel: ReleaseGuardViewModel,
    modifier: Modifier = Modifier
) {
    val tests by viewModel.calculationTests.collectAsState()
    val isRunning by viewModel.isTestingRunning.collectAsState()
    val workbench by viewModel.calculationWorkbench.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Tax & VAT", "Discounts", "Currency Precision", "Proration", "Boundary")

    val filteredTests = if (selectedCategory == "All") {
        tests
    } else {
        tests.filter { it.category == selectedCategory }
    }

    val passedCount = tests.count { it.status == TestStatus.PASSED }
    val failedCount = tests.count { it.status == TestStatus.FAILED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Stage 6: Calculations Verification",
                subtitle = "Automated boundary testing, Banker's rounding, tax proration & precision proof",
                badgeText = "Stage 6"
            )
        }

        item {
            // Action & Stats Bar
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Validation Suite Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$passedCount Passed • $failedCount Failed • ${tests.size} Total Cases",
                                fontSize = 12.sp,
                                color = if (failedCount > 0) RoseError else EmeraldSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.runAllCalculationTests() },
                            enabled = !isRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("run_all_tests_button")
                        ) {
                            if (isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Running...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run All (10)", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Category filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) CyanPrimary else SlateNavy800)
                            .border(
                                1.dp,
                                if (isSelected) CyanPrimary else SlateBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("cat_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.Black else SlateTextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Automated Test Vectors",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Test list items
        items(filteredTests) { item ->
            CalculationTestCard(item = item)
        }

        item {
            // Interactive Financial Calculation Sandbox
            InteractiveCalculationSandbox(
                workbench = workbench,
                onUpdate = { amt, tax, disc -> viewModel.updateWorkbenchInputs(amt, tax, disc) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CalculationTestCard(item: CalculationTestItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("test_card_${item.id}"),
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
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                TestStatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
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
                        text = "Formula: ${item.formula}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Input: ${item.inputSummary}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Expected: ${item.expectedOutput}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldSuccess
                    )
                    if (item.actualOutput.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Actual: ${item.actualOutput} (${item.latencyMs}ms)",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (item.status == TestStatus.PASSED) EmeraldSuccess else RoseError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveCalculationSandbox(
    workbench: com.example.viewmodel.CalculationWorkbenchState,
    onUpdate: (String, String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_calc_sandbox"),
        colors = CardDefaults.cardColors(containerColor = SlateNavy800),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Financial Math Sandbox",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Live validation of Net Price, Discount, and VAT with IEEE-754 drift detection",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = workbench.amount,
                    onValueChange = { onUpdate(it, workbench.taxRate, workbench.discount) },
                    label = { Text("Base ($)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_calc_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SlateNavy700
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = workbench.taxRate,
                    onValueChange = { onUpdate(workbench.amount, it, workbench.discount) },
                    label = { Text("VAT (%)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_calc_tax"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SlateNavy700
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = workbench.discount,
                    onValueChange = { onUpdate(workbench.amount, workbench.taxRate, it) },
                    label = { Text("Disc (%)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_calc_discount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SlateNavy700
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calculation Results
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090D16))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Price:", color = SlateTextMuted, fontSize = 12.sp)
                        Text(workbench.result["base"] ?: "$0.00", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount Applied:", color = SlateTextMuted, fontSize = 12.sp)
                        Text(workbench.result["discount"] ?: "$0.00", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("VAT / Sales Tax:", color = SlateTextMuted, fontSize = 12.sp)
                        Text(workbench.result["tax"] ?: "$0.00", color = CyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SlateNavy700))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Final Customer Charge:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(workbench.result["finalTotal"] ?: "$0.00", color = CyanPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Float Drift Offset: ${workbench.result["floatDifference"] ?: "0.000000"} (BigDecimal eliminates floating rounding leak)",
                        fontSize = 10.sp,
                        color = Color(0xFFF59E0B),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
