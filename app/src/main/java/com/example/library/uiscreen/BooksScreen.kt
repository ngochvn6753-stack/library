package com.example.library.uiscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.LibraryViewModel
import com.example.library.model.LibraryItem
import com.example.library.model.formatMoney

@Composable
fun BooksScreen(vm: LibraryViewModel) {
    var query by rememberSaveable { mutableStateOf("") }
    val shown = vm.items.filter {
        it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text("Danh sách sách", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Tìm theo tên hoặc tác giả") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            if (shown.isEmpty()) {
                item { Text("Không tìm thấy tài liệu nào", color = Color.Gray) }
            }
            items(shown, key = { it.id }) { BookCard(it) }
        }
    }
}

@Composable
private fun BookCard(item: LibraryItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    item.category,
                    fontSize = 11.sp,
                    color = LibBlue,
                    modifier = Modifier
                        .background(Color(0xFFE3EAFB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(item.describe(), color = Color.Gray, fontSize = 12.sp)
            Text(
                "Mượn ${item.loanDays} ngày · phạt ${formatMoney(item.finePerDay)}/ngày trễ",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                if (item.isAvailable) "Còn ${item.availableCopies}/${item.totalCopies} bản" else "Hết sách",
                color = if (item.isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}