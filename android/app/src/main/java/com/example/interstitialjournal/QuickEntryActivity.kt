package com.example.interstitialjournal

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.example.interstitialjournal.ui.theme.InterstitialJournalTheme
import kotlinx.coroutines.launch

class QuickEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InterstitialJournalTheme {
                QuickEntryScreen(onDismiss = { finish() })
            }
        }
    }
}

@Composable
fun QuickEntryScreen(onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val repository = remember { EntryRepository() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .imePadding() // Automatically pushes content above the keyboard
            .navigationBarsPadding(), // Ensures content respects system navigation bar
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What're you up to?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    maxLines = 5,
                    enabled = !isSaving
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (text.isNotBlank() && !isSaving) {
                                isSaving = true
                                coroutineScope.launch {
                                    val success = repository.addEntry(text)
                                    if (success) {
                                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                                        JournalWidget().updateAll(context)
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                        isSaving = false
                                    }
                                }
                            }
                        },
                        enabled = text.isNotBlank() && !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
