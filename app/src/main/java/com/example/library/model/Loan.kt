package com.example.library.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

const val DAY_MS = 24L * 60 * 60 * 1000
const val MAX_RENEWALS = 2

class Loan(
    val id: Int,
    val item: LibraryItem,
    val borrower: Person,
    val borrowedAt: Long,
    initialDueAt: Long
) {
    var dueAt by mutableLongStateOf(initialDueAt)
        private set

    var renewCount by mutableIntStateOf(0)
        private set

    var returnedAt by mutableStateOf<Long?>(null)
        private set

    val isReturned: Boolean get() = returnedAt != null

    fun isOverdue(now: Long = System.currentTimeMillis()): Boolean = !isReturned && now > dueAt

    val canRenew: Boolean get() = !isReturned && !isOverdue() && renewCount < MAX_RENEWALS

    fun fine(now: Long = System.currentTimeMillis()): Long {
        val end = returnedAt ?: now
        val daysLate = (end - dueAt + DAY_MS - 1) / DAY_MS
        return item.calculateFine(daysLate)
    }

    internal fun markReturned(now: Long) {
        returnedAt = now
    }

    internal fun renew() {
        dueAt += item.loanDays * DAY_MS
        renewCount++
    }
}