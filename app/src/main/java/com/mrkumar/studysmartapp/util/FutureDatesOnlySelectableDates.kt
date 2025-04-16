package com.mrkumar.studysmartapp.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
class FutureDatesOnlySelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val currentDate = LocalDate.now(ZoneId.systemDefault())
        return !selectedDate.isBefore(currentDate)
    }

    override fun isSelectableYear(year: Int): Boolean {
        val currentYear = LocalDate.now().year
        return year >= currentYear
    }
}
