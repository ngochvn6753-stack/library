package com.example.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.library.model.Book
import com.example.library.model.DAY_MS
import com.example.library.model.ItemType
import com.example.library.model.Lecturer
import com.example.library.model.LibraryItem
import com.example.library.model.Loan
import com.example.library.model.Magazine
import com.example.library.model.Person
import com.example.library.model.Student
import com.example.library.model.Thesis
import com.example.library.model.formatMoney
import com.example.library.model.MAX_RENEWALS
import com.example.library.model.toDateText
class LibraryViewModel : ViewModel(), LibraryService {

    private val _items = mutableStateListOf<LibraryItem>()
    private val _people = mutableStateListOf<Person>()
    private val _loans = mutableStateListOf<Loan>()

    val items: List<LibraryItem> get() = _items
    val people: List<Person> get() = _people

    var currentPerson by mutableStateOf<Person?>(null)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    private var itemSeq = 0
    private var personSeq = 0
    private var loanSeq = 0

    init {
        seedDemoData()
    }

    fun activeLoansOf(person: Person): List<Loan> =
        _loans.filter { it.borrower.id == person.id && !it.isReturned }

    fun loansOf(person: Person): List<Loan> =
        _loans.filter { it.borrower.id == person.id }

    fun selectPerson(person: Person) {
        currentPerson = person
    }

    fun addPerson(name: String) {
        if (name.isBlank()) {
            showMessage("Hãy nhập tên sinh viên")
            return
        }
        val p = createPerson(name)
        if (currentPerson == null) currentPerson = p
        showMessage("Đã thêm ${p.name} (${p.id})")
    }

    fun addItem(type: ItemType, title: String, author: String, copies: Int) {
        if (title.isBlank() || copies < 1) {
            showMessage("Thông tin tài liệu chưa hợp lệ")
            return
        }
        val item = createItem(type, title, author, copies)
        showMessage("Đã thêm \"${item.title}\"")
    }

    fun borrowSelected(itemIds: Set<String>) {
        val person = currentPerson
        if (person == null) {
            showMessage("Hãy chọn sinh viên trước")
            return
        }
        if (itemIds.isEmpty()) {
            showMessage("Hãy tick ít nhất một tài liệu")
            return
        }
        val results = _items.filter { it.id in itemIds }.map { borrow(person, it) }
        val okCount = results.count { it is Outcome.Ok }
        val firstError = results.filterIsInstance<Outcome.Error>().firstOrNull()
        showMessage(
            when {
                firstError == null -> "${person.name} đã mượn $okCount tài liệu"
                okCount == 0 -> firstError.message
                else -> "Mượn được $okCount tài liệu. ${firstError.message}"
            }
        )
    }

    fun returnLoan(loan: Loan) = showMessage(giveBack(loan).message)
    fun returnLoans(loanIds: Set<Int>) {
        val list = _loans.filter { it.id in loanIds && !it.isReturned }
        if (list.isEmpty()) {
            showMessage("Hãy tick tài liệu cần trả")
            return
        }
        list.forEach { giveBack(it) }
        val totalFine = list.sumOf { it.fine() }
        showMessage(
            if (totalFine > 0) "Đã trả ${list.size} tài liệu. Tiền phạt: ${formatMoney(totalFine)}"
            else "Đã trả ${list.size} tài liệu"
        )
    }

    fun renewLoan(loan: Loan) {
        showMessage(
            when {
                loan.isReturned -> "Tài liệu này đã trả rồi"
                loan.isOverdue() -> "Sách đã quá hạn, hãy trả sách thay vì gia hạn"
                loan.renewCount >= MAX_RENEWALS -> "Chỉ được gia hạn tối đa $MAX_RENEWALS lần"
                else -> {
                    loan.renew()
                    "Đã gia hạn \"${loan.item.title}\" đến ${loan.dueAt.toDateText()}"
                }
            }
        )
    }
    fun consumeMessage() {
        message = null
    }

    override fun borrow(person: Person, item: LibraryItem): Outcome {
        val active = activeLoansOf(person)
        if (active.any { it.isOverdue() })
            return Outcome.Error("${person.name} đang có sách quá hạn, hãy trả trước khi mượn thêm")
        if (active.size >= person.maxBooks)
            return Outcome.Error("${person.name} đã mượn tối đa ${person.maxBooks} tài liệu")
        if (active.any { it.item.id == item.id })
            return Outcome.Error("${person.name} đang mượn \"${item.title}\" rồi")
        if (!item.checkOut())
            return Outcome.Error("\"${item.title}\" đã hết bản")

        val now = System.currentTimeMillis()
        // POLYMORPHISM: loanDays do từng loại tài liệu tự quyết định
        _loans.add(0, Loan(++loanSeq, item, person, now, now + item.loanDays * DAY_MS))
        return Outcome.Ok("Đã mượn \"${item.title}\"")
    }

    override fun giveBack(loan: Loan): Outcome {
        if (loan.isReturned) return Outcome.Error("Tài liệu này đã được trả rồi")
        loan.markReturned(System.currentTimeMillis())
        loan.item.checkIn()
        val fine = loan.fine()
        return Outcome.Ok(
            if (fine > 0) "Đã trả \"${loan.item.title}\". Tiền phạt: ${formatMoney(fine)}"
            else "Đã trả \"${loan.item.title}\""
        )
    }

    private fun createItem(type: ItemType, title: String, author: String, copies: Int): LibraryItem {
        val id = "TL" + (++itemSeq).toString().padStart(3, '0')
        val t = title.trim()
        val a = author.trim().ifBlank { "Không rõ" }
        val item = when (type) {
            ItemType.BOOK -> Book(id, t, a, copies)
            ItemType.MAGAZINE -> Magazine(id, t, a, copies)
            ItemType.THESIS -> Thesis(id, t, a, copies)
        }
        _items.add(item)
        return item
    }

    private fun createPerson(name: String, lecturer: Boolean = false): Person {
        val prefix = if (lecturer) "GV" else "SV"
        val id = prefix + (++personSeq).toString().padStart(3, '0')
        val p = if (lecturer) Lecturer(id, name) else Student(id, name)
        _people.add(p)
        return p
    }

    private fun showMessage(text: String) {
        message = text
    }

    private fun seedDemoData() {
        val kotlin = createItem(ItemType.BOOK, "Lập trình Kotlin cơ bản", "Nguyễn Văn An", 3)
        createItem(ItemType.BOOK, "Cấu trúc dữ liệu và giải thuật", "Lê Minh Quân", 2)
        createItem(ItemType.BOOK, "Lập trình hướng đối tượng", "Trần Thị Hoa", 2)
        createItem(ItemType.MAGAZINE, "Tạp chí Giao thông Vận tải", "Bộ GTVT", 2)
        createItem(ItemType.THESIS, "Ứng dụng AI trong logistics", "Phạm Đức Anh", 1)

        val a = createPerson("Nguyen Van A")
        val b = createPerson("Tran Thi B")
        createPerson("TS. Le Van C", lecturer = true)

        val now = System.currentTimeMillis()
        if (kotlin.checkOut()) {
            _loans.add(Loan(++loanSeq, kotlin, b, now - 20 * DAY_MS, now - 6 * DAY_MS))
        }
        val ds = _items.first { it.title.startsWith("Cấu trúc") }
        val oop = _items.first { it.title.startsWith("Lập trình hướng") }
        if (ds.checkOut()) _loans.add(Loan(++loanSeq, ds, a, now - 2 * DAY_MS, now + 12 * DAY_MS))
        if (oop.checkOut()) _loans.add(Loan(++loanSeq, oop, a, now - 1 * DAY_MS, now + 13 * DAY_MS))
    }
}