package com.example.library.uiscreen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.library.LibraryViewModel
import com.example.library.model.Loan
import com.example.library.model.Person
import com.example.library.model.formatMoney
import com.example.library.model.toDateText

@Composable
fun PeopleScreen(vm: LibraryViewModel) {
    val current = vm.currentPerson

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item { Text("Thành viên thư viện", fontWeight = FontWeight.Bold, fontSize = 20.sp) }

        items(vm.people, key = { it.id }) { p ->
            PersonCard(
                person = p,
                isCurrent = p.id == current?.id,
                borrowing = vm.activeLoansOf(p).size,
                onClick = { vm.selectPerson(p) }
            )
        }

        if (current != null) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Lịch sử mượn của ${current.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            val loans = vm.loansOf(current)
            if (loans.isEmpty()) {
                item { Text("Chưa mượn tài liệu nào", color = Color.Gray) }
            }
            items(loans, key = { it.id }) { loan ->
                LoanCard(loan, onReturn = { vm.returnLoan(loan) })
            }
        }
    }
}

@Composable
private fun PersonCard(person: Person, isCurrent: Boolean, borrowing: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if (isCurrent) BorderStroke(2.dp, LibBlue) else null,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(person.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${person.id} · ${person.role}", color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                "$borrowing/${person.maxBooks}",
                color = LibBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoanCard(loan: Loan, onReturn: () -> Unit) {
    val fine = loan.fine()
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(loan.item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "Mượn ${loan.borrowedAt.toDateText()} · Hạn ${loan.dueAt.toDateText()}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                when {
                    loan.isReturned -> Text(
                        "Đã trả ${loan.returnedAt?.toDateText()}" +
                                if (fine > 0) " · phạt ${formatMoney(fine)}" else "",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    loan.isOverdue() -> Text(
                        "QUÁ HẠN · phạt tạm tính ${formatMoney(fine)}",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    else -> Text("Còn hạn", color = Color(0xFF2E7D32), fontSize = 12.sp)
                }
            }
            if (!loan.isReturned) {
                Button(
                    onClick = onReturn,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LibBlue)
                ) { Text("Trả") }
            }
        }
    }
}