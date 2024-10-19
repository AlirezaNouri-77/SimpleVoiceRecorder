package com.shermanrex.recorderApp.data.util

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

inline fun <T : Number> T?.convertMilliSecondToTime(showMilliSecond: Boolean = true): String {
  if (this == null) return "00:00"

  val hour = TimeUnit.MILLISECONDS.toHours(this.toLong()) % 60
  var minute = TimeUnit.MILLISECONDS.toMinutes(this.toLong())
  val second = TimeUnit.MILLISECONDS.toSeconds(this.toLong()) % 60
  val milliSecond = TimeUnit.MILLISECONDS.toMillis(this.toLong()) % 1000

  val format = StringBuilder().run {
    if (minute < 60) { //if time is less than 60 minute
      append("%02d:%02d")
    } else { //if time is more than 60 minute
      minute %= 60
      append("%02d:%02d:%02d")
    }
    if (showMilliSecond) append(".%01d")
    toString()
  }

  return if (hour > 1) {
    String.format(
      Locale.ENGLISH,
      format,
      hour,
      minute,
      second,
      if (showMilliSecond) BigDecimal(milliSecond).toInt() / 100 else null,
    )
  } else {
    String.format(
      Locale.ENGLISH,
      format,
      minute,
      second,
      if (showMilliSecond) BigDecimal(milliSecond).toInt() / 100 else null,
    )
  }

}

fun <T : Number> T.convertByteToReadableSize(): AnnotatedString {
  if (this !is Long) throw IllegalArgumentException("input should be long")
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

fun Int.convertHzToKhz(): String {
  return this.div(1000).toString() + "KHz"
}

fun String.getFileFormat(): String {
  return this.substringAfterLast(".")
}

fun String.removeFileFormat(): String {
  return this.substringBeforeLast(".")
}
