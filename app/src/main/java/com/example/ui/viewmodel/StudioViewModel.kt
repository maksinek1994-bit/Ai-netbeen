package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.AiMessageEntity
import com.example.data.model.CodeFileEntity
import com.example.data.model.ProjectEntity
import com.example.data.repository.CodeFixResult
import com.example.data.repository.ExecutionResult
import com.example.data.repository.GeminiRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class StudioTab {
    EDITOR,
    CONSOLE,
    AI_ASSISTANT,
    PROJECTS,
    APK_GOOGLE
}

sealed interface AiTaskStatus {
    object Idle : AiTaskStatus
    data class Working(val message: String) : AiTaskStatus
    data class Success(val message: String, val fixResult: CodeFixResult? = null) : AiTaskStatus
    data class Error(val error: String) : AiTaskStatus
}

sealed interface ExecutionStatus {
    object Idle : ExecutionStatus
    object Running : ExecutionStatus
    data class Finished(val result: ExecutionResult) : ExecutionStatus
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val projectRepository = ProjectRepository(db)
    val geminiRepository = GeminiRepository(application)

    val allProjects: StateFlow<List<ProjectEntity>> = projectRepository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProjectId = MutableStateFlow<Long?>(null)
    val activeProjectId: StateFlow<Long?> = _activeProjectId.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<CodeFileEntity>>(emptyList())
    val projectFiles: StateFlow<List<CodeFileEntity>> = _projectFiles.asStateFlow()

    private val _activeFile = MutableStateFlow<CodeFileEntity?>(null)
    val activeFile: StateFlow<CodeFileEntity?> = _activeFile.asStateFlow()

    private val _currentCode = MutableStateFlow("")
    val currentCode: StateFlow<String> = _currentCode.asStateFlow()

    private val _selectedTab = MutableStateFlow(StudioTab.EDITOR)
    val selectedTab: StateFlow<StudioTab> = _selectedTab.asStateFlow()

    private val _aiStatus = MutableStateFlow<AiTaskStatus>(AiTaskStatus.Idle)
    val aiStatus: StateFlow<AiTaskStatus> = _aiStatus.asStateFlow()

    private val _executionStatus = MutableStateFlow<ExecutionStatus>(ExecutionStatus.Idle)
    val executionStatus: StateFlow<ExecutionStatus> = _executionStatus.asStateFlow()

    private val _consoleLogs = MutableStateFlow<String>("")
    val consoleLogs: StateFlow<String> = _consoleLogs.asStateFlow()

    private val _aiMessages = MutableStateFlow<List<AiMessageEntity>>(emptyList())
    val aiMessages: StateFlow<List<AiMessageEntity>> = _aiMessages.asStateFlow()

    private val _customApiKeyInput = MutableStateFlow(geminiRepository.getEffectiveApiKey())
    val customApiKeyInput: StateFlow<String> = _customApiKeyInput.asStateFlow()

    private var filesObserverJob: Job? = null
    private var aiMessagesObserverJob: Job? = null

    init {
        viewModelScope.launch {
            projectRepository.seedSampleProjectsIfEmpty()
            // Observe projects and set initial active project
            allProjects.collect { projects ->
                if (_activeProjectId.value == null && projects.isNotEmpty()) {
                    selectProject(projects.first().id)
                }
            }
        }
    }

    fun selectTab(tab: StudioTab) {
        _selectedTab.value = tab
    }

    fun selectProject(projectId: Long) {
        _activeProjectId.value = projectId
        filesObserverJob?.cancel()
        filesObserverJob = viewModelScope.launch {
            projectRepository.getFilesForProject(projectId).collect { files ->
                _projectFiles.value = files
                val current = _activeFile.value
                if (current == null || current.projectId != projectId || files.none { it.id == current.id }) {
                    val mainOrFirst = files.firstOrNull { it.isMain } ?: files.firstOrNull()
                    if (mainOrFirst != null) {
                        selectFile(mainOrFirst)
                    }
                }
            }
        }

        aiMessagesObserverJob?.cancel()
        aiMessagesObserverJob = viewModelScope.launch {
            projectRepository.getAiMessages(projectId).collect { msgs ->
                _aiMessages.value = msgs
            }
        }
    }

    fun selectFile(file: CodeFileEntity) {
        // Save current code if modified
        saveCurrentCode()
        _activeFile.value = file
        _currentCode.value = file.content
    }

    fun onCodeChange(newCode: String) {
        _currentCode.value = newCode
    }

    fun saveCurrentCode() {
        val file = _activeFile.value ?: return
        val code = _currentCode.value
        if (code != file.content) {
            viewModelScope.launch {
                projectRepository.updateFileContent(file.id, code)
                _activeFile.value = file.copy(content = code, updatedAt = System.currentTimeMillis())
            }
        }
    }

    fun createProject(name: String, desc: String, category: String) {
        viewModelScope.launch {
            val newId = projectRepository.createProject(name, desc, category)
            selectProject(newId)
            _selectedTab.value = StudioTab.EDITOR
        }
    }

    fun createNewFile(name: String, ext: String) {
        val pId = _activeProjectId.value ?: return
        viewModelScope.launch {
            val newFileId = projectRepository.addFile(pId, name, ext)
            val newFile = projectRepository.getFileByIdOnce(newFileId)
            if (newFile != null) {
                selectFile(newFile)
            }
        }
    }

    fun deleteFile(fileId: Long) {
        viewModelScope.launch {
            projectRepository.deleteFile(fileId)
        }
    }

    fun deleteCurrentProject() {
        val pId = _activeProjectId.value ?: return
        val proj = allProjects.value.firstOrNull { it.id == pId } ?: return
        viewModelScope.launch {
            projectRepository.deleteProject(proj)
            val remaining = allProjects.value.filter { it.id != pId }
            if (remaining.isNotEmpty()) {
                selectProject(remaining.first().id)
            } else {
                _activeProjectId.value = null
                _projectFiles.value = emptyList()
                _activeFile.value = null
                _currentCode.value = ""
            }
        }
    }

    fun insertSnippet(snippet: String) {
        _currentCode.value += snippet
        saveCurrentCode()
    }

    fun formatCode() {
        // Basic code indentation formatting for Java/Kotlin
        val lines = _currentCode.value.lines()
        val formatted = StringBuilder()
        var indentLevel = 0
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("}") || trimmed.startsWith(")")) {
                indentLevel = (indentLevel - 1).coerceAtLeast(0)
            }
            formatted.append("    ".repeat(indentLevel)).append(trimmed).append("\n")
            if (trimmed.endsWith("{") || trimmed.endsWith("(")) {
                indentLevel++
            }
        }
        _currentCode.value = formatted.toString().trimEnd()
        saveCurrentCode()
    }

    fun runCode() {
        saveCurrentCode()
        val file = _activeFile.value ?: return
        val code = _currentCode.value

        _selectedTab.value = StudioTab.CONSOLE
        _executionStatus.value = ExecutionStatus.Running

        viewModelScope.launch {
            if (file.fileExtension.equals("html", ignoreCase = true)) {
                // HTML is handled live in WebView
                _executionStatus.value = ExecutionStatus.Finished(
                    ExecutionResult(
                        output = "[Podgląd Live HTML5 / JS załadowany pomyślnie w WebView]",
                        exitCode = 0,
                        executionTimeMs = 42,
                        isSimulated = false
                    )
                )
                return@launch
            }

            // Run using Gemini AI simulation compiler or local evaluation
            val result = geminiRepository.simulateExecutionWithGemini(code, file.fileExtension)
            result.onSuccess { execRes ->
                _executionStatus.value = ExecutionStatus.Finished(execRes)
                _consoleLogs.value = execRes.output
            }.onFailure { err ->
                _executionStatus.value = ExecutionStatus.Finished(
                    ExecutionResult(
                        output = "Błąd wykonania / brak klucza Gemini AI:\n${err.message}\n\nPodpowiedź: Upewnij się, że skonfigurowałeś klucz Gemini API w zakładce Narzędzia Google & APK.",
                        exitCode = 1,
                        executionTimeMs = 0,
                        isSimulated = true
                    )
                )
                _consoleLogs.value = err.message ?: "Błąd wykonania"
            }
        }
    }

    fun debugAndFixCode(userIssue: String = "") {
        saveCurrentCode()
        val file = _activeFile.value ?: return
        val code = _currentCode.value

        _selectedTab.value = StudioTab.AI_ASSISTANT
        _aiStatus.value = AiTaskStatus.Working("Gemini AI analizuje kod i szuka błędów...")

        viewModelScope.launch {
            val result = geminiRepository.debugAndFixCode(code, file.fileExtension, userIssue)
            result.onSuccess { fixResult ->
                _aiStatus.value = AiTaskStatus.Success(
                    message = fixResult.explanation,
                    fixResult = fixResult
                )
                val pId = _activeProjectId.value ?: 0
                projectRepository.saveAiMessage(pId, "user", "Zbadaj i napraw kod w pliku ${file.fileName}", "debug")
                projectRepository.saveAiMessage(pId, "model", fixResult.explanation, "debug")
            }.onFailure { err ->
                _aiStatus.value = AiTaskStatus.Error(err.message ?: "Nieznany błąd analizy")
            }
        }
    }

    fun generateCode(prompt: String) {
        val file = _activeFile.value
        val lang = file?.fileExtension ?: "java"

        _selectedTab.value = StudioTab.AI_ASSISTANT
        _aiStatus.value = AiTaskStatus.Working("Gemini AI generuje kod na podstawie opisu...")

        viewModelScope.launch {
            val result = geminiRepository.generateCode(prompt, lang, _currentCode.value)
            result.onSuccess { generatedText ->
                val codeRegex = Regex("```(?:[a-zA-Z0-9_-]+)?\\s*([\\s\\S]*?)```")
                val match = codeRegex.find(generatedText)
                val extractedCode = match?.groups?.get(1)?.value?.trim()

                _aiStatus.value = AiTaskStatus.Success(
                    message = generatedText,
                    fixResult = CodeFixResult(explanation = generatedText, fixedCode = extractedCode)
                )
                val pId = _activeProjectId.value ?: 0
                projectRepository.saveAiMessage(pId, "user", prompt, "generate")
                projectRepository.saveAiMessage(pId, "model", generatedText, "generate")
            }.onFailure { err ->
                _aiStatus.value = AiTaskStatus.Error(err.message ?: "Błąd generowania kodu")
            }
        }
    }

    fun explainCode() {
        val file = _activeFile.value ?: return
        val code = _currentCode.value

        _selectedTab.value = StudioTab.AI_ASSISTANT
        _aiStatus.value = AiTaskStatus.Working("Gemini AI przygotowuje szczegółowe wyjaśnienie...")

        viewModelScope.launch {
            val result = geminiRepository.explainCode(code, file.fileExtension)
            result.onSuccess { explanation ->
                _aiStatus.value = AiTaskStatus.Success(message = explanation)
                val pId = _activeProjectId.value ?: 0
                projectRepository.saveAiMessage(pId, "user", "Wyjaśnij kod ${file.fileName}", "explain")
                projectRepository.saveAiMessage(pId, "model", explanation, "explain")
            }.onFailure { err ->
                _aiStatus.value = AiTaskStatus.Error(err.message ?: "Błąd wyjaśniania kodu")
            }
        }
    }

    fun sendAiChat(message: String) {
        if (message.isBlank()) return
        val file = _activeFile.value
        val fileName = file?.fileName ?: "brak_pliku"
        val lang = file?.fileExtension ?: "java"
        val code = _currentCode.value
        val pId = _activeProjectId.value ?: 0

        val currentMessages = _aiMessages.value.map { it.role to it.content }

        viewModelScope.launch {
            projectRepository.saveAiMessage(pId, "user", message, "chat")
            _aiStatus.value = AiTaskStatus.Working("Gemini AI myśli...")

            val result = geminiRepository.chatWithCodeContext(
                history = currentMessages,
                currentCode = code,
                fileName = fileName,
                language = lang,
                userMessage = message
            )

            result.onSuccess { reply ->
                _aiStatus.value = AiTaskStatus.Idle
                projectRepository.saveAiMessage(pId, "model", reply, "chat")
            }.onFailure { err ->
                _aiStatus.value = AiTaskStatus.Error(err.message ?: "Błąd czatu")
            }
        }
    }

    fun applyFixedCode(fixedCode: String) {
        _currentCode.value = fixedCode
        saveCurrentCode()
        _selectedTab.value = StudioTab.EDITOR
    }

    fun saveCustomApiKey(key: String) {
        geminiRepository.saveCustomApiKey(key)
        _customApiKeyInput.value = key
    }

    fun shareCurrentCode() {
        val file = _activeFile.value ?: return
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, _currentCode.value)
            putExtra(Intent.EXTRA_SUBJECT, "NetBeans Studio: ${file.fileName}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Udostępnij kod").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(shareIntent)
    }
}
