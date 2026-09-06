package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBlueDark
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantGeminiGradient
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenLight
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantRed
import com.example.ui.theme.VibrantRedLight
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ApkAndGoogleHubView(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentSavedApiKey by viewModel.customApiKeyInput.collectAsState()
    val isKeyActive = currentSavedApiKey.isNotBlank()

    var keyInput by remember(currentSavedApiKey) { mutableStateOf(currentSavedApiKey) }
    var keySavedNotification by remember { mutableStateOf(false) }
    var googleSearchQuery by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // --- 1. Top Header (Vibrant Palette style) ---
        Surface(
            color = VibrantSurface,
            border = BorderStroke(1.dp, VibrantBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VibrantGreenLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = Color(0xFF065F46),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Instalacja APK & Integracja z Google",
                        color = VibrantTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "Samodzielna aplikacja na telefon + Gemini AI",
                        color = VibrantTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // --- 2. Scrollable Body Content ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- 2.1 APK Standalone Installation Card ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, VibrantBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("apk_installation_guide_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VibrantGreenLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF065F46),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Plik APK na telefon",
                                color = VibrantTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = VibrantGreenLight,
                            border = BorderStroke(1.dp, VibrantGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "GOTOWY DO INSTALACJI",
                                color = Color(0xFF065F46),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Jak zainstalować NetBeans Studio jako osobną aplikację na telefonie:",
                        color = VibrantTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val steps = listOf(
                        "1. W Google AI Studio kliknij menu w prawym górnym rogu (ikona zębatki / opcje) i wybierz opcję 'Download APK' lub 'Export ZIP'.",
                        "2. Skompilowany plik 'app-debug.apk' znajduje się w ścieżce: app/build/outputs/apk/debug/app-debug.apk.",
                        "3. Pobierz lub prześlij plik APK bezpośrednio na swój telefon (przez Dysk Google, e-mail lub kabel USB).",
                        "4. Otwórz pobrany plik na telefonie. Android zapyta o zgodę: włącz opcję 'Zezwalaj na instalowanie z tego źródła'.",
                        "5. Kliknij 'Zainstaluj' – na Twoim pulpicie pojawi się pełna, osobna aplikacja NetBeans Studio z dedykowaną ikoną!"
                    )

                    steps.forEach { step ->
                        Text(
                            text = step,
                            color = VibrantTextSecondary,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.shareCurrentCode() },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("share_project_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Udostępnij kod źródłowy z telefonu (Share)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // --- 2.2 Google Gemini AI Environment Card ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, VibrantBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VibrantGeminiGradient)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Środowisko Gemini AI (Google)",
                                color = VibrantTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isKeyActive) VibrantGreenLight else VibrantRedLight,
                            border = BorderStroke(1.dp, if (isKeyActive) VibrantGreen.copy(alpha = 0.3f) else VibrantRed.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isKeyActive) "POŁĄCZONO" else "WYMAGA KLUCZA",
                                color = if (isKeyActive) Color(0xFF065F46) else Color(0xFF991B1B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Aplikacja współpracuje bezpośrednio z modelem gemini-3.5-flash z rodziny Google Gemini. Aby asystent działał po zainstalowaniu na fizycznym telefonie:",
                        color = VibrantTextSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("Klucz Google Gemini API (opcjonalny dla telefonu)") },
                        placeholder = { Text("Wklej klucz z Google AI Studio (AIzaSy...)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VibrantTextPrimary,
                            unfocusedTextColor = VibrantTextPrimary,
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("gemini_api_key_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantBg, contentColor = VibrantTextSecondary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("get_key_from_google_button")
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pobierz klucz z Google", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                viewModel.saveCustomApiKey(keyInput)
                                keySavedNotification = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("save_gemini_key_button")
                        ) {
                            Text("Zapisz klucz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (keySavedNotification) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VibrantGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Klucz Gemini AI został zapisany pomyślnie na urządzeniu!",
                                color = Color(0xFF065F46),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // --- 2.3 Google Developer Hub & Docs ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, VibrantBorderLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Baza Wiedzy & Dokumentacja Google",
                        color = VibrantTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Oficjalne zasoby programistyczne Google, Android i Java dostępne na wyciągnięcie ręki:",
                        color = VibrantTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Search in Google
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = googleSearchQuery,
                            onValueChange = { googleSearchQuery = it },
                            placeholder = { Text("Wyszukaj błąd lub bibliotekę w Google...", fontSize = 12.5.sp, color = VibrantTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VibrantTextPrimary,
                                unfocusedTextColor = VibrantTextPrimary,
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).testTag("google_search_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (googleSearchQuery.isNotBlank()) {
                                    val url = "https://www.google.com/search?q=" + Uri.encode(googleSearchQuery + " android kotlin java")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(52.dp).testTag("google_search_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Szukaj w Google")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Docs Links
                    val docLinks = listOf(
                        Triple("Android Developers (Google)", "Dokumentacja SDK, Jetpack Compose, Kotlin", "https://developer.android.com/"),
                        Triple("Google AI Studio", "Dokumentacja modeli Gemini i API", "https://ai.google.dev/"),
                        Triple("Kotlin Docs", "Oficjalna dokumentacja języka Kotlin", "https://kotlinlang.org/docs/home.html"),
                        Triple("Oracle Java SE", "Specyfikacja klas i metod języka Java", "https://docs.oracle.com/en/java/")
                    )

                    docLinks.forEach { (title, subtitle, url) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = VibrantBg,
                            border = BorderStroke(1.dp, VibrantBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, color = VibrantBlueDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(subtitle, color = VibrantTextMuted, fontSize = 11.5.sp)
                                }
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = VibrantTextSubtle, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
