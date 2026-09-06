package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBlueDark
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantGeminiGradient
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantGreenLight
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantRed
import com.example.ui.theme.VibrantRedLight
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.viewmodel.AiTaskStatus
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun GeminiAiView(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val aiStatus by viewModel.aiStatus.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var promptInput by remember { mutableStateOf("") }
    var selectedAiTool by remember { mutableStateOf(0) } // 0: Czat, 1: Generowanie, 2: Debugger

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // --- 1. Top Bar / Subheader (Vibrant Palette style) ---
        Surface(
            color = VibrantSurface,
            border = BorderStroke(1.dp, VibrantBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                            contentDescription = "Gemini AI",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gemini AI Code Studio",
                            color = VibrantTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            text = "Model: gemini-3.5-flash • NetBeans Sync Active",
                            color = VibrantBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = VibrantPurpleContainer,
                    border = BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Google AI",
                        color = VibrantPurpleDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // --- 2. Vibrant Mode Selector Tabs (Chips) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VibrantBg)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedAiTool == 0,
                onClick = { selectedAiTool = 0 },
                label = { Text("💬 Czat & Pair Programmer", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VibrantBlue,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = VibrantTextSecondary
                ),
                border = BorderStroke(1.dp, if (selectedAiTool == 0) VibrantBlue else VibrantBorder)
            )
            FilterChip(
                selected = selectedAiTool == 1,
                onClick = { selectedAiTool = 1 },
                label = { Text("✨ Generuj kod", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VibrantPurple,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = VibrantTextSecondary
                ),
                border = BorderStroke(1.dp, if (selectedAiTool == 1) VibrantPurple else VibrantBorder)
            )
            FilterChip(
                selected = selectedAiTool == 2,
                onClick = {
                    selectedAiTool = 2
                    viewModel.debugAndFixCode()
                },
                label = { Text("🐛 Debuguj i napraw", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VibrantBlue,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = VibrantTextSecondary
                ),
                border = BorderStroke(1.dp, if (selectedAiTool == 2) VibrantBlue else VibrantBorder)
            )
        }

        // --- 3. Active AI Task Status Box ---
        when (val status = aiStatus) {
            is AiTaskStatus.Working -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, VibrantBorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = VibrantPurple,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = status.message,
                            color = VibrantTextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            is AiTaskStatus.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VibrantRedLight),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, VibrantRed.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Błąd: ${status.error}",
                        color = Color(0xFF991B1B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
            is AiTaskStatus.Success -> {
                if (status.fixResult?.fixedCode != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VibrantGreenLight),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, VibrantGreen.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡 Gemini AI przygotował poprawkę",
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Button(
                                    onClick = { viewModel.applyFixedCode(status.fixResult.fixedCode) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("apply_ai_fix_button")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Zastosuj w edytorze", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }

        // --- 4. Main Tool Content ---
        when (selectedAiTool) {
            0 -> {
                // Chat Mode
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Current Context Card (matching HTML design card)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, VibrantBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = VibrantBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "CURRENT PROJECT CONTEXT",
                                        color = VibrantTextSubtle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = activeFile?.fileName ?: "NetBeans Project",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "NetBeans Studio environment active. Asystent analizuje kod otwartego pliku w czasie rzeczywistym.",
                                    fontSize = 13.sp,
                                    color = VibrantTextMuted,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Quick suggestions pill buttons
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val pills = listOf(
                                Triple("Jak zoptymalizować kod?", VibrantBlueContainer, VibrantBlueDark),
                                Triple("Dodaj obsługę wyjątków", VibrantPurpleContainer, VibrantPurpleDark),
                                Triple("Wyjaśnij architekturę", Color(0xFFF1F5F9), VibrantTextSecondary),
                                Triple("Napisz test jednostkowy", Color(0xFFE0F2FE), Color(0xFF0369A1))
                            )
                            pills.forEach { (prompt, bg, fg) ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = bg,
                                    border = BorderStroke(1.dp, fg.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.sendAiChat(prompt)
                                        }
                                        .testTag("chip_$prompt")
                                ) {
                                    Text(
                                        text = prompt,
                                        color = fg,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (aiMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(VibrantBlueContainer)
                                    ) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = VibrantBlue,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Zadaj pytanie asystentowi Gemini o swój kod",
                                        color = VibrantTextSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Wpisz zapytanie poniżej lub wybierz jedną z szybkich sugestii",
                                        color = VibrantTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    items(aiMessages) { msg ->
                        val isUser = msg.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (isUser) {
                                // User message (matching HTML self-end max-w-[85%] bg-blue-600 text-white p-4 rounded-2xl rounded-tr-none shadow-md)
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = VibrantBlue),
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 4.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = 20.dp
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = msg.content,
                                            color = Color.White,
                                            fontSize = 13.5.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            } else {
                                // Assistant message (matching HTML self-start max-w-[85%] bg-white p-4 rounded-2xl rounded-tl-none shadow-sm border border-slate-100)
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(
                                        topStart = 4.dp,
                                        topEnd = 20.dp,
                                        bottomStart = 20.dp,
                                        bottomEnd = 20.dp
                                    ),
                                    border = BorderStroke(1.dp, VibrantBorderLight),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        // Vibrant 3-dot animation header (Blue, Purple, Pink)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(VibrantBlueLight)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(VibrantPurple)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(VibrantPink)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Gemini AI",
                                                color = VibrantPurple,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Text(
                                            text = msg.content,
                                            color = VibrantTextSecondary,
                                            fontSize = 13.5.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Input Bar (matching HTML rounded-full py-4 pl-6 pr-14 shadow-xl and button)
                Surface(
                    color = VibrantSurface,
                    border = BorderStroke(1.dp, VibrantBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Zapytaj Gemini o kod...", fontSize = 13.5.sp, color = VibrantTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = VibrantBorder,
                                focusedTextColor = VibrantTextPrimary,
                                unfocusedTextColor = VibrantTextPrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(26.dp),
                            maxLines = 3,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gemini_chat_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(VibrantBlue)
                                .clickable {
                                    if (chatInput.isNotBlank()) {
                                        val text = chatInput
                                        chatInput = ""
                                        viewModel.sendAiChat(text)
                                    }
                                }
                                .testTag("gemini_send_button")
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Wyślij",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            1 -> {
                // Generator Mode
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, VibrantBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Generator Kodu Gemini AI",
                                color = VibrantTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "Opisz po polsku funkcję, klasę lub algorytm, który chcesz utworzyć.",
                                color = VibrantTextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = promptInput,
                                onValueChange = { promptInput = it },
                                placeholder = {
                                    Text(
                                        "Np.: Stwórz klasę w Javie obsługującą pobieranie pogody z API i deserializację JSON...",
                                        fontSize = 13.sp,
                                        color = VibrantTextMuted
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VibrantPurple,
                                    unfocusedBorderColor = VibrantBorder,
                                    focusedTextColor = VibrantTextPrimary,
                                    unfocusedTextColor = VibrantTextPrimary,
                                    focusedContainerColor = VibrantBg,
                                    unfocusedContainerColor = VibrantBg
                                ),
                                shape = RoundedCornerShape(16.dp),
                                minLines = 4,
                                maxLines = 8,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generator_prompt_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (promptInput.isNotBlank()) {
                                        viewModel.generateCode(promptInput)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_code_submit_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Wygeneruj kod z Gemini AI", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Popularne szablony promptów:",
                        color = VibrantTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        "Kalkulator z obsługą działań matematycznych i historii",
                        "Algorytm szybkiego sortowania (Quicksort) z pomiarem czasu",
                        "Singleton i wzorce projektowe w Kotlinie",
                        "Walidator formularza (email, telefon, hasło) z regexem"
                    ).forEach { template ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, VibrantBorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(template, color = VibrantTextSecondary, fontSize = 12.5.sp, modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        promptInput = template
                                        viewModel.generateCode(template)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlueContainer, contentColor = VibrantBlueDark),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Użyj", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Debugger & Explanation Mode
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, VibrantBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Inspekcja & Analiza Kodu",
                                color = VibrantTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "Gemini bada kod otwartego pliku pod kątem poprawności, bezpieczeństwa i wydajności.",
                                color = VibrantTextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.debugAndFixCode() },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Skanuj błędy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.explainCode() },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Wyjaśnij kod", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
