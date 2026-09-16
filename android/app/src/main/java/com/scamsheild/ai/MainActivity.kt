package com.scamsheild.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scamsheild.ai.ui.theme.ScamSheildAITheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScamSheildAITheme {
                ScamShieldScreen()
            }
        }
    }
}

@Composable
fun ScamShieldScreen() {

    var message by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("SMS") }
    var expanded by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(text = "ScamShield AI")

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "AI that protects you from scams")

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Message to analyze")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = {
                Text("Paste or type a suspicious message")
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Source")

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { expanded = true }
        ) {
            Text(source)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = { Text("SMS") },
                onClick = {
                    source = "SMS"
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("WhatsApp") },
                onClick = {
                    source = "WhatsApp"
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Email") },
                onClick = {
                    source = "Email"
                    expanded = false
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (message.isBlank()) {
                    result = "Please enter a message."
                    return@Button
                }

                isLoading = true
                result = "Analyzing..."

                val request = AnalyzeRequest(
                    source = source,
                    text = message
                )

                ApiClient.api.analyzeMessage(request)
                    .enqueue(object : Callback<AnalyzeResponse> {

                        override fun onResponse(
                            call: Call<AnalyzeResponse>,
                            response: Response<AnalyzeResponse>
                        ) {

                            isLoading = false

                            if (response.isSuccessful) {

                                val data = response.body()

                                if (data != null) {

                                    result = """
                                        Risk Score: ${data.risk_score}/100
                                        Level: ${data.level}
                                        Category: ${data.category}
                                        
                                        Reasons:
                                        ${data.reasons.joinToString("\n")}
                                        
                                        Explanation:
                                        ${data.explanation}
                                        
                                        Recommendation:
                                        ${data.recommendation}
                                    """.trimIndent()

                                } else {
                                    result = "No analysis result received."
                                }

                            } else {
                                result = "Server error: ${response.code()}"
                            }
                        }

                        override fun onFailure(
                            call: Call<AnalyzeResponse>,
                            t: Throwable
                        ) {

                            isLoading = false
                            result = "Connection failed:\n${t.message}"
                        }
                    })
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Analyzing..." else "Analyze Message"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Analysis Result")

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = result)
        }
    }
}