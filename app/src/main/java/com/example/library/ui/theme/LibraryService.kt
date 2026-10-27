package com.example.library

import com.example.library.model.LibraryItem
import com.example.library.model.Loan
import com.example.library.model.Person

sealed interface Outcome {
    val message: String
    data class Ok(override val message: String = "") : Outcome
    data class Error(override val message: String) : Outcome
}

/** ABSTRACTION: hợp đồng nghiệp vụ của thư viện, UI chỉ cần biết "mượn" và "trả". */
interface LibraryService {
    fun borrow(person: Person, item: LibraryItem): Outcome
    fun giveBack(loan: Loan): Outcome
}