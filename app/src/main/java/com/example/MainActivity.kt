package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.CountryRegistry
import com.example.ui.components.CountrySelectorGrid
import com.example.ui.components.HolographicLogoHeader
import com.example.ui.components.InteractiveEarthGlobe
import com.example.ui.components.MainConnectionConsole
import com.example.ui.components.RealTimeTerminalLogs
import com.example.ui.components.SettingsAndShieldsConsole
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.VpnViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VpnViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CosmicBackground)
                ) { innerPadding ->
                    VpnDashboardScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VpnDashboardScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.vpnConfig.collectAsState()
    val logs by viewModel.vpnLogs.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()

    val currentConfig = config ?: com.example.db.VpnConfig()
    val activeCountry = CountryRegistry.getCountryByCode(currentConfig.selectedCountryCode)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBackground)
            .verticalScroll(rememberScrollState())
    ) {
        HolographicLogoHeader()

        Spacer(modifier = Modifier.height(12.dp))

        // Large beautiful Planet Earth Globe
        InteractiveEarthGlobe(
            selectedCountry = activeCountry,
            isConnected = isConnected,
            onCountrySelected = { viewModel.selectCountry(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Main Connection button / state bar
            MainConnectionConsole(
                selectedCountry = activeCountry,
                isConnected = isConnected,
                isConnecting = isConnecting,
                onToggleConnection = { viewModel.toggleVpnConnection() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Country selection grid
            CountrySelectorGrid(
                selectedCountry = activeCountry,
                onCountrySelected = { viewModel.selectCountry(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Shields & Ad blocking configs
            SettingsAndShieldsConsole(
                config = currentConfig,
                onAdBlockChange = { viewModel.updateAdBlockConfig(it) },
                onMalwareChange = { viewModel.updateMalwareProtection(it) },
                onTriggerVerify = { viewModel.triggerBypassVerify(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live event logging console log terminal
            RealTimeTerminalLogs(
                logs = logs,
                onClearLogs = { viewModel.clearVpnLogs() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
