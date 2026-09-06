package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBlueDark
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsExplorerView(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val projectFiles by viewModel.projectFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()

    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showNewFileDialog by remember { mutableStateOf(false) }

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
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Projekty",
                            tint = VibrantBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Eksplorator Projektów",
                            color = VibrantTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            text = "Lokalna przestrzeń robocza IDE",
                            color = VibrantTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { showNewProjectDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("create_new_project_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nowy projekt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- 2. Project List Cards ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(projects) { project ->
                val isActive = project.id == activeProjectId

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = if (isActive) BorderStroke(1.5.dp, VibrantBlue)
                    else BorderStroke(1.dp, VibrantBorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 2.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectProject(project.id) }
                        .testTag("project_card_${project.name}")
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
                                        .background(if (isActive) VibrantBlueContainer else VibrantBg)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (isActive) VibrantBlueDark else VibrantTextSubtle,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = project.name,
                                        color = VibrantTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Kategoria: ${project.category}",
                                        color = VibrantTextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = VibrantBlueContainer
                            ) {
                                Text(
                                    text = project.category.uppercase(),
                                    color = VibrantBlueDark,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = project.description,
                            color = VibrantTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        // If active, show file listing under it
                        if (isActive) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pliki w projekcie (${projectFiles.size}):",
                                    color = VibrantTextPrimary,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { showNewFileDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlueContainer, contentColor = VibrantBlueDark),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .testTag("add_file_to_project_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Plik", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                projectFiles.forEach { file ->
                                    val isFileActive = file.id == activeFile?.id
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isFileActive) VibrantBlueContainer else VibrantBg,
                                        border = BorderStroke(1.dp, if (isFileActive) VibrantBlue.copy(alpha = 0.3f) else VibrantBorderLight),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectFile(file)
                                                viewModel.selectTab(StudioTab.EDITOR)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Description,
                                                    contentDescription = null,
                                                    tint = if (isFileActive) VibrantBlueDark else VibrantTextSubtle,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = file.fileName,
                                                    color = if (isFileActive) VibrantBlueDark else VibrantTextPrimary,
                                                    fontWeight = if (isFileActive) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 13.sp
                                                )
                                                if (file.isMain) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "[main]",
                                                        color = VibrantBlue,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }

                                            if (projectFiles.size > 1) {
                                                IconButton(
                                                    onClick = { viewModel.deleteFile(file.id) },
                                                    modifier = Modifier.size(26.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Usuń plik",
                                                        tint = VibrantTextSubtle,
                                                        modifier = Modifier.size(15.dp)
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
            }
        }
    }

    // --- Create New Project Dialog ---
    if (showNewProjectDialog) {
        var newProjName by remember { mutableStateOf("") }
        var newProjDesc by remember { mutableStateOf("") }
        var newProjCat by remember { mutableStateOf("Java") }
        var expandedCat by remember { mutableStateOf(false) }
        val categories = listOf("Java", "Kotlin", "Web", "Python", "Android")

        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Utwórz nowy projekt NetBeans", color = VibrantTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newProjName,
                        onValueChange = { newProjName = it },
                        label = { Text("Nazwa projektu (np. MyCalc)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VibrantTextPrimary,
                            unfocusedTextColor = VibrantTextPrimary,
                            focusedBorderColor = VibrantBlue,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_project_name_input")
                    )

                    OutlinedTextField(
                        value = newProjDesc,
                        onValueChange = { newProjDesc = it },
                        label = { Text("Opis projektu") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VibrantTextPrimary,
                            unfocusedTextColor = VibrantTextPrimary,
                            focusedBorderColor = VibrantBlue,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_project_desc_input")
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat }
                    ) {
                        OutlinedTextField(
                            value = newProjCat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategoria / Język") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VibrantTextPrimary,
                                unfocusedTextColor = VibrantTextPrimary,
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = VibrantBorder
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = VibrantTextPrimary) },
                                    onClick = {
                                        newProjCat = cat
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProjName.isNotBlank()) {
                            viewModel.createProject(newProjName, newProjDesc, newProjCat)
                            showNewProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("dialog_confirm_create_project")
                ) {
                    Text("Utwórz", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Anuluj", color = VibrantTextMuted)
                }
            }
        )
    }

    // --- Create New File Dialog ---
    if (showNewFileDialog) {
        var newFileName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Nowy plik źródłowy", color = VibrantTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Podaj nazwę pliku wraz z rozszerzeniem, np. Service.java, Helper.kt, test.py",
                        color = VibrantTextMuted,
                        fontSize = 12.5.sp
                    )
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("np. DatabaseHelper.java") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VibrantTextPrimary,
                            unfocusedTextColor = VibrantTextPrimary,
                            focusedBorderColor = VibrantBlue,
                            unfocusedBorderColor = VibrantBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_file_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            val ext = newFileName.substringAfterLast('.', "txt")
                            viewModel.createNewFile(newFileName, ext)
                            showNewFileDialog = false
                            viewModel.selectTab(StudioTab.EDITOR)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("dialog_confirm_create_file")
                ) {
                    Text("Dodaj plik", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Anuluj", color = VibrantTextMuted)
                }
            }
        )
    }
}
