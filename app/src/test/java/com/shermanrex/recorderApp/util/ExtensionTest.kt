package com.shermanrex.recorderApp.util

import com.google.common.truth.Truth.assertThat
import com.shermanrex.recorderApp.data.util.convertByteToReadableSize
import com.shermanrex.recorderApp.data.util.convertMilliSecondToTime
import com.shermanrex.recorderApp.data.util.convertToKbps
import com.shermanrex.recorderApp.data.util.convertHzToKhz
import com.shermanrex.recorderApp.data.util.getFileFormat
import com.shermanrex.recorderApp.data.util.removeFileFormat
import org.junit.Test

class ExtensionTest {

  @Test
  fun remove_File_Extension() {
    val input = "new record.m4a"
    val except = "new record"

    val test = input.removeFileFormat()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun get_File_Extension() {
    val input = "new record.m4a"
    val except = "m4a"

    val test = input.getFileFormat()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_Hz_To_Khz_with_extension() {
    val input = 44_000
    val except = "44KHz"

    val test = input.convertHzToKhz()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_bps_To_kbps_with_extension() {
    val input = 128_000
    val except = "128 Kbps"

    val test = input.convertToKbps()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_byte_To_readableSize_with_extension_test_one() {
    val input = 1_000_000_000L
    val except = "1 gb"

    val test = input.convertByteToReadableSize().text
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_byte_To_readableSize_with_extension_test_two() {
    val input = 1_200_000L
    val except = "1.2 mg"

    val test = input.convertByteToReadableSize().text
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_one() {
    val input = 143_000
    val except = "02:23"

    val test = input.convertMilliSecondToTime(false)
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_two() {
    val input = 12_322_300
    val except = "03:25:22"

    val test = input.convertMilliSecondToTime(false)
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_three() {
    val input = 22_422_300
    val except = "06:13:42.3"

    val test = input.convertMilliSecondToTime()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_four() {
    val input = 22_422_000
    val except = "06:13:42.0"

    val test = input.convertMilliSecondToTime()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_five() {
    val input = 2_242_200
    val except = "37:22.2"

    val test = input.convertMilliSecondToTime()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_six() {
    val input = 22_422
    val except = "00:22.4"

    val test = input.convertMilliSecondToTime()
    assertThat(test).isEqualTo(except)
  }

  @Test
  fun convert_milliSecond_to_time_without_milliSecond_test_seven() {
    val input =  5_422
    val except = "00:05"

    val test = input.convertMilliSecondToTime(false)
    assertThat(test).isEqualTo(except)
  }
}