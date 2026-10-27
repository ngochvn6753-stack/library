package com.example.library.uiscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.LibraryViewModel
import com.example.library.model.Loan
import com.example.library.model.Person
import com.example.library.model.formatMoney
import com.example.library.model.toDateText

private val TrayGray = Color(0xFFE6E6E6)

@Composable
fun ManageScreen(vm: LibraryViewModel) {
    val person = vm.currentPerson
    val loans = if (person != null) vm.activeLoansOf(person) else emptyList()

    val selected = remember(person?.id) { mutableStateListOf<Int>() }
    var showPicker by remember { mutableStateOf(false) }
    var showBorrow by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            item {
                Text(
                    "Hệ thống\nQuản lý Thư viện",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(24.dp))
                Text("Sinh viên", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = person?.name ?: "Chưa chọn sinh viên",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    Button(
                        onClick = { showPicker = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LibBlue)
                    ) { Text("Thay đổi") }
                }
                if (person != null) {
                    Text(
                        "Đang mượn ${loans.size}/${person.maxBooks} tài liệu",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Danh sách sách", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            when {
                person == null -> item {
                    TrayMessage("Hãy bấm \"Thay đổi\" để chọn sinh viên")
                }
                loans.isEmpty() -> item {
                    TrayMessage("${person.name} chưa mượn tài liệu nào.\nBấm \"Thêm\" để cho mượn.")
                }
                else -> itemsIndexed(loans, key = { _, loan -> loan.id }) { index, loan ->
                    val first = index == 0
                    val last = index == loans.lastIndex
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(TrayGray, trayShape(first, last))
                            .padding(
                                start = 12.dp,
                                end = 12.dp,
                                top = if (first) 12.dp else 4.dp,
                                bottom = if (last) 12.dp else 4.dp
                            )
                    ) {
                        LoanPickRow(
                            loan = loan,
                            checked = loan.id in selected,
                            onToggle = { if (it) selected.add(loan.id) else selected.remove(loan.id) },
                            onRenew = { vm.renewLoan(loan) }
                        )
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showBorrow = true },
                enabled = person != null,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LibBlue),
                modifier = Modifier.weight(1f)
            ) { Text("Thêm") }

            OutlinedButton(
                onClick = {
                    vm.returnLoans(selected.toSet())
                    selected.clear()
                },
                enabled = person != null && selected.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) { Text("Trả sách") }
        }
    }

    if (showPicker) {
        PersonPickerDialog(vm, person, onDismiss = { showPicker = false })
    }
    if (showBorrow && person != null) {
        BorrowDialog(vm, person, onDismiss = { showBorrow = false })
    }
}

private fun trayShape(first: Boolean, last: Boolean) = RoundedCornerShape(
    topStart = if (first) 12.dp else 0.dp,
    topEnd = if (first) 12.dp else 0.dp,
    bottomStart = if (last) 12.dp else 0.dp,
    bottomEnd = if (last) 12.dp else 0.dp
)

@Composable
private fun TrayMessage(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(TrayGray, RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LoanPickRow(
    loan: Loan,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
    onRenew: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = LibMaroon)
        )
        Column(Modifier.weight(1f)) {
            Text(loan.item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                "Hạn ${loan.dueAt.toDateText()}" +
                        if (loan.renewCount > 0) " · đã gia hạn ${loan.renewCount} lần" else "",
                fontSize = 11.sp,
                color = Color.Gray
            )
            if (loan.isOverdue()) {
                Text(
                    "QUÁ HẠN · phạt ${formatMoney(loan.fine())}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
            }
        }
        TextButton(onClick = onRenew, enabled = loan.canRenew) {
            Text("Gia hạn", fontSize = 12.sp)
        }
    }
}

@Composable
private fun PersonPickerDialog(vm: LibraryViewModel, current: Person?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn người mượn") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                vm.people.forEach { p ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.selectPerson(p)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            p.name,
                            fontWeight = if (p.id == current?.id) FontWeight.Bold else FontWeight.Normal
                        )
                        Text("${p.id} · ${p.role}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng") } }
    )
}

@Composable
private fun BorrowDialog(vm: LibraryViewModel, person: Person, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val picked = remember { mutableStateListOf<String>() }
    val borrowedIds = vm.activeLoansOf(person).map { it.item.id }.toSet()
    val shown = vm.items.filter {
        it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm sách cho ${person.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Tìm tên sách hoặc tác giả") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.heightIn(max = 320.dp)) {
                    items(shown, key = { it.id }) { item ->
                        val already = item.id in borrowedIds
                        val enabled = item.isAvailable && !already
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(enabled = enabled) {
                                    if (item.id in picked) picked.remove(item.id) else picked.add(item.id)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.id in picked,
                                onCheckedChange = { if (it) picked.add(item.id) else picked.remove(item.id) },
                                enabled = enabled,
                                colors = CheckboxDefaults.colors(checkedColor = LibMaroon)
                            )
                            Column {
                                Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    when {
                                        already -> "Đang mượn"
                                        !item.isAvailable -> "Hết sách"
                                        else -> "${item.author} · Còn ${item.availableCopies}/${item.totalCopies}"
                                    },
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = picked.isNotEmpty(),
                onClick = {
                    vm.borrowSelected(picked.toSet())
                    onDismiss()
                }
            ) { Text("Mượn (${picked.size})") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}