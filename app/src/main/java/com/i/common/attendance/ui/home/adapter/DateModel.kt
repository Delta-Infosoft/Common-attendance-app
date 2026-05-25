package com.i.common.attendance.ui.home.adapter

data class DateModel(
    val day: String,
    val date: String,
    val month: String,
    var isSelected: Boolean = false
)