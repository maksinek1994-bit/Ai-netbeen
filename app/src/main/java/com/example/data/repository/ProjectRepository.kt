package com.example.data.repository

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.model.AiMessageEntity
import com.example.data.model.CodeFileEntity
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProjectRepository(private val database: AppDatabase) {

    val allProjects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()

    fun getFilesForProject(projectId: Long): Flow<List<CodeFileEntity>> {
        return database.codeFileDao().getFilesForProject(projectId)
    }

    fun getFileById(fileId: Long): Flow<CodeFileEntity?> {
        return database.codeFileDao().getFileById(fileId)
    }

    suspend fun getFileByIdOnce(fileId: Long): CodeFileEntity? = withContext(Dispatchers.IO) {
        database.codeFileDao().getFileByIdOnce(fileId)
    }

    fun getAiMessages(projectId: Long): Flow<List<AiMessageEntity>> {
        return database.aiMessageDao().getMessagesForProject(projectId)
    }

    suspend fun saveAiMessage(projectId: Long, role: String, content: String, mode: String): Long =
        withContext(Dispatchers.IO) {
            database.aiMessageDao().insertMessage(
                AiMessageEntity(
                    projectId = projectId,
                    role = role,
                    content = content,
                    mode = mode
                )
            )
        }

    suspend fun clearAiMessages(projectId: Long) = withContext(Dispatchers.IO) {
        database.aiMessageDao().clearMessagesForProject(projectId)
    }

    suspend fun updateFileContent(fileId: Long, newContent: String) = withContext(Dispatchers.IO) {
        val existing = database.codeFileDao().getFileByIdOnce(fileId)
        if (existing != null) {
            database.codeFileDao().updateFile(
                existing.copy(content = newContent, updatedAt = System.currentTimeMillis())
            )
            val project = database.projectDao().getProjectByIdOnce(existing.projectId)
            if (project != null) {
                database.projectDao().updateProject(
                    project.copy(updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    suspend fun createProject(name: String, description: String, category: String): Long =
        withContext(Dispatchers.IO) {
            val projectId = database.projectDao().insertProject(
                ProjectEntity(
                    name = name,
                    description = description,
                    category = category
                )
            )
            // Create default file based on category
            val defaultFile = when (category.lowercase()) {
                "java" -> CodeFileEntity(
                    projectId = projectId,
                    fileName = "Main.java",
                    fileExtension = "java",
                    content = """
                        // NetBeans Java Project: $name
                        package com.example;

                        public class Main {
                            public static void main(String[] args) {
                                System.out.println("Hello from NetBeans Studio!");
                                System.out.println("Project: " + "$name");
                            }
                        }
                    """.trimIndent(),
                    isMain = true
                )
                "kotlin", "android" -> CodeFileEntity(
                    projectId = projectId,
                    fileName = "Main.kt",
                    fileExtension = "kt",
                    content = """
                        // NetBeans Kotlin Project: $name
                        package com.example

                        fun main() {
                            println("Hello Kotlin from NetBeans Studio!")
                            val welcome = "AI Powered Mobile IDE"
                            println(welcome)
                        }
                    """.trimIndent(),
                    isMain = true
                )
                "web", "html" -> CodeFileEntity(
                    projectId = projectId,
                    fileName = "index.html",
                    fileExtension = "html",
                    content = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <title>$name</title>
                            <style>
                                body { font-family: sans-serif; background: #0f172a; color: #f8fafc; padding: 24px; text-align: center; }
                                h1 { color: #38bdf8; }
                                button { background: #1b6ac6; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-size: 16px; cursor: pointer; }
                            </style>
                        </head>
                        <body>
                            <h1>$name</h1>
                            <p>Uruchomiono w podglądzie NetBeans Studio!</p>
                            <button onclick="alert('Działa!')">Kliknij mnie</button>
                        </body>
                        </html>
                    """.trimIndent(),
                    isMain = true
                )
                "python" -> CodeFileEntity(
                    projectId = projectId,
                    fileName = "main.py",
                    fileExtension = "py",
                    content = """
                        # NetBeans Python Script: $name
                        def main():
                            print("Hello from NetBeans Studio Python Engine!")
                            items = [1, 2, 3, 4, 5]
                            print(f"Kwadraty liczb: {[x**2 for x in items]}")

                        if __name__ == "__main__":
                            main()
                    """.trimIndent(),
                    isMain = true
                )
                else -> CodeFileEntity(
                    projectId = projectId,
                    fileName = "code.txt",
                    fileExtension = "txt",
                    content = "// NetBeans Code File\n",
                    isMain = true
                )
            }
            database.codeFileDao().insertFile(defaultFile)
            projectId
        }

    suspend fun addFile(projectId: Long, fileName: String, extension: String, content: String = ""): Long =
        withContext(Dispatchers.IO) {
            database.codeFileDao().insertFile(
                CodeFileEntity(
                    projectId = projectId,
                    fileName = fileName,
                    fileExtension = extension,
                    content = content.ifEmpty { "// Plik: $fileName\n" }
                )
            )
        }

    suspend fun deleteFile(fileId: Long) = withContext(Dispatchers.IO) {
        database.codeFileDao().deleteFileById(fileId)
    }

    suspend fun deleteProject(project: ProjectEntity) = withContext(Dispatchers.IO) {
        database.projectDao().deleteProject(project)
    }

    suspend fun seedSampleProjectsIfEmpty() = withContext(Dispatchers.IO) {
        val count = database.projectDao().getProjectCount()
        if (count == 0) {
            // Project 1: NetBeans Java Demo
            val p1Id = database.projectDao().insertProject(
                ProjectEntity(
                    name = "NetBeans Java Demo",
                    description = "Klasyczny projekt Java z klasami, algorytmem obliczeniowym i logiką biznesową",
                    category = "Java"
                )
            )
            database.codeFileDao().insertFiles(
                listOf(
                    CodeFileEntity(
                        projectId = p1Id,
                        fileName = "Main.java",
                        fileExtension = "java",
                        content = """
                            package com.example.netbeans;

                            /**
                             * NetBeans Studio Mobile IDE
                             * Autor: Gemini AI Assistant & NetBeans Mobile
                             */
                            public class Main {
                                public static void main(String[] args) {
                                    System.out.println("========================================");
                                    System.out.println("  Witaj w NetBeans Studio na Androidzie! ");
                                    System.out.println("  Zintegrowane z Google & Gemini AI     ");
                                    System.out.println("========================================");

                                    MathCalculator calc = new MathCalculator();
                                    int n = 10;
                                    System.out.println("Ciąg Fibonacciego do " + n + ":");
                                    for (int i = 0; i < n; i++) {
                                        System.out.print(calc.fibonacci(i) + " ");
                                    }
                                    System.out.println();

                                    double poleKola = calc.obliczPoleKola(7.5);
                                    System.out.printf("Pole koła o r=7.5: %.2f\n", poleKola);
                                    System.out.println("Kompilacja i wykonanie zakończone sukcesem.");
                                }
                            }
                        """.trimIndent(),
                        isMain = true
                    ),
                    CodeFileEntity(
                        projectId = p1Id,
                        fileName = "MathCalculator.java",
                        fileExtension = "java",
                        content = """
                            package com.example.netbeans;

                            public class MathCalculator {
                                public long fibonacci(int n) {
                                    if (n <= 0) return 0;
                                    if (n == 1) return 1;
                                    long a = 0, b = 1;
                                    for (int i = 2; i <= n; i++) {
                                        long temp = a + b;
                                        a = b;
                                        b = temp;
                                    }
                                    return b;
                                }

                                public double obliczPoleKola(double promien) {
                                    return Math.PI * promien * promien;
                                }

                                public boolean czyLiczbaPierwsza(int x) {
                                    if (x < 2) return false;
                                    for (int i = 2; i * i <= x; i++) {
                                        if (x % i == 0) return false;
                                    }
                                    return true;
                                }
                            }
                        """.trimIndent()
                    ),
                    CodeFileEntity(
                        projectId = p1Id,
                        fileName = "README.md",
                        fileExtension = "md",
                        content = """
                            # NetBeans Studio Java Demo
                            Projekt demonstracyjny środowiska NetBeans Studio na telefonie z Androidem.

                            ## Funkcje:
                            - Pełny edytor z kolorowaniem składni
                            - Asystent Gemini AI do debugowania i generowania kodu
                            - Integracja z dokumentacją Google
                            - Eksport aplikacji jako samodzielnego pliku APK na telefon!
                        """.trimIndent()
                    )
                )
            )

            // Project 2: Kotlin & Android Quickstart
            val p2Id = database.projectDao().insertProject(
                ProjectEntity(
                    name = "Kotlin Android Studio",
                    description = "Nowoczesny kod w języku Kotlin z korutynami, klasami danych i lambdami",
                    category = "Kotlin"
                )
            )
            database.codeFileDao().insertFiles(
                listOf(
                    CodeFileEntity(
                        projectId = p2Id,
                        fileName = "Main.kt",
                        fileExtension = "kt",
                        content = """
                            package com.example.kotlin

                            data class Developer(val name: String, val favoriteLang: String, val experienceYears: Int)

                            fun main() {
                                println("🚀 Start aplikacji Kotlin NetBeans")
                                val devs = listOf(
                                    Developer("Maks", "Kotlin/Java", 5),
                                    Developer("Gemini AI", "All", 99),
                                    Developer("Android Dev", "Kotlin Compose", 3)
                                )

                                println("\n--- Programiści w projekcie ---")
                                devs.forEach { dev ->
                                    println("• ${'$'}{dev.name} - Ulubiony: ${'$'}{dev.favoriteLang} (${'$'}{dev.experienceYears} lat)")
                                }

                                val expert = devs.maxByOrNull { it.experienceYears }
                                println("\nEkspert zespołu: ${'$'}{expert?.name}")
                            }
                        """.trimIndent(),
                        isMain = true
                    ),
                    CodeFileEntity(
                        projectId = p2Id,
                        fileName = "AppModel.kt",
                        fileExtension = "kt",
                        content = """
                            package com.example.kotlin

                            sealed interface AppState {
                                object Idle : AppState
                                data class Loading(val message: String) : AppState
                                data class Success(val data: List<String>) : AppState
                                data class Error(val exception: Throwable) : AppState
                            }
                        """.trimIndent()
                    )
                )
            )

            // Project 3: Web HTML5 / JS Live Studio
            val p3Id = database.projectDao().insertProject(
                ProjectEntity(
                    name = "Web HTML5 & JS App",
                    description = "Interaktywna aplikacja webowa z natychmiastowym podglądem w WebView",
                    category = "Web"
                )
            )
            database.codeFileDao().insertFiles(
                listOf(
                    CodeFileEntity(
                        projectId = p3Id,
                        fileName = "index.html",
                        fileExtension = "html",
                        content = """
                            <!DOCTYPE html>
                            <html lang="pl">
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>NetBeans Web App</title>
                                <style>
                                    body {
                                        background: #0f172a;
                                        color: #f8fafc;
                                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                        display: flex;
                                        flex-direction: column;
                                        align-items: center;
                                        justify-content: center;
                                        min-height: 100vh;
                                        margin: 0;
                                        padding: 16px;
                                        box-sizing: border-box;
                                    }
                                    .card {
                                        background: #1e293b;
                                        border: 1px solid #334155;
                                        border-radius: 16px;
                                        padding: 24px;
                                        max-width: 400px;
                                        width: 100%;
                                        box-shadow: 0 10px 25px rgba(0,0,0,0.5);
                                        text-align: center;
                                    }
                                    h1 { color: #38bdf8; margin-top: 0; font-size: 22px; }
                                    .counter-box { font-size: 48px; font-weight: bold; color: #a78bfa; margin: 20px 0; }
                                    .btn-group { display: flex; gap: 10px; justify-content: center; }
                                    button {
                                        background: #1b6ac6;
                                        color: white;
                                        border: none;
                                        padding: 12px 20px;
                                        border-radius: 10px;
                                        font-size: 16px;
                                        font-weight: 600;
                                        cursor: pointer;
                                    }
                                    button:active { opacity: 0.8; transform: scale(0.98); }
                                    .badge {
                                        background: #334155;
                                        color: #94a3b8;
                                        display: inline-block;
                                        padding: 4px 10px;
                                        border-radius: 20px;
                                        font-size: 12px;
                                        margin-top: 15px;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="card">
                                    <h1>⚡ NetBeans Web Runner</h1>
                                    <p>Aplikacja HTML5 / JavaScript działająca na żywo w edytorze.</p>
                                    <div class="counter-box" id="count">0</div>
                                    <div class="btn-group">
                                        <button onclick="decrement()">- Odejmij</button>
                                        <button onclick="increment()">+ Dodaj</button>
                                    </div>
                                    <br>
                                    <button style="background: #10b981;" onclick="askGemini()">✨ Zapytaj Gemini AI</button>
                                    <div class="badge">NetBeans Studio Mobile Engine</div>
                                </div>

                                <script>
                                    let c = 0;
                                    function increment() {
                                        c++;
                                        document.getElementById('count').innerText = c;
                                    }
                                    function decrement() {
                                        c--;
                                        document.getElementById('count').innerText = c;
                                    }
                                    function askGemini() {
                                        alert("Gemini AI: Wynik licznika to: " + c + "! Kod działa perfekcyjnie.");
                                    }
                                </script>
                            </body>
                            </html>
                        """.trimIndent(),
                        isMain = true
                    )
                )
            )
        }
    }
}
