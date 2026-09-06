package com.example.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.SyntaxAnnotation
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxType

object CodeSyntaxHighlighter {

    private val javaKotlinKeywords = setOf(
        "package", "import", "class", "interface", "object", "fun", "val", "var",
        "public", "private", "protected", "internal", "static", "final", "void",
        "return", "if", "else", "when", "for", "while", "do", "try", "catch",
        "finally", "throw", "throws", "new", "this", "super", "null", "true", "false",
        "data", "sealed", "abstract", "override", "suspend", "companion", "enum"
    )

    private val commonTypes = setOf(
        "String", "Int", "Long", "Double", "Float", "Boolean", "List", "Map", "Set",
        "ArrayList", "HashMap", "void", "int", "long", "double", "float", "boolean",
        "char", "byte", "short", "Object", "Unit", "Any"
    )

    fun highlightCode(code: String, extension: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            if (code.isEmpty()) return@buildAnnotatedString

            // 1. Strings: "..." or '...'
            val stringRegex = Regex("\"(\\\\.|[^\"])*\"|'(\\\\.|[^'])*'")
            for (match in stringRegex.findAll(code)) {
                addStyle(
                    SpanStyle(color = SyntaxString),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 2. Comments: // ... or /* ... */
            val lineCommentRegex = Regex("//.*")
            for (match in lineCommentRegex.findAll(code)) {
                addStyle(
                    SpanStyle(color = SyntaxComment, fontWeight = FontWeight.Normal),
                    match.range.first,
                    match.range.last + 1
                )
            }

            val blockCommentRegex = Regex("/\\*[\\s\\S]*?\\*/")
            for (match in blockCommentRegex.findAll(code)) {
                addStyle(
                    SpanStyle(color = SyntaxComment),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 3. Numbers
            val numberRegex = Regex("\\b\\d+(?:\\.\\d+)?(?:[fFdDlL])?\\b")
            for (match in numberRegex.findAll(code)) {
                addStyle(
                    SpanStyle(color = SyntaxNumber),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 4. Annotations: @Override, @Entity, etc.
            val annotationRegex = Regex("@[a-zA-Z0-9_]+")
            for (match in annotationRegex.findAll(code)) {
                addStyle(
                    SpanStyle(color = SyntaxAnnotation, fontWeight = FontWeight.Bold),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // 5. Keywords & Types
            val wordRegex = Regex("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
            for (match in wordRegex.findAll(code)) {
                val word = match.value
                when {
                    word in javaKotlinKeywords -> {
                        addStyle(
                            SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.SemiBold),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                    word in commonTypes -> {
                        addStyle(
                            SpanStyle(color = SyntaxType, fontWeight = FontWeight.Medium),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                }
            }
        }
    }
}
