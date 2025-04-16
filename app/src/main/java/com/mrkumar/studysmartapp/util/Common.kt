package com.mrkumar.studysmartapp.util

import androidx.compose.ui.graphics.Color
import com.mrkumar.studysmartapp.presentation.theme.Green
import com.mrkumar.studysmartapp.presentation.theme.Orange
import com.mrkumar.studysmartapp.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String,val color: Color,val value: Int){
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object{
        fun fromInt(value: Int)=values().firstOrNull{it.value==value}?:MEDIUM
    }
}

fun Long?.changeMillsToDateString(): String{
    val date: LocalDate=this?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()
return date.format(DateTimeFormatter.ofPattern("dd MM yyyy"))
}

fun Long.toHours():Float{
    val hours=this.toFloat()/3600f
    return "%.2f".format(hours).toFloat()

}