package com.example.library.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val VN: Locale = Locale.forLanguageTag("vi-VN")

fun Long.toDateText(): String = SimpleDateFormat("dd/MM/yyyy", VN).format(Date(this))

fun formatMoney(amount: Long): String = String.format(VN, "%,d đ", amount)