package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkEditorBackground
import com.example.ui.theme.DarkEditorSurface
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBlueDark
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantGreen
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.utils.CodeSyntaxHighlighter
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun CodeEditorView(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier,
    onNewFileClick: () -> Unit = {}
) {
    val files by viewModel.projectFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val currentCode by viewModel.currentCode.collectAsState()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val lineCount = remember(currentCode) {
        if (currentCode.isEmpty()) 1 else currentCode.lines().size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // --- 1. File Tabs Bar (Vibrant White Surface with Blue Indicator) ---
        if (files.isNotEmpty()) {
            val selectedIndex = files.indexOfFirst { it.id == activeFile?.id }.coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 12.dp,
                containerColor = VibrantSurface,
                contentColor = VibrantBlue,
                indicator = { tabPositions ->
                    if (selectedIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            color = VibrantBlue,
                            height = 3.dp
                        )
                    }
                },
                divider = {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(VibrantBorderLight))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                files.forEach { file ->
                    val isSelected = file.id == activeFile?.id
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectFile(file) },
                        modifier = Modifier
                            .testTag("file_tab_${file.fileName}")
                            .height(44.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = if (isSelected) VibrantBlue else VibrantTextSubtle,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = file.fileName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) VibrantBlue else VibrantTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Action Toolbar (Vibrant Pill Buttons & Crisp Action Icons) ---
        Surface(
            color = VibrantSurface,
            border = BorderStroke(1.dp, VibrantBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.runCode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("run_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Uruchom kod",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Uruchom", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.debugAndFixCode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("gemini_fix_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "AI Fix", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.formatCode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .testTag("format_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatAlignLeft,
                            contentDescription = "Formatuj kod",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.saveCurrentCode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .testTag("save_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Zapisz",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.shareCurrentCode() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .testTag("share_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Udostępnij kod",
                            tint = VibrantTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        // --- 3. Main Editor Surface (Wrapped in Vibrant Rounded Card) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkEditorBackground),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, VibrantBorderLight),
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                ) {
                    // Line numbers gutter
                    Column(
                        modifier = Modifier
                            .background(DarkEditorSurface)
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lineCount) {
                            Text(
                                text = "$i",
                                color = SyntaxComment,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Scrollable code area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(horizontalScrollState)
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                    ) {
                        BasicTextField(
                            value = currentCode,
                            onValueChange = { viewModel.onCodeChange(it) },
                            textStyle = TextStyle(
                                color = Color(0xFFF8FAFC),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            cursorBrush = SolidColor(VibrantBlueLight),
                            visualTransformation = { text ->
                                val ext = activeFile?.fileExtension ?: "java"
                                val highlighted = CodeSyntaxHighlighter.highlightCode(text.text, ext)
                                androidx.compose.ui.text.input.TransformedText(
                                    highlighted,
                                    androidx.compose.ui.text.input.OffsetMapping.Identity
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("code_editor_text_field")
                        )
                    }
                }
            }
        }

        // --- 4. Developer Accessory Keyboard Bar (Vibrant White Pill Keys) ---
        DeveloperKeyBar(
            onInsert = { snippet -> viewModel.insertSnippet(snippet) },
            modifier = Modifier.fillMaxWidth()
        )

        // --- 5. Status Bar ---
        Surface(
            color = VibrantSurface,
            border = BorderStroke(1.dp, VibrantBorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${activeFile?.fileName ?: "Brak pliku"} [${activeFile?.fileExtension?.uppercase() ?: "TXT"}]",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = VibrantTextMuted
                )
                Text(
                    text = "Linie: $lineCount • Znaków: ${currentCode.length} • UTF-8",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = VibrantTextMuted
                )
            }
        }
    }
}

@Composable
fun DeveloperKeyBar(
    onInsert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val symbols = listOf(
        "{", "}", "(", ")", "[", "]", ";", "=", "\"", "'",
        "<", ">", ":", "->", ".", "//", "    ", "fun", "class", "public", "void"
    )

    val scrollState = rememberScrollState()

    Surface(
        color = VibrantSurface,
        border = BorderStroke(1.dp, VibrantBorderLight),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            symbols.forEach { symbol ->
                val label = if (symbol == "    ") "Tab" else symbol
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VibrantBg,
                    border = BorderStroke(1.dp, VibrantBorder),
                    modifier = Modifier
                        .clickable { onInsert(symbol) }
                        .testTag("key_sym_$label")
                ) {
                    Text(
                        text = label,
                        color = VibrantBlueDark,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
