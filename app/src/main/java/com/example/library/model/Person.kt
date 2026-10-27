package com.example.library.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * ABSTRACTION: lớp trừu tượng, chỉ định nghĩa "khuôn" chung.
 * ENCAPSULATION: `name` chỉ đổi được qua rename() (có kiểm tra dữ liệu).
 */
abstract class Person(val id: String, initialName: String) {

    var name by mutableStateOf(initialName.trim())
        private set

    fun rename(newName: String): Boolean {
        if (newName.isBlank()) return false
        name = newName.trim()
        return true
    }

    abstract val role: String
    abstract val maxBooks: Int
}

class Student(id: String, name: String) : Person(id, name) {
    override val role = "Sinh viên"
    override val maxBooks = 5
}


class Lecturer(id: String, name: String) : Person(id, name) {
    override val role = "Giảng viên"
    override val maxBooks = 10
}