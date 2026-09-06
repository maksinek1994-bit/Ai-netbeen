package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ApkAndGoogleHubView
import com.example.ui.components.CodeEditorView
import com.example.ui.components.ConsoleOutputView
import com.example.ui.components.GeminiAiView
import com.example.ui.components.ProjectsExplorerView
import com.example.ui.theme.DarkEditorBackground
import com.example.ui.theme.DarkEditorBorder
import com.example.ui.theme.DarkEditorSurface
import com.example.ui.theme.GeminiPurple
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NetBeansAccent
import com.example.ui.theme.NetBeansBlue
import com.example.ui.theme.NetBeansBlueDark
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantGeminiGradient
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = false) {
                NetBeansStudioApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetBeansStudioApp(
    viewModel: StudioViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val activeProject = allProjects.firstOrNull { it.id == activeProjectId }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = VibrantBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Vibrant Gradient Gemini & NetBeans Icon (from-blue-500 via-purple-500 to-red-400)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VibrantGeminiGradient)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "NetBeans Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = VibrantTextPrimary,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "Gemini AI & Google IDE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = VibrantBlue
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Active project badge
                        if (activeProject != null) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder),
                                modifier = Modifier
                                    .clickable { viewModel.selectTab(StudioTab.PROJECTS) }
                                    .testTag("top_project_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(VibrantBlue)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = activeProject.name,
                                        color = VibrantTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { viewModel.selectTab(StudioTab.APK_GOOGLE) },
                            modifier = Modifier.testTag("top_apk_help_button")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, VibrantBorder, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Android,
                                    contentDescription = "Instalacja APK i Google",
                                    tint = GoogleGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VibrantSurface,
                    titleContentColor = VibrantTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = VibrantSurface,
                contentColor = VibrantBlue,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .border(androidx.compose.foundation.BorderStroke(1.dp, VibrantBorder))
            ) {
                NavigationBarItem(
                    selected = selectedTab == StudioTab.EDITOR,
                    onClick = { viewModel.selectTab(StudioTab.EDITOR) },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Edytor") },
                    label = { Text("Edytor", fontSize = 11.sp, fontWeight = if (selectedTab == StudioTab.EDITOR) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibrantBlue,
                        selectedTextColor = VibrantBlue,
                        indicatorColor = VibrantBlueContainer,
                        unselectedIconColor = VibrantTextSubtle,
                        unselectedTextColor = VibrantTextSubtle
                    ),
                    modifier = Modifier.testTag("nav_tab_editor")
                )

                NavigationBarItem(
                    selected = selectedTab == StudioTab.CONSOLE,
                    onClick = { viewModel.selectTab(StudioTab.CONSOLE) },
                    icon = { Icon(Icons.Default.Computer, contentDescription = "Konsola") },
                    label = { Text("Konsola", fontSize = 11.sp, fontWeight = if (selectedTab == StudioTab.CONSOLE) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibrantBlue,
                        selectedTextColor = VibrantBlue,
                        indicatorColor = VibrantBlueContainer,
                        unselectedIconColor = VibrantTextSubtle,
                        unselectedTextColor = VibrantTextSubtle
                    ),
                    modifier = Modifier.testTag("nav_tab_console")
                )

                NavigationBarItem(
                    selected = selectedTab == StudioTab.AI_ASSISTANT,
                    onClick = { viewModel.selectTab(StudioTab.AI_ASSISTANT) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Gemini AI") },
                    label = { Text("Gemini AI", fontSize = 11.sp, fontWeight = if (selectedTab == StudioTab.AI_ASSISTANT) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibrantPurple,
                        selectedTextColor = VibrantPurple,
                        indicatorColor = VibrantPurple.copy(alpha = 0.15f),
                        unselectedIconColor = VibrantTextSubtle,
                        unselectedTextColor = VibrantTextSubtle
                    ),
                    modifier = Modifier.testTag("nav_tab_ai")
                )

                NavigationBarItem(
                    selected = selectedTab == StudioTab.PROJECTS,
                    onClick = { viewModel.selectTab(StudioTab.PROJECTS) },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Projekty") },
                    label = { Text("Projekty", fontSize = 11.sp, fontWeight = if (selectedTab == StudioTab.PROJECTS) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VibrantBlue,
                        selectedTextColor = VibrantBlue,
                        indicatorColor = VibrantBlueContainer,
                        unselectedIconColor = VibrantTextSubtle,
                        unselectedTextColor = VibrantTextSubtle
                    ),
                    modifier = Modifier.testTag("nav_tab_projects")
                )

                NavigationBarItem(
                    selected = selectedTab == StudioTab.APK_GOOGLE,
                    onClick = { viewModel.selectTab(StudioTab.APK_GOOGLE) },
                    icon = { Icon(Icons.Default.Android, contentDescription = "APK & Google") },
                    label = { Text("APK / Google", fontSize = 11.sp, fontWeight = if (selectedTab == StudioTab.APK_GOOGLE) FontWeight.SemiBold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoogleGreen,
                        selectedTextColor = GoogleGreen,
                        indicatorColor = GoogleGreen.copy(alpha = 0.15f),
                        unselectedIconColor = VibrantTextSubtle,
                        unselectedTextColor = VibrantTextSubtle
                    ),
                    modifier = Modifier.testTag("nav_tab_apk_google")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(VibrantBg)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "StudioTabAnimation"
            ) { tab ->
                when (tab) {
                    StudioTab.EDITOR -> CodeEditorView(viewModel = viewModel)
                    StudioTab.CONSOLE -> ConsoleOutputView(viewModel = viewModel)
                    StudioTab.AI_ASSISTANT -> GeminiAiView(viewModel = viewModel)
                    StudioTab.PROJECTS -> ProjectsExplorerView(viewModel = viewModel)
                    StudioTab.APK_GOOGLE -> ApkAndGoogleHubView(viewModel = viewModel)
                }
            }
        }
    }
}
