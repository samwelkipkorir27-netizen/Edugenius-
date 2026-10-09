package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CalculationsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PaymentsRecoveryScreen
import com.example.ui.screens.PermissionsSecurityScreen
import com.example.ui.screens.PrivacyBackupScreen
import com.example.ui.screens.PublishMaintainScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateNavy800
import com.example.ui.theme.SlateNavy900
import com.example.ui.theme.SlateTextMuted
import com.example.viewmodel.AppNavSection
import com.example.viewmodel.ReleaseGuardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ReleaseGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: ReleaseGuardViewModel) {
    val currentSection by viewModel.currentSection.collectAsState()

    // BackHandler: If user is on a sub-screen, pressing Back navigates back to Dashboard
    if (currentSection != AppNavSection.DASHBOARD) {
        BackHandler {
            viewModel.setNavSection(AppNavSection.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_edugenius_1791553015274),
                                contentDescription = "Edugenius Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Edugenius",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STAGE 6 & 7",
                                color = CyanPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ASSURANCE ON",
                            color = EmeraldSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateNavy900,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SlateNavy800,
                modifier = Modifier
                    .border(1.dp, SlateBorder)
                    .testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentSection == AppNavSection.DASHBOARD,
                    onClick = { viewModel.setNavSection(AppNavSection.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                NavigationBarItem(
                    selected = currentSection == AppNavSection.CALCULATIONS,
                    onClick = { viewModel.setNavSection(AppNavSection.CALCULATIONS) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculations") },
                    label = { Text("Calc", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_calculations")
                )

                NavigationBarItem(
                    selected = currentSection == AppNavSection.PERMISSIONS_SECURITY,
                    onClick = { viewModel.setNavSection(AppNavSection.PERMISSIONS_SECURITY) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Security") },
                    label = { Text("Security", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_security")
                )

                NavigationBarItem(
                    selected = currentSection == AppNavSection.PAYMENTS_RECOVERY,
                    onClick = { viewModel.setNavSection(AppNavSection.PAYMENTS_RECOVERY) },
                    icon = { Icon(Icons.Default.LockReset, contentDescription = "Payments") },
                    label = { Text("Payments", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_payments")
                )

                NavigationBarItem(
                    selected = currentSection == AppNavSection.PRIVACY_BACKUPS,
                    onClick = { viewModel.setNavSection(AppNavSection.PRIVACY_BACKUPS) },
                    icon = { Icon(Icons.Default.Policy, contentDescription = "Privacy") },
                    label = { Text("Privacy", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_privacy")
                )

                NavigationBarItem(
                    selected = currentSection == AppNavSection.PUBLISH_MAINTAIN,
                    onClick = { viewModel.setNavSection(AppNavSection.PUBLISH_MAINTAIN) },
                    icon = { Icon(Icons.Default.RocketLaunch, contentDescription = "Publish") },
                    label = { Text("Launch", fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CyanPrimary,
                        indicatorColor = CyanPrimary,
                        unselectedIconColor = SlateTextMuted,
                        unselectedTextColor = SlateTextMuted
                    ),
                    modifier = Modifier.testTag("nav_item_publish")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateNavy900)
                .padding(innerPadding)
        ) {
            when (currentSection) {
                AppNavSection.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppNavSection.CALCULATIONS -> CalculationsScreen(viewModel = viewModel)
                AppNavSection.PERMISSIONS_SECURITY -> PermissionsSecurityScreen(viewModel = viewModel)
                AppNavSection.PAYMENTS_RECOVERY -> PaymentsRecoveryScreen(viewModel = viewModel)
                AppNavSection.PRIVACY_BACKUPS -> PrivacyBackupScreen(viewModel = viewModel)
                AppNavSection.PUBLISH_MAINTAIN -> PublishMaintainScreen(viewModel = viewModel)
            }
        }
    }
}
