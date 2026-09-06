package com.example.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkEditorBackground
import com.example.ui.theme.DarkEditorSurface
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TerminalRed
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBlueDark
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenLight
import com.example.ui.theme.VibrantRedLight
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.viewmodel.ExecutionStatus
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ConsoleOutputView(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val executionStatus by viewModel.executionStatus.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val currentCode by viewModel.currentCode.collectAsState()

    val isHtmlFile = activeFile?.fileExtension.equals("html", ignoreCase = true)
    var viewMode by remember(isHtmlFile) {
        mutableStateOf(if (isHtmlFile) 1 else 0) // 0: Terminal, 1: Web Preview
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // --- 1. Top Header Bar (Vibrant Palette style) ---
        Surface(
            color = VibrantSurface,
            border = BorderStroke(1.dp, VibrantBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VibrantBlueContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Konsola",
                            tint = VibrantBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Konsola NetBeans",
                            color = VibrantTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            text = "Środowisko uruchomieniowe JVM & Web",
                            color = VibrantTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    when (val status = executionStatus) {
                        is ExecutionStatus.Running -> {
                            CircularProgressIndicator(
                                color = VibrantBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kompilowanie...", color = VibrantBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        is ExecutionStatus.Finished -> {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (status.result.exitCode == 0) VibrantGreenLight else VibrantRedLight,
                                border = BorderStroke(1.dp, if (status.result.exitCode == 0) VibrantGreen.copy(alpha = 0.3f) else Color(0xFFFCA5A5))
                            ) {
                                Text(
                                    text = if (status.result.exitCode == 0) "SUKCES (0)" else "BŁĄD (${status.result.exitCode})",
                                    color = if (status.result.exitCode == 0) Color(0xFF065F46) else Color(0xFF991B1B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }

                Button(
                    onClick = { viewModel.runCode() },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("console_rerun_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Uruchom ponownie", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Wykonaj", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 2. Tabs: Terminal vs Web Preview ---
        if (isHtmlFile) {
            TabRow(
                selectedTabIndex = viewMode,
                containerColor = VibrantSurface,
                contentColor = VibrantBlue,
                indicator = { tabPositions ->
                    if (viewMode < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[viewMode]),
                            color = VibrantBlue,
                            height = 3.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = viewMode == 0,
                    onClick = { viewMode = 0 },
                    text = {
                        Text(
                            "Terminal / Konsola",
                            fontWeight = if (viewMode == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == 0) VibrantBlue else VibrantTextSecondary
                        )
                    }
                )
                Tab(
                    selected = viewMode == 1,
                    onClick = { viewMode = 1 },
                    text = {
                        Text(
                            "Podgląd HTML5 / JS",
                            fontWeight = if (viewMode == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == 1) VibrantBlue else VibrantTextSecondary
                        )
                    }
                )
            }
        }

        // --- 3. Content Area ---
        if (viewMode == 1 && isHtmlFile) {
            // Live WebView embedded in card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, VibrantBorderLight),
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                loadDataWithBaseURL(null, currentCode, "text/html", "UTF-8", null)
                            }
                        },
                        update = { webView ->
                            webView.loadDataWithBaseURL(null, currentCode, "text/html", "UTF-8", null)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Terminal View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkEditorBackground),
                    border = BorderStroke(1.dp, VibrantBorderLight),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = "NetBeans Studio Runtime Environment v1.0",
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "root@android-ide:~/workspace/${activeFile?.fileName ?: "main"}$ run",
                            color = TerminalGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        when (val status = executionStatus) {
                            is ExecutionStatus.Running -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = VibrantBlueLight,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Kompilacja i wykonywanie kodu w toku...",
                                        color = Color(0xFFE2E8F0),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            is ExecutionStatus.Finished -> {
                                Text(
                                    text = status.result.output,
                                    color = if (status.result.exitCode == 0) Color(0xFFE2E8F0) else TerminalRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "--- Zakończono z kodem wyjścia ${status.result.exitCode} w ${status.result.executionTimeMs}ms ---",
                                    color = Color(0xFF64748B),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                            else -> {
                                if (consoleLogs.isNotEmpty()) {
                                    Text(
                                        text = consoleLogs,
                                        color = Color(0xFFE2E8F0),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                } else {
                                    Text(
                                        text = "Konsola jest gotowa. Kliknij 'Wykonaj' lub 'Uruchom' w edytorze kodu, aby zobaczyć wynik kompilacji i działania programu.",
                                        color = Color(0xFF64748B),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
