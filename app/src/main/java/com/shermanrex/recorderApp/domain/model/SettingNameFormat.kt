package com.shermanrex.recorderApp.domain.model

enum class SettingNameFormat(val value: String, val id: Int, val pattern: String) {
  FULL_DATE_TIME("YYYY/MM/DD - Time", 1, "yyyy-MM-dd HH:mm"),
  ASK_ON_RECORD("Ask on record", 2, ""),
  SEMI_DATE_TIME("MM/DD - Time", 3, "MM-dd HH:mm"),
  TIME("Time", 4, "HH:mm"),
}