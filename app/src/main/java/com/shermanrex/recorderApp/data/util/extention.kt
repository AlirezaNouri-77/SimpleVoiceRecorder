package com.shermanrex.recorderApp.data.util

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun <T : Number> T?.convertMilliSecondToTime(showMilliSecond: Boolean = true): String {
  if (this == null) return "00:00"

  val minute = TimeUnit.MILLISECONDS.toMinutes(this.toLong()) % 60
  val second = TimeUnit.MILLISECONDS.toSeconds(this.toLong()) % 60
  val milliSecond = TimeUnit.MILLISECONDS.toMillis(this.toLong()) % 1000
  return if (showMilliSecond) {
    String.format(
      Locale.ENGLISH,
      "%02d:%02d.%01d",
      minute,
      second,
      BigDecimal(milliSecond).toInt() / 100
    )
  } else {
    String.format(
      Locale.ENGLISH,
      "%02d:%02d",
      minute,
      second,
    )
  }
}

fun Long.convertByteToReadableSize(): AnnotatedString {
  val input = this
  return if (input >= 1_000_000_000) {
    buildAnnotatedString {
      withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)) {
        append(DecimalFormat("##.#").format(input.div(1_000_000_000f)).toString())
      }
      withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Light)) {
        append(" gb")
      }
    }
  } else {
    buildAnnotatedString {
      withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)) {
        append(DecimalFormat("##.##").format(input.div(1_000_000f)).toString()).toString()
      }
      withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Light)) {
        append(" mg")
      }
    }
  }
}

fun Int.convertToKbps(): String {
  return (this / 1000).toString() + " Kbps"
}

fun convertTimeStampToDate(pattern: String): String {
  val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
  return sdf.format(Date(System.currentTimeMillis()))
}

fun Int.bitToKbps(): String {
  return this.div(1000).toString() + "Kbps"
}

fun Int.convertToKhz(): String {
  return this.div(1000).toString() + "KHz"
}

fun String.getFileFormat(): String {
  return this.substringAfterLast(".")
}

fun String.removeFileformat(): String {
  return this.substringBeforeLast(".")
}
