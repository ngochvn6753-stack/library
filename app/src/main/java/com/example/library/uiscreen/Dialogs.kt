package com.example.library.uiscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.library.model.ItemType

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (ItemType, String, String, Int) -> Unit
) {
    var type by remember { mutableStateOf(ItemType.BOOK) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var copies by remember { mutableStateOf("1") }
    val copiesInt = copies.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm tài liệu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ItemType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t.label) }
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên tài liệu") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Tác giả") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = copies,
                    onValueChange = { copies = it.filter(Char::isDigit) },
                    label = { Text("Số lượng") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && copiesInt >= 1,
                onClick = { onConfirm(type, title, author, copiesInt) }
            ) { Text("Thêm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
fun AddPersonDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm sinh viên") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và tên") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = { onConfirm(name) }) { Text("Thêm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}