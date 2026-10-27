package com.example.library.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

enum class ItemType(val label: String) {
    BOOK("Sách"),
    MAGAZINE("Tạp chí"),
    THESIS("Luận văn")
}


abstract class LibraryItem(
    val id: String,
    val title: String,
    val author: String,
    val totalCopies: Int
) {
    var availableCopies by mutableIntStateOf(totalCopies)
        private set

    abstract val category: String
    abstract val loanDays: Int        // số ngày được mượn
    abstract val finePerDay: Long     // tiền phạt mỗi ngày trễ (VND)

    val isAvailable: Boolean get() = availableCopies > 0

    internal fun checkOut(): Boolean {
        if (availableCopies <= 0) return false
        availableCopies--
        return true
    }

    internal fun checkIn() {
        if (availableCopies < totalCopies) availableCopies++
    }

    fun calculateFine(daysLate: Long): Long = if (daysLate > 0) daysLate * finePerDay else 0L

    open fun describe(): String = "$category · $author"
}

class Book(id: String, title: String, author: String, copies: Int) :
    LibraryItem(id, title, author, copies) {
    override val category = "Sách"
    override val loanDays = 14
    override val finePerDay = 2_000L
}

class Magazine(id: String, title: String, author: String, copies: Int) :
    LibraryItem(id, title, author, copies) {
    override val category = "Tạp chí"
    override val loanDays = 7
    override val finePerDay = 3_000L
}

class Thesis(id: String, title: String, author: String, copies: Int) :
    LibraryItem(id, title, author, copies) {
    override val category = "Luận văn"
    override val loanDays = 3
    override val finePerDay = 10_000L
    override fun describe() = "$category · $author · mượn ngắn hạn"
}